package no.ssb.klass.core.model;

import static com.google.common.base.Preconditions.*;
import static java.util.stream.Collectors.*;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.springframework.util.StringUtils;

import no.ssb.klass.core.util.DateRange;
import no.ssb.klass.core.util.Translatable;

@Entity
@DiscriminatorValue("version")
public class ClassificationVersion extends StatisticalClassification {
    @ManyToOne
    private ClassificationSeries classification;


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "classificationVersion")
    private List<ClassificationVariant> classificationVariants;

    @Column(length = 4000)
    private Translatable legalBase;
    @Column(length = 4000)
    private Translatable publications;
    @Column(length = 4000)
    private Translatable derivedFrom;
    @Column(length = 4000)
    private Translatable alias;

    // For Hibernate
    protected ClassificationVersion() {
    }

    public ClassificationVersion(DateRange dateRange) {
        this(dateRange, Translatable.empty(), Translatable.empty(), Translatable.empty(), Translatable.empty());
    }

    public ClassificationVersion(DateRange dateRange, Translatable introduction, Translatable legalBase,
            Translatable publications, Translatable derivedFrom) {
        super(introduction);
        checkNotNull(dateRange);
        this.validFrom = checkNotNull(dateRange.getFrom());
        this.validTo = checkNotNull(dateRange.getTo());
        this.legalBase = checkNotNull(legalBase);
        this.publications = checkNotNull(publications);
        this.derivedFrom = checkNotNull(derivedFrom);
        this.classificationVariants = new ArrayList<>();
        this.alias = Translatable.empty();
    }

    public List<ClassificationVariant> getClassificationVariants() {
        return classificationVariants.stream().filter(variant -> !variant.isDeleted()).collect(toList());
    }

    public List<ClassificationVariant> getPublicClassificationVariants() {
        return classificationVariants.stream()
                .filter(variant -> !variant.isDeleted())
                .filter(variant -> !variant.isDraft())
                .filter(ClassificationVariant::isPublishedInAnyLanguage)
                .collect(toList());
    }

    public void addClassificationVariant(ClassificationVariant classificationVariant) {
        checkNotNull(classificationVariant);
        classificationVariants.add(classificationVariant);
        classificationVariant.setClassificationVersion(this);
    }

    public ClassificationVersion copyClassificationVersion(DateRange dateRange) {
        ClassificationVersion newClassificationVersion = new ClassificationVersion(dateRange);
        newClassificationVersion.introduction = introduction;
        newClassificationVersion.derivedFrom = derivedFrom;
        newClassificationVersion.legalBase = legalBase;
        newClassificationVersion.publications = publications;
        for (Level originalLevel : getLevels()) {
            newClassificationVersion.addLevel(new Level(originalLevel.getLevelNumber()));
            for (ClassificationItem originalItem : originalLevel.getClassificationItems()) {
                ClassificationItem newItem = originalItem.copy();
                ClassificationItem parent = originalItem.getParent() == null ? null
                        : newClassificationVersion.findItem(originalItem.getParent().getCode());
                newClassificationVersion.addClassificationItem(newItem, originalItem.getLevel().getLevelNumber(),
                        parent);
            }
        }
        return newClassificationVersion;
    }



    public ClassificationSeries getClassification() {
        return classification;
    }

    void setClassification(ClassificationSeries classification) {
        this.classification = checkNotNull(classification);
    }

    public ClassificationVariant findVariantByNameBase(String variantName, Language language) {
        return getClassificationVariants().stream().filter(variant -> variantName.equals(variant.getNameBase(language)))
                .findFirst().orElse(null);
    }

    public ClassificationVariant findVariantByFullName(String variantName, Language language) {
        return getClassificationVariants().stream().filter(variant -> variantName.equals(variant.getFullName(language)))
                .findFirst().orElse(null);
    }


    @Override
    public Language getPrimaryLanguage() {
        return classification.getPrimaryLanguage();
    }

    @Override
    public boolean isPublishedInAnyLanguage() {
        if (super.isPublishedInAnyLanguage()) {
            return true;
        }
        for (ClassificationVariant classificationVariant : getClassificationVariants()) {
            if (classificationVariant.isPublishedInAnyLanguage()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Use this method for displaying version (formatted output) When more than one given version exists for a given
     * year, the year is prefixed with the month (Exs: Bydeler 03-2014), note that if given version contains an alias
     * value then this will be returned instead.
     * 
     * @return Formatted name
     */
    public String getName(Language language) {
        if (!alias.isEmpty()) {
            return getAlias(language);
        } else {
            return getGeneratedName(language);
        }

    }

    private String getGeneratedName(Language language) {
        String datePostfix = getDatePostfix(validFrom, validTo);
        return StringUtils.capitalize(classification.getNameWithoutPrefix(language)) + ' ' + datePostfix;
    }



    @Override
    public String getNameInPrimaryLanguage() {
        return getName(classification.getPrimaryLanguage());
    }

    @Override
    public User getContactPerson() {
        return classification.getContactPerson();
    }

    @Override
    public String getCategoryName() {
        return "Versjon";
    }

    @Override
    public ClassificationSeries getOwnerClassification() {
        return classification;
    }

    public String getLegalBase(Language language) {
        return legalBase.getString(language);
    }

    public void setLegalBase(String value, Language language) {
        this.legalBase = legalBase.withLanguage(value, language);
    }

    public String getPublications(Language language) {
        return publications.getString(language);
    }

    public void setPublications(String value, Language language) {
        this.publications = publications.withLanguage(value, language);
    }

    public String getDerivedFrom(Language language) {
        return derivedFrom.getString(language);
    }

    public void setDerivedFrom(String value, Language language) {
        derivedFrom = derivedFrom.withLanguage(value, language);
    }

    public String getAlias(Language language) {
        return alias.getString(language);
    }

    public void setAlias(String value, Language language) {
        alias = alias.withLanguage(value, language);
    }

    @Override
    public boolean isIncludeShortName() {
        return getOwnerClassification().isIncludeShortName();
    }

    @Override
    public boolean isIncludeNotes() {
        return getOwnerClassification().isIncludeNotes();
    }

    public boolean isIncludeValidity() {
        return getOwnerClassification().isIncludeValidity();
    }

    @Override
    public boolean isDeleted() {
        return super.isDeleted() || classification.isDeleted();
    }

    public boolean hasNotes() {
        for (ClassificationItem item : getAllClassificationItems()) {
            if (item.hasNotes()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasShortNames() {
        for (ClassificationItem item : getAllClassificationItems()) {
            if (item.hasShortName()) {
                return true;
            }
        }
        return false;
    }
}
