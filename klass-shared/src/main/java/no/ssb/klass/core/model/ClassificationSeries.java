package no.ssb.klass.core.model;

import static com.google.common.base.Preconditions.*;
import static java.util.stream.Collectors.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Strings;

import no.ssb.klass.core.util.DateRange;
import no.ssb.klass.core.util.KlassResourceNotFoundException;
import no.ssb.klass.core.util.TimeUtil;
import no.ssb.klass.core.util.Translatable;
import no.ssb.klass.core.util.AlphaNumericalComparator;

@Entity
@Table(indexes = {
        // These must be case insensitive (JPA does not support specifying function based indexes)
        @Index(columnList = "name_no", name = "cs_name_no_idx", unique = true),
        @Index(columnList = "name_nn", name = "cs_name_nn_idx", unique = true),
        @Index(columnList = "name_en", name = "cs_name_en_idx", unique = true)
})
public class ClassificationSeries extends BaseEntity implements ClassificationEntityOperations {

    private static final String PREFIX_CLASSIFICATION_NB = "Standard for ";
    private static final String PREFIX_CLASSIFICATION_NN = "Standard for ";
    private static final String PREFIX_CLASSIFICATION_EN = "Classification of ";

    private static final String PREFIX_CODELIST_NB = "Kodeliste for ";
    private static final String PREFIX_CODELIST_NN = "Kodeliste for ";
    private static final String PREFIX_CODELIST_EN = "Codelist for ";

