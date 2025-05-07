package no.ssb.klass.core.service;

import com.google.common.collect.Lists;
import no.ssb.klass.core.model.ClassificationEntityOperations;
import no.ssb.klass.core.model.ClassificationFamily;
import no.ssb.klass.core.model.ClassificationItem;
import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.ClassificationType;
import no.ssb.klass.core.model.ClassificationVariant;
import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.model.CorrespondenceMap;
import no.ssb.klass.core.model.CorrespondenceTable;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.model.Level;
import no.ssb.klass.core.model.MigratedFrom;
import no.ssb.klass.core.model.ReferencingClassificationItem;
import no.ssb.klass.core.model.StatisticalClassification;
import no.ssb.klass.core.model.StatisticalUnit;
import no.ssb.klass.core.model.User;
import no.ssb.klass.core.repository.ClassificationFamilyRepository;
import no.ssb.klass.core.repository.ClassificationFamilySummary;
import no.ssb.klass.core.repository.ClassificationSeriesRepository;
import no.ssb.klass.core.repository.ClassificationSeriesSpecification;
import no.ssb.klass.core.repository.ClassificationVariantRepository;
import no.ssb.klass.core.repository.ClassificationVersionRepository;
import no.ssb.klass.core.repository.CorrespondenceMapRepository;
import no.ssb.klass.core.repository.CorrespondenceTableRepository;
import no.ssb.klass.core.repository.ReferencingClassificationItemRepository;
import no.ssb.klass.core.repository.StatisticalUnitRepository;
import no.ssb.klass.core.repository.UserRepository;
import no.ssb.klass.core.service.dto.CodeDto;
import no.ssb.klass.core.service.dto.CorrespondenceDto;
import no.ssb.klass.core.util.DateRange;
import no.ssb.klass.core.util.KlassResourceNotFoundException;
import no.ssb.klass.core.util.TimeUtil;
import no.ssb.klass.core.util.Translatable;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Service
@Transactional()
public class ClassificationServiceImpl implements ClassificationService {

    private final ClassificationFamilyRepository classificationFamilyRepository;
    private final ClassificationSeriesRepository classificationRepository;
    private final ClassificationVersionRepository classificationVersionRepository;
    private final ClassificationVariantRepository classificationVariantRepository;
    private final CorrespondenceTableRepository correspondenceTableRepository;
    private final ReferencingClassificationItemRepository referencingClassificationItemRepository;
    private final CorrespondenceMapRepository correspondenceMapRepository;
    private final StatisticalUnitRepository statisticalUnitRepository;
    private final SearchService searchService;
    private final UserRepository userRepository;

