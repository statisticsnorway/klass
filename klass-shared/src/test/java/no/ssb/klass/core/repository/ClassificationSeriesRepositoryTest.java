package no.ssb.klass.core.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import no.ssb.klass.core.config.ConfigurationProfiles;
import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.ClassificationType;
import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.model.Level;
import no.ssb.klass.core.model.User;
import no.ssb.klass.core.util.TimeUtil;
import no.ssb.klass.core.util.Translatable;
import no.ssb.klass.core.util.TranslatablePersistenceConverter;
import no.ssb.klass.testutil.IncrementableClockSource;
import no.ssb.klass.testutil.TestUtil;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles(ConfigurationProfiles.H2_INMEMORY)
@Transactional
public class ClassificationSeriesRepositoryTest {
        @Autowired
        private ClassificationSeriesRepository subject;
        @Autowired
        private ClassificationFamilyRepository classificationFamilyRepository;
        @Autowired
        private UserRepository userRepository;
        @Autowired
        private EntityManager entityManager;
        private IncrementableClockSource clockSource;
        private User user;

        @BeforeEach
        public void setup() {
                clockSource = new IncrementableClockSource(new Date().getTime());
                TimeUtil.setClockSource(clockSource);
                user = userRepository.save(TestUtil.createUser());
        }

        @AfterEach
        public void teardown() {
                TimeUtil.revertClockSource();
        }

        @Test
        public void verifyMapping() {
                // given
                ClassificationSeries classification = createClassificationSeriesWithVersion(user, "name");
                classificationFamilyRepository.save(TestUtil.createClassificationFamily("family"))
                                .addClassificationSeries(
                                                classification);
                subject.save(classification);
                subject.flush();
                entityManager.detach(classification);

                // when
                ClassificationSeries result = subject.findById(classification.getId()).orElseThrow();

                // then
                assertEquals(1, result.getClassificationVersions().size());
                assertEquals(2, result.getClassificationVersions().get(0).getAllClassificationItems().size());
        }

        @Test
        public void findAllCodelist() {
                // given
                ClassificationSeries codelist = new ClassificationSeries(Translatable.create("name", Language.NB),
                                Translatable.create("description", Language.NB), false, Language.NB,
                                ClassificationType.CODELIST, user);
                classificationFamilyRepository.save(TestUtil.createClassificationFamily("name"))
                                .addClassificationSeries(
                                                codelist);
                subject.save(codelist);

                ClassificationSeriesSpecification includeCodelistsSpecification = new ClassificationSeriesSpecification(
                                true,
                                null);
                ClassificationSeriesSpecification excludeCodelistsSpecification = new ClassificationSeriesSpecification(
                                false,
                                null);

                // then
                assertEquals(1, subject.findAll(includeCodelistsSpecification, createPageable()).getTotalElements());
                assertEquals(0, subject.findAll(excludeCodelistsSpecification, createPageable()).getTotalElements());
        }

        @Test
        public void findAllChangedSince() throws Exception {
                // given
                Date beforeCreated = TimeUtil.now();
                clockSource.increment();
                ClassificationSeries classification = new ClassificationSeries(Translatable.create("name", Language.NB),
                                Translatable.create("description", Language.NB), false, Language.NB,
                                ClassificationType.CLASSIFICATION, user);
                classificationFamilyRepository.save(TestUtil.createClassificationFamily("name"))
                                .addClassificationSeries(
                                                classification);
                subject.save(classification);
                clockSource.increment();
                Date afterCreated = TimeUtil.now();

                ClassificationSeriesSpecification beforeCreatedSpecification = new ClassificationSeriesSpecification(
                                true,
                                beforeCreated);
                ClassificationSeriesSpecification afterCreatedSpecification = new ClassificationSeriesSpecification(
                                true,
                                afterCreated);

                // then
                assertEquals(1, subject.findAll(beforeCreatedSpecification, createPageable()).getTotalElements());
                assertEquals(0, subject.findAll(afterCreatedSpecification, createPageable()).getTotalElements());
        }

        @Test
        public void findAllCopyrightedExcluded() {
                // given
                ClassificationSeries classification = new ClassificationSeries(Translatable.create("name", Language.NB),
                                Translatable.create("description", Language.NB), true, Language.NB,
                                ClassificationType.CLASSIFICATION, user);
                classificationFamilyRepository.save(TestUtil.createClassificationFamily("name"))
                                .addClassificationSeries(
                                                classification);
                subject.save(classification);

                ClassificationSeriesSpecification specification = new ClassificationSeriesSpecification(true, null);

                // then
                assertEquals(0, subject.findAll(specification, createPageable()).getTotalElements());
        }

