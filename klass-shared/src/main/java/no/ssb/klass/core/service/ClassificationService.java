package no.ssb.klass.core.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import no.ssb.klass.core.exception.KlassMessageException;
import no.ssb.klass.core.model.*;
import no.ssb.klass.core.repository.ClassificationFamilySummary;
import no.ssb.klass.core.service.dto.CodeDto;
import no.ssb.klass.core.service.dto.CorrespondenceDto;
import no.ssb.klass.core.util.DateRange;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClassificationService {

  List<CodeDto> findClassificationCodes(
      Long id, DateRange dateRange, Language language, Boolean includeFuture);

  List<CodeDto> findVariantClassificationCodes(
      Long id, String variantName, Language language, DateRange dateRange, Boolean includeFuture);

  List<CorrespondenceDto> findCorrespondences(
      Long sourceClassificationId,
      Long targetClassificationId,
      DateRange dateRange,
      Language language,
      Boolean includeFuture,
      Boolean inverted);

  Page<ClassificationSeries> findAll(
      boolean includeCodelists, Date changedSince, Pageable pageable);

  Page<ClassificationSeries> findAllPublic(
      boolean includeCodelists, Date changedSince, Pageable pageable);

  /**
   * Save ClassificationSeries. Note that the classification is not indexed for search. Front end
   * (Vaadin) code shall use {@link no.ssb.klass.designer.service.ClassificationFacade} which has
   * methods for saving and indexing.
   *
   * @param classification classification to save
   * @return updated classification after saving
   */
  ClassificationSeries saveNotIndexClassification(ClassificationSeries classification);

  /**
   * Save ClassificationSeries. The classification is indexed for search, and this happens
   * synchronously within same thread. This shall not be used from Front end (Vaadin) code, since
   * user must wait for indexing to happen. Front end (Vaadin) code shall use {@link
   * no.ssb.klass.designer.service.ClassificationFacade} which has methods for saving and indexing.
   *
   * <p>This is expected mostly/only to be used by integration tests
   *
   * @param classification classification to save and index for search
   * @return updated classification after saving
   */
  ClassificationSeries saveAndIndexClassification(ClassificationSeries classification);

  /**
   * Save ClassificationVersion. Note that the version is not indexed for search. Front end (Vaadin)
   * code shall use {@link no.ssb.klass.designer.service.ClassificationFacade} which has methods for
   * saving and indexing.
   *
   * @param version version to save
   * @return updated version after saving
   */
  ClassificationVersion saveNotIndexVersion(ClassificationVersion version);

  /**
   * Save ClassificationVariant. Note that the variant is not indexed for search. Front end (Vaadin)
   * code shall use {@link no.ssb.klass.designer.service.ClassificationFacade} which has methods for
   * saving and indexing.
   *
   * @param variant variant to save
   * @return updated variant after saving
   */
  ClassificationVariant saveNotIndexVariant(ClassificationVariant variant);

  /**
   * Save CorrespondenceTable. Note that the correspondenceTable is not indexed for search. Front
   * end (Vaadin) code shall use {@link no.ssb.klass.designer.service.ClassificationFacade} which
   * has methods for saving and indexing.
   *
   * @param correspondenceTable correspondenceTable to save
   * @return updated correspondenceTable after saving
   */
  CorrespondenceTable saveNotIndexCorrespondenceTable(CorrespondenceTable correspondenceTable);

  Optional<ClassificationSeries> findOneClassificationSeriesWithName(
      String name, Language language);

  /**
   * Gets a ClassificationSeries
   *
   * @param id
   * @return ClassificationSeries, never null
   * @throws no.ssb.klass.core.util.KlassResourceNotFoundException if no ClassificationSeries found
   *     with id
   */
  ClassificationSeries getClassificationSeries(Long id);

  /**
   * Gets a ClassificationSeries which has initialized ClassificationVersions and also the versions
   * ClassificationVariants and CorrespondenceTables are initilized.
   *
   * @param id
   * @return ClassificationSeries, never null
   * @throws no.ssb.klass.core.util.KlassResourceNotFoundException if no ClassificationSeries found
   *     with id
   */
  ClassificationSeries getClassificationSeriesFullyInitialized(Long id);

  /**
   * Gets a ClassificationVersion
   *
   * @param id
   * @return ClassificationVersion, never null
   * @throws no.ssb.klass.core.util.KlassResourceNotFoundException if no ClassificationVersion found
   *     with id
   */
  ClassificationVersion getClassificationVersion(Long id);

  /**
   * Gets a ClassificationVariant
   *
   * @param id
   * @return ClassificationVariant, never null
   * @throws no.ssb.klass.core.util.KlassResourceNotFoundException if no ClassificationVariant found
   *     with id
   */
  ClassificationVariant getClassificationVariant(Long id);

  /**
   * Gets a CorrespondenceTable
   *
   * @param id
   * @return CorrespondenceTable, never null
   * @throws no.ssb.klass.core.util.KlassResourceNotFoundException if no CorrespondenceTable found
   *     with id
   */
  CorrespondenceTable getCorrespondenceTable(long id);

  /**
   * Gets a ClassificationFamily
   *
   * @param id
   * @return ClassificationFamily, never null
   * @throws no.ssb.klass.core.util.KlassResourceNotFoundException if no ClassificationFamily found
   *     with id
   */
  ClassificationFamily getClassificationFamily(long id);

  List<ClassificationSeries> findAllClassificationSeries();

  List<ClassificationFamily> findAllClassificationFamilies();

  List<StatisticalUnit> findAllStatisticalUnits();

  List<StatisticalUnit> findClassificationStatisticalUnit(StatisticalUnit statUnit);

  /**
   * Find all classificationFamilySummaries
   *
   * @param section null means all sections
   * @param classificationType null means all classificationTypes
   * @return classificationFamilySummaries
   */
  List<ClassificationFamilySummary> findAllClassificationFamilySummaries(
      String section, ClassificationType classificationType);

  List<ClassificationFamilySummary> findPublicClassificationFamilySummaries(
      String section, ClassificationType classificationType);

  ClassificationFamily saveClassificationFamily(ClassificationFamily classificationFamily);

  StatisticalUnit saveStatisticalUnit(StatisticalUnit unit);

  StatisticalUnit createAndSaveNewStatisticalUnit(StatisticalUnit unitDetails);

  ClassificationFamily findClassificationFamily(String name);

  /**
   * Finds all sections that is responsible for at least one classification.
   *
   * @return set of classifications responsible sections
   */
  Set<String> findAllResponsibleSections();

  /**
   * Finds all sections that is responsible for at least one classification with at least one
   * version published in at least one language.
   *
   * <p>(Intended for use with Klass WEB/JS to avoid confusion as only published data is shown ).
   *
   * @return set of classifications responsible sections
   */
  Set<String> findResponsibleSectionsWithPublishedVersions();

  /**
   * Delete ClassificationSeries. Note that the classification is not removed from search index.
   * Front end (Vaadin) code shall use {@link no.ssb.klass.designer.service.ClassificationFacade}
   * which has methods for deleting and removing from index.
   *
   * <p>Also classification is not actually deleted from database, but only marked as deleted.
   *
   * @param classification classification to delete
   */
  void deleteNotIndexClassification(User currentUser, ClassificationSeries classification)
      throws KlassMessageException;

  /**
   * Delete ClassificationVariant. Note that the variant is not removed from search index. Front end
   * (Vaadin) code shall use {@link no.ssb.klass.designer.service.ClassificationFacade} which has
   * methods for deleting and removing from index.
   *
   * <p>Also variant is not actually deleted from database, but only marked as deleted.
   *
   * @param variant variant to delete
   */
  void deleteNotIndexVariant(User user, ClassificationVariant variant) throws KlassMessageException;

  /**
   * Delete ClassificationVersion. Note that the version is not removed from search index. Front end
   * (Vaadin) code shall use {@link no.ssb.klass.designer.service.ClassificationFacade} which has
   * methods for deleting and removing from index.
   *
   * <p>Also version is not actually deleted from database, but only marked as deleted.
   *
   * @param version version to delete
   */
  void deleteNotIndexVersion(User user, ClassificationVersion version) throws KlassMessageException;

  /**
   * Delete CorrespondenceTable. Note that the correspondenceTable is not removed from search index.
   * Front end (Vaadin) code shall use {@link no.ssb.klass.designer.service.ClassificationFacade}
   * which has methods for deleting and removing from index.
   *
   * <p>Also correspondenceTable is not actually deleted from database, but only marked as deleted.
   *
   * @param correspondenceTable correspondenceTable to delete
   */
  void deleteNotIndexCorrespondenceTable(User user, CorrespondenceTable correspondenceTable)
      throws KlassMessageException;

  void deleteStatisticalUnit(StatisticalUnit stat);

  ClassificationVersion copyClassificationVersion(
      ClassificationVersion originalVersion, DateRange dateRange);

  /** return number of ClassificationSeries */
  long countClassifications();

  /** return number of ClassificationSeries that has been migrated from a particular source */
  long countClassifications(MigratedFrom migratedFrom);

  /**
   * Finds classificationItems or correspondenceMaps that references the input classificationItem. A
   * ClassificationVariant may reference classificationItems in its base ClassificationVersion.
   * Similarly a CorrespondenceTable will reference classificationItems in target and source
   * ClassificationVersion.
   *
   * @param classificationItem classificationItem to check if is referenced, null not allowed
   * @return names for ClassificationVariants and CorrespondenceTables that refer to
   *     classificationItem
   */
  Set<String> findReferencesOfClassificationItem(ClassificationItem classificationItem);

  Set<String> findReferencesOfClassificationItems(
      StatisticalClassification statisticalClassification);

  List<CorrespondenceTable> findCorrespondenceTablesWithSource(ClassificationVersion source);

  List<CorrespondenceTable> findCorrespondenceTablesWithTarget(ClassificationVersion target);

  List<CorrespondenceTable> findPublicCorrespondenceTablesWithTarget(ClassificationVersion version);

  List<ClassificationSeries> findClassificationSeriesByContactPerson(User contactPerson);

  List<ClassificationVariant> findVariantsByContactPerson(User contactPerson);

  /**
   * Finds all correspondenceTables between two specified classification versions and corresponding
   * levels.
   *
   * @return all correspondenceTables between specified versions (and levels)
   */
  List<CorrespondenceTable> findCorrespondenceTablesBetween(
      ClassificationVersion version1, Level level1, ClassificationVersion version2, Level level2);

  String resolveSectionName(String sectionCode, Language language);
}
