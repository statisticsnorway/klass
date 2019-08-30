package no.ssb.klass.core.repository;

import no.ssb.klass.core.model.ClassificationFamily;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.util.Translatable;

public class ClassificationFamilySummary {
    private final Long id;
    private final Translatable classificationFamilyName;
    private final String iconPath;
    private final int numberOfClassifications;

    public ClassificationFamilySummary(Long id, Translatable classificationFamilyName, String iconName,
            long numberOfClassifications) {
        if (!iconName.startsWith(ClassificationFamily.BASE_ICON_PATH)) {
            iconName = ClassificationFamily.BASE_ICON_PATH + iconName;
        }
        this.id = id;
        this.classificationFamilyName = classificationFamilyName;
        this.iconPath = iconName;
        this.numberOfClassifications = (int) numberOfClassifications;
    }

    public Long getId() {
        return id;
    }

    public String getIconPath() {
        return iconPath;
    }

    public String getClassificationFamilyName(Language language) {
        return classificationFamilyName.getString(language);
    }

    public String getClassificationFamilyName() {
        return getClassificationFamilyName(Language.getDefault());
    }

    public int getNumberOfClassifications() {
        return numberOfClassifications;
    }
}
