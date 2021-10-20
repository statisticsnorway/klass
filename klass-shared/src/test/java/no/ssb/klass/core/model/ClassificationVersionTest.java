package no.ssb.klass.core.model;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.Test;

import no.ssb.klass.core.util.DateRange;
import no.ssb.klass.core.util.Translatable;
import no.ssb.klass.testutil.TestUtil;

public class ClassificationVersionTest {

    @Test
    public void getAllClassificationItems() {
        // given
        ClassificationVersion subject = createVersionWithLevels();

        // when
        List<ClassificationItem> result = subject.getAllClassificationItems();

        // then
        assertEquals(2, result.size());
    }

    @Test
    public void findItem() {
        // given
        ClassificationVersion subject = createVersionWithLevels();
        ClassificationItem item = subject.getAllClassificationItems().get(0);

        // when
        ClassificationItem result = subject.findItem(item.getCode());

        // then
        assertEquals(item.getCode(), result.getCode());
    }

    @Test(expected = IllegalArgumentException.class)
    public void findItemNotFound() {
        // given
        ClassificationVersion subject = createVersionWithLevels();

        // when
        subject.findItem("unknown");

        // then expect exception
    }

    @Test
    public void getCorrespondenceTablesWithTarget() {
        // given
        ClassificationVersion subject = createVersionWithLevels();
        ClassificationVersion target = createVersionWithLevels();
        CorrespondenceTable correspondenceTable = TestUtil.createCorrespondenceTable(subject, target);
        subject.addCorrespondenceTable(correspondenceTable);

        // when
        List<CorrespondenceTable> result = subject.getCorrespondenceTablesWithTarget(target.getClassification());

        // then
        assertEquals(1, result.size());
    }

    @Test
    public void getCorrespondenceTablesWithTargetVersion() {
        // given
        ClassificationVersion subject = createVersionWithLevels();
        ClassificationVersion target = createVersionWithLevels();
        CorrespondenceTable correspondenceTable = TestUtil.createCorrespondenceTable(subject, target);
        subject.addCorrespondenceTable(correspondenceTable);

        // when
        List<CorrespondenceTable> result = subject.getCorrespondenceTablesWithTargetVersion(target);

        // then
        assertEquals(1, result.size());
    }

    @Test
    public void findVariant() {
        // given
        final String variantName = "variantName";
        ClassificationVersion subject = createVersion();
        ClassificationVariant variant = TestUtil.createClassificationVariant(variantName, TestUtil.createUser());
        subject.addClassificationVariant(variant);

        // when
        ClassificationVariant result = subject.findVariantByNameBase(variantName, Language.NB);

        // then
        assertEquals(variantName, result.getNameBase(Language.NB));
    }

    @Test
    public void findVariantFullName() {
        // given
        final String variantName = "variantName";
        final String variantFullName = "variantName 2006  - variant av Name 2012";
        ClassificationVersion subject = createVersion();
        ClassificationVariant variant = TestUtil.createClassificationVariant(variantName, TestUtil.createUser());
        subject.addClassificationVariant(variant);

        // when
        ClassificationVariant result = subject.findVariantByFullName(variantFullName, Language.NB);

        // then
        assertEquals(variantFullName, result.getFullName(Language.NB));
    }

    @Test
    public void findVariantFiltersDeleted() {
        // given
        final String variantName = "variantName";
        ClassificationVersion subject = createVersion();
        ClassificationVariant variant = TestUtil.createClassificationVariant(variantName, TestUtil.createUser());
        variant.setDeleted();
        subject.addClassificationVariant(variant);

        // when
        ClassificationVariant result = subject.findVariantByNameBase(variantName, Language.NB);
        assertThat(result, nullValue());
        // then expect exception
    }

    @Test
    public void findVariantNotFound() {
        // given
        ClassificationVersion subject = createVersionWithLevels();
        ClassificationVariant variant = TestUtil.createClassificationVariant("variantName", TestUtil.createUser());
        subject.addClassificationVariant(variant);

        // when
        ClassificationVariant result = subject.findVariantByNameBase("unknown", Language.NB);
        assertThat(result, nullValue());
        // then expect exception
    }