        @Test
        public void findAllDeletedExcluded() {
                // given
                ClassificationSeries classification = TestUtil.createClassification("name");
                classification.setContactPerson(user);
                classificationFamilyRepository.save(TestUtil.createClassificationFamily("name"))
                                .addClassificationSeries(
                                                classification);
                subject.save(classification);

                ClassificationSeriesSpecification specification = new ClassificationSeriesSpecification(true, null);

                // then
                assertEquals(1, subject.findAll(specification, createPageable()).getTotalElements());
                classification.setDeleted();
                subject.save(classification);
                assertEquals(0, subject.findAll(specification, createPageable()).getTotalElements());
        }

        @Test
        public void testNumberOfClassifications() {
                ClassificationSeries classification1 = createClassificationSeriesWithVersion(user, "Duppeditt");
                classification1.setContactPerson(user);
                classificationFamilyRepository.save(TestUtil.createClassificationFamily("Duppeditt"))
                                .addClassificationSeries(
                                                classification1);
                subject.save(classification1);
                ClassificationSeries classification2 = createClassificationSeriesWithVersion(user, "Dingseboms");
                classification2.setContactPerson(user);
                classificationFamilyRepository.save(TestUtil.createClassificationFamily("Dingseboms"))
                                .addClassificationSeries(
                                                classification2);
                subject.save(classification2);

                int result = subject.finNumberOfClassifications(null, null);
                assertEquals(2, result);
                result = subject.findNumberOfPublishedClassifications(ClassificationType.CLASSIFICATION,
                                user.getSection());
                assertEquals(2, result);
                classification1.getClassificationVersions().get(0).unpublish(Language.getDefault());
                subject.save(classification1);
                result = subject.findNumberOfPublishedClassifications(ClassificationType.CLASSIFICATION,
                                user.getSection());
                assertEquals(1, result);
                result = subject.findNumberOfPublishedClassifications(null, null);
                assertEquals(1, result);
                result = subject.findNumberOfPublishedClassifications(ClassificationType.CODELIST, user.getSection());
                assertEquals(0, result);
                result = subject.findNumberOfPublishedClassifications(ClassificationType.CLASSIFICATION, "007");
                assertEquals(0, result);
                classification2.setDeleted();
                subject.save(classification2);
                result = subject.findNumberOfPublishedClassifications(ClassificationType.CLASSIFICATION,
                                user.getSection());
                assertEquals(0, result);
                result = subject.finNumberOfClassifications(ClassificationType.CLASSIFICATION, user.getSection());
                assertEquals(1, result);
                result = subject.finNumberOfClassifications(ClassificationType.CLASSIFICATION, "007");
                assertEquals(0, result);
                result = subject.finNumberOfClassifications(ClassificationType.CODELIST, user.getSection());
                assertEquals(0, result);
        }

        @Test
        public void testNumberOfVersions() {
                ClassificationSeries classification1 = createClassificationSeriesWithVersion(user, "Duppeditt");
                classification1.setContactPerson(user);
                classificationFamilyRepository.save(TestUtil.createClassificationFamily("Duppeditt"))
                                .addClassificationSeries(
                                                classification1);
                subject.save(classification1);

                int result = subject.findNumberOfPublishedVersionsAnyLanguages(ClassificationType.CLASSIFICATION, user
                                .getSection());
                assertEquals(1, result);

                ClassificationSeries classification2 = createClassificationSeriesWithVersion(user, "Dingseboms");
                classification2.setContactPerson(user);
                classificationFamilyRepository.save(TestUtil.createClassificationFamily("Dingseboms"))
                                .addClassificationSeries(
                                                classification2);
                subject.save(classification2);
                result = subject.findNumberOfPublishedVersionsAnyLanguages(ClassificationType.CLASSIFICATION, user
                                .getSection());
                assertEquals(2, result);
                result = subject.findNumberOfPublishedVersionsAllLanguages(ClassificationType.CLASSIFICATION, user
                                .getSection());
                assertEquals(0, result);

                classification1.getClassificationVersions().get(0).publish(Language.EN);
                classification1.getClassificationVersions().get(0).publish(Language.NN);
                subject.save(classification1);
                result = subject.findNumberOfPublishedVersionsAllLanguages(ClassificationType.CLASSIFICATION, user
                                .getSection());
                assertEquals(1, result);
                result = subject.findNumberOfPublishedVersionsAllLanguages(null, null);
                assertEquals(1, result);
                result = subject.findNumberOfPublishedVersionsAllLanguages(ClassificationType.CLASSIFICATION, "007");
                assertEquals(0, result);
                result = subject.findNumberOfPublishedVersionsAllLanguages(ClassificationType.CODELIST,
                                user.getSection());
                assertEquals(0, result);

                classification1.getClassificationVersions().get(0).setDeleted();
                subject.save(classification1);
                result = subject.findNumberOfPublishedVersionsAllLanguages(ClassificationType.CLASSIFICATION, user
                                .getSection());
                assertEquals(0, result);
                result = subject.findNumberOfPublishedVersionsAnyLanguages(ClassificationType.CLASSIFICATION, user
                                .getSection());
                assertEquals(1, result);
                result = subject.findNumberOfPublishedVersionsAnyLanguages(null, null);
                assertEquals(1, result);
                result = subject.findNumberOfPublishedVersionsAnyLanguages(ClassificationType.CLASSIFICATION, "007");
                assertEquals(0, result);
                result = subject.findNumberOfPublishedVersionsAnyLanguages(ClassificationType.CODELIST,
                                user.getSection());
                assertEquals(0, result);

        }

