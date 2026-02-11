package no.ssb.klass.core.model;

import static com.google.common.base.Preconditions.*;

import static java.util.stream.Collectors.*;

import com.google.common.base.Strings;

import jakarta.persistence.*;

import no.ssb.klass.core.util.DateRange;
import no.ssb.klass.core.util.DraftUtil;
import no.ssb.klass.core.util.TimeUtil;
import no.ssb.klass.core.util.Translatable;
import no.ssb.klass.core.util.TranslatablePersistenceConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

@Entity
public abstract class StatisticalClassification extends BaseEntity
        implements ClassificationEntityOperations, Publishable, Draftable {
    public static final int FIRST_LEVEL_NUMBER = 1;

    @Column(columnDefinition = "text", nullable = false)
    @Convert(converter = TranslatablePersistenceConverter.class)
    protected Translatable introduction;

    private Published published;

    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            mappedBy = "statisticalClassification",
            fetch = FetchType.EAGER)
    private final List<Level> levels;

    @OneToMany(mappedBy = "source")
    private final List<CorrespondenceTable> correspondenceTables;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "statisticalclassification_changelog",
            joinColumns = @JoinColumn(name = "statisticalclassification_id"),
            inverseJoinColumns = @JoinColumn(name = "changelog_id"))
    private final List<Changelog> changelogs;

    private transient List<ClassificationItem> deletedClassificationItems;

    @Column(nullable = false)
    private boolean deleted;

    protected LocalDate validFrom;
    protected LocalDate validTo;

    // For Hibernate
    protected StatisticalClassification() {
        levels = null;
        correspondenceTables = null;
        changelogs = null;
    }

    protected StatisticalClassification(Translatable introduction) {
        this.introduction = checkNotNull(introduction);
        this.levels = new ArrayList<>();
        this.correspondenceTables = new ArrayList<>();
        this.changelogs = new ArrayList<>();
        this.published = Published.none();
        this.deletedClassificationItems = new ArrayList<>();
        this.deleted = false;
    }

    public boolean hasLevel(int levelNumber) {
        return levels.stream().anyMatch(level -> level.getLevelNumber() == levelNumber);
    }

    public List<Level> getLevels() {
        return levels;
    }

    @Override
    public boolean isPublished(Language language) {
        return published.isPublished(language);
    }

    @Override
    public boolean isPublishedInAnyLanguage() {
        return published.isPublishedInAnyLanguage();
    }

    @Override
    public void publish(Language language) {
        if (isDraft()) {
            throw new RuntimeException("Drafts can not be published, please set validity");
        }
        this.published = published.publish(language);
    }

    @Override
    public void unpublish(Language language) {
        this.published = published.unpublish(language);
    }

    @Override
    public String canPublish(Language language) {
        StringJoiner stringJoiner = new StringJoiner("\n");
        if (isDraft()) {
            stringJoiner.add(" Utkast kan ikke publiseres, vennligst sett gyldighet");
        }
        if (getIntroduction(language).trim().isEmpty()) {
            stringJoiner.add(" Beskrivelse");
        }
        for (ClassificationItem item : getAllClassificationItems()) {
            if (item.getOfficialName(language).isEmpty()) {
                stringJoiner.add(item.getCode());
            }
        }
        // OwnerClassification is null during import
        if (getOwnerClassification() != null
                && getOwnerClassification().getName(language).isEmpty()) {
            stringJoiner
                    .add(" Kodeverket må ha navn på ")
                    .add(language.getDisplayName().toLowerCase())
                    .add(" før en tilhørende versjon kan publiseres med samme språk");
        }
        if (getAllClassificationItems().isEmpty()) {
            stringJoiner.add(" Du må ha opprettet minimum ett element før du kan publisere");
        }
        return stringJoiner.toString();
    }

    public List<CorrespondenceTable> getCorrespondenceTables() {
        return correspondenceTables.stream()
                .filter(correspondenceTable -> !correspondenceTable.isThisOrSourceOrTargetDeleted())
                .collect(toList());
    }

    public List<CorrespondenceTable> getPublicCorrespondenceTables() {
        return correspondenceTables.stream()
                .filter(correspondenceTable -> !correspondenceTable.isThisOrSourceOrTargetDeleted())
                .filter(correspondenceTable -> !correspondenceTable.isDraft())
                .filter(correspondenceTable -> !correspondenceTable.getSource().isDraft())
                .filter(correspondenceTable -> !correspondenceTable.getTarget().isDraft())
                .filter(CorrespondenceTable::isPublishedInAnyLanguage)
                .collect(toList());
    }

    public void addCorrespondenceTable(CorrespondenceTable correspondenceTable) {
        checkNotNull(correspondenceTable);
        correspondenceTables.add(correspondenceTable);
    }

    public List<CorrespondenceTable> getCorrespondenceTablesWithTarget(
            ClassificationSeries classification) {
        checkNotNull(classification);
        List<CorrespondenceTable> tables = new ArrayList<>();
        for (CorrespondenceTable correspondenceTable : getCorrespondenceTables()) {
            if (correspondenceTable.getTarget().getClassification().equals(classification)) {
                tables.add(correspondenceTable);
            }
        }
        return tables;
    }

    public List<CorrespondenceTable> getCorrespondenceTablesWithTargetVersion(
            ClassificationVersion version) {
        checkNotNull(version);
        List<CorrespondenceTable> tables = new ArrayList<>();
        for (CorrespondenceTable correspondenceTable : getCorrespondenceTables()) {
            if (correspondenceTable.getTarget().equals(version)) {
                tables.add(correspondenceTable);
            }
        }
        return tables;
    }

    public List<Changelog> getChangelogs() {
        return changelogs;
    }

    public void addChangelog(Changelog changelog) {
        checkNotNull(changelog);
        changelogs.add(changelog);
    }

    public void deleteChangelog(Changelog changelog) {
        checkNotNull(changelog);
        changelogs.remove(changelog);
    }

    public String getIntroduction(Language language) {
        return introduction.getString(language);
    }

    public void setIntroduction(String value, Language language) {
        introduction = introduction.withLanguage(value, language);
    }

    public List<ClassificationItem> getAllClassificationItems() {
        return levels.stream()
                .flatMap(level -> level.getClassificationItems().stream())
                .sorted()
                .collect(toList());
    }

    // for the sake of find item performance
    private List<ClassificationItem> getAllClassificationItemsNoSort() {
        return levels.parallelStream()
                .flatMap(level -> level.getClassificationItems().stream())
                .collect(toList());
    }

    /**
     * Gets classificationItems for level 1 first, then classificationItems for level 2, and so on
     */
    public List<ClassificationItem> getAllClassificationItemsLevelForLevel() {
        List<ClassificationItem> items = new ArrayList<>();
        Optional<Level> level = getFirstLevel();
        while (level.isPresent()) {
            items.addAll(level.get().getClassificationItems().stream().sorted().collect(toList()));
            level = getNextLevel(level.get());
        }
        return items;
    }

    public ClassificationItem findItem(String code) {
        checkNotNull(code);
        return getAllClassificationItemsNoSort().stream()
                .filter(item -> item.getCode().equals(code))
                .findFirst()
                .orElseThrow(
                        () ->
                                new IllegalArgumentException(
                                        "No item found with code: "
                                                + code
                                                + "in StatisticalClassification "
                                                + getId()));
    }

    public void addLevel(Level newLevel) {
        checkNotNull(newLevel);
        if (levels.stream()
                .anyMatch(level -> level.getLevelNumber() == newLevel.getLevelNumber())) {
            throw new IllegalArgumentException(
                    "A level is already present with level number: " + newLevel.getLevelNumber());
        }
        levels.add(newLevel);
        newLevel.setStatisticalClassification(this);
    }

    @Override
    public abstract Language getPrimaryLanguage();

    public Level addNextLevel() {
        Level level = new Level(getNextLevelNumber());
        addLevel(level);
        return level;
    }

    /** Finds level with levelNumber == 1 */
    public Optional<Level> getFirstLevel() {
        return levels.stream().filter(l -> l.getLevelNumber() == FIRST_LEVEL_NUMBER).findFirst();
    }

    /** Finds level with levelNumber == level.levelNumber + 1 */
    public Optional<Level> getNextLevel(Level level) {
        int nextLevelNumber = level.getLevelNumber() + 1;
        return levels.stream().filter(l -> l.getLevelNumber() == nextLevelNumber).findFirst();
    }

    /** Checks if level has highest levelNumber */
    public boolean isLastLevel(Level level) {
        return level.getLevelNumber() == getLastLevelNumber();
    }

    public Optional<Level> getLastLevel() {
        int lastLevelNumber = getLastLevelNumber();
        if (lastLevelNumber > 0) {
            return Optional.of(getLevel(lastLevelNumber));
        }
        return Optional.empty();
    }

    /**
     * Deletes a level. Level must be empty, i.e. not have any classificationItems
     *
     * @param level level to delete
     */
    public void deleteLevel(Level level) {
        checkArgument(
                level.getClassificationItems().isEmpty(),
                "Can not remove level that has classification items, delete classification items first");
        levels.remove(level);
    }

    /** Checks if first level */
    public boolean isFirstLevel(Level level) {
        return level.getLevelNumber() == FIRST_LEVEL_NUMBER;
    }

    public boolean hasClassificationItem(String code) {
        return getAllClassificationItemsNoSort().stream()
                .anyMatch(item -> item.getCode().equals(code));
    }

    public Optional<ClassificationItem> getClassificationItem(String code) {
        return getAllClassificationItemsNoSort().stream()
                .filter(item -> item.getCode().equals(code))
                .findFirst();
    }

    public void addClassificationItem(
            ClassificationItem classificationItem, int levelNumber, ClassificationItem parent) {
        if (hasClassificationItem(classificationItem.getCode())) {
            throw new IllegalArgumentException(
                    "ClassificationItem already exist with code: " + classificationItem.getCode());
        }

        if (parent == null && levelNumber != FIRST_LEVEL_NUMBER) {
            throw new IllegalArgumentException(
                    "ClassificationItem: "
                            + classificationItem.getCode()
                            + " with no parent must be at level "
                            + FIRST_LEVEL_NUMBER
                            + ", was level "
                            + levelNumber);
        }

        if (parent != null && levelNumber == FIRST_LEVEL_NUMBER) {
            throw new IllegalArgumentException(
                    "ClassificationItem with parent can not be at first level");
        }
        if (parent != null && Objects.equals(classificationItem.getCode(), parent.getCode())) {
            throw new IllegalArgumentException(
                    "ClassificationItem and parent can not have the same code");
        }

        classificationItem.setParent(parent);

        getLevel(levelNumber).addClassificationItem(classificationItem);
    }

    /**
     * Delete classificationItem and all its children
     *
     * @param classificationItem
     */
    public void deleteClassificationItem(ClassificationItem classificationItem) {
        deletedClassificationItems.add(classificationItem);
        for (ClassificationItem child : getChildrenOfClassificationItem(classificationItem)) {
            deleteClassificationItem(child);
        }
        classificationItem.getLevel().removeClassificationItem(classificationItem);
    }

    public List<ClassificationItem> getChildrenOfClassificationItem(
            ClassificationItem classificationItem) {
        checkNotNull(classificationItem);
        Optional<Level> nextLevel = getNextLevel(classificationItem.getLevel());
        if (nextLevel.isPresent()) {
            return nextLevel.get().getClassificationItems().stream()
                    .filter(item -> item.getParent().equals(classificationItem))
                    .collect(toList());
        }
        return Collections.emptyList();
    }

    public List<ClassificationItem> getDeletedClassificationItems() {
        return deletedClassificationItems;
    }

    public void clearDeletedClassificationItems() {
        deletedClassificationItems.clear();
    }

    public Level getLevel(int levelNumber) {
        return levels.stream()
                .filter(l -> l.getLevelNumber() == levelNumber)
                .findAny()
                .orElseThrow(
                        () ->
                                new IllegalArgumentException(
                                        "No level with levelNumber: "
                                                + levelNumber
                                                + ", in: "
                                                + getNameInPrimaryLanguage()));
    }

    public abstract boolean isIncludeShortName();

    public abstract boolean isIncludeNotes();

    private int getNextLevelNumber() {
        return getLastLevelNumber() + 1;
    }

    public int getLastLevelNumber() {
        return levels.stream().mapToInt(Level::getLevelNumber).max().orElse(0);
    }

    /**
     * @return only concrete classificationItems
     */
    public List<ConcreteClassificationItem> getAllConcreteClassificationItems() {
        List<ConcreteClassificationItem> concreteClassificationItems = new ArrayList<>();
        for (ClassificationItem item : getAllClassificationItems()) {
            if (!item.isReference()) {
                concreteClassificationItems.add((ConcreteClassificationItem) item);
            }
        }
        return concreteClassificationItems;
    }

    /**
     * Checks if any concrete classificationItem has translation of officialName for given language.
     *
     * @param language
     * @return true if has any translation for language, false otherwise
     */
    public boolean hasTranslations(Language language) {
        for (ClassificationItem item : getAllConcreteClassificationItems()) {
            if (!Strings.isNullOrEmpty(item.getOfficialName(language))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void init() {
        deletedClassificationItems = new ArrayList<>();
    }

    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public void setDeleted() {
        this.deleted = true;
    }

    public DateRange getDateRange() {
        return DateRange.create(validFrom, validTo);
    }

    public void setDateRange(DateRange dateRange) {
        checkNotNull(dateRange);
        this.validFrom = checkNotNull(dateRange.getFrom());
        this.validTo = checkNotNull(dateRange.getTo());
    }

    public boolean isDraft() {
        return DraftUtil.isDraft(validFrom, validTo);
    }

    public static String getDatePostfix(LocalDate validFrom, LocalDate validTo) {
        if (DraftUtil.isDraft(validFrom, validTo)) {
            return "(utkast)";
        }
        String pattern = "yyyy-MM";
        if (isFromDateStartOfYear(validFrom) && !sameYear(validFrom, validTo)) {
            pattern = "yyyy";
        }
        DateTimeFormatter formatedDate = DateTimeFormatter.ofPattern(pattern);
        return validFrom.format(formatedDate);
    }

    private static boolean sameYear(LocalDate fromDate, LocalDate toDate) {
        return fromDate.getYear() == toDate.getYear();
    }

    private static boolean isFromDateStartOfYear(LocalDate fromDate) {
        return TimeUtil.isMinDate(fromDate) || fromDate.getDayOfYear() == 1;
    }

    public Boolean isFutureVersion() {
        return validFrom.isAfter(LocalDate.now());
    }

    public Boolean showVersion(Boolean paramIncludeFuture) {
        if (isFutureVersion() && !paramIncludeFuture) {
            return false;
        }
        return true;
    }

    public Boolean isCurrentVersion() {
        return !isDraft()
                && (LocalDate.now().isEqual(validFrom) || LocalDate.now().isAfter(validFrom))
                && (LocalDate.now().isBefore(validTo));
    }
}