    @Test
    public void addNextLevel() {
        // given
        ClassificationVersion version = createVersion();
        Level firstLevel = version.addNextLevel();
        Level secondLevel = version.addNextLevel();

        // then
        assertEquals(1, firstLevel.getLevelNumber());
        assertEquals(2, secondLevel.getLevelNumber());
    }

    @Test
    public void findLevel() {
        // given
        ClassificationVersion version = createVersion();
        version.addNextLevel();
        version.addNextLevel();

        // then
        assertEquals(true, version.hasLevel(1));
        assertEquals(true, version.hasLevel(2));
        assertEquals(false, version.hasLevel(3));
    }

    @Test(expected = IllegalArgumentException.class)
    public void addLevel() {
        // given
        ClassificationVersion version = createVersion();
        version.addNextLevel();

        // when
        version.addLevel(TestUtil.createLevel(1));

        // then expect exception
    }

    @Test
    public void getFirstLevel() {
        // given
        ClassificationVersion version = createVersion();
        version.addLevel(TestUtil.createLevel(2));
        version.addLevel(TestUtil.createLevel(1));

        // when
        Optional<Level> result = version.getFirstLevel();

        // then
        assertEquals(1, result.get().getLevelNumber());
    }

    @Test
    public void isLastLevel() {
        // given
        ClassificationVersion version = createVersion();
        Level level2 = TestUtil.createLevel(2);
        version.addLevel(level2);
        Level level1 = TestUtil.createLevel(1);
        version.addLevel(level1);

        // then
        assertEquals(true, version.isLastLevel(level2));
        assertEquals(false, version.isLastLevel(level1));
    }

    @Test
    public void addClassificationItem() {
        // given
        ClassificationVersion version = createVersion();
        Level level = TestUtil.createLevel(1);
        version.addLevel(level);
        ClassificationItem classificationItem = TestUtil.createClassificationItem("code", "officialName");

        // when
        version.addClassificationItem(classificationItem, level.getLevelNumber(), null);

        // then
        assertEquals(level.getLevelNumber(), classificationItem.getLevel().getLevelNumber());
    }

    @Test(expected = IllegalArgumentException.class)
    public void addClassificationItemWithParentForFirstLevel() {
        // given
        ClassificationVersion version = createVersion();
        Level level = TestUtil.createLevel(1);
        version.addLevel(level);
        ClassificationItem classificationItem = TestUtil.createClassificationItem("code", "officialName");
        ClassificationItem parent = TestUtil.createClassificationItem("parent", "officialName");

        // when
        version.addClassificationItem(classificationItem, level.getLevelNumber(), parent);

        // then expect exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void addClassificationItemWithNoParentForNotFirstLevel() {
        // given
        ClassificationVersion version = createVersion();
        version.addNextLevel();
        version.addNextLevel();
        ClassificationItem classificationItem = TestUtil.createClassificationItem("code", "officialName");

        // when
        version.addClassificationItem(classificationItem, 2, null);

        // then expect exception
    }

    @Test
    public void deleteClassificationItem() {
        // given
        ClassificationVersion version = createVersion();
        Level level = TestUtil.createLevel(1);
        version.addLevel(level);
        ClassificationItem classificationItem = TestUtil.createClassificationItem("code", "officialName");
        version.addClassificationItem(classificationItem, level.getLevelNumber(), null);

        // when
        version.deleteClassificationItem(classificationItem);

        // then
        assertEquals(0, level.getClassificationItems().size());
        assertEquals(0, version.getAllClassificationItems().size());
    }

