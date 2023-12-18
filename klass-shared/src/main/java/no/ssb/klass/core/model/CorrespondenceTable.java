package no.ssb.klass.core.model;

import static com.google.common.base.Preconditions.*;
import static java.util.stream.Collectors.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import com.google.common.collect.Lists;

import no.ssb.klass.core.util.DateRange;
import no.ssb.klass.core.util.TimeUtil;
import no.ssb.klass.core.util.Translatable;
import no.ssb.klass.core.util.TranslatablePersistenceConverter;

@Entity
@Table(indexes = { @Index(columnList = "source_id", name = "ct_source_idx"),
        @Index(columnList = "target_id", name = "ct_target_idx") })
public class CorrespondenceTable extends BaseEntity implements ClassificationEntityOperations, Publishable, Draftable {
    @Lob
    @Column(columnDefinition = "text", nullable = false)
    @Convert(converter = TranslatablePersistenceConverter.class)
    private Translatable description;
    private Published published;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private ClassificationVersion source;
    private int sourceLevelNumber;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private ClassificationVersion target;
    private int targetLevelNumber;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "correspondenceTable")
    private List<CorrespondenceMap> correspondenceMaps;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "correspondencetable_changelog", joinColumns = @JoinColumn(name = "correspondencetable_id"), inverseJoinColumns = @JoinColumn(name = "changelog_id"))
    private List<Changelog> changelogs;
    @Column(nullable = false)
    private boolean deleted;
    @Column(nullable = false)
    private boolean draft;

    protected CorrespondenceTable() {
    }

    /**
     * Creates a CorrespondenceTable
     *
     * @param description
     * @param source
     * @param sourceLevelNumber
     *                          0 means all levels from source version
     * @param target
     * @param targetLevelNumber
     *                          0 means all levels from target version
     */
    public CorrespondenceTable(Translatable description, ClassificationVersion source, int sourceLevelNumber,
            ClassificationVersion target, int targetLevelNumber) {
        this.description = checkNotNull(description);
        this.published = Published.none();
        this.source = checkNotNull(source);
        checkArgument(sourceLevelNumber == 0 || source.hasLevel(sourceLevelNumber),
                "Source version does not have level: " + sourceLevelNumber);
        this.sourceLevelNumber = sourceLevelNumber;
        this.target = checkNotNull(target);
        checkArgument(targetLevelNumber == 0 || target.hasLevel(targetLevelNumber),
                "Target version does not have level: " + targetLevelNumber);
        this.targetLevelNumber = targetLevelNumber;
        this.correspondenceMaps = new ArrayList<>();
        this.changelogs = new ArrayList<>();
        verifyDateRangesAreOverlaping();
        this.deleted = false;
        this.draft = false;
    }

    public String getName(Language language) {
        return buildName(source.getName(language), target.getName(language));
    }

    private String buildName(String sourceName, String targetName) {
        if (isDraft()) {
            return sourceName + " - " + targetName + " (utkast)";
        } else {
            return sourceName + " - " + targetName;
        }
    }

    @Override
    public String getNameInPrimaryLanguage() {
        return buildName(source.getNameInPrimaryLanguage(), target.getNameInPrimaryLanguage());
    }

    public String getDescription(Language language) {
        return description.getString(language);
    }

    public void setDescription(String value, Language language) {
        description = description.withLanguage(value, language);
    }

    public List<Changelog> getChangelogs() {
        return changelogs;
    }

    public void addChangelog(Changelog changelog) {
        checkNotNull(changelog);
        if (!isPublishedInAnyLanguage()) {
            throw new IllegalStateException("Adding changelog when not published");
        }
        changelogs.add(changelog);
    }

    @Override
    public boolean isPublished(Language language) {
        return published.isPublished(language);
    }

    @Override
    public void publish(Language language) {
        this.published = published.publish(language);
    }

    @Override
    public void unpublish(Language language) {
        this.published = published.unpublish(language);
    }

    public ClassificationVersion getSource() {
        return source;
    }

    public Optional<Level> getSourceLevel() {
        return getLevel(source, sourceLevelNumber);
    }

    public ClassificationVersion getTarget() {
        return target;
    }

    public Optional<Level> getTargetLevel() {
        return getLevel(target, targetLevelNumber);
    }

    private Optional<Level> getLevel(ClassificationVersion version, int levelNumber) {
        if (levelNumber == 0) {
            return Optional.empty();
        }
        if (!version.hasLevel(levelNumber)) {
            // May happen if level has been deleted in source or target version
            return Optional.empty();
        }
        return Optional.of(version.getLevel(levelNumber));
    }

    public List<CorrespondenceMap> getCorrespondenceMaps() {
        return correspondenceMaps.stream().sorted().collect(toList());
    }

    public void removeAllCorrespondenceMaps() {
        correspondenceMaps.clear();
    }

    public void addCorrespondenceMap(CorrespondenceMap map) {
        checkNotNull(map);
        verifyNotAlreadyContainsIdenticalMap(map);
        correspondenceMaps.add(map);
        map.setCorrespondenceTable(this);
    }

    public void updateCorrespondenceMapSource(CorrespondenceMap map, ClassificationItem newSource) {
        verifyNotAlreadyContainsIdenticalMap(new CorrespondenceMap(newSource, map.getTarget().orElse(null)));
        map.setSource(newSource);
    }

    public void updateCorrespondenceMapTarget(CorrespondenceMap map, ClassificationItem newTarget) {
        verifyNotAlreadyContainsIdenticalMap(new CorrespondenceMap(map.getSource().orElse(null), newTarget));
        map.setTarget(newTarget);
    }

    private void verifyNotAlreadyContainsIdenticalMap(CorrespondenceMap map) {
        if (alreadyContainsIdenticalMap(map)) {
            throw new IllegalArgumentException("Correspondence table already contains identical map " + map);
        }
    }

    /**
     * Checks if already contains a correspondenceMap with identical source and
     * target mapping.
     *
     * @param map
     * @return true if already contains a correspondenceMap with identical source
     *         and target mapping, false otherwise
     */
    public boolean alreadyContainsIdenticalMap(CorrespondenceMap map) {
        return correspondenceMaps.stream().filter(m -> m.hasSameSourceAndTarget(map)).findAny().map(present -> true)
                .orElse(false);
    }

    public void removeCorrespondenceMap(CorrespondenceMap map) {
        correspondenceMaps.remove(map);
    }

    /**
     * Gets latest from time for source and target classification versions
     */
    public LocalDate getOccured() {
        return TimeUtil.max(Lists.newArrayList(getSource().getDateRange().getFrom(), getTarget().getDateRange()
                .getFrom()));
    }

    public DateRange getDateRange() {
        verifyDateRangesAreOverlaping();
        LocalDate from = TimeUtil.max(Lists.newArrayList(source.getDateRange().getFrom(), target.getDateRange()
                .getFrom()));
        LocalDate to = TimeUtil.min(Lists.newArrayList(source.getDateRange().getTo(), target.getDateRange().getTo()));
        return DateRange.create(from, to);
    }

    private void verifyDateRangesAreOverlaping() {
        if (!hasSameClassification()) {
            if (!source.getDateRange().overlaps(target.getDateRange())) {
                throw new IllegalArgumentException("CorrespondenceTable: " + getNameInPrimaryLanguage()
                        + ". dateRanges of source and target classification version does not overlap. Source: " + source
                                .getNameInPrimaryLanguage()
                        + " " + source.getDateRange() + ". Target: " + target
                                .getNameInPrimaryLanguage()
                        + " " + target.getDateRange());
            }
        }
    }

    private boolean hasSameClassification() {
        return source.getClassification().equals(target.getClassification());
    }

    public boolean isChangeTable() {
        return hasSameClassification();
    }

    @Override
    public Language getPrimaryLanguage() {
        return getSource().getPrimaryLanguage();
    }

    @Override
    public User getContactPerson() {
        return getSource().getClassification().getContactPerson();
    }

    @Override
    public boolean isPublishedInAnyLanguage() {
        return published.isPublishedInAnyLanguage();
    }

    @Override
    public String getCategoryName() {
        return "Korrespondansetabell";
    }

    @Override
    public ClassificationSeries getOwnerClassification() {
        return getSource().getOwnerClassification();
    }

    @Override
    public String canPublish(Language language) {
        StringBuilder fieldList = new StringBuilder();
        if (isDraft()) {
            fieldList.append("Du kan ikke publisere en korrespondansetabell som er markert som utkast");
        }
        if (!source.isPublished(language)) {
            fieldList.append("Publiser kilde versjon '").append(source.getName(language)).append("'");
        }
        if (!target.isPublished(language)) {
            fieldList.append("Publiser mål versjon '").append(target.getName(language)).append("'");
        }
        return fieldList.toString();
    }

    @Override
    public void setDeleted() {
        deleted = true;
    }

    @Override
    public boolean isDeleted() {
        return deleted;
    }

    public boolean isThisOrSourceOrTargetDeleted() {
        return this.isDeleted() || source.isDeleted() || target.isDeleted();
    }

    @Override
    public boolean isDraft() {
        return draft;
    }

    public void setDraft(boolean draft) {
        this.draft = draft;
    }
}
