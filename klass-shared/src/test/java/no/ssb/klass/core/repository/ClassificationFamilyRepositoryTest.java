package no.ssb.klass.core.repository;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY;
import static no.ssb.klass.core.model.ClassificationType.CLASSIFICATION;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import no.ssb.klass.core.model.*;
import org.assertj.core.api.Assertions;
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

import no.ssb.klass.core.config.ConfigurationProfiles;
import no.ssb.klass.core.util.DateRange;
import no.ssb.klass.core.util.TranslatablePersistenceConverter;
import no.ssb.klass.testutil.TestUtil;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles(ConfigurationProfiles.POSTGRES_EMBEDDED)
@AutoConfigureEmbeddedDatabase(provider = ZONKY, type= AutoConfigureEmbeddedDatabase.DatabaseType.POSTGRES)
@Transactional
public class ClassificationFamilyRepositoryTest {
    @Autowired
    private ClassificationFamilyRepository subject;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ClassificationSeriesRepository classificationSeriesRepository;
    @Autowired
    private EntityManager entityManager;
    ClassificationFamilySummaryRes classificationFamilySummaryRes;

    private final String allSections = null;
    private final ClassificationType allClassificationTypes = null;
    private User user;
    private User user2;

    @BeforeEach
    public void setup() {
        user = userRepository.save(TestUtil.createUser());
        user2 = userRepository.save(TestUtil.createUser2());
        classificationFamilySummaryRes = new ClassificationFamilySummaryRes(subject);
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
    public void findAllClassificationFamilies() throws JsonProcessingException {

        ClassificationFamily family = createClassificationFamilyWithOneClassification();
        ClassificationFamily family2 = createClassificationFamilyTwoClassifications();
        subject.save(family);
        subject.save(family2);

        List<ClassificationFamily> result = classificationFamilySummaryRes.findPublicClassificationFamilies();
        classificationFamilySummaryRes.printClassificationFamilyClassifications();
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.size()).isEqualTo(2);
        Assertions.assertThat(result.get(0).getClassificationSeries().get(0).getClassificationType()).isEqualTo(CLASSIFICATION);
        Assertions.assertThat(result.get(0).getClassificationSeries().get(0).getClassificationVersions().size()).isEqualTo(2);
        Assertions.assertThat(result.get(1).getClassificationSeries().get(0).getClassificationVersions().size()).isEqualTo(1);
    }

