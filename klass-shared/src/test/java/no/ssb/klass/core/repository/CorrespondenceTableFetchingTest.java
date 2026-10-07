package no.ssb.klass.core.repository;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.EMBEDDED;

import static org.assertj.core.api.Assertions.assertThat;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import no.ssb.klass.core.config.ConfigurationProfiles;
import no.ssb.klass.core.model.ClassificationFamily;
import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.model.CorrespondenceMap;
import no.ssb.klass.core.model.CorrespondenceTable;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.model.Level;
import no.ssb.klass.core.model.User;
import no.ssb.klass.core.util.TranslatablePersistenceConverter;
import no.ssb.klass.testutil.TestUtil;

import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Loading a correspondence table must cost a fixed number of queries, however many maps it has, and
 * however many times it has been loaded before.
 *
 * <p>This guards against the second-level cache regression that made {@code
 * /correspondencetables/{id}} take ~20 seconds in production. Caching {@code CorrespondenceMap} and
 * the {@code correspondenceMaps} collection, but not the {@code ClassificationItem}s each map
 * points at, meant a warm cache resolved every map's source and target with its own query. At 3000
 * maps that was 6004 queries against the 5 a cold cache needed.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(
        properties = {
            "spring.jpa.properties.hibernate.generate_statistics=true",
            "spring.jpa.properties.hibernate.default_batch_fetch_size=20",
            "spring.jpa.properties.hibernate.max_fetch_depth=2",
            // The second-level cache is switched on here even though klass-api disables it, so that
            // re-introducing @Cache on CorrespondenceMap trips this test rather than production.
            "spring.cache.type=none",
            "spring.autoconfigure.exclude="
                    + "org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration,"
                    + "org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration",
            "spring.jpa.properties.hibernate.cache.use_second_level_cache=true",
            "spring.jpa.properties.hibernate.cache.region.factory_class="
                    + "org.hibernate.cache.jcache.internal.JCacheRegionFactory",
            "spring.jpa.properties.hibernate.javax.cache.provider="
                    + "org.ehcache.jsr107.EhcacheCachingProvider",
            "spring.jpa.properties.hibernate.javax.cache.uri=ehcache.xml",
            "spring.jpa.properties.hibernate.javax.cache.missing_cache_strategy=create-warn"
        })
@ActiveProfiles({
    ConfigurationProfiles.POSTGRES_EMBEDDED,
    ConfigurationProfiles.MOCK_MAILSERVER,
    ConfigurationProfiles.MOCK_SEARCH
})
@AutoConfigureEmbeddedDatabase(
        provider = EMBEDDED,
        type = AutoConfigureEmbeddedDatabase.DatabaseType.POSTGRES)
class CorrespondenceTableFetchingTest {

    private static final int MAP_COUNT = 500;

    /**
     * Generous: the point is that cost does not scale with map count, not the exact query plan.
     * Loading a table currently takes 5 queries.
     */
    private static final int QUERY_BUDGET = 25;

    @Autowired private CorrespondenceTableRepository correspondenceTableRepository;
    @Autowired private ClassificationSeriesRepository classificationSeriesRepository;
    @Autowired private ClassificationFamilyRepository classificationFamilyRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private PlatformTransactionManager transactionManager;

    @PersistenceContext private EntityManager entityManager;

    private TransactionTemplate tx;
    private User user;
    private ClassificationFamily classificationFamily;

    @BeforeEach
    void setup() {
        tx = new TransactionTemplate(transactionManager);
        tx.executeWithoutResult(
                status -> {
                    user = userRepository.save(TestUtil.createUser());
                    classificationFamily =
                            classificationFamilyRepository.save(
                                    TestUtil.createClassificationFamily("family"));
                });
    }

    @Test
    void repeatedLoadsDoNotCostAQueryPerCorrespondenceMap() {
        long id = tx.execute(status -> createAndSaveCorrespondenceTableWithMaps());
        SessionFactory sessionFactory =
                entityManager.getEntityManagerFactory().unwrap(SessionFactory.class);

        // The first load populates any caches; later loads are where the regression showed up.
        for (int run = 1; run <= 3; run++) {
            Statistics statistics = sessionFactory.getStatistics();
            statistics.clear();

            int maps = tx.execute(status -> loadAsTheServiceDoes(id));

            assertThat(maps).isEqualTo(MAP_COUNT);
            assertThat(statistics.getPrepareStatementCount())
                    .as("queries to load %d correspondence maps on run %d", MAP_COUNT, run)
                    .isLessThanOrEqualTo(QUERY_BUDGET);
        }
    }

    /** Mirrors {@code ClassificationServiceImpl.getCorrespondenceTable}. */
    private int loadAsTheServiceDoes(long id) {
        CorrespondenceTable table = correspondenceTableRepository.findById(id).orElseThrow();
        table.isThisOrSourceOrTargetDeleted();
        Hibernate.initialize(table.getSource().getLevels());
        Hibernate.initialize(table.getTarget().getLevels());
        int maps = table.getCorrespondenceMaps().size();
        Hibernate.initialize(table.getChangelogs());
        return maps;
    }

    private long createAndSaveCorrespondenceTableWithMaps() {
        ClassificationVersion source = createAndSaveVersionWithItems("source");
        ClassificationVersion target = createAndSaveVersionWithItems("target");
        CorrespondenceTable correspondenceTable =
                TestUtil.createCorrespondenceTable(source, target);
        correspondenceTable.publish(Language.getDefault());
        for (int i = 0; i < MAP_COUNT; i++) {
            correspondenceTable.addCorrespondenceMap(
                    new CorrespondenceMap(source.findItem(code(i)), target.findItem(code(i))));
        }
        return correspondenceTableRepository.save(correspondenceTable).getId();
    }

    private ClassificationVersion createAndSaveVersionWithItems(String name) {
        ClassificationSeries classification = TestUtil.createClassification(name);
        ClassificationVersion version =
                TestUtil.createClassificationVersion(TestUtil.anyDateRange());
        Level level = TestUtil.createLevel(1);
        version.addLevel(level);
        for (int i = 0; i < MAP_COUNT; i++) {
            version.addClassificationItem(
                    TestUtil.createClassificationItem(code(i), name + " item " + i),
                    level.getLevelNumber(),
                    null);
        }
        classification.addClassificationVersion(version);
        classification.setContactPerson(user);
        classificationFamily.addClassificationSeries(classification);
        classificationSeriesRepository.save(classification);
        return version;
    }

    private static String code(int i) {
        return String.format("%05d", i);
    }

    @Configuration
    @EnableAutoConfiguration
    @EntityScan(basePackageClasses = {CorrespondenceTable.class})
    @ComponentScan(basePackageClasses = TranslatablePersistenceConverter.class)
    static class Config {}
}