    // name is not stored as a Translatable since name is used in db queries for sorting and filtering
    @Column(name = "name_no")
    private String nameNo;
    @Column(name = "name_nn")
    private String nameNn;
    @Column(name = "name_en")
    private String nameEn;
    @Lob
    @Column(columnDefinition = "text", nullable = false)
    private Translatable description;
    @Column(nullable = false)
    private Language primaryLanguage;
    @Column(nullable = false)
    private boolean copyrighted;
    @Column(nullable = false)
    private boolean includeShortName;
    @Column(nullable = false)
    private boolean includeNotes;
    @Column(nullable = false)
    private boolean includeValidity;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClassificationType classificationType;
    @ManyToOne(optional = false)
    private ClassificationFamily classificationFamily;
    @ManyToOne(optional = false)
    private User contactPerson;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "classification")
    private final List<ClassificationVersion> classificationVersions = new ArrayList<>();
    @ManyToMany
    private final List<StatisticalUnit> statisticalUnits = new ArrayList<>();
    @Column(nullable = false)
    private boolean deleted;
    @Enumerated(EnumType.STRING)
    private MigratedFrom migratedFrom;
    @SuppressWarnings("unused")
    private Long migratedFromId;

    public ClassificationSeries(Translatable name, Translatable description, boolean copyrighted,
            Language primaryLanguage, ClassificationType classificationType, User contactPerson) {
        this.primaryLanguage = checkNotNull(primaryLanguage);
        this.description = checkNotNull(description);
        checkArgument(!description.isEmpty(), "Description is empty");
        checkNotNull(name);
        checkArgument(!name.isEmpty(), "Name is empty");
        this.nameNo = name.getStringOrNull(Language.NB);
        this.nameNn = name.getStringOrNull(Language.NN);
        this.nameEn = name.getStringOrNull(Language.EN);
        this.copyrighted = copyrighted;
        this.classificationType = checkNotNull(classificationType);
        this.contactPerson = checkNotNull(contactPerson);
        this.deleted = false;
    }

    public ClassificationSeries() {
        description = Translatable.empty();
    }

    @Override
    public User getContactPerson() {
        return contactPerson;
    }

    public boolean isCopyrighted() {
        return copyrighted;
    }

    public void setCopyrighted(boolean copyrighted) {
        this.copyrighted = copyrighted;
    }

    public ClassificationType getClassificationType() {
        return classificationType;
    }

    public String getDescription(Language language) {
        return description.getString(language);
    }

    public void setDescription(Language language, String value) {
        description = description.withLanguage(value, language);
    }

    public String getDescriptionInPrimaryLanguage() {
        return description.getString(primaryLanguage);
    }

    public String getName(Language language) {
        return new Translatable(nameNo, nameNn, nameEn).getString(language);
    }

    @Override
    public String getNameInPrimaryLanguage() {
        return getName(primaryLanguage);
    }

    public List<ClassificationVersion> getClassificationVersions() {
        return classificationVersions.stream().filter(version -> !version.isDeleted())
                .sorted(AlphaNumericalComparator.comparing(ClassificationVersion::getNameInPrimaryLanguage, true))
                .collect(toList());
    }

    public List<ClassificationVersion> getPublicClassificationVersions() {
        return classificationVersions.stream()
                .filter(version -> !version.isDeleted() && !version.isDraft())
                .filter(ClassificationVersion::isPublishedInAnyLanguage)
                .collect(toList());
    }

    public void addClassificationVersion(ClassificationVersion version) {
        checkNotNull(version);
        if (version.isDraft()) {
            if (getClassificationVersionDraft() != null) {
                throw new IllegalArgumentException("Only one draft allowed at any given time");
            }
        } else if (!getClassificationVersionsInRange(version.getDateRange()).isEmpty()) {
            throw new IllegalArgumentException("Date range " + version.getDateRange()
                    + " of version overlaps with already existing version: "
                    + getClassificationVersionsInRange(version.getDateRange()).get(0).getNameInPrimaryLanguage());
        }
        classificationVersions.add(version);
        version.setClassification(this);
    }

    public ClassificationVersion getClassificationVersionDraft() {
        for (ClassificationVersion classificationVersion : getClassificationVersions()) {
            if (classificationVersion.isDraft()) {
                return classificationVersion;
            }
        }
        return null;
    }

    /**
     * drafts are excluded as they do not have a real date range.
     * 
     * @param dateRange
     *            that versions should overlap
     * @return list of version that overlap provided DateRange
     */
    public List<ClassificationVersion> getClassificationVersionsInRange(DateRange dateRange) {
        checkNotNull(dateRange);
        List<ClassificationVersion> results = new ArrayList<>();
        for (ClassificationVersion classificationVersion : getClassificationVersions()) {
            if (classificationVersion.isDraft()) {
                continue;
            }
            if (classificationVersion.getDateRange().overlaps(dateRange)) {
                results.add(classificationVersion);
            }
        }
        return results;
    }

    /**
     * drafts are excluded as they do not have a real date range.
     *
     * @param dateRange
     *            that versions should overlap
     * @param includeFuture
     *            if future version is included
     * @return list of version that overlap provided DateRange and futured versions if value is true
     */
    public List<ClassificationVersion> getClassificationVersionsInRange(DateRange dateRange, Boolean includeFuture) {
        checkNotNull(dateRange);
        List<ClassificationVersion> results = new ArrayList<>();
        for (ClassificationVersion classificationVersion : getClassificationVersions()) {
            if (classificationVersion.isDraft()) {
                continue;
            }
            if (classificationVersion.getDateRange().overlaps(dateRange) && classificationVersion.showVersion(includeFuture)) {
                results.add(classificationVersion);
            }
        }
        return results;
    }

    /**
     * Find a version with a given range (drafts are excluded)
     *
     * @param dateRange
     * @return Result of the search, null if none found
     */
    public ClassificationVersion getVersionByRange(DateRange dateRange) {
        for (ClassificationVersion version : getClassificationVersions()) {
            if (version.isDraft()) {
                continue;
            }
            if (version.getDateRange().equals(dateRange)) {
                return version;
            }
        }
        return null;
    }

    /**
     * Find correspondenceTables that describes changes between versions of same classification, referred to as
     * changeTables. A changeTable list changes between two classification versions.
     * 
     * @param dateRange
     * @return
     */
    public List<CorrespondenceTable> getChangeTables(DateRange dateRange, Boolean includeFuture) {
        checkNotNull(dateRange);
        List<CorrespondenceTable> changeTables = new ArrayList<>();
        List<ClassificationVersion> versions = getClassificationVersionsInRange(dateRange, includeFuture);
        if (versions.size() < 2) {
            // Must have at least 2 versions to have a changeTable describing changes between versions
            return changeTables;
        }
        sortVersionsByFromDate(versions);
        for (int i = 0; i < versions.size() - 1; i++) {
            changeTables.add(getChangeTableBetween(versions.get(i), versions.get(i + 1)));
        }
        return changeTables;
    }

    private CorrespondenceTable getChangeTableBetween(ClassificationVersion first,
            ClassificationVersion second) {
        List<CorrespondenceTable> changeTables = new ArrayList<>();
        changeTables.addAll(first.getCorrespondenceTablesWithTargetVersion(second));
        changeTables.addAll(second.getCorrespondenceTablesWithTargetVersion(first));
        if (changeTables.size() > 1) {
            throw new KlassResourceNotFoundException(createMultipleChangeTablesFoundErrorMessage(first, second));
        }
        if (changeTables.isEmpty()) {
            throw new KlassResourceNotFoundException(createCorrespondenceNotFoundErrorMessage(first, second));
        }
        return changeTables.get(0);
    }

    private String createMultipleChangeTablesFoundErrorMessage(ClassificationVersion first,
            ClassificationVersion second) {
        return first.getNameInPrimaryLanguage() + " has multiple change tables (correspondenceTables) with: " + second
                .getNameInPrimaryLanguage();
    }

    private String createCorrespondenceNotFoundErrorMessage(ClassificationVersion first,
            ClassificationVersion second) {
        return first.getNameInPrimaryLanguage() + " has no change table (correspondenceTable) with: " + second
                .getNameInPrimaryLanguage();
    }

    public ClassificationVersion getNewestVersion() {
        if (getClassificationVersions().isEmpty()) {
            return null;
        }
        List<ClassificationVersion> sorted = getClassificationVersionsSortedByFromDateReversed();
        return sorted.get(0);
    }

    public List<ClassificationVersion> getClassificationVersionsSortedByFromDateReversed() {
        List<ClassificationVersion> versions = getClassificationVersionsSortedByFromDate();
        Collections.reverse(versions);
        moveDraftToTop(versions);
        return versions;
    }

    // dont want to add yet another sort argument for something that is only used for 1 administration view
    // the alphanumerical sort algorithm is all ready a bit slow
    private void moveDraftToTop(List<ClassificationVersion> versions) {
        Optional<ClassificationVersion> find = versions.stream().filter(StatisticalClassification::isDraft).findAny();
        if (find.isPresent()) {
            ClassificationVersion draft = find.get();
            versions.remove(draft);
            versions.add(0, draft);
        }
    }

    private List<ClassificationVersion> getClassificationVersionsSortedByFromDate() {
        List<ClassificationVersion> versions = getClassificationVersions();
        sortVersionsByFromDate(versions);
        return versions;
    }

    private void sortVersionsByFromDate(List<ClassificationVersion> versions) {
        versions.sort(Comparator.comparing(v -> v.getDateRange().getFrom()));
    }

    public ClassificationFamily getClassificationFamily() {
        return classificationFamily;
    }

    public void setClassificationFamily(ClassificationFamily classificationFamily) {
        this.classificationFamily = classificationFamily;
    }

    @Override
    public Language getPrimaryLanguage() {
        return primaryLanguage;
    }

    @Override
    public boolean isPublishedInAnyLanguage() {
        for (ClassificationVersion classificationVersion : getClassificationVersions()) {
            if (classificationVersion.isPublishedInAnyLanguage()) {
                return true;
            }
        }
        return false;
    }

    public boolean isPublished(Language language) {
        for (ClassificationVersion classificationVersion : getClassificationVersions()) {
            if (classificationVersion.isPublished(language)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getCategoryName() {
        return "Kodeverk";
    }

    @Override
    public ClassificationSeries getOwnerClassification() {
        return this;
    }

    public boolean isIncludeShortName() {
        return includeShortName;
    }

    public void setIncludeShortName(boolean includeShortName) {
        this.includeShortName = includeShortName;
    }

    public List<StatisticalUnit> getStatisticalUnits() {
        return statisticalUnits;
    }

    private void setName(String name, Language language) {
        switch (language) {
        case NB:
            nameNo = name;
            break;
        case NN:
            nameNn = name;
            break;
        case EN:
            nameEn = name;
            break;
        default:
            throw new IllegalArgumentException("Unknown language: " + language);
        }
    }

    public void setNameAndAddPrefix(String name, Language language) {
        if (StringUtils.isNotEmpty(name)) {
            String prefix = getNamePrefix(language);
            String fullName = prefix + name;
            setName(fullName, language);
        } else {
            setName(null, language);
        }
    }

    public void setPrimaryLanguage(Language primaryLanguage) {
        this.primaryLanguage = checkNotNull(primaryLanguage);
    }

    public void setContactPerson(User contactPerson) {
        this.contactPerson = checkNotNull(contactPerson);
    }

    public void setClassificationType(ClassificationType classificationType) {
        this.classificationType = checkNotNull(classificationType);
    }

    public boolean isIncludeNotes() {
        return includeNotes;
    }

    public void setIncludeNotes(boolean includeNotes) {
        this.includeNotes = includeNotes;
    }

    public boolean isIncludeValidity() {
        return includeValidity;
    }

    public void setIncludeValidity(boolean includeValidity) {
        this.includeValidity = includeValidity;
    }

    public String getNameWithoutPrefix(Language language) {
        String prefixedName = getName(language);
        return getNameWithoutPrefix(prefixedName, language, classificationType);
    }

    public static String getNameWithoutPrefix(String prefixedName, Language language,
            ClassificationType classificationType) {
        String prefix = ClassificationSeries.getNamePrefix(language, classificationType);
        if (prefixedName.matches("^" + prefix + ".*")) {
            return prefixedName.substring(prefix.length());
        } else {
            return prefixedName;
        }
    }

    public String getNamePrefix(Language language) {
        return getNamePrefix(language, classificationType);
    }

    public static String getNamePrefix(Language language, ClassificationType classificationType) {
        if (classificationType == ClassificationType.CODELIST) {
            return getKodelistePrefix(language);
        } else {
            return getKlassifikasjonPrefix(language);
        }
    }

    public static String getKlassifikasjonPrefix(Language language) {
        switch (language) {
        case NB:
            return PREFIX_CLASSIFICATION_NB;
        case NN:
            return PREFIX_CLASSIFICATION_NN;
        case EN:
            return PREFIX_CLASSIFICATION_EN;
        default:
            return "";
        }
    }

    public static String getKodelistePrefix(Language language) {
        switch (language) {
        case NB:
            return PREFIX_CODELIST_NB;
        case NN:
            return PREFIX_CODELIST_NN;
        case EN:
            return PREFIX_CODELIST_EN;
        default:
            return "";
        }
    }

    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public void setDeleted() {
        // Soft delete (where classification is only marked as deleted and not actually deleted from database) causes
        // problems with unique constraint on classification name. To ensure that a deleted classification will not
        // block further use of a classification name, a timestamp is added to the name of the deleted classification
        Date now = TimeUtil.now();
        for (Language language : Language.getAllSupportedLanguages()) {
            String name = getName(language);
            if (!Strings.isNullOrEmpty(name)) {
                setName(getName(language) + " :: " + now.getTime(), language);
            }
        }
        deleted = true;
    }

    public void updateMigratedFrom(MigratedFrom migratedFrom, Long migratedFromId) {
        this.migratedFrom = migratedFrom;
        this.migratedFromId = migratedFromId;
    }

}