        @Test()
        @Disabled("skrevet om service laget")
        public void testClassificationsReport() {
                // ClassificationSeries classification1 =
                // createClassificationSeriesWithVersion(user, "Duppeditt");
                // classification1.setContactPerson(user);
                // classificationFamilyRepository.save(TestUtil.createClassificationFamily("Duppeditt")).addClassificationSeries(
                // classification1);
                // subject.save(classification1);
                // ClassificationSeries classification2 =
                // createClassificationSeriesWithVersion(user, "Dingseboms");
                // classification2.setContactPerson(user);
                // classificationFamilyRepository.save(TestUtil.createClassificationFamily("Dingseboms")).addClassificationSeries(
                // classification2);
                // subject.save(classification2);
                //
                // List<ClassificationReportDto> list =
                // subject.getClassificationReport(ClassificationType.CLASSIFICATION, user
                // .getSection());
                // assertEquals(2, list.size());
                // ClassificationReportData<ClassificationReportDto> wrapper =
                // new ClassificationReportData<>(list);
                // assertEquals(
                // "section\tDuppeditt\tKlassifikasjon\tZiggy
                // Stardust\t\nsection\tDingseboms\tKlassifikasjon\tZiggy Stardust\t\n",
                // wrapper.exportToExcel('\t'));
                // list = subject.getClassificationReport(null, null);
                // assertEquals(2, list.size());
                // wrapper = new ClassificationReportData<>(list);
                // assertEquals(
                // "section\tDuppeditt\tKlassifikasjon\tZiggy
                // Stardust\t\nsection\tDingseboms\tKlassifikasjon\tZiggy Stardust\t\n",
                // wrapper.exportToExcel('\t'));
                // list = subject.getClassificationReport(ClassificationType.CODELIST,
                // user.getSection());
                // assertEquals(0, list.size());
                // list = subject.getClassificationReport(ClassificationType.CLASSIFICATION,
                // "007");
                // assertEquals(0, list.size());
                //
                // classification2.setDeleted();
                // subject.save(classification2);
                // list = subject.getClassificationReport(null, null);
                // assertEquals(1, list.size());
                // wrapper = new ClassificationReportData<>(list);
                // assertEquals("section\tDuppeditt\tKlassifikasjon\tZiggy Stardust\t\n",
                // wrapper.exportToExcel('\t'));
        }

