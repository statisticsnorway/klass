package no.ssb.klass.designer.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import no.ssb.klass.core.model.Changelog;
import no.ssb.klass.core.model.ClassificationFamily;
import no.ssb.klass.core.model.ClassificationItem;
import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.ClassificationType;
import no.ssb.klass.core.model.ClassificationVariant;
import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.model.CorrespondenceTable;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.model.Level;
import no.ssb.klass.core.model.StatisticalClassification;
import no.ssb.klass.core.model.StatisticalUnit;
import no.ssb.klass.core.model.User;
import no.ssb.klass.core.repository.ClassificationFamilySummary;
import no.ssb.klass.core.service.ChangeLogService;
import no.ssb.klass.core.service.ClassificationService;
import no.ssb.klass.core.service.KlassMessageException;
import no.ssb.klass.core.service.SearchService;
import no.ssb.klass.core.service.SubscriberService;
import no.ssb.klass.core.util.DateRange;
import no.ssb.klass.core.util.KlassResourceNotFoundException;
import no.ssb.klass.designer.exceptions.ParameterException;

/**
 * Facade to be used for front end (vaadin) code. Mostly a wrapper around ClassificationService, but provides among
 * others a place to put front end specific error messages.
 */
@Service
public class ClassificationFacade {
    private final ClassificationService classificationService;
    private final SearchService searchService;
    private final SubscriberService subscriberService;
    private final ChangeLogService changeLogService;

    @Autowired
    public ClassificationFacade(ClassificationService classificationService, SearchService searchService,
            SubscriberService subscriberService, ChangeLogService changeLogService) {
        this.classificationService = classificationService;
        this.searchService = searchService;
        this.subscriberService = subscriberService;
        this.changeLogService = changeLogService;
    }

    public ClassificationFamily getRequiredClassificationFamily(Long familyId) {
        ClassificationFamily classificationFamily = classificationService.getClassificationFamily(familyId);
        if (classificationFamily == null) {
            throw new ParameterException("Klassifikasjon", "ClassificationFamily ID", familyId != null ? familyId
                    .toString() : "ingen verdi");
        }
        return classificationFamily;
    }
    public ClassificationVersion getRequiredClassificationVersion(Long versionId) {
        ClassificationVersion classificationVersion;
        try {
            classificationVersion = classificationService.getClassificationVersion(versionId);
        } catch (KlassResourceNotFoundException e) {
            throw new ParameterException("Versjon", "ClassificationVersion ID", versionId != null ? versionId.toString()
                    : "null");
        }
        return classificationVersion;
    }

    public ClassificationSeries getRequiredClassificationSeries(Long classificationId) {
        ClassificationSeries classificationSeries;
        try {
            classificationSeries = classificationService.getClassificationSeries(classificationId);
        } catch (KlassResourceNotFoundException e) {
            throw new ParameterException("Klassifikasjon", "ClassificationSeries ID", classificationId != null
                    ? classificationId.toString() : "null");
        }
        return classificationSeries;
    }

    public ClassificationVariant getRequiredClassificationVariant(Long variantId) {
        ClassificationVariant classificationVariant;
        try {
            classificationVariant = classificationService.getClassificationVariant(variantId);
        } catch (KlassResourceNotFoundException e) {
            throw new ParameterException("Variant", "ClassificationVariant ID", variantId != null ? variantId.toString()
                    : "null");
        }
        return classificationVariant;
    }

    public CorrespondenceTable getRequiredCorrespondenceTable(Long correspondenceId) {
        CorrespondenceTable correspondenceTable;
        try {
            correspondenceTable = classificationService.getCorrespondenceTable(correspondenceId);
        } catch (KlassResourceNotFoundException e) {
            throw new ParameterException("Korrespondansetabell", "CorrespondenceTable ID", correspondenceId != null
                    ? correspondenceId.toString() : "null");
        }
        return correspondenceTable;
    }

