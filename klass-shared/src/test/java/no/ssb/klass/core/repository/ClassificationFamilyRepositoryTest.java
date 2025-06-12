package no.ssb.klass.core.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import no.ssb.klass.core.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import no.ssb.klass.core.config.ConfigurationProfiles;
import no.ssb.klass.core.util.DateRange;
import no.ssb.klass.core.util.TranslatablePersistenceConverter;
import no.ssb.klass.testutil.TestUtil;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles(ConfigurationProfiles.H2_INMEMORY)
@Transactional
public class ClassificationFamilyRepositoryTest {

    private static final Logger logger = LoggerFactory.getLogger(ClassificationFamilyRepositoryTest.class);

    @Autowired
    private ClassificationFamilyRepository subject;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ClassificationSeriesRepository classificationSeriesRepository;
    @Autowired
    private EntityManager entityManager;
    private final String allSections = null;
    private final ClassificationType allClassificationTypes = null;
    private User user;
    private ClassificationFamilySummaryBuilder classificationFamilySummaries;

    @BeforeEach
    public void setup() {

        user = userRepository.save(TestUtil.createUser());
        classificationFamilySummaries = new ClassificationFamilySummaryBuilder(subject);
    }

    @Test
    public void verifyMapping() {
        // given
        ClassificationFamily family = createClassificationFamilyWithOneClassification();
        subject.save(family);
        subject.flush();
        entityManager.detach(family);

        // when
        ClassificationFamily result = subject.findById(family.getId()).orElseThrow(() ->
                new RuntimeException("ClassificationFamily not found"));

        // then
        assertEquals(1, result.getClassificationSeries().size());
        assertEquals(2, result.getClassificationSeries().get(0).getClassificationVersions().size());
    }

    @Test
    public void findClassificationFamilySummariesForAllSectionsAndAllClassificationTypes() {
        // given
        ClassificationFamily family = createClassificationFamilyWithOneClassification();
        subject.save(family);

        logger.info(LOGGER_MESSAGE_FAMILIES, subject.count());

        // when
        List<ClassificationFamilySummary> result = subject.findPublicClassificationFamilySummaries(allSections,
                allClassificationTypes);

        List<ClassificationFamilySummary> result2 = classificationFamilySummaries.buildPublicClassificationSummaries();

        // then
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getNumberOfClassifications());