    @Test
    public void getNameInPrimaryLanguage() {

        DateRange dateRangeMatch1 = DateRange.create(LocalDate.of(2016, 2, 1), LocalDate.of(2016, 6, 1));
        DateRange dateRangeMatch2 = DateRange.create(LocalDate.of(2016, 7, 1), LocalDate.of(2016, 10, 1));
        DateRange dateRangeMatch3 = DateRange.create(LocalDate.of(2016, 11, 1), LocalDate.of(2017, 2, 1));
        DateRange dateRangeNoMatch1 = DateRange.create(LocalDate.of(2015, 1, 1), LocalDate.of(2016, 1, 1));
        DateRange dateRangeNoMatch2 = DateRange.create(LocalDate.of(2017, 3, 1), LocalDate.of(2017, 12, 1));
        Translatable translatable = Translatable.create("Test", Language.NB);

        ClassificationVersion classificationVersion1 = new ClassificationVersion(dateRangeMatch1);
        ClassificationVersion classificationVersion2 = new ClassificationVersion(dateRangeNoMatch1);
        ClassificationVersion classificationVersion3 = new ClassificationVersion(dateRangeMatch2);
        ClassificationVersion classificationVersion4 = new ClassificationVersion(dateRangeNoMatch2);
        ClassificationVersion classificationVersion5 = new ClassificationVersion(dateRangeMatch3);
        ClassificationSeries subject = new ClassificationSeries(translatable, new Translatable("no", "nn", "en"),
                false, Language.NB, ClassificationType.CLASSIFICATION, TestUtil.createUser());
        subject.addClassificationVersion(classificationVersion1);
        subject.addClassificationVersion(classificationVersion2);
        subject.addClassificationVersion(classificationVersion3);
        subject.addClassificationVersion(classificationVersion4);
        subject.addClassificationVersion(classificationVersion5);

        assertEquals("Test 2016-02", classificationVersion1.getNameInPrimaryLanguage());
        assertEquals("Test 2015", classificationVersion2.getNameInPrimaryLanguage());
        assertEquals("Test 2016-07", classificationVersion3.getNameInPrimaryLanguage());
        assertEquals("Test 2017-03", classificationVersion4.getNameInPrimaryLanguage());
        assertEquals("Test 2016-11", classificationVersion5.getNameInPrimaryLanguage());
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeLevelWhenNotEmpty() {
        // given
        ClassificationVersion version = createVersion();
        Level level = TestUtil.createLevel(1);
        version.addLevel(level);
        ClassificationItem classificationItem = TestUtil.createClassificationItem("code", "officialName");
        version.addClassificationItem(classificationItem, level.getLevelNumber(), null);

        // when
        version.deleteLevel(level);

        // then expect exception
    }

    @Test
    public void removeLevel() {
        // given
        ClassificationVersion version = createVersion();
        Level level = TestUtil.createLevel(1);
        version.addLevel(level);

        // when
        version.deleteLevel(level);

        // then
        assertEquals(0, version.getLevels().size());
    }

    @Test
    public void getLastLevel() {
        // given
        ClassificationVersion version = createVersion();
        Level level1 = TestUtil.createLevel(1);
        version.addLevel(level1);
        Level level3 = TestUtil.createLevel(3);
        version.addLevel(level3);
        Level level2 = TestUtil.createLevel(2);
        version.addLevel(level2);

        // when
        Level result = version.getLastLevel().get();

        // then
        assertEquals(3, result.getLevelNumber());
    }

    @Test
    public void isFirstLevel() {
        // given
        ClassificationVersion version = createVersion();
        Level level2 = TestUtil.createLevel(2);
        version.addLevel(level2);
        Level level1 = TestUtil.createLevel(1);
        version.addLevel(level1);

        // then
        assertEquals(true, version.isFirstLevel(level1));
        assertEquals(false, version.isFirstLevel(level2));
    }

    @Test
    public void hasTranslations() {
        // given
        ClassificationVersion version = createVersion();
        version.addNextLevel();
        version.addClassificationItem(new ConcreteClassificationItem("code", new Translatable("name", "name", null),
                Translatable.empty()), 1, null);

        // then
        assertEquals(true, version.hasTranslations(Language.NB));
        assertEquals(true, version.hasTranslations(Language.NN));
        assertEquals(false, version.hasTranslations(Language.EN));
    }

    @Test
    public void getAllConcreteClassificationItems() {
        // given
        ClassificationVersion version = createVersion();
        version.addNextLevel();
        ClassificationItem item = TestUtil.createClassificationItem("code", "officialName");
        version.addClassificationItem(new ReferencingClassificationItem(item), 1, null);

        // then
        assertEquals(1, version.getAllClassificationItems().size());
        assertEquals(0, version.getAllConcreteClassificationItems().size());
    }

    @Test
    public void getAllClassificationItemsAreSorted() {
        // given
        ClassificationVersion version = createVersion();
        version.addNextLevel();
        version.addClassificationItem(TestUtil.createClassificationItem("2", "any"), 1, null);
        version.addClassificationItem(TestUtil.createClassificationItem("1", "any"), 1, null);

        // then
        assertEquals("1", version.getAllClassificationItems().get(0).getCode());
        assertEquals("2", version.getAllClassificationItems().get(1).getCode());
    }

    @Test
    public void getClassificationVariants() {
        // given
        ClassificationVersion version = createVersion();
        ClassificationVariant variant = TestUtil.createClassificationVariant("name", TestUtil.createUser());
        version.addClassificationVariant(variant);

        // when
        List<ClassificationVariant> result = version.getClassificationVariants();

        // then
        assertEquals(1, result.size());
    }

    @Test
    public void getClassificationVariantsFiltersDeleted() {
        // given
        ClassificationVersion version = createVersion();
        ClassificationVariant variant = TestUtil.createClassificationVariant("name", TestUtil.createUser());
        variant.setDeleted();
        version.addClassificationVariant(variant);

        // when
        List<ClassificationVariant> result = version.getClassificationVariants();

        // then
        assertEquals(0, result.size());
    }

    @Test
    public void hasNotes() {
        // given
        ClassificationVersion version = createVersion();
        version.addNextLevel();
        ConcreteClassificationItem item = TestUtil.createClassificationItem("code", "officialName");
        item.setNotes("notes", Language.NB);
        version.addClassificationItem(item, 1, null);

        // when
        boolean result = version.hasNotes();

        // then
        assertEquals(true, result);
    }

    @Test
    public void notHasNotes() {
        // given
        ClassificationVersion version = createVersion();
        version.addNextLevel();
        version.addClassificationItem(TestUtil.createClassificationItem("code", "officialName"), 1, null);

        // when
        boolean result = version.hasNotes();

        // then
        assertEquals(false, result);
    }

    @Test
    public void hasShortNames() {
        // given
        ClassificationVersion version = createVersion();
        version.addNextLevel();
        version.addClassificationItem(TestUtil.createClassificationItem("code", "officialName", "shortName", "notes"), 1, null);

        // when
        boolean result = version.hasShortNames();

        // then
        assertEquals(true, result);
    }

    @Test
    public void notHasShortNames() {
        // given
        ClassificationVersion version = createVersion();
        version.addNextLevel();
        version.addClassificationItem(TestUtil.createClassificationItem("code", "officialName"), 1, null);

        // when
        boolean result = version.hasShortNames();

        // then
        assertEquals(false, result);
    }

    @Test
    public void addChangelog() {
        // given
        final String user = "user";
        final String description = "description";
        ClassificationVersion version = createVersion();
        version.publish(Language.NB);

        // when
        version.addChangelog(new Changelog(user, description));

        // then
        assertEquals(1, version.getChangelogs().size());
        Changelog result = version.getChangelogs().get(0);
        assertNotNull(result.getChangeOccured());
        assertEquals(user, result.getChangedBy());
        assertEquals(description, result.getDescription());
    }

    private ClassificationVersion createVersion() {
        ClassificationSeries classification = TestUtil.createClassification("name");
        ClassificationVersion version = TestUtil.createClassificationVersion(TestUtil.anyDateRange());
        classification.addClassificationVersion(version);
        return version;
    }

    private ClassificationVersion createVersionWithLevels() {
        ClassificationVersion version = createVersion();
        Level level1 = TestUtil.createLevel(1);
        level1.addClassificationItem(TestUtil.createClassificationItem("code1", "name1"));
        Level level2 = TestUtil.createLevel(2);
        level2.addClassificationItem(TestUtil.createClassificationItem("code2", "name2"));
        version.addLevel(level1);
        version.addLevel(level2);
        return version;
    }
}
