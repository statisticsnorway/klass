package no.ssb.klass.core.repository;

import static org.hamcrest.core.Is.*;
import static org.junit.Assert.*;

import java.util.List;

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
import no.ssb.klass.core.model.ClassificationItem;
import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.ClassificationVariant;
import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.model.ConcreteClassificationItem;
import no.ssb.klass.core.model.CorrespondenceTable;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.model.Level;
import no.ssb.klass.core.model.ReferencingClassificationItem;
import no.ssb.klass.core.model.User;
import no.ssb.klass.core.util.Translatable;
import no.ssb.klass.core.util.TranslatablePersistenceConverter;
import no.ssb.klass.testutil.TestUtil;

/**
 * @author Mads Lundemo, SSB.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles(ConfigurationProfiles.H2_INMEMORY)
@Transactional
public class ReferencingClassificationItemRepositoryTest {

        @Autowired
        private ReferencingClassificationItemRepository referencingItemRepository;
        @Autowired
        private ClassificationVariantRepository classificationVariantRepository;
        @Autowired
        private ClassificationSeriesRepository classificationSeriesRepository;
        @Autowired
        private ClassificationFamilyRepository classificationFamilyRepository;
        @Autowired
        private UserRepository userRepository;
        private User user;
        private ClassificationFamily classificationFamily;

        private ClassificationVariant normalVariant;
        private ClassificationVariant deletedVariant;
        // added to check that we dont get items/maps from other classifications
        private ClassificationVariant secondNormalVariant;
        private ClassificationVariant secondDeletedVariant;

        @Before
        public void setup() {
                user = userRepository.save(TestUtil.createUser());
                classificationFamily = classificationFamilyRepository
                                .save(TestUtil.createClassificationFamily("family"));

                normalVariant = createAndSaveVariant("normal", 12, false);
                deletedVariant = createAndSaveVariant("deleted", 10, true);
                secondDeletedVariant = createAndSaveVariant("deleted2", 15, true);
                secondNormalVariant = createAndSaveVariant("normal2", 14, false);
        }

        @Test
        public void findByReference() {

                ClassificationVersion normalVersion = normalVariant.getClassificationVersion();
                ClassificationItem classificationItem = normalVersion.getAllClassificationItems().get(0);

                ClassificationVersion deletedDeleted = deletedVariant.getClassificationVersion();
                ClassificationItem deletedClassificationItem = deletedDeleted.getAllClassificationItems().get(0);

                List<ReferencingClassificationItem> deletedItemReferences = referencingItemRepository.findByReference(
                                classificationItem, true);
                List<ReferencingClassificationItem> normalItemReferences = referencingItemRepository.findByReference(
                                classificationItem, false);

                assertThat(deletedItemReferences.size(), is(0));
                assertThat(normalItemReferences.size(), is(1));

                List<ReferencingClassificationItem> deletedReferencesForDeletedVariant = referencingItemRepository
                                .findByReference(deletedClassificationItem, true);
                List<ReferencingClassificationItem> normalReferencesForDeletedVariant = referencingItemRepository
                                .findByReference(deletedClassificationItem, false);

                assertThat(deletedReferencesForDeletedVariant.size(), is(1));
                assertThat(normalReferencesForDeletedVariant.size(), is(0));
        }

        @Test
        public void findByReferenceInList() {
                ClassificationVersion normalVersion = normalVariant.getClassificationVersion();

                ClassificationVersion deletedDeleted = deletedVariant.getClassificationVersion();

                List<ReferencingClassificationItem> deletedItemReferences = referencingItemRepository
                                .findByReferenceInList(
                                                normalVersion.getAllClassificationItems(), true);
                List<ReferencingClassificationItem> normalItemReferences = referencingItemRepository
                                .findByReferenceInList(
                                                normalVersion.getAllClassificationItems(), false);

                assertThat(deletedItemReferences.size(), is(0));
                assertThat(normalItemReferences.size(), is(12));

                List<ReferencingClassificationItem> deletedReferencesForDeletedVariant = referencingItemRepository
                                .findByReferenceInList(deletedDeleted.getAllClassificationItems(), true);
                List<ReferencingClassificationItem> normalReferencesForDeletedVariant = referencingItemRepository
                                .findByReferenceInList(deletedDeleted.getAllClassificationItems(), false);

                assertThat(deletedReferencesForDeletedVariant.size(), is(10));
                assertThat(normalReferencesForDeletedVariant.size(), is(0));

        }

        @Test
        public void findItemReferences() {
                ClassificationVersion versionForNormalVariant = normalVariant.getClassificationVersion();
                ClassificationVersion versionForDeletedVariant = deletedVariant.getClassificationVersion();

                List<ReferencingClassificationItem> deletedReferences = referencingItemRepository.findItemReferences(
                                versionForNormalVariant, true);
                List<ReferencingClassificationItem> normalReferences = referencingItemRepository.findItemReferences(
                                versionForNormalVariant, false);

                assertThat(deletedReferences.size(), is(0));
                assertThat(normalReferences.size(), is(12));

                List<ReferencingClassificationItem> deletedReferencesForDeletedVariant = referencingItemRepository
                                .findItemReferences(versionForDeletedVariant, true);
                List<ReferencingClassificationItem> normalReferencesForDeletedVariant = referencingItemRepository
                                .findItemReferences(versionForDeletedVariant, false);

                assertThat(deletedReferencesForDeletedVariant.size(), is(10));
                assertThat(normalReferencesForDeletedVariant.size(), is(0));

        }

        @Configuration
        @EnableAutoConfiguration
        @EntityScan(basePackageClasses = { CorrespondenceTable.class })
        @ComponentScan(basePackageClasses = TranslatablePersistenceConverter.class)
        static class Config {
        }

        private ClassificationVariant createAndSaveVariant(String name, int itemCount, boolean deleted) {
                ClassificationVersion source = createAndSaveClassificationWithVersion(name, itemCount);
                ClassificationVariant variant = TestUtil.createClassificationVariant(name + "_variant", user);
                source.addClassificationVariant(variant);
                variant.setClassificationVersion(source);

                for (int i = 0; i < itemCount; i++) {
                        ClassificationItem sourceItem = source.getAllClassificationItems().get(i);
                        ClassificationItem item = new ReferencingClassificationItem(sourceItem);
                        variant.addClassificationItem(item, 1, null);
                }

                if (deleted) {
                        variant.setDeleted();
                }
                return classificationVariantRepository.save(variant);
        }

        private ClassificationVersion createAndSaveClassificationWithVersion(String classificationName, int itemCount) {
                ClassificationSeries classification = TestUtil.createClassification(classificationName);
                ClassificationVersion version = TestUtil.createClassificationVersion(TestUtil.anyDateRange());
                classification.addClassificationVersion(version);
                classification.setContactPerson(user);
                version.addLevel(new Level(1));
                for (int i = 0; i < itemCount; i++) {
                        Translatable name = Translatable.create(classificationName, Language.NB);
                        ConcreteClassificationItem item = new ConcreteClassificationItem(classificationName + i, name,
                                        name);
                        version.addClassificationItem(item, 1, null);
                }
                classificationFamily.addClassificationSeries(classification);
                classificationSeriesRepository.save(classification);
                return version;
        }

}