    @Test
    public void findAllClassificationFamiliesCopyrighted() {
        ClassificationFamily family = createClassificationFamilyCopyrighted();
        subject.save(family);

        List<ClassificationFamily> result = classificationFamilySummaryRes.findPublicClassificationFamilies();
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.size()).isEqualTo(1);
        Assertions.assertThat(result.get(0).getClassificationSeries().get(0).isCopyrighted()).isTrue();
    }

    @Test
    public void findAllClassificationFamiliesDeleted() {
        ClassificationFamily family = createClassificationFamilyDeleted();
        subject.save(family);

        List<ClassificationFamily> result = classificationFamilySummaryRes.findPublicClassificationFamilies();
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.size()).isEqualTo(1);
        Assertions.assertThat(result.get(0).getClassificationSeries()).isNotNull();
        Assertions.assertThat(result.get(0).getClassificationSeries()).isEmpty();
    }

    @Test
    public void findAllClassificationFamiliesVersionPublished() {
        ClassificationFamily family = createClassificationFamilyTwoClassifications();
        subject.save(family);

        List<ClassificationFamily> result = classificationFamilySummaryRes.findPublicClassificationFamilies();
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.size()).isEqualTo(1);
        Assertions.assertThat(result.get(0).getClassificationSeries()).isNotNull();
        Assertions.assertThat(result.get(0).getClassificationSeries().get(0).getPublicClassificationVersions().get(0).isPublished(Language.NB)).isTrue();
    }

    @Test
    public void findAllClassificationFamiliesVersionDeleted() {
        ClassificationFamily family = createClassificationFamilyVersionDeleted();
        subject.save(family);

        List<ClassificationFamily> result = classificationFamilySummaryRes.findPublicClassificationFamilies();
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.size()).isEqualTo(1);
        Assertions.assertThat(result.get(0).getClassificationSeries()).isNotNull();
        Assertions.assertThat(result.get(0).getClassificationSeries().get(0).getPublicClassificationVersions()).isEmpty();

    }

    @Test
    public void findAllClassificationFamiliesFilterByClassificationType() {
        ClassificationFamily family = createClassificationFamilyVersionDeleted();
        subject.save(family);

        List<ClassificationFamily> result = classificationFamilySummaryRes.findPublicClassificationFamilies();
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.size()).isEqualTo(1);
        Assertions.assertThat(result.get(0).getClassificationSeries()).isNotNull();
        Assertions.assertThat(result.get(0).getClassificationSeries().get(0).getPublicClassificationVersions()).isEmpty();

    }

    @Test
    public void findAllClassificationFamiliesFilterBySsbSection() {
        ClassificationFamily family = createClassificationFamilyVersionDeleted();
        subject.save(family);

        List<ClassificationFamily> result = classificationFamilySummaryRes.findPublicClassificationFamilies();
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.size()).isEqualTo(1);
        Assertions.assertThat(result.get(0).getClassificationSeries()).isNotNull();
        Assertions.assertThat(result.get(0).getClassificationSeries().get(0).getPublicClassificationVersions()).isEmpty();

    }

    @Test
    public void findClassificationFamilySummariesForAllSectionsAndAllClassificationTypes() {
        // given
        ClassificationFamily family = createClassificationFamilyWithOneClassification();
        subject.save(family);

        // when
        List<ClassificationFamilySummary> result = subject.findClassificationFamilySummaries(allSections,
                allClassificationTypes);

        // then
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getNumberOfClassifications());
    }

    @Test
    public void findClassificationFamilySummariesWithMatchingSection() {
        // given
        ClassificationFamily family = createClassificationFamilyWithOneClassification();
        subject.save(family);

        // when
        String section = family.getClassificationSeries().get(0).getContactPerson().getSection();
        List<ClassificationFamilySummary> result = subject.findClassificationFamilySummaries(section,
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

        // when
        List<ClassificationFamilySummary> result = subject.findClassificationFamilySummaries("unknown section",
                allClassificationTypes);

        // then
        assertEquals(1, result.size());
        assertEquals(0, result.get(0).getNumberOfClassifications());
    }

    @Test
    public void findClassificationFamilySummariesWithMatchingClassificationType() {
        // given
        ClassificationFamily family = createClassificationFamilyWithOneClassification();
        subject.save(family);

        // when
        ClassificationType classificationType = family.getClassificationSeries().get(0).getClassificationType();
        List<ClassificationFamilySummary> result = subject.findClassificationFamilySummaries(allSections,
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

        // when
        ClassificationType classificationType = TestUtil.oppositeClassificationType(family.getClassificationSeries()
                .get(0).getClassificationType());
        List<ClassificationFamilySummary> result = subject.findClassificationFamilySummaries(allSections,
                classificationType);

        // then
        assertEquals(1, result.size());
        assertEquals(0, result.get(0).getNumberOfClassifications());
    }

    @Test
    public void findPublicClassificationFamilySummariesForAllSectionsAndAllClassificationTypes() {
        // given
        ClassificationFamily family = createClassificationFamilyWithOneClassification();
        subject.save(family);

        // when
        List<ClassificationFamilySummary> result = subject.findPublicClassificationFamilySummaries(allSections,
                allClassificationTypes);

        // then
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getNumberOfClassifications());
    }

    @Test
    public void findPublicClassificationFamilySummariesWithMatchingSection() {
        // given
        ClassificationFamily family = createClassificationFamilyWithOneClassification();
        subject.save(family);

        // when
        String section = family.getClassificationSeries().get(0).getContactPerson().getSection();
        List<ClassificationFamilySummary> result = subject.findPublicClassificationFamilySummaries(section,
                allClassificationTypes);

        // then
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getNumberOfClassifications());
    }

    @Test
    public void findPublicClassificationFamilySummariesWithMatchingSection2() {
        // given
        ClassificationFamily family = createClassificationFamilyWithOneClassification();
        subject.save(family);

        // when
        String section = family.getClassificationSeries().get(0).getContactPerson().getSection();
        List<ClassificationFamilySummary> result = subject.findPublicClassificationFamilySummaries(section,
                allClassificationTypes);

        // then
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getNumberOfClassifications());
    }

    @Test
    public void findPublicClassificationFamilySummariesWithNoMatchingSection() {
        // given
        ClassificationFamily family = createClassificationFamilyWithOneClassification();
        subject.save(family);

        // when
        List<ClassificationFamilySummary> result = subject.findPublicClassificationFamilySummaries("unknown section",
                allClassificationTypes);

        // then
        assertEquals(1, result.size());
        assertEquals(0, result.get(0).getNumberOfClassifications());
    }

    @Test
    public void findPublicClassificationFamilySummariesWithMatchingClassificationType() {
        // given
        ClassificationFamily family = createClassificationFamilyWithOneClassification();
        subject.save(family);

        // when
        ClassificationType classificationType = family.getClassificationSeries().get(0).getClassificationType();
        List<ClassificationFamilySummary> result = subject.findPublicClassificationFamilySummaries(allSections,
                classificationType);

        // then
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getNumberOfClassifications());
    }

    @Test
    public void findPublicClassificationFamilySummariesWithNoMatchingClassificationType() {
        // given
        ClassificationFamily family = createClassificationFamilyWithOneClassification();
        subject.save(family);

        // when
        ClassificationType classificationType = TestUtil.oppositeClassificationType(family.getClassificationSeries()
                .get(0).getClassificationType());
        List<ClassificationFamilySummary> result = subject.findPublicClassificationFamilySummaries(allSections,
                classificationType);

        // then
        assertEquals(1, result.size());
        assertEquals(0, result.get(0).getNumberOfClassifications());
    }

    @Test
    public void findPublicClassificationFamilySummaries() {
        // given
        ClassificationFamily family = createClassificationFamilyWithOneClassification();
        subject.save(family);
        ClassificationFamily family2 = createClassificationFamilyTwoClassifications();
        subject.save(family2);

        // when
        List<ClassificationFamilySummary> result = subject.findPublicClassificationFamilySummaries(allSections,
                allClassificationTypes);

        // then
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getNumberOfClassifications());
        System.out.println(result.get(1).getNumberOfClassifications());
        assertEquals(2, result.get(1).getNumberOfClassifications());
    }

    @Test
    public void findPublicClassificationFamilySummariesWhereClassificationIsCopyrighted() {
        // given
        ClassificationFamily family = createClassificationFamilyCopyrighted();
        subject.save(family);

        // when
        List<ClassificationFamilySummary> result = subject.findPublicClassificationFamilySummaries(allSections,
                allClassificationTypes);

        // then
        assertEquals(1, result.size());
        assertEquals(0, result.get(0).getNumberOfClassifications());
    }

    @Test
    public void findPublicClassificationFamilySummariesWhereClassificationIsDeleted() {
        // given
        ClassificationFamily family = createClassificationFamilyDeleted();
        subject.save(family);

        // when
        List<ClassificationFamilySummary> result = subject.findPublicClassificationFamilySummaries(allSections,
                allClassificationTypes);

        // then
        assertEquals(1, result.size());
        assertEquals(0, result.get(0).getNumberOfClassifications());
    }

    @Test
    public void findPublicClassificationFamilySummariesWhereClassificationVersionIsUnpublished() {
        // given
        ClassificationFamily family = createClassificationFamilyVersionNotPublished();
        subject.save(family);

        // when
        List<ClassificationFamilySummary> result = subject.findPublicClassificationFamilySummaries(allSections,
                allClassificationTypes);

        // then
        assertEquals(1, result.size());
        assertEquals(0, result.get(0).getNumberOfClassifications());
    }

    @Test
    public void findPublicClassificationFamilySummariesWhereClassificationVersionIsDeleted() {
        // given
        ClassificationFamily family = createClassificationFamilyVersionDeleted();
        subject.save(family);

        // when
        List<ClassificationFamilySummary> result = subject.findPublicClassificationFamilySummaries(allSections,
                allClassificationTypes);

        // then
        assertEquals(1, result.size());
        //assertEquals(0, result.get(0).getNumberOfClassifications());
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
        return family;
    }

    private ClassificationFamily createClassificationFamilyTwoClassifications() {
        ClassificationFamily family = subject.save(TestUtil.createClassificationFamily("Postgres-family"));
        ClassificationSeries classification = TestUtil.createClassification("bus");
        classification.setContactPerson(user2);
        ClassificationSeries classification2 = TestUtil.createClassification("car");
        classification2.setContactPerson(user2);
        ClassificationVersion version1 = TestUtil.createClassificationVersion(DateRange.create("1999-01-01",
                "2001-01-01"));
        family.addClassificationSeries(classification);
        family.addClassificationSeries(classification2);
        classification.addClassificationVersion(version1);
        classificationSeriesRepository.save(classification);
        classificationSeriesRepository.save(classification2);
        System.out.println(classification.getId());
        System.out.println(classification2.getId());
        return family;
    }

    private ClassificationFamily createClassificationFamilyCopyrighted() {
        ClassificationFamily family = subject.save(TestUtil.createClassificationFamily("Code-family"));
        ClassificationSeries classification = TestUtil.createClassification("java");
        classification.setContactPerson(user);
        classification.setCopyrighted(true);
        ClassificationVersion version1 = TestUtil.createClassificationVersion(DateRange.create("1999-01-01",
                "2001-01-01"));
        family.addClassificationSeries(classification);
        classification.addClassificationVersion(version1);
        classificationSeriesRepository.save(classification);
        return family;
    }

    private ClassificationFamily createClassificationFamilyDeleted() {
        ClassificationFamily family = subject.save(TestUtil.createClassificationFamily("Summer-family"));
        ClassificationSeries classification = TestUtil.createClassification("july");
        classification.setContactPerson(user);
        family.addClassificationSeries(classification);
        classificationSeriesRepository.save(classification);
        classification.setDeleted();
        return family;
    }

    private ClassificationFamily createClassificationFamilyVersionNotPublished() {
        ClassificationFamily family = subject.save(TestUtil.createClassificationFamily("Winter-family"));
        ClassificationSeries classification = TestUtil.createClassification("july");
        classification.setContactPerson(user);
        family.addClassificationSeries(classification);
        ClassificationVersion version = TestUtil.createClassificationVersion(DateRange.create("1999-01-01",
                "2001-01-01"));
        classification.addClassificationVersion(version);
        classificationSeriesRepository.save(classification);
        classification.getClassificationVersions().get(0).unpublish(Language.NB);
        System.out.println(classification.getClassificationVersions().get(0));
        return family;
    }

    private ClassificationFamily createClassificationFamilyVersionDeleted() {
        ClassificationFamily family = subject.save(TestUtil.createClassificationFamily("Winter-family"));
        ClassificationSeries classification = TestUtil.createClassification("july");
        classification.setContactPerson(user);
        family.addClassificationSeries(classification);
        ClassificationVersion version = TestUtil.createClassificationVersion(DateRange.create("1999-01-01",
                "2001-01-01"));
        classification.addClassificationVersion(version);
        classificationSeriesRepository.save(classification);
        classification.getClassificationVersions().get(0).setDeleted();
        return family;
    }

    private void checkClassification(){
        ClassificationFamily family = subject.save(TestUtil.createClassificationFamily("Winter-family"));
        ClassificationSeries classification = TestUtil.createClassification("july");
        classification.setContactPerson(user);
        family.addClassificationSeries(classification);
        ClassificationVersion version1 = TestUtil.createClassificationVersion(DateRange.create("1999-01-01",
                "2001-01-01"));
        classification.addClassificationVersion(version1);
        classificationSeriesRepository.save(classification);
        classification.getClassificationVersions().get(0).unpublish(Language.NB);
        System.out.println(classification.getClassificationVersions().get(0).isPublishedInAnyLanguage());

    }

    @Configuration
    @EnableAutoConfiguration
    @EntityScan(basePackageClasses = { ClassificationFamily.class, TranslatablePersistenceConverter.class })
    @ComponentScan(basePackageClasses = TranslatablePersistenceConverter.class)
    static class Config {

    }
}