        @Test
        @Disabled("skrevet om service laget")
        public void testPublishedClassificationReport() {
                // ClassificationSeries classification1 =
                // createClassificationSeriesWithVersion(user, "Duppeditt");
                // classification1.setContactPerson(user);
                // classificationFamilyRepository.save(TestUtil.createClassificationFamily("Duppeditt")).addClassificationSeries(
                // classification1);
                // subject.save(classification1);
                // ClassificationSeries classification2 =
                // createClassificationSeriesWithVersion(user, "Dingseboms");
                // classification2.setContactPerson(user);
                // classificationFamilyRepository.save(TestUtil.createClassificationFamily("Dingseboms")).addClassificationSeries(
                // classification2);
                // subject.save(classification2);
                //
                // List<ClassificationReportDto> list =
                // subject.getPublishedClassificationReport(ClassificationType.CLASSIFICATION,
                // user
                // .getSection());
                // assertEquals(2, list.size());
                // ClassificationReportData<ClassificationReportDto> wrapper =
                // new ClassificationReportData<>(list);
                // assertEquals(
                // "section\tDuppeditt\tKlassifikasjon\tZiggy
                // Stardust\t\nsection\tDingseboms\tKlassifikasjon\tZiggy Stardust\t\n",
                // wrapper.exportToExcel('\t'));
                // classification2.getClassificationVersions().get(0).unpublish(Language.getDefault());
                // list = subject.getPublishedClassificationReport(null, null);
                // assertEquals(1, list.size());
                // wrapper = new ClassificationReportData<>(list);
                // assertEquals("section\tDuppeditt\tKlassifikasjon\tZiggy Stardust\t\n",
                // wrapper.exportToExcel('\t'));
                // list = subject.getPublishedClassificationReport(ClassificationType.CODELIST,
                // user.getSection());
                // assertEquals(0, list.size());
                // list =
                // subject.getPublishedClassificationReport(ClassificationType.CLASSIFICATION,
                // "007");
                // assertEquals(0, list.size());
                //
                // classification1.setDeleted();
                // subject.save(classification1);
                // list = subject.getPublishedClassificationReport(null, null);
                // assertEquals(0, list.size());
        }

        @Test
        @Disabled("skrevet om service laget")
        public void testPublishedVersionsAnyLanguages() {
                // ClassificationSeries classification1 =
                // createClassificationSeriesWithVersion(user, "Duppeditt");
                // classification1.setContactPerson(user);
                // classificationFamilyRepository.save(TestUtil.createClassificationFamily("Duppeditt")).addClassificationSeries(
                // classification1);
                // subject.save(classification1);
                // ClassificationSeries classification2 =
                // createClassificationSeriesWithVersion(user, "Dingseboms");
                // classification2.setContactPerson(user);
                // classificationFamilyRepository.save(TestUtil.createClassificationFamily("Dingseboms")).addClassificationSeries(
                // classification2);
                // subject.save(classification2);
                //
                // List<ClassificationVersionReportDto> list =
                // subject.getPublishedVersionsAnyLanguages(
                // ClassificationType.CLASSIFICATION, user
                // .getSection());
                // assertEquals(2, list.size());
                // ClassificationReportData<ClassificationVersionReportDto> wrapper =
                // new ClassificationReportData<>(list);
                // assertEquals(
                // "section\tDuppeditt\tKlassifikasjon\tZiggy
                // Stardust\t\nsection\tDingseboms\tKlassifikasjon\tZiggy Stardust\t\n",
                // wrapper.exportToExcel('\t'));
                // classification2.getClassificationVersions().get(0).unpublish(Language.getDefault());
                // list = subject.getPublishedVersionsAnyLanguages(null, null);
                // assertEquals(1, list.size());
                // wrapper = new ClassificationReportData<>(list);
                // assertEquals("section\tDuppeditt\tKlassifikasjon\tZiggy Stardust\t\n",
                // wrapper.exportToExcel('\t'));
                // list = subject.getPublishedVersionsAnyLanguages(ClassificationType.CODELIST,
                // user.getSection());
                // assertEquals(0, list.size());
                // list =
                // subject.getPublishedVersionsAnyLanguages(ClassificationType.CLASSIFICATION,
                // "007");
                // assertEquals(0, list.size());
                //
                // classification1.getClassificationVersions().get(0).setDeleted();
                // subject.save(classification1);
                // list = subject.getPublishedVersionsAnyLanguages(null, null);
                // assertEquals(0, list.size());
        }