    @Autowired
    public ClassificationServiceImpl(ClassificationFamilyRepository classificationFamilyRepository,
            ClassificationSeriesRepository classificationRepository,
            ClassificationVersionRepository classificationVersionRepository,
            ClassificationVariantRepository classificationVariantRepository,
            CorrespondenceTableRepository correspondenceTableRepository,
            ReferencingClassificationItemRepository referencingClassificationItemRepository,
            CorrespondenceMapRepository correspondenceMapRepository,
            StatisticalUnitRepository statisticalUnitRepository,
            SearchService searchService, UserRepository userRepository) {
        this.classificationFamilyRepository = classificationFamilyRepository;
        this.classificationRepository = classificationRepository;
        this.classificationVersionRepository = classificationVersionRepository;
        this.classificationVariantRepository = classificationVariantRepository;
        this.correspondenceTableRepository = correspondenceTableRepository;
        this.referencingClassificationItemRepository = referencingClassificationItemRepository;
        this.correspondenceMapRepository = correspondenceMapRepository;
        this.statisticalUnitRepository = statisticalUnitRepository;
        this.searchService = searchService;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClassificationSeries> findAll(boolean includeCodelists, Date changedSince, Pageable pageable) {
        // Note deleted ClassificationSeries are not returned. Ensured by
        // ClassificationSeriesSpecification.
        return classificationRepository.findAll(new ClassificationSeriesSpecification(includeCodelists, changedSince),
                pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClassificationSeries> findAllPublic(boolean includeCodelists, Date changedSince, Pageable pageable) {
        // Note deleted ClassificationSeries are not returned. Ensured by
        // ClassificationSeriesSpecification.
        return classificationRepository.findAll(
                new ClassificationSeriesSpecification(includeCodelists, changedSince, true), pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClassificationSeries> findAllClassificationSeries() {
        return classificationRepository.findAll().stream().filter(classification -> !classification.isDeleted())
                .collect(toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ClassificationSeries getClassificationSeries(Long id) {
        ClassificationSeries classificationSeries = classificationRepository.findById(id).orElseThrow(() -> new
                KlassResourceNotFoundException("Classification not found with id = " + id));
        if (classificationSeries.isDeleted()) {
            throw new KlassResourceNotFoundException("Classification not found with id = " + id);
        }
        Hibernate.initialize(classificationSeries.getClassificationVersions());
        Hibernate.initialize(classificationSeries.getStatisticalUnits());
        return classificationSeries;
    }

    @Override
    @Transactional(readOnly = true)
    public ClassificationSeries getClassificationSeriesFullyInitialized(Long id) {
        ClassificationSeries classification = getClassificationSeries(id);
        for (ClassificationVersion version : classification.getClassificationVersions()) {
            Hibernate.initialize(version.getClassificationVariants());
            Hibernate.initialize(version.getCorrespondenceTables());
        }
        return classification;
    }

    @Override
    @Transactional(readOnly = true)
    public ClassificationVariant getClassificationVariant(Long id) {
        ClassificationVariant variant = classificationVariantRepository.findById(id).orElseThrow(() -> new
                KlassResourceNotFoundException("Classification Variant not found with id = " + id));
        if (variant.isDeleted()) {
            throw new KlassResourceNotFoundException("Classification Variant not found with id = " + id);
        }
        Hibernate.initialize(variant.getLevels());
        Hibernate.initialize(variant.getChangelogs());
        return variant;
    }

    @Override
    @Transactional(readOnly = true)
    public ClassificationVersion getClassificationVersion(Long id) {
        ClassificationVersion version = classificationVersionRepository.findById(id).orElseThrow(() -> new
                KlassResourceNotFoundException("Classification Version not found with id = " + id));
        if (version.isDeleted()) {
            throw new KlassResourceNotFoundException("Classification Version not found with id = " + id);
        }
        Hibernate.initialize(version.getLevels());
        Hibernate.initialize(version.getCorrespondenceTables());
        Hibernate.initialize(version.getClassificationVariants());
        Hibernate.initialize(version.getChangelogs());
        return version;
    }

    @Override
    @Transactional(readOnly = true)
    public CorrespondenceTable getCorrespondenceTable(long id) {
        CorrespondenceTable correspondenceTable = correspondenceTableRepository.findById(id).orElseThrow(() -> new
                KlassResourceNotFoundException("Correspondence Table not found with id = " + id));
        if (correspondenceTable.isThisOrSourceOrTargetDeleted()) {
            throw new KlassResourceNotFoundException("Correspondence Table not found with id = " + id);
        }
        Hibernate.initialize(correspondenceTable.getSource().getLevels());
        Hibernate.initialize(correspondenceTable.getTarget().getLevels());
        Hibernate.initialize(correspondenceTable.getCorrespondenceMaps());
        Hibernate.initialize(correspondenceTable.getChangelogs());
        return correspondenceTable;
    }

    @Override
    @Transactional(readOnly = true)
    public ClassificationFamily getClassificationFamily(long id) {
        ClassificationFamily classificationFamily = classificationFamilyRepository.findById(id).orElseThrow(() -> new
                KlassResourceNotFoundException("ClassificationFamily not found with id = " + id));
        Hibernate.initialize(classificationFamily.getClassificationSeries());
        return classificationFamily;
    }

    @Override
    public ClassificationSeries saveNotIndexClassification(ClassificationSeries classification) {

        classification = classificationRepository.save(classification);
        return classification;
    }

    @Override
    public ClassificationSeries saveAndIndexClassification(ClassificationSeries classification) {
        classification = classificationRepository.save(classification);
        searchService.indexSync(classification);
        return classification;
    }

    @Override
    public ClassificationVersion saveNotIndexVersion(ClassificationVersion version) {
        deleteAnyDeletedReferencesToClassificationItems(version);
        version.clearDeletedClassificationItems();
        updateClassificationLastModified(version.getOwnerClassification().getId());
        return classificationVersionRepository.save(version);
    }

    @Override
    public ClassificationVariant saveNotIndexVariant(ClassificationVariant variant) {
        variant = classificationVariantRepository.save(variant);
        updateClassificationLastModified(variant.getOwnerClassification().getId());
        return variant;
    }

    @Override
    public CorrespondenceTable saveNotIndexCorrespondenceTable(CorrespondenceTable correspondenceTable) {
        correspondenceTable = correspondenceTableRepository.save(correspondenceTable);
        updateClassificationLastModified(correspondenceTable.getOwnerClassification().getId());
        return correspondenceTable;
    }

    private void updateClassificationLastModified(Long ClassificationId) {
        classificationRepository.updateClassificationLastModified(ClassificationId, TimeUtil.now());
    }

    /**
     * Checks if there are any references to the deleted classificationItems. If the
     * references are deleted, e.g.
     * belonging to a deleted ClassificationVersion, the reference is deleted in
     * order to avoid constraint violation.
     */
    private void deleteAnyDeletedReferencesToClassificationItems(ClassificationVersion version) {
        Set<CorrespondenceMap> softDeletedMaps = new HashSet<>();
        List<ReferencingClassificationItem> softDeletedItems = new ArrayList<>();

        List<ClassificationItem> deletedClassificationItems = version.getDeletedClassificationItems()
                .stream().filter(item -> item.getId() != null).collect(toList());

        // Important as SQL query will fail if no items are provided (using where ... IN
        // (collection))
        if (!deletedClassificationItems.isEmpty()) {
            softDeletedMaps.addAll(correspondenceMapRepository.findAllMapsUsingDeletedItems(version,
                    deletedClassificationItems));
            softDeletedItems.addAll(referencingClassificationItemRepository.findByReferenceInList(
                    deletedClassificationItems, true));
        }
        softDeletedMaps.forEach(map -> map.getCorrespondenceTable().removeCorrespondenceMap(map));
        softDeletedItems.forEach(item -> item.getLevel().getStatisticalClassification().deleteClassificationItem(item));
        classificationRepository.flush();
    }

    private Set<CorrespondenceMap> findCorrespondenceMapsReferencing(ClassificationItem classificationItem,
            boolean deleted) {
        return correspondenceMapRepository.findBySourceOrTarget(classificationItem, deleted);
    }

    private List<ReferencingClassificationItem> findClassificationItemsReferencing(
            ClassificationItem classificationItem, boolean deleted) {
        return referencingClassificationItemRepository.findByReference(classificationItem, deleted);
    }

    private List<ReferencingClassificationItem> findClassificationItemsReferencing(
            StatisticalClassification statisticalClassification, boolean deleted) {
        return referencingClassificationItemRepository.findItemReferences(statisticalClassification, deleted);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ClassificationSeries> findOneClassificationSeriesWithName(String name, Language language) {
        ClassificationSeries classification;
        switch (language) {
            case NB:
                classification = classificationRepository.findByNameNoIgnoreCase(name);
                break;
            case NN:
                classification = classificationRepository.findByNameNnIgnoreCase(name);
                break;
            case EN:
                classification = classificationRepository.findByNameEnIgnoreCase(name);
                break;
            default:
                throw new IllegalArgumentException("Language not supported: " + language);
        }

        if (classification != null && classification.isDeleted()) {
            classification = null;
        }
        return Optional.ofNullable(classification);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CorrespondenceDto> findCorrespondences(Long sourceClassificationId, Long targetClassificationId,
            DateRange dateRange, Language language, Boolean includeFuture,
            Boolean inverted) {
        checkNotNull(dateRange);
        checkNotNull(language);
        ClassificationSeries sourceClassification = getClassificationSeries(sourceClassificationId);
        ClassificationSeries targetClassification = getClassificationSeries(targetClassificationId);
        List<CorrespondenceDto> correspondences = ClassificationServiceHelper.findCorrespondences(
                sourceClassification, targetClassification, dateRange, language, includeFuture, inverted);
        if (correspondences.isEmpty()) {
            correspondences = ClassificationServiceHelper.findCorrespondences(
                    targetClassification, sourceClassification, dateRange, language, includeFuture, !inverted);
        }
        if (correspondences.isEmpty())
            throw new KlassResourceNotFoundException(createCorrespondenceNotFoundErrorMessage(
                    sourceClassification, targetClassification));
        return correspondences;
    }

    public static String createCorrespondenceNotFoundErrorMessage(ClassificationSeries sourceClassification,
            ClassificationSeries targetClassification) {
        return "Classification '" + sourceClassification.getName(Language.getDefault())
                + "' has no correspondence table with Classification '" + targetClassification.getName(Language
                        .getDefault())
                + "'";
    }

    @Override
    @Transactional(readOnly = true)
    public List<CodeDto> findVariantClassificationCodes(Long id, String variantName, Language language,
            DateRange dateRange, Boolean includeFuture) {
        checkNotNull(variantName);
        checkNotNull(language);
        checkNotNull(dateRange);
        ClassificationSeries classification = getClassificationSeries(id);
        return ClassificationServiceHelper.findVariantClassificationCodes(classification, variantName, language,
                dateRange, includeFuture);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CodeDto> findClassificationCodes(Long id, DateRange dateRange, Language language,
            Boolean includeFuture) {
        checkNotNull(dateRange);
        checkNotNull(language);
        ClassificationSeries classification = getClassificationSeries(id);
        return ClassificationServiceHelper.findClassificationCodes(classification, dateRange, language, includeFuture);
    }

    private boolean isPublishedInAnyLanguage(ClassificationEntityOperations entity) {
        return entity.isPublishedInAnyLanguage();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClassificationFamily> findAllClassificationFamilies() {
        return classificationFamilyRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StatisticalUnit> findAllStatisticalUnits() {
        return statisticalUnitRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StatisticalUnit> findClassificationStatisticalUnit(StatisticalUnit statUnit) {
        return classificationRepository.findAllClassificationStatisticalUnits().stream().filter(
                statisticalUnit -> statisticalUnit.equals(statUnit)).collect(toList());
    }

    @Override
    public ClassificationFamily saveClassificationFamily(ClassificationFamily classificationFamily) {
        return classificationFamilyRepository.save(classificationFamily);
    }

    @Override
    public StatisticalUnit saveStatisticalUnit(StatisticalUnit unit) {
        return statisticalUnitRepository.save(unit);
    }

    @Override
    public StatisticalUnit createAndSaveNewStatisticalUnit(StatisticalUnit unitDetails) {
        Translatable name = new Translatable(unitDetails.getName(Language.NB), unitDetails.getName(Language.NN),
                unitDetails.getName(Language.EN));
        StatisticalUnit newStatUnit = new StatisticalUnit(name);
        return statisticalUnitRepository.save(newStatUnit);
    }

    @Override
    @Transactional(readOnly = true)
    public ClassificationFamily findClassificationFamily(String name) {
        ClassificationFamily family = classificationFamilyRepository.findByName(name);
        if (family != null) {
            Hibernate.initialize(family.getClassificationSeries());
        }
        return family;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClassificationFamilySummary> findAllClassificationFamilySummaries(String section,
            ClassificationType classificationType) {
        return classificationFamilyRepository.findClassificationFamilySummaries(section, classificationType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClassificationFamilySummary> findPublicClassificationFamilySummaries(
            String section, ClassificationType classificationType) {
        return classificationFamilyRepository.findPublicClassificationFamilySummaries(section, classificationType);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<String> findAllResponsibleSections() {
        return classificationRepository.findAllResponsibleSections();
    }

    @Override
    @Transactional(readOnly = true)
    public Set<String> findResponsibleSectionsWithPublishedVersions() {
        return classificationRepository.findResponsibleSectionsWithPublishedVersions();
    }

    @Override
    public void deleteNotIndexClassification(User user, ClassificationSeries classification)
            throws KlassMessageException {
        classification = reloadToAvoidLazyInitialization(classification);
        user = userRepository.findById(user.getId()).orElseThrow(() ->
                new RuntimeException("User not found")); // get attached object
        checkAllowedToDelete(user, classification);
        classification.setDeleted();
        saveNotIndexClassification(classification);
        user.removeFavorite(classification);
    }

    @Override
    public void deleteNotIndexVariant(User user, ClassificationVariant variant) throws KlassMessageException {
        checkAllowedToDelete(user, variant);
        variant.setDeleted();
        saveNotIndexVariant(variant);
    }

    @Override
    public void deleteStatisticalUnit(StatisticalUnit stat) {
        statisticalUnitRepository.delete(stat);
    }

    @Override
    public void deleteNotIndexVersion(User user, ClassificationVersion version) throws KlassMessageException {
        version = reloadToAvoidLazyInitialization(version);
        checkAllowedToDelete(user, version);
        version.setDeleted();
        saveNotIndexVersion(version);
    }

    @Override
    public void deleteNotIndexCorrespondenceTable(User user, CorrespondenceTable correspondenceTable)
            throws KlassMessageException {
        checkAllowedToDelete(user, correspondenceTable);
        correspondenceTable.setDeleted();
        saveNotIndexCorrespondenceTable(correspondenceTable);
    }

    private ClassificationSeries reloadToAvoidLazyInitialization(ClassificationSeries classification) {
        return getClassificationSeries(classification.getId());
    }

    private ClassificationVersion reloadToAvoidLazyInitialization(ClassificationVersion version) {
        return getClassificationVersion(version.getId());
    }

    private void checkAllowedToDelete(User user, ClassificationEntityOperations entity) throws KlassMessageException {
        User owner = entity.getContactPerson();
        boolean isOwner = user.getUsername().equals(owner.getUsername());
        if (user.isAdministrator() || (isOwner && !isPublishedInAnyLanguage(entity))) {
            return;
        }
        StringBuilder errorMsg = new StringBuilder("Kunne ikke slette ").append(entity.getCategoryName().toLowerCase())
                .append(" \"").append(entity.getNameInPrimaryLanguage().toLowerCase()).append("\", ");
        if (!isOwner) {
            makeNotOwnerErrorMessage(user, entity, owner, errorMsg);
        } else {
            makePublishedErrorMessage(entity, errorMsg);
        }
        throw new KlassMessageException(errorMsg.toString());
    }

    private void makePublishedErrorMessage(ClassificationEntityOperations entity, StringBuilder errorMsg) {
        errorMsg.append("siden den allerede er publisert.\nBare administrator kan slette denne.");
    }

    private void makeNotOwnerErrorMessage(User user, ClassificationEntityOperations entity, User owner,
            StringBuilder errorMsg) {
        errorMsg.append("siden du er ikke eier av den\n");
        errorMsg.append("Bare eier og administrator kan slette denne.");
    }

    @Override
    public ClassificationVersion copyClassificationVersion(ClassificationVersion originalVersion, DateRange dateRange) {
        originalVersion = reloadToAvoidLazyInitialization(originalVersion);

        ClassificationVersion newClassificationVersion = originalVersion.copyClassificationVersion(dateRange);
        originalVersion.getOwnerClassification().addClassificationVersion(newClassificationVersion);
        ClassificationVersion save = classificationVersionRepository.save(newClassificationVersion);
        updateClassificationLastModified(save.getOwnerClassification().getId());
        return save;

    }

    @Override
    @Transactional(readOnly = true)
    public long countClassifications() {
        return classificationRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long countClassifications(MigratedFrom migratedFrom) {
        checkNotNull(migratedFrom);
        return classificationRepository.countByMigratedFrom(migratedFrom);
    }

    @Override
    public Set<String> findReferencesOfClassificationItem(ClassificationItem classificationItem) {
        checkNotNull(classificationItem);
        if (classificationItem.getId() == null) {
            // Not yet saved, hence no references possible
            return new HashSet<>();
        }

        List<ReferencingClassificationItem> items = findClassificationItemsReferencing(classificationItem, false);

        Set<String> result = items.stream().map(item -> item.getLevel().getStatisticalClassification()
                .getNameInPrimaryLanguage()).collect(toSet());

        result.addAll(findCorrespondenceMapsReferencing(classificationItem, false).stream().map(map -> map
                .getCorrespondenceTable().getNameInPrimaryLanguage()).collect(toSet()));

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Set<String> findReferencesOfClassificationItems(StatisticalClassification statisticalClassification) {
        checkNotNull(statisticalClassification);
        if (statisticalClassification.getId() == null) {
            // Not yet saved, hence no references possible
            return new HashSet<>();
        }

        List<ReferencingClassificationItem> items = new LinkedList<>();
        items.addAll(findClassificationItemsReferencing(statisticalClassification, false));

        Set<String> result = items.stream().map(item -> item.getLevel().getStatisticalClassification()
                .getNameInPrimaryLanguage()).collect(toSet());

        Set<CorrespondenceTable> referencingCorrespondenceTables = correspondenceMapRepository
                .findAllMapsUsingItems(statisticalClassification)
                .stream()
                .map(map -> map.getCorrespondenceTable())
                .filter(table -> !table.isThisOrSourceOrTargetDeleted())
                .collect(toSet());

        result.addAll(referencingCorrespondenceTables.stream().map(table -> table.getNameInPrimaryLanguage()
                + " ID=" + table.getId()) // Also returns ID for error printing
                .collect(toSet()));

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CorrespondenceTable> findCorrespondenceTablesWithSource(ClassificationVersion source) {
        return correspondenceTableRepository.findBySource(source).stream().filter(
                correspondenceTable -> !correspondenceTable.isThisOrSourceOrTargetDeleted()).collect(toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CorrespondenceTable> findCorrespondenceTablesWithTarget(ClassificationVersion target) {
        return correspondenceTableRepository.findByTarget(target).stream().filter(
                correspondenceTable -> !correspondenceTable.isThisOrSourceOrTargetDeleted()).collect(toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CorrespondenceTable> findPublicCorrespondenceTablesWithTarget(ClassificationVersion target) {
        return correspondenceTableRepository.findByTarget(target).stream()
                .filter(correspondenceTable -> !correspondenceTable.isThisOrSourceOrTargetDeleted())
                .filter(CorrespondenceTable::isPublishedInAnyLanguage)
                .collect(toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClassificationSeries> findClassificationSeriesByContactPerson(User contactPerson) {
        return classificationRepository.findByContactPerson(contactPerson);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClassificationVariant> findVariantsByContactPerson(User contactPerson) {
        return classificationVariantRepository.findByContactPerson(contactPerson);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CorrespondenceTable> findCorrespondenceTablesBetween(ClassificationVersion version1, Level level1,
            ClassificationVersion version2, Level level2) {
        if (level1 != null) {
            checkArgument(version1.equals(level1.getStatisticalClassification()));
        }
        if (level2 != null) {
            checkArgument(version2.equals(level2.getStatisticalClassification()));
        }
        List<CorrespondenceTable> matchingCorrespondenceTables = new ArrayList<>();
        List<ClassificationVersion> versions = Lists.newArrayList(version1, version2);
        Optional<Level> sourceLevel;
        Optional<Level> targetLevel;
        for (CorrespondenceTable correspondenceTable : correspondenceTableRepository.findBySourceInAndTargetIn(versions,
                versions)) {
            if (correspondenceTable.isThisOrSourceOrTargetDeleted()) {
                continue;
            }
            if (version1.equals(correspondenceTable.getSource())) {
                sourceLevel = Optional.ofNullable(level1);
                targetLevel = Optional.ofNullable(level2);
            } else {
                sourceLevel = Optional.ofNullable(level2);
                targetLevel = Optional.ofNullable(level1);
            }
            if (correspondenceTable.getSourceLevel().equals(sourceLevel) && correspondenceTable
                    .getTargetLevel().equals(targetLevel)) {
                matchingCorrespondenceTables.add(correspondenceTable);
            }

        }
        return matchingCorrespondenceTables;
    }

}