    public StatisticalUnit saveStatisticalUnit(StatisticalUnit stat) {
        return classificationService.saveStatisticalUnit(stat);
    }
    /*
     * Must not run in transaction, since saving and indexing must be performed in separate transactions
     */
    @Transactional(propagation = Propagation.NEVER)
    public ClassificationSeries saveAndIndexClassification(ClassificationSeries classification) {
        classificationService.saveNotIndexClassification(classification);
        searchService.indexAsync(classification.getId());
        subscriberService.informSubscribersOfUpdatedClassification(classification,
                "Endring i metadata for klassifikasjonen: " + classification.getNameInPrimaryLanguage(),
                "Oppdatert metadata for klassifikasjonen");
        return classification;
    }

    /*
     * Must not run in transaction, since saving and indexing must be performed in separate transactions
     */
    @Transactional(propagation = Propagation.NEVER)
    public CorrespondenceTable saveAndIndexCorrespondenceTable(CorrespondenceTable correspondenceTable,
            InformSubscribers informSubscribers) {
        classificationService.saveNotIndexCorrespondenceTable(correspondenceTable);
        searchService.indexAsync(correspondenceTable.getOwnerClassification().getId());
        if (informSubscribers.isInformSubscribers()) {
            subscriberService.informSubscribersOfUpdatedClassification(correspondenceTable.getOwnerClassification(),
                    "Endring i korrespondansetabellen: " + correspondenceTable.getNameInPrimaryLanguage(),
                    informSubscribers.getDescriptionOfChange());
        }
        return correspondenceTable;
    }

    /*
     * Must not run in transaction, since saving and indexing must be performed in separate transactions
     */
    @Transactional(propagation = Propagation.NEVER)
    public ClassificationVariant saveAndIndexVariant(ClassificationVariant variant,
            InformSubscribers informSubscribers) {
        classificationService.saveNotIndexVariant(variant);
        searchService.indexAsync(variant.getOwnerClassification().getId());
        if (informSubscribers.isInformSubscribers()) {
            subscriberService.informSubscribersOfUpdatedClassification(variant.getOwnerClassification(),
                    "Endring i varianten: " + variant.getNameInPrimaryLanguage(), informSubscribers
                            .getDescriptionOfChange());
        }
        return variant;
    }

    /*
     * Must not run in transaction, since saving and indexing must be performed in separate transactions
     */
    @Transactional(propagation = Propagation.NEVER)
    public ClassificationVersion saveAndIndexVersion(ClassificationVersion version,
            InformSubscribers informSubscribers) {
        classificationService.saveNotIndexVersion(version);
        searchService.indexAsync(version.getOwnerClassification().getId());
        if (informSubscribers.isInformSubscribers()) {
            subscriberService.informSubscribersOfUpdatedClassification(version.getOwnerClassification(),
                    "Endring i versjonen: " + version.getNameInPrimaryLanguage(), informSubscribers
                            .getDescriptionOfChange());
        }
        return version;
    }

    public ClassificationVersion copyClassificationVersion(ClassificationVersion originalVersion, DateRange dateRange) {
        return classificationService.copyClassificationVersion(originalVersion, dateRange);
    }

    /*
     * Must not run in transaction, since deleting and indexing must be performed in separate transactions
     */
    @Transactional(propagation = Propagation.NEVER)
    public void deleteAndIndexClassification(User currentUser, ClassificationSeries classification)
            throws KlassMessageException {
        classificationService.deleteNotIndexClassification(currentUser, classification);
        searchService.indexAsync(classification.getId());
        subscriberService.informSubscribersOfUpdatedClassification(classification, "Klassifikasjonen er slettet: "
                + classification.getNameInPrimaryLanguage(), "Klassifikasjonen er slettet");
    }

    /*
     * Must not run in transaction, since saving and indexing must be performed in separate transactions
     */
    @Transactional(propagation = Propagation.NEVER)
    public void deleteAndIndexCorrespondenceTable(User currentUser,
            CorrespondenceTable correspondenceTable) throws KlassMessageException {
        classificationService.deleteNotIndexCorrespondenceTable(currentUser, correspondenceTable);
        searchService.indexAsync(correspondenceTable.getOwnerClassification().getId());
        if (correspondenceTable.isPublishedInAnyLanguage()) {
            subscriberService.informSubscribersOfUpdatedClassification(correspondenceTable.getOwnerClassification(),
                    "Korrespondansetabellen er slettet: " + correspondenceTable.getNameInPrimaryLanguage(),
                    "En korrespondansetabell er slettet");
        }
    }

