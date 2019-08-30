package no.ssb.klass.core.model;

import static com.google.common.base.Preconditions.*;
import static java.util.stream.Collectors.*;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.Hibernate;

import no.ssb.klass.core.util.Translatable;

@Entity
@Table(indexes = {
        @Index(columnList = "name", name = "cf_name_idx", unique = true)
})
public class ClassificationFamily extends BaseEntity {
    public static final String BASE_ICON_PATH = "/icons/";
    @Column(nullable = false)
    private final Translatable name;
    @Column(nullable = false)
    private final String iconName;
    @OneToMany(mappedBy = "classificationFamily", fetch = FetchType.LAZY)
    private final List<ClassificationSeries> classificationSeriesList;

    public ClassificationFamily(Translatable name, String iconName) {
        this.name = checkNotNull(name);
        checkArgument(!name.isEmpty(), "Name is empty");
        this.iconName = checkNotNull(iconName);
        this.classificationSeriesList = new ArrayList<>();
    }

    public void addClassificationSeries(ClassificationSeries classification) {
        checkNotNull(classification);
        if (Hibernate.isInitialized(classificationSeriesList)) {
            classificationSeriesList.add(classification);
        }
        classification.setClassificationFamily(this);
    }

    public void removeClassificationSeries(ClassificationSeries classification) {
        classificationSeriesList.remove(classification);
    }

    public String getName() {
        return getName(Language.getDefault());
    }

    public String getName(Language language) {
        return name.getString(language);
    }

    public List<ClassificationSeries> getClassificationSeries() {
        return classificationSeriesList.stream().filter(classification -> !classification.isDeleted()).collect(
                toList());
    }

    public List<ClassificationSeries> getPublicClassificationSeries() {
        return classificationSeriesList.stream()
                .filter(classification -> !classification.isDeleted())
                .filter(classification -> !classification.isCopyrighted())
                .filter(ClassificationSeries::isPublishedInAnyLanguage)
                .collect(toList());
    }

    public String getIconPath() {
        return BASE_ICON_PATH + iconName;
    }

    public List<ClassificationSeries> getClassificationSeriesBySectionAndClassificationType(
            String section, ClassificationType classificationType) {
        return getClassificationSeriesBySectionAndClassificationType(section, classificationType, false);
    }
    public List<ClassificationSeries> getClassificationSeriesBySectionAndClassificationType(String section,
            ClassificationType classificationType, boolean publicOnly) {
        // @formatter:off
        List<ClassificationSeries> list = publicOnly ?  getPublicClassificationSeries() : getClassificationSeries();
        return list.stream()
                .filter(classification -> section == null || section.equals(classification.getContactPerson().getSection()))
                .filter(classification -> classificationType == null || classificationType.equals(classification.getClassificationType()))
                .collect(toList());
        // @formatter:on
    }
}
