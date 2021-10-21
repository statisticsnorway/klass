package no.ssb.klass.testutil;

import static com.google.common.base.Preconditions.*;

import no.ssb.klass.core.model.*;
import no.ssb.klass.core.util.DateRange;
import no.ssb.klass.core.util.TimeUtil;
import no.ssb.klass.core.util.Translatable;

public final class TestUtil {
    private TestUtil() {
        // Utility class
    }

    public static ClassificationSeries createClassification(String name) {
        return new ClassificationSeries(Translatable.create(name, Language.getDefault()), Translatable.create(
                "description", Language.getDefault()), false, Language.NB, ClassificationType.CLASSIFICATION,
                createUser());
    }

    public static ClassificationSeries createCodelist(String name) {
        return createCodelist(name, "description");
    }

    public static ClassificationSeries createCodelist(String name, String description) {
        return new ClassificationSeries(Translatable.create(name, Language.getDefault()), Translatable.create(
                description, Language.getDefault()), false, Language.NB, ClassificationType.CODELIST,
                createUser());
    }

    public static ClassificationSeries createClassification(String name, String description) {
        return new ClassificationSeries(Translatable.create(name, Language.getDefault()), Translatable.create(
                description, Language.getDefault()), false, Language.NB, ClassificationType.CLASSIFICATION,
                createUser());
    }

    public static ClassificationSeries createClassification(String nameNO, String nameNN, String nameEN,
            String descriptionNO, String descriptionNN, String descriptionEN) {
        return new ClassificationSeries(new Translatable(nameNO, nameNN, nameEN), new Translatable(descriptionNO,
                descriptionNN, descriptionEN), false, Language.NB, ClassificationType.CLASSIFICATION,
                createUser());
    }

    public static ClassificationSeries createClassificationWithId(long id, String name) {
        ClassificationSeries classification = createClassification(name);
        classification.setClassificationFamily(createClassificationFamily("family"));
        classification.setId(id);
        return classification;
    }

    public static ClassificationSeries createCodelistWithId(long id, String name) {
        ClassificationSeries classification = createCodelist(name);
        classification.setClassificationFamily(createClassificationFamily("family"));
        classification.setId(id);
        return classification;
    }

    public static ClassificationSeries createCopyrightedClassificationWithId(long id, String name) {
        ClassificationSeries classification = new ClassificationSeries(Translatable.create(name, Language.getDefault()),
                Translatable.create("description", Language.getDefault()), true, Language.NB,
                ClassificationType.CLASSIFICATION, createUser());
        classification.setId(id);
        return classification;
    }

    public static ClassificationSeries createClassificationWithId(long id, String name, String description) {
        ClassificationSeries classification = createClassification(name, description);
        classification.setId(id);
        return classification;
    }

    public static ClassificationVersion createClassificationVersion(DateRange dateRange) {
        ClassificationVersion version = new ClassificationVersion(dateRange);
        version.publish(Language.getDefault());
        return version;
    }

    public static ClassificationVersion createClassificationVersionWithTable(long id, DateRange dateRange, String name) {
        ClassificationVersion version = createClassificationVersion(dateRange);
        version.setId(id);
        createClassificationWithId(id, name).addClassificationVersion(version);
        return version;
    }

    public static ClassificationVariant createClassificationVariant(String name, User user) {
        ClassificationVariant variant = new ClassificationVariant(Translatable.create(name, Language.getDefault()),
                user);
        variant.setDateRange(DateRange.create("2006-01-01", TestDataProvider.TEN_YEARS_LATER_DATE));
        variant.publish(Language.getDefault());
        return variant;
    }

    public static ClassificationVariant createClassificationVariantFuture(String name, User user) {
        ClassificationVariant variant = new ClassificationVariant(Translatable.create(name, Language.getDefault()),
                user);
        variant.setDateRange(DateRange.create(TestDataProvider.TEN_YEARS_LATER_DATE, null));
        variant.publish(Language.getDefault());
        return variant;
    }

    public static ConcreteClassificationItem createClassificationItem(String code, String officialName) {
        return createClassificationItem(code, officialName, null);
    }

    public static ConcreteClassificationItem createClassificationItem(String code, String officialName,
            String shortName) {
        return new ConcreteClassificationItem(code, Translatable.create(officialName, Language.getDefault()),
                Translatable.create(shortName, Language.getDefault()));
    }

    public static ConcreteClassificationItem createClassificationItem(String code, String officialName, String shortName, String notes) {
        return new ConcreteClassificationItem(code, Translatable.create(officialName, Language.getDefault()),
                Translatable.create(shortName, Language.getDefault()), Translatable.create(notes, Language.getDefault()));
    }

    public static ReferencingClassificationItem createReferencingClassificationItem(ClassificationItem item) {
        return new ReferencingClassificationItem(item);
    }

    public static ClassificationFamily createClassificationFamily(String name) {
        return new ClassificationFamily(Translatable.create(name, Language.getDefault()), "ikon_arbeid.svg");
    }

    public static CorrespondenceTable createCorrespondenceTable(ClassificationVersion source,
            ClassificationVersion target) {
        return createCorrespondenceTable("description", source, target);
    }

    public static CorrespondenceTable createCorrespondenceTable(String description, ClassificationVersion source,
            ClassificationVersion target) {
        return createCorrespondenceTable(description, source, 0, target, 0);
    }

    public static CorrespondenceTable createCorrespondenceTable(String description, ClassificationVersion source,
            int sourceLevelNumber, ClassificationVersion target, int targetLevelNumber) {
        CorrespondenceTable correspondenceTable = new CorrespondenceTable(Translatable.create(description, Language
                .getDefault()), source, sourceLevelNumber, target, targetLevelNumber);
        correspondenceTable.publish(Language.getDefault());
        return correspondenceTable;
    }

    public static Level createLevel(int levelNumber) {
        return new Level(levelNumber);
    }

    public static User createUser() {
        return new User("ziggy", "Ziggy Stardust", "section");
    }

    public static DateRange anyDateRange() {
        return DateRange.create(TimeUtil.createDate("2012-01-01"), TimeUtil.createDate("2015-01-01"));
    }

    public static ClassificationType oppositeClassificationType(ClassificationType classificationType) {
        checkNotNull(classificationType);
        return classificationType == ClassificationType.CLASSIFICATION ? ClassificationType.CODELIST
                : ClassificationType.CLASSIFICATION;
    }
}