    /*
     * Must not run in transaction, since saving and indexing must be performed in separate transactions
     */
    @Transactional(propagation = Propagation.NEVER)
    public void deleteAndIndexVariant(User currentUser, ClassificationVariant variant)
            throws KlassMessageException {
        classificationService.deleteNotIndexVariant(currentUser, variant);
        searchService.indexAsync(variant.getOwnerClassification().getId());
        if (variant.isPublishedInAnyLanguage()) {
            subscriberService.informSubscribersOfUpdatedClassification(variant.getOwnerClassification(),
                    "Varianten er slettet: " + variant.getNameInPrimaryLanguage(), "En variant er slettet");
        }
    }

    /*
     * Must not run in transaction, since saving and indexing must be performed in separate transactions
     */
    @Transactional(propagation = Propagation.NEVER)
    public void deleteAndIndexVersion(User currentUser, ClassificationVersion version) throws KlassMessageException {
        classificationService.deleteNotIndexVersion(currentUser, version);
        searchService.indexAsync(version.getOwnerClassification().getId());
        if (version.isPublishedInAnyLanguage()) {
            subscriberService.informSubscribersOfUpdatedClassification(version.getOwnerClassification(),
                    "Versjonen er slettet: " + version.getNameInPrimaryLanguage(), "En versjon er slettet");
        }
    }

    public void deleteStatisticalUnit(StatisticalUnit statisticalUnit) {
        classificationService.deleteStatisticalUnit(statisticalUnit);
    }

    public StatisticalUnit createAndSaveNewStatisticalUnit(StatisticalUnit statisticalUnit) {
        return classificationService.createAndSaveNewStatisticalUnit(statisticalUnit);
    }

    public List<ClassificationFamily> findAllClassificationFamilies() {
        return classificationService.findAllClassificationFamilies();
    }

    public List<ClassificationFamilySummary> findAllClassificationFamilySummaries(String section,
            ClassificationType classificationType) {
        return classificationService.findAllClassificationFamilySummaries(section, classificationType);
    }

    public List<ClassificationSeries> findAllClassificationSeries() {
        return classificationService.findAllClassificationSeries();
    }

    public Set<String> findAllResponsibleSections() {
        return classificationService.findAllResponsibleSections();
    }

    public List<StatisticalUnit> findAllStatisticalUnits() {
        return classificationService.findAllStatisticalUnits();
    }


    public List<StatisticalUnit> findClassificationStatisticalUnits(StatisticalUnit statUnit) {
            return classificationService.findClassificationStatisticalUnit(statUnit);
    }
    public List<CorrespondenceTable> findCorrespondenceTablesBetween(ClassificationVersion version1, Level level1,
            ClassificationVersion version2, Level level2) {
        return classificationService.findCorrespondenceTablesBetween(version1, level1, version2, level2);
    }

    public List<CorrespondenceTable> findCorrespondenceTablesWithSource(ClassificationVersion version) {
        return classificationService.findCorrespondenceTablesWithSource(version);
    }

    public List<CorrespondenceTable> findCorrespondenceTablesWithTarget(ClassificationVersion version) {
        return classificationService.findCorrespondenceTablesWithTarget(version);
    }

    public Optional<ClassificationSeries> findOneClassificationSeriesWithName(String name, Language language) {
        return classificationService.findOneClassificationSeriesWithName(name, language);
    }

    public Set<String> findReferencesOfClassificationItem(ClassificationItem classificationItem) {
        return classificationService.findReferencesOfClassificationItem(classificationItem);
    }

    public Set<String> findReferencesOfClassificationItems(StatisticalClassification statisticalClassification) {
        return classificationService.findReferencesOfClassificationItems(statisticalClassification);
    }

    public ClassificationSeries getClassificationSeriesFullyInitialized(Long id) {
        return classificationService.getClassificationSeriesFullyInitialized(id);
    }
    public List<Changelog> getVersionChangelogs(Long versionId) {
        ClassificationVersion version = getRequiredClassificationVersion(versionId);
        return version.getChangelogs();
    }
    public void deleteChangelog(Changelog changelog) {
        changeLogService.deleteChangelog(changelog);
    }
}