        assertEquals(1, result2.size());
        assertEquals(1, result2.get(0).getNumberOfClassifications());
    }

    @Test
    public void findClassificationFamilySummariesMultipleFamilies() {
        // given
        ClassificationFamily family = createClassificationFamilyWithOneClassification();
        subject.save(family);

        ClassificationFamily family1 = createClassificationFamilyOneClassificationIsCopyrighted();
        subject.save(family1);

        ClassificationFamily family2 = createClassificationFamilyOneVersionNotPublished();
        subject.save(family2);

        logger.info(LOGGER_MESSAGE_FAMILIES, subject.count());

        // when
        List<ClassificationFamilySummary> result = subject.findPublicClassificationFamilySummaries(allSections,
                allClassificationTypes);

        List<ClassificationFamilySummary> result2 = classificationFamilySummaries.buildPublicClassificationSummaries();

        // then
        assertEquals(3, result.size());
        assertEquals(3, result2.size());
    }

    @Test
    public void findClassificationFamilySummariesWithMatchingSection() {
        // given
        ClassificationFamily family = createClassificationFamilyWithOneClassification();
        subject.save(family);

        logger.info(LOGGER_MESSAGE_FAMILIES, subject.count());

        // when
        String section = family.getClassificationSeries().get(0).getContactPerson().getSection();
        List<ClassificationFamilySummary> result = subject.findPublicClassificationFamilySummaries(section,
                allClassificationTypes);

        // then
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getNumberOfClassifications());
    }

    @Test
    public void findClassificationFamilySummariesWithNoMatchingSection() {
        // given
        ClassificationFamily family = createClassificationFamilyWithOneClassification();
        subject.save(family);

        logger.info(LOGGER_MESSAGE_FAMILIES, subject.count());

        // when
        List<ClassificationFamilySummary> result = subject.findPublicClassificationFamilySummaries("unknown section",
                allClassificationTypes);

        // then
        assertEquals(0, result.size());
    }

    @Test
    public void findClassificationFamilySummariesWithMatchingClassificationType() {
        // given
        ClassificationFamily family = createClassificationFamilyWithOneClassification();
        subject.save(family);

        logger.info(LOGGER_MESSAGE_FAMILIES, subject.count());

        // when
        ClassificationType classificationType = family.getClassificationSeries().get(0).getClassificationType();
        List<ClassificationFamilySummary> result = subject.findPublicClassificationFamilySummaries(allSections,
                classificationType);

        // then
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getNumberOfClassifications());
    }

    @Test
    public void findClassificationFamilySummariesWithNoMatchingClassificationType() {
        // given
        ClassificationFamily family = createClassificationFamilyWithOneClassification();
        subject.save(family);

        logger.info(LOGGER_MESSAGE_FAMILIES, subject.count());

        // when
        ClassificationType classificationType = TestUtil.oppositeClassificationType(family.getClassificationSeries()
                .get(0).getClassificationType());
        List<ClassificationFamilySummary> result = subject.findPublicClassificationFamilySummaries(allSections,
                classificationType);

        // then
        assertEquals(0, result.size());
    }

    @Test
    public void findClassificationFamilySummariesOneClassificationIsCopyrighted() {
        // given
        ClassificationFamily family = createClassificationFamilyOneClassificationIsCopyrighted();
        subject.save(family);

        logger.info(LOGGER_MESSAGE_FAMILIES, subject.count());

        assertThat(family.getClassificationSeries().size()).isEqualTo(2);

        // when
        List<ClassificationFamilySummary> result = subject.findPublicClassificationFamilySummaries(allSections,
                allClassificationTypes);

        List<ClassificationFamilySummary> result2 = classificationFamilySummaries.buildPublicClassificationSummaries();

        // then
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getNumberOfClassifications());

        assertEquals(1, result2.size());
        assertEquals(1, result2.get(0).getNumberOfClassifications());
    }

    @Test
    public void findClassificationFamilySummariesOneClassificationIsCopyrightedNotPublic() {
        // This test is for demonstrating why we should test method 'findPublicClassificationFamilySummaries' which
        // is used in klass-api and not 'findClassificationFamilySummaries'

        // given
        ClassificationFamily family = createClassificationFamilyOneClassificationIsCopyrighted();
        subject.save(family);

        logger.info(LOGGER_MESSAGE_FAMILIES, subject.count());

        assertThat(family.getClassificationSeries().size()).isEqualTo(2);

        // when
        List<ClassificationFamilySummary> result = subject.findClassificationFamilySummaries(allSections,
                allClassificationTypes);

        // then
        assertEquals(1, result.size());
        assertEquals(2, result.get(0).getNumberOfClassifications());
    }

    @Test
    public void findClassificationFamilySummariesOneClassificationIsDeleted() {
        // given
        ClassificationFamily family = createClassificationFamilyOneClassificationIsDeleted();
        subject.save(family);

        logger.info(LOGGER_MESSAGE_FAMILIES, subject.count());

        // when
        List<ClassificationFamilySummary> result = subject.findPublicClassificationFamilySummaries(allSections,
                allClassificationTypes);

        List<ClassificationFamilySummary> result2 = classificationFamilySummaries.buildPublicClassificationSummaries();

        // then
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getNumberOfClassifications());

        assertEquals(1, result2.size());
        assertEquals(1, result2.get(0).getNumberOfClassifications());
    }

    @Test
    public void findClassificationFamilySummariesOneClassificationVersionIsNotPublished() {
        // given
        ClassificationFamily family = createClassificationFamilyOneVersionNotPublished();
        subject.save(family);

        logger.info(LOGGER_MESSAGE_FAMILIES, subject.count());

        // when
        List<ClassificationFamilySummary> result = subject.findPublicClassificationFamilySummaries(allSections,
                allClassificationTypes);

        List<ClassificationFamilySummary> result2 = classificationFamilySummaries.buildPublicClassificationSummaries();

        // then
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getNumberOfClassifications());

        assertEquals(1, result2.size());
        assertEquals(1, result2.get(0).getNumberOfClassifications());
    }

    @Test
    public void findClassificationFamilySummariesOneClassificationVersionIsDeleted() {
        // given
        ClassificationFamily family = createClassificationFamilyOneVersionDeleted();
        subject.save(family);

        logger.info(LOGGER_MESSAGE_FAMILIES, subject.count());

        // when
        List<ClassificationFamilySummary> result = subject.findPublicClassificationFamilySummaries(allSections,
                allClassificationTypes);

        List<ClassificationFamilySummary> result2 = classificationFamilySummaries.buildPublicClassificationSummaries();

        // then
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getNumberOfClassifications());

        assertEquals(1, result2.size());
        assertEquals(1, result2.get(0).getNumberOfClassifications());
    }

    private ClassificationFamily createClassificationFamilyWithOneClassification() {
        ClassificationFamily family = subject.save(TestUtil.createClassificationFamily("family"));
        ClassificationSeries classification = TestUtil.createClassification("classification");
        classification.setContactPerson(user);
        ClassificationVersion version1 = TestUtil.createClassificationVersion(DateRange.create("2000-01-01",
                "2010-01-01"));
        ClassificationVersion version2 = TestUtil.createClassificationVersion(DateRange.create("2010-01-01",
                "2020-01-01"));
        family.addClassificationSeries(classification);
        classification.addClassificationVersion(version1);
        classification.addClassificationVersion(version2);
        classificationSeriesRepository.save(classification);

        logger.info(LOGGER_MESSAGE_CLASSIFICATION_SERIES, family.getClassificationSeries().size(),family.getName());

        return family;
    }

    private ClassificationFamily createClassificationFamilyOneClassificationIsCopyrighted() {
        ClassificationFamily family = subject.save(TestUtil.createClassificationFamily("Code family"));
        ClassificationSeries classification = TestUtil.createClassification("java");
        ClassificationSeries classification1 = TestUtil.createClassification("python");
        classification.setContactPerson(user);
        classification.setCopyrighted(true);
        classification1.setContactPerson(user);
        ClassificationVersion version1 = TestUtil.createClassificationVersion(DateRange.create("2000-01-01",
                "2010-01-01"));
        ClassificationVersion version2 = TestUtil.createClassificationVersion(DateRange.create("2010-01-01",
                "2020-01-01"));
        family.addClassificationSeries(classification);
        family.addClassificationSeries(classification1);
        classification.addClassificationVersion(version1);
        classification1.addClassificationVersion(version2);
        classificationSeriesRepository.save(classification);
        classificationSeriesRepository.save(classification1);

        logger.info(LOGGER_MESSAGE_CLASSIFICATION_SERIES, family.getClassificationSeries().size(),family.getName());
        logger.info(LOGGER_MESSAGE_CLASSIFICATION_VERSIONS, classification.getClassificationVersions().size(), family.getName());

        return family;
    }

    private ClassificationFamily createClassificationFamilyOneClassificationIsDeleted() {
        ClassificationFamily family = subject.save(TestUtil.createClassificationFamily("Winter family"));
        ClassificationSeries classification = TestUtil.createClassification("snow");
        ClassificationSeries classification1 = TestUtil.createClassification("snowflake");
        classification.setContactPerson(user);
        classification1.setContactPerson(user);
        ClassificationVersion version1 = TestUtil.createClassificationVersion(DateRange.create("2000-01-01",
                "2010-01-01"));
        family.addClassificationSeries(classification);
        family.addClassificationSeries(classification1);
        classification.addClassificationVersion(version1);
        classification1.addClassificationVersion(version1);
        classificationSeriesRepository.save(classification);
        classificationSeriesRepository.save(classification1);
        classification.setDeleted();

        logger.info(LOGGER_MESSAGE_CLASSIFICATION_SERIES, family.getClassificationSeries().size(),family.getName());
        logger.info(LOGGER_MESSAGE_CLASSIFICATION_VERSIONS, classification.getClassificationVersions().size(), family.getName());

        return family;
    }

    private ClassificationFamily createClassificationFamilyOneVersionNotPublished() {
        ClassificationFamily family = subject.save(TestUtil.createClassificationFamily("Summer family"));
        ClassificationSeries classification = TestUtil.createClassification("july");
        classification.setContactPerson(user);
        family.addClassificationSeries(classification);
        ClassificationVersion version = TestUtil.createClassificationVersion(DateRange.create("1999-01-01",
                "2001-01-01"));
        ClassificationVersion version2 = TestUtil.createClassificationVersion(DateRange.create("2010-01-01",
                "2020-01-01"));
        classification.addClassificationVersion(version);
        classification.addClassificationVersion(version2);
        classificationSeriesRepository.save(classification);
        classification.getClassificationVersions().get(0).unpublish(Language.NB);

        logger.info(LOGGER_MESSAGE_CLASSIFICATION_SERIES, family.getClassificationSeries().size(),family.getName());
        logger.info(LOGGER_MESSAGE_CLASSIFICATION_VERSIONS, classification.getClassificationVersions().size(), family.getName());

        return family;
    }

    private ClassificationFamily createClassificationFamilyOneVersionDeleted() {
        ClassificationFamily family = subject.save(TestUtil.createClassificationFamily("Boat family"));
        ClassificationSeries classification = TestUtil.createClassification("sailboat");
        classification.setContactPerson(user);
        family.addClassificationSeries(classification);
        ClassificationVersion version = TestUtil.createClassificationVersion(DateRange.create("1999-01-01",
                "2001-01-01"));
        ClassificationVersion version2 = TestUtil.createClassificationVersion(DateRange.create("2010-01-01",
                "2020-01-01"));
        classification.addClassificationVersion(version);
        classification.addClassificationVersion(version2);
        classificationSeriesRepository.save(classification);
        classification.getClassificationVersions().get(1).setDeleted();

        logger.info(LOGGER_MESSAGE_CLASSIFICATION_SERIES, family.getClassificationSeries().size(),family.getName());
        logger.info(LOGGER_MESSAGE_CLASSIFICATION_VERSIONS, classification.getClassificationVersions().size(), family.getName());

        return family;
    }

    private ClassificationFamily createClassificationFamilyNoVersionPublished() {
        ClassificationFamily family = subject.save(TestUtil.createClassificationFamily("Skate family"));
        ClassificationSeries classification = TestUtil.createClassification("surf skate");
        classification.setContactPerson(user);
        family.addClassificationSeries(classification);
        ClassificationVersion version = TestUtil.createClassificationVersion(DateRange.create("1999-01-01",
                "2001-01-01"));
        classification.addClassificationVersion(version);
        classificationSeriesRepository.save(classification);
        classification.getClassificationVersions().get(0).unpublish(Language.NB);

        logger.info(LOGGER_MESSAGE_CLASSIFICATION_SERIES, family.getClassificationSeries().size(),family.getName());
        logger.info(LOGGER_MESSAGE_CLASSIFICATION_VERSIONS, classification.getClassificationVersions().size(), family.getName());

        return family;
    }

    private final String LOGGER_MESSAGE_CLASSIFICATION_SERIES = "Number of classification series: {} for classification family: {}";
    private final String LOGGER_MESSAGE_FAMILIES = "Total number of families: {}";
    private final String LOGGER_MESSAGE_CLASSIFICATION_VERSIONS = "Number of classification versions: {} for classification family: {}";

    @Configuration
    @EnableAutoConfiguration
    @EntityScan(basePackageClasses = { ClassificationFamily.class, TranslatablePersistenceConverter.class })
    @ComponentScan(basePackageClasses = TranslatablePersistenceConverter.class)
    static class Config {

    }

    // temp tests for dev
    @Test
    public void verifyFindAllClassificationFamiliesCount() {
        ClassificationFamily summer_family = createClassificationFamilyOneVersionNotPublished();
        subject.save(summer_family);
        ClassificationFamily family = createClassificationFamilyWithOneClassification();
        subject.save(family);
        ClassificationFamily code_family = createClassificationFamilyOneClassificationIsCopyrighted();
        subject.save(code_family);
        ClassificationFamily skate_family = createClassificationFamilyNoVersionPublished();
        subject.save(skate_family);

        logger.info(LOGGER_MESSAGE_FAMILIES, subject.count());

        Map<String, Long> classificationsNum = classificationFamilySummaries.countValidClassificationSeriesPerFamily();
        assertEquals(1, classificationsNum.get("family"));
        assertEquals(1, classificationsNum.get("Code family"));
        assertEquals(1, classificationsNum.get("Summer family"));
        assertEquals(0, classificationsNum.get("Skate family"));

        Map<String, Long> classificationsNum2 = classificationFamilySummaries.countValidClassificationSeriesPerFamilyMethod();
        assertEquals(1, classificationsNum2.get("family"));
        assertEquals(1, classificationsNum2.get("Code family"));
        assertEquals(1, classificationsNum2.get("Summer family"));
        assertEquals(0, classificationsNum2.get("Skate family"));
    }

    @Test
    public void verifyFindAllClassificationFamiliesBuildSummaries() {
        ClassificationFamily summer_family = createClassificationFamilyOneVersionNotPublished();
        subject.save(summer_family);
        ClassificationFamily family = createClassificationFamilyWithOneClassification();
        subject.save(family);
        ClassificationFamily code_family = createClassificationFamilyOneClassificationIsCopyrighted();
        subject.save(code_family);
        ClassificationFamily skate_family = createClassificationFamilyNoVersionPublished();
        subject.save(skate_family);

        logger.info(LOGGER_MESSAGE_FAMILIES, subject.count());

        List<ClassificationFamilySummary> classificationFamilyBuilder;
        classificationFamilyBuilder = classificationFamilySummaries.buildPublicClassificationSummaries();
        assertEquals(4, classificationFamilyBuilder.size());
        assertThat(classificationFamilyBuilder.get(0).getNumberOfClassifications()).isEqualTo(1);
        assertThat(classificationFamilyBuilder.get(1).getNumberOfClassifications()).isEqualTo(1);
        assertThat(classificationFamilyBuilder.get(2).getNumberOfClassifications()).isEqualTo(1);
        assertThat(classificationFamilyBuilder.get(3).getNumberOfClassifications()).isEqualTo(0);
    }
}
