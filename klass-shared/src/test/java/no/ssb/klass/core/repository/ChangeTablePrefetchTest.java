package no.ssb.klass.core.repository;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.EMBEDDED;

import static org.assertj.core.api.Assertions.assertThat;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import no.ssb.klass.core.config.ConfigurationProfiles;
import no.ssb.klass.core.model.ClassificationFamily;
import no.ssb.klass.core.model.ClassificationItem;
import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.model.CorrespondenceMap;
import no.ssb.klass.core.model.CorrespondenceTable;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.model.Level;
import no.ssb.klass.core.model.User;
import no.ssb.klass.core.util.TranslatablePersistenceConverter;
import no.ssb.klass.testutil.TestUtil;

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

import java.util.ArrayList;
import java.util.List;

/**
 * Walking every change table of a classification must not cost a round trip per table.
 *
 * <p>The changes endpoint visits one change table per pair of consecutive versions. Standard for
 * kommuneinndeling has 142 versions, so {@code /changes?from=1838-01-01} visits 141 tables. Loading
 * each table's maps on demand costs three queries per table; fetching them together costs a
 * handful, and that difference is the bulk of the endpoint's response time.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(
        properties = {
            "spring.jpa.properties.hibernate.generate_statistics=true",
            // Match klass-api, where batch fetching collapses the eager
            // ClassificationItem -> Level -> StatisticalClassification chain.
            "spring.jpa.properties.hibernate.default_batch_fetch_size=20",
            "spring.jpa.properties.hibernate.max_fetch_depth=2"
        })
@ActiveProfiles({
    ConfigurationProfiles.POSTGRES_EMBEDDED,
    ConfigurationProfiles.MOCK_MAILSERVER,
    ConfigurationProfiles.MOCK_SEARCH
})
@AutoConfigureEmbeddedDatabase(
        provider = EMBEDDED,
        type = AutoConfigureEmbeddedDatabase.DatabaseType.POSTGRES)
class ChangeTablePrefetchTest {

    private static final int CHANGE_TABLES = 40;
    private static final int MAPS_PER_TABLE = 20;

    /**
     * Loading the tables one at a time costs three queries each, so 40 tables would be 120;
     * fetching them together costs 4. The budget is loose on purpose: the point is that cost does
     * not scale with table count.
     */
    private static final int QUERY_BUDGET = 15;

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
    void prefetchingChangeTablesCostsAFixedNumberOfQueries() {
        List<Long> tableIds = tx.execute(status -> createChangeTables());
        Statistics statistics =
                entityManager
                        .getEntityManagerFactory()
                        .unwrap(SessionFactory.class)
                        .getStatistics();
        statistics.clear();

        int touched =
                tx.execute(
                        status -> {
                            List<CorrespondenceTable> tables =
                                    correspondenceTableRepository.findAllByIdWithMaps(tableIds);
                            return readEveryMap(tables);
                        });

        assertThat(touched).isEqualTo(CHANGE_TABLES * MAPS_PER_TABLE);
        assertThat(statistics.getPrepareStatementCount())
                .as("queries to walk %d change tables", CHANGE_TABLES)
                .isLessThanOrEqualTo(QUERY_BUDGET);
    }

    /** Mirrors what the changes endpoint reads from each correspondence map. */
    private int readEveryMap(List<CorrespondenceTable> tables) {
        int touched = 0;
        for (CorrespondenceTable table : tables) {
            for (CorrespondenceMap map : table.getCorrespondenceMaps()) {
                map.getSource().map(ClassificationItem::getCode);
                map.getTarget().map(ClassificationItem::getCode);
                touched++;
            }
        }
        return touched;
    }

    private List<Long> createChangeTables() {
        List<ClassificationVersion> versions = new ArrayList<>();
        for (int v = 0; v <= CHANGE_TABLES; v++) {
            versions.add(createAndSaveVersionWithItems("version" + v));
        }
        List<Long> ids = new ArrayList<>();
        for (int i = 0; i < CHANGE_TABLES; i++) {
            ClassificationVersion source = versions.get(i);
            ClassificationVersion target = versions.get(i + 1);
            CorrespondenceTable table = TestUtil.createCorrespondenceTable(source, target);
            table.publish(Language.getDefault());
            for (int m = 0; m < MAPS_PER_TABLE; m++) {
                table.addCorrespondenceMap(
                        new CorrespondenceMap(source.findItem(code(m)), target.findItem(code(m))));
            }
            ids.add(correspondenceTableRepository.save(table).getId());
        }
        return ids;
    }

    private ClassificationVersion createAndSaveVersionWithItems(String name) {
        ClassificationSeries classification = TestUtil.createClassification(name);
        ClassificationVersion version =
                TestUtil.createClassificationVersion(TestUtil.anyDateRange());
        Level level = TestUtil.createLevel(1);
        version.addLevel(level);
        for (int i = 0; i < MAPS_PER_TABLE; i++) {
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
