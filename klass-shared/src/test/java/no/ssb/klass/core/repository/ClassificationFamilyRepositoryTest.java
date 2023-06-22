package no.ssb.klass.core.repository;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import no.ssb.klass.core.config.ConfigurationProfiles;
import no.ssb.klass.core.model.ClassificationFamily;
import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.ClassificationType;
import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.model.User;
import no.ssb.klass.core.util.DateRange;
import no.ssb.klass.core.util.TranslatablePersistenceConverter;
import no.ssb.klass.testutil.TestUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles(ConfigurationProfiles.H2_INMEMORY)
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
    private final String allSections = null;
    private final ClassificationType allClassificationTypes = null;
    private User user;

    @Before
    public void setup() {
        user = userRepository.save(TestUtil.createUser());
    }

    @Test
    public void verifyMapping() {
        // given
        ClassificationFamily family = createClassificationFamilyWithOneClassification();
        subject.save(family);
        subject.flush();
        entityManager.detach(family);

        // when
        ClassificationFamily result = subject.findOne(family.getId());

        // then
        assertEquals(1, result.getClassificationSeries().size());
        assertEquals(2, result.getClassificationSeries().get(0).getClassificationVersions().size());
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

    @Configuration
    @EnableAutoConfiguration
    @EntityScan(basePackageClasses = { ClassificationFamily.class, TranslatablePersistenceConverter.class })
    @ComponentScan(basePackageClasses = TranslatablePersistenceConverter.class)
    static class Config {

    }
}
