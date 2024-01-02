package no.ssb.klass.core.repository;

import static org.hamcrest.core.Is.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import javax.transaction.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

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
import no.ssb.klass.core.model.ClassificationFamily;
import no.ssb.klass.core.model.ClassificationItem;
import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.model.ConcreteClassificationItem;
import no.ssb.klass.core.model.CorrespondenceMap;
import no.ssb.klass.core.model.CorrespondenceTable;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.model.Level;
import no.ssb.klass.core.model.User;
import no.ssb.klass.core.util.Translatable;
import no.ssb.klass.core.util.TranslatablePersistenceConverter;
import no.ssb.klass.testutil.TestUtil;

/**
 * @author Mads Lundemo, SSB.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles(ConfigurationProfiles.H2_INMEMORY)
@Transactional
public class CorrespondenceMapRepositoryTest {

    @Autowired
    private CorrespondenceMapRepository correspondenceMapRepository;
    @Autowired
    private CorrespondenceTableRepository correspondenceTableRepository;
    @Autowired
    private ClassificationSeriesRepository classificationSeriesRepository;
    @Autowired
    private ClassificationFamilyRepository classificationFamilyRepository;
    @Autowired
    private UserRepository userRepository;
    private User user;
    private ClassificationFamily classificationFamily;

    private CorrespondenceTable normalCorrespondenceTable;
    private CorrespondenceTable deletedCorrespondenceTable;
    // added to check that we dont get items/maps from other classifications
    private CorrespondenceTable secondNormalCorrespondenceTable;
    private CorrespondenceTable secondDeletedCorrespondenceTable;

    @BeforeEach
    public void setup() {
        user = userRepository.save(TestUtil.createUser());
        classificationFamily = classificationFamilyRepository.save(TestUtil.createClassificationFamily("family"));

        normalCorrespondenceTable = createAndSaveCorrespondenceTable("normal", 12, false);
        deletedCorrespondenceTable = createAndSaveCorrespondenceTable("deleted", 10, true);
        secondDeletedCorrespondenceTable = createAndSaveCorrespondenceTable("deleted2", 15, true);
        secondNormalCorrespondenceTable = createAndSaveCorrespondenceTable("normal2", 12, false);
    }

    @Test
    public void findAllMapsUsingDeletedItems() {
        ClassificationVersion deletedSource = deletedCorrespondenceTable.getSource();
        ClassificationVersion deletedTarget = deletedCorrespondenceTable.getTarget();
        // when
        Set<CorrespondenceMap> sourceMapsUsingDeletedItems = correspondenceMapRepository.findAllMapsUsingDeletedItems(
                deletedSource, deletedSource.getAllClassificationItems());
        Set<CorrespondenceMap> targetMapsUsingDeletedItems = correspondenceMapRepository.findAllMapsUsingDeletedItems(
                deletedTarget, deletedTarget.getAllClassificationItems());

        // then
        assertThat(sourceMapsUsingDeletedItems.size()).isEqualTo(10);
        assertThat(targetMapsUsingDeletedItems.size()).isEqualTo(10);

        sourceMapsUsingDeletedItems.forEach(map -> assertTrue(deletedSource.getAllClassificationItems().contains(map
                .getSource().get())));
        targetMapsUsingDeletedItems.forEach(map -> assertTrue(deletedTarget.getAllClassificationItems().contains(map
                .getTarget().get())));
    }

    @Test
    public void findAllMapsUsingItems() {
        ClassificationVersion normalSource = normalCorrespondenceTable.getSource();
        ClassificationVersion normalTarget = normalCorrespondenceTable.getTarget();
        // when
        Set<CorrespondenceMap> itemsUsingNormalSource = correspondenceMapRepository.findAllMapsUsingItems(normalSource);
        Set<CorrespondenceMap> itemsUsingNormalTarget = correspondenceMapRepository.findAllMapsUsingItems(normalTarget);

        // then
        assertThat(itemsUsingNormalSource.size()).isEqualTo(12);
        assertThat(itemsUsingNormalTarget.size()).isEqualTo(12);
        itemsUsingNormalSource.forEach(map -> assertTrue(normalSource.getAllClassificationItems().contains(map
                .getSource().get())));
        itemsUsingNormalSource.forEach(map -> assertTrue(normalSource.getAllClassificationItems().contains(map
                .getSource().get())));
    }

    @Test
    public void findBySourceOrTarget() {
        ClassificationVersion normalSource = normalCorrespondenceTable.getSource();
        ClassificationItem classificationItem = normalSource.getAllClassificationItems().get(0);

        ClassificationVersion deletedSource = deletedCorrespondenceTable.getSource();
        ClassificationItem deletedClassificationItem = deletedSource.getAllClassificationItems().get(0);
        // when
        Set<CorrespondenceMap> deletedFromNormal = correspondenceMapRepository.findBySourceOrTarget(classificationItem,
                true);
        Set<CorrespondenceMap> normalFromNormal = correspondenceMapRepository.findBySourceOrTarget(classificationItem,
                false);

        Set<CorrespondenceMap> deletedFromDeleted = correspondenceMapRepository.findBySourceOrTarget(
                deletedClassificationItem, true);
        Set<CorrespondenceMap> normalFromDeleted = correspondenceMapRepository.findBySourceOrTarget(
                deletedClassificationItem, false);

        // then
        assertThat(deletedFromNormal.size()).isEqualTo(0);
        assertThat(normalFromNormal.size()).isEqualTo(1);

        assertThat(deletedFromDeleted.size()).isEqualTo(1);
        assertThat(normalFromDeleted.size()).isEqualTo(0);

    }

    @Configuration
    @EnableAutoConfiguration
    @EntityScan(basePackageClasses = { CorrespondenceTable.class })
    @ComponentScan(basePackageClasses = TranslatablePersistenceConverter.class)
    static class Config {
    }

    private CorrespondenceTable createAndSaveCorrespondenceTable(String name, int itemCount, boolean deleted) {
        ClassificationVersion source = createAndSaveClassificationWithVersion(name + 1, itemCount);
        ClassificationVersion target = createAndSaveClassificationWithVersion(name + 2, itemCount);
        CorrespondenceTable correspondenceTable = TestUtil.createCorrespondenceTable(source, target);

        for (int i = 0; i < itemCount; i++) {
            ClassificationItem sourceItem = source.getAllClassificationItems().get(i);
            ClassificationItem targetItem = target.getAllClassificationItems().get(i);
            correspondenceTable.addCorrespondenceMap(new CorrespondenceMap(sourceItem, targetItem));
        }

        if (deleted) {
            correspondenceTable.setDeleted();
        }
        return correspondenceTableRepository.save(correspondenceTable);
    }

    private ClassificationVersion createAndSaveClassificationWithVersion(String classificationName, int itemCount) {
        ClassificationSeries classification = TestUtil.createClassification(classificationName);
        ClassificationVersion version = TestUtil.createClassificationVersion(TestUtil.anyDateRange());
        classification.addClassificationVersion(version);
        classification.setContactPerson(user);
        version.addLevel(new Level(1));
        for (int i = 0; i < itemCount; i++) {
            Translatable name = Translatable.create(classificationName, Language.NB);
            ConcreteClassificationItem item = new ConcreteClassificationItem(classificationName + i, name, name);
            version.addClassificationItem(item, 1, null);
        }
        classificationFamily.addClassificationSeries(classification);
        classificationSeriesRepository.save(classification);
        return version;
    }

}
