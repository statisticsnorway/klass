package no.ssb.klass.core.service;

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
import no.ssb.klass.core.repository.ClassificationFamilyRepository;
import no.ssb.klass.core.repository.ClassificationSeriesRepository;
import no.ssb.klass.core.repository.CorrespondenceTableRepository;
import no.ssb.klass.core.repository.UserRepository;
import no.ssb.klass.core.service.ClassificationServiceHelper.CorrespondenceTableInRange;
import no.ssb.klass.core.util.DateRange;
import no.ssb.klass.core.util.TranslatablePersistenceConverter;
import no.ssb.klass.testutil.TestUtil;

import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.util.List;

/**
 * Answering a correspondence lookup must not cost a round trip per contributing table.
 *
 * <p>{@code /corresponds} and {@code /correspondsAt} gather one correspondence table per source
 * version, and Standard for kommuneinndeling has 142 versions. Hibernate batch fetches the maps a
 * few tables at a time; prefetching them together roughly halves both the query count and the time.
 * This guards against a change that reintroduces a round trip per table.
 */
@SpringBootTest(
        properties = {
            "spring.jpa.properties.hibernate.generate_statistics=true",
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
class CorrespondenceLookupPrefetchTest {

    private static final int SOURCE_VERSIONS = 30;
    private static final int MAPS_PER_TABLE = 20;

    /**
     * Prefetching keeps this to a handful of queries. The budget is loose on purpose: the point is
     * that cost does not scale with the number of versions.
     */
    private static final int QUERY_BUDGET = 20;

    private static final int FIRST_YEAR = 1900;

    private static final DateRange WHOLE_PERIOD =
            DateRange.create(LocalDate.of(FIRST_YEAR, 1, 1), LocalDate.of(FIRST_YEAR + 500, 1, 1));

    @Autowired private CorrespondenceTableRepository correspondenceTableRepository;
    @Autowired private ClassificationSeriesRepository classificationSeriesRepository;
    @Autowired private ClassificationFamilyRepository classificationFamilyRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private PlatformTransactionManager transactionManager;

    @PersistenceContext private EntityManager entityManager;

    private TransactionTemplate tx;
    private User user;
    private ClassificationFamily classificationFamily;
    private Long sourceClassificationId;
    private Long targetClassificationId;

    @BeforeEach
    void setup() {
        tx = new TransactionTemplate(transactionManager);
        tx.executeWithoutResult(
                status -> {
                    user = userRepository.save(TestUtil.createUser());
                    classificationFamily =
                            classificationFamilyRepository.save(
                                    TestUtil.createClassificationFamily("family"));
                    createCorrespondences();
                });
    }

    @Test
    void gatheringCorrespondencesCostsAFixedNumberOfQueries() {
        Statistics statistics =
                entityManager
                        .getEntityManagerFactory()
                        .unwrap(SessionFactory.class)
                        .getStatistics();
        statistics.clear();

        List<?> correspondences = tx.execute(status -> gatherCorrespondences());

        assertThat(correspondences).hasSize(SOURCE_VERSIONS * MAPS_PER_TABLE);
        assertThat(statistics.getPrepareStatementCount())
                .as("queries to gather correspondences across %d versions", SOURCE_VERSIONS)
                .isLessThanOrEqualTo(QUERY_BUDGET);
    }

    /** Mirrors how the service answers a correspondence lookup. */
    private List<?> gatherCorrespondences() {
        ClassificationSeries source =
                classificationSeriesRepository.findById(sourceClassificationId).orElseThrow();
        ClassificationSeries target =
                classificationSeriesRepository.findById(targetClassificationId).orElseThrow();

        List<CorrespondenceTableInRange> tables =
                ClassificationServiceHelper.findCorrespondenceTables(
                        source, target, WHOLE_PERIOD, Language.getDefault(), true);
        correspondenceTableRepository.findAllByIdWithMaps(
                tables.stream()
                        .map(CorrespondenceTableInRange::correspondenceTable)
                        .map(CorrespondenceTable::getId)
                        .toList());
        return ClassificationServiceHelper.mapCorrespondences(tables, Language.getDefault(), false);
    }

    private void createCorrespondences() {
        ClassificationSeries target =
                classificationSeriesRepository.save(createClassification("target", 1, true));
        ClassificationSeries source =
                classificationSeriesRepository.save(
                        createClassification("source", SOURCE_VERSIONS, false));
        ClassificationVersion targetVersion = target.getClassificationVersions().get(0);
        // CorrespondenceTable is not cascaded from the version, so save each one explicitly.
        for (ClassificationVersion sourceVersion : source.getClassificationVersions()) {
            CorrespondenceTable table =
                    TestUtil.createCorrespondenceTable(sourceVersion, targetVersion);
            table.publish(Language.getDefault());
            for (int m = 0; m < MAPS_PER_TABLE; m++) {
                table.addCorrespondenceMap(
                        new CorrespondenceMap(
                                sourceVersion.findItem(code(m)), targetVersion.findItem(code(m))));
            }
            correspondenceTableRepository.save(table);
        }
        sourceClassificationId = source.getId();
        targetClassificationId = target.getId();
    }

    private ClassificationSeries createClassification(
            String name, int versionCount, boolean singleWideVersion) {
        ClassificationSeries classification = TestUtil.createClassification(name);
        classification.setContactPerson(user);
        classificationFamily.addClassificationSeries(classification);
        for (int v = 0; v < versionCount; v++) {
            ClassificationVersion version =
                    TestUtil.createClassificationVersion(
                            singleWideVersion ? WHOLE_PERIOD : yearRange(v));
            Level level = TestUtil.createLevel(1);
            version.addLevel(level);
            for (int i = 0; i < MAPS_PER_TABLE; i++) {
                version.addClassificationItem(
                        TestUtil.createClassificationItem(code(i), name + " item " + i),
                        level.getLevelNumber(),
                        null);
            }
            classification.addClassificationVersion(version);
        }
        return classification;
    }

    /** Versions of one classification may not overlap, so each source version gets its own year. */
    private static DateRange yearRange(int index) {
        return DateRange.create(
                LocalDate.of(FIRST_YEAR + index, 1, 1), LocalDate.of(FIRST_YEAR + index + 1, 1, 1));
    }

    private static String code(int i) {
        return String.format("%05d", i);
    }

    @Configuration
    @EnableAutoConfiguration
    @EnableJpaRepositories(basePackageClasses = CorrespondenceTableRepository.class)
    @EntityScan(basePackageClasses = {CorrespondenceTable.class})
    @ComponentScan(basePackageClasses = TranslatablePersistenceConverter.class)
    static class Config {}
}