        @Test
        @Disabled("skrevet om service laget")
        public void testPublishedVersionsAllLanguages() {
                // ClassificationSeries classification1 =
                // createClassificationSeriesWithVersion(user, "Duppeditt");
                // classificationFamilyRepository.save(TestUtil.createClassificationFamily("Duppeditt")).addClassificationSeries(
                // classification1);
                // subject.save(classification1);
                // ClassificationSeries classification2 =
                // createClassificationSeriesWithVersion(user, "Dingseboms");
                // classificationFamilyRepository.save(TestUtil.createClassificationFamily("Dingseboms")).addClassificationSeries(
                // classification2);
                // subject.save(classification2);
                //
                // List<ClassificationVersionReportDto> list =
                // subject.getPublishedVersionsAllLanguages(
                // ClassificationType.CLASSIFICATION, user
                // .getSection());
                // assertEquals(0, list.size());
                //
                // classification1.getClassificationVersions().get(0).publish(Language.EN);
                // classification1.getClassificationVersions().get(0).publish(Language.NN);
                // classification2.getClassificationVersions().get(0).publish(Language.EN);
                // classification2.getClassificationVersions().get(0).publish(Language.NN);
                // subject.save(classification1);
                // subject.save(classification2);
                //
                // list =
                // subject.getPublishedVersionsAllLanguages(ClassificationType.CLASSIFICATION,
                // user.getSection());
                // assertEquals(2, list.size());
                // ClassificationReportData<ClassificationVersionReportDto> wrapper =
                // new ClassificationReportData<>(list);
                // assertEquals(
                // "section\tDuppeditt\tKlassifikasjon\tZiggy
                // Stardust\t\nsection\tDingseboms\tKlassifikasjon\tZiggy Stardust\t\n",
                // wrapper.exportToExcel('\t'));
                //
                // classification2.getClassificationVersions().get(0).unpublish(Language.getDefault());
                // list = subject.getPublishedVersionsAllLanguages(null, null);
                // assertEquals(1, list.size());
                // wrapper = new ClassificationReportData<>(list);
                // assertEquals("section\tDuppeditt\tKlassifikasjon\tZiggy Stardust\t\n",
                // wrapper.exportToExcel('\t'));
                // list = subject.getPublishedVersionsAllLanguages(ClassificationType.CODELIST,
                // user.getSection());
                // assertEquals(0, list.size());
                // list =
                // subject.getPublishedVersionsAllLanguages(ClassificationType.CLASSIFICATION,
                // "007");
                // assertEquals(0, list.size());
                //
                // classification1.getClassificationVersions().get(0).setDeleted();
                // subject.save(classification1);
                // list = subject.getPublishedVersionsAllLanguages(null, null);
                // assertEquals(0, list.size());
        }

        @Test
        public void findAllClassificationIds() {
                // given
                ClassificationSeries classification = createClassificationSeriesWithVersion(user, "anyname");
                classificationFamilyRepository.save(TestUtil.createClassificationFamily("name"))
                                .addClassificationSeries(
                                                classification);
                subject.save(classification);

                // when
                List<Long> result = subject.findAllClassificationIds();

                // then
                assertEquals(1, result.size());
        }

        @Test
        public void getNumberOfClassificationForUserAndUpdateUserTest() {
                User anotherUser = userRepository.save(new User("Donald", "Donald Duck", "section"));

                ClassificationSeries classification = createClassificationSeriesWithVersion(user, "anyname");
                ClassificationSeries classification2 = createClassificationSeriesWithVersion(user, "anyname2");
                ClassificationSeries classification3 = createClassificationSeriesWithVersion(user, "anyname3");
                classification3.setContactPerson(anotherUser);
                classificationFamilyRepository.save(TestUtil.createClassificationFamily("name"))
                                .addClassificationSeries(
                                                classification);
                classificationFamilyRepository.save(TestUtil.createClassificationFamily("name2"))
                                .addClassificationSeries(
                                                classification2);
                classificationFamilyRepository.save(TestUtil.createClassificationFamily("name3"))
                                .addClassificationSeries(
                                                classification3);
                subject.save(classification);
                subject.save(classification2);
                subject.save(classification3);
                assertEquals(2, subject.getNumberOfClassificationForUser(user));
                assertEquals(1, subject.getNumberOfClassificationForUser(anotherUser));
                subject.updateUser(user, anotherUser);
                assertEquals(3, subject.getNumberOfClassificationForUser(anotherUser));
                assertEquals(0, subject.getNumberOfClassificationForUser(user));
        }

        public static ClassificationSeries createClassificationSeriesWithVersion(User user, String classificationName) {
                ClassificationVersion version = TestUtil.createClassificationVersion(TestUtil.anyDateRange());
                Level level = TestUtil.createLevel(1);
                version.addLevel(level);
                version.addClassificationItem(TestUtil.createClassificationItem("0001", "firstItem"),
                                level.getLevelNumber(),
                                null);
                version.addClassificationItem(TestUtil.createClassificationItem("0002", "secondItem"),
                                level.getLevelNumber(),
                                null);
                ClassificationSeries classification = TestUtil.createClassification(classificationName);
                classification.addClassificationVersion(version);
                classification.setContactPerson(user);
                return classification;
        }

        private Pageable createPageable() {
                return PageRequest.of(0, 3);
        }

        @Configuration
        @EnableAutoConfiguration
        @EntityScan(basePackageClasses = { TranslatablePersistenceConverter.class, ClassificationSeries.class })
        @ComponentScan(basePackageClasses = TranslatablePersistenceConverter.class)
        static class Config {
        }
}
