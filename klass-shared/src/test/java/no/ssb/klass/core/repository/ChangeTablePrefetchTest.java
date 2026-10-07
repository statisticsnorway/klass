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
import no.ssb.klass.core.util.DateRange;
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

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

/**
 * Walking every change table of a classification must cost a bounded number of queries.
 *
 * <p>The changes endpoint visits one change table per pair of consecutive versions, and Standard
 * for kommuneinndeling has 142 versions. Hibernate batch fetches the maps a few tables at a time,
 * so the cost already grows slowly rather than per table; prefetching them together roughly halves
 * it again. This guards against a change that removes either, which would reintroduce a round trip
 * per change table.
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

    private static final int VERSIONS = 40;
    private static final int MAPS_PER_TABLE = 20;
    private static final int CHANGE_TABLES = VERSIONS - 1;

    private static final int FIRST_YEAR = 1900;
    private static final DateRange WHOLE_PERIOD =
            DateRange.create(
                    LocalDate.of(FIRST_YEAR, 1, 1), LocalDate.of(FIRST_YEAR + VERSIONS + 1, 1, 1));

    /**
     * Fetching the tables together costs a handful of queries. The budget is loose on purpose: the
     * point is that cost does not scale with the number of change tables.
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
    private Long classificationId;

    @BeforeEach
    void setup() {
        tx = new TransactionTemplate(transactionManager);
        tx.executeWithoutResult(
                status -> {
                    user = userRepository.save(TestUtil.createUser());
                    classificationFamily =
                            classificationFamilyRepository.save(
                                    TestUtil.createClassificationFamily("family"));
                    classificationId = createClassificationWithChangeTables();
                });
    }

    @Test
    void walkingEveryChangeTableCostsABoundedNumberOfQueries() {
        Statistics statistics =
                entityManager
                        .getEntityManagerFactory()
                        .unwrap(SessionFactory.class)
                        .getStatistics();
        statistics.clear();

        int touched = tx.execute(status -> walkChangeTablesAsTheEndpointDoes());

        assertThat(touched).isEqualTo(CHANGE_TABLES * MAPS_PER_TABLE);
        assertThat(statistics.getPrepareStatementCount())
                .as("queries to walk %d change tables", CHANGE_TABLES)
                .isLessThanOrEqualTo(QUERY_BUDGET);
    }

    /** Mirrors the changes endpoint: gather the change tables, prefetch, then read every map. */
    private int walkChangeTablesAsTheEndpointDoes() {
        ClassificationSeries classification =
                classificationSeriesRepository.findById(classificationId).orElseThrow();
        List<CorrespondenceTable> changeTables = classification.getChangeTables(WHOLE_PERIOD, true);
        correspondenceTableRepository.findAllByIdWithMaps(
                changeTables.stream().map(CorrespondenceTable::getId).toList());

        int touched = 0;
        for (CorrespondenceTable table : changeTables) {
            for (CorrespondenceMap map : table.getCorrespondenceMaps()) {
                map.getSource().map(ClassificationItem::getCode);
                map.getTarget().map(ClassificationItem::getCode);
                touched++;
            }
        }
        return touched;
    }

    private long createClassificationWithChangeTables() {
        ClassificationSeries classification = TestUtil.createClassification("kommuneinndeling");
        classification.setContactPerson(user);
        classificationFamily.addClassificationSeries(classification);
        for (int v = 0; v < VERSIONS; v++) {
            ClassificationVersion version =
                    TestUtil.createClassificationVersion(
                            DateRange.create(
                                    LocalDate.of(FIRST_YEAR + v, 1, 1),
                                    LocalDate.of(FIRST_YEAR + v + 1, 1, 1)));
            Level level = TestUtil.createLevel(1);
            version.addLevel(level);
            for (int i = 0; i < MAPS_PER_TABLE; i++) {
                version.addClassificationItem(
                        TestUtil.createClassificationItem(code(i), "item " + i),
                        level.getLevelNumber(),
                        null);
            }
            classification.addClassificationVersion(version);
        }
        ClassificationSeries saved = classificationSeriesRepository.save(classification);

        List<ClassificationVersion> versions = saved.getClassificationVersions();
        versions.sort(Comparator.comparing(version -> version.getDateRange().getFrom()));
        // CorrespondenceTable is not cascaded from the version, so save each one explicitly.
        for (int i = 0; i < versions.size() - 1; i++) {
            ClassificationVersion source = versions.get(i);
            ClassificationVersion target = versions.get(i + 1);
            CorrespondenceTable table = TestUtil.createCorrespondenceTable(source, target);
            table.publish(Language.getDefault());
            for (int m = 0; m < MAPS_PER_TABLE; m++) {
                table.addCorrespondenceMap(
                        new CorrespondenceMap(source.findItem(code(m)), target.findItem(code(m))));
            }
            correspondenceTableRepository.save(table);
        }
        return saved.getId();
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
