package no.ssb.klass.core.repository;

import java.util.List;
import java.util.Set;
import no.ssb.klass.core.model.ClassificationItem;
import no.ssb.klass.core.model.CorrespondenceMap;
import no.ssb.klass.core.model.StatisticalClassification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

@org.springframework.stereotype.Repository
public interface CorrespondenceMapRepository extends CrudRepository<CorrespondenceMap, Long> {

  /**
   * Finds CorrespondenceMaps whose source or target references a specific classificationItem
   *
   * @param item classificationItem to match
   * @param deleted flag that decides if deleted or not deleted CorrespondenceMaps are returned
   * @return list of CorrespondenceMaps whose source or target references provided
   *     classificationItem
   */
  @Query(
      "FROM no.ssb.klass.core.model.CorrespondenceMap map "
          + "where (map.source = :item OR map.target = :item)and map.correspondenceTable.deleted = :deleted")
  Set<CorrespondenceMap> findBySourceOrTarget(
      @Param("item") ClassificationItem item, @Param("deleted") boolean deleted);

  /**
   * Finds all CorrespondenceMaps whose source or target references a classificationItem in provided
   * statistical classification
   *
   * @param classification StatisticalClassification with items to match
   * @return list of CorrespondenceMaps whose source or target references one or more
   *     classificationItem in provided statistical classification
   */
  @Query(
      "SELECT map FROM no.ssb.klass.core.model.CorrespondenceMap map "
          + " INNER JOIN map.source source "
          + " INNER JOIN map.target target "
          + " INNER JOIN source.level slevel"
          + " INNER JOIN target.level tlevel"
          + " INNER JOIN map.correspondenceTable table "
          + " WHERE table.deleted = false"
          + " AND (slevel.statisticalClassification = :version  or tlevel.statisticalClassification = :version)")
  Set<CorrespondenceMap> findAllMapsUsingItems(
      @Param("version") StatisticalClassification classification);

  /**
   * Finds all CorrespondenceMaps whose source or target references a deleted classificationItem
   * from provided statistical classification
   *
   * @param classification StatisticalClassification with items to match
   * @param deletedClassificationItems Since deletedItems is a transient collection you will have to
   *     provide it manually here
   * @return list of CorrespondenceMaps whose source or target references one or more
   *     classificationItem in provided statistical classification and is found in list of deleted
   *     items.
   */
  @Query(
      "SELECT map FROM no.ssb.klass.core.model.CorrespondenceMap map "
          + " INNER JOIN map.source source "
          + " INNER JOIN map.target target "
          + " INNER JOIN source.level slevel"
          + " INNER JOIN target.level tlevel"
          + " INNER JOIN map.correspondenceTable table "
          + " WHERE (table.deleted = true OR slevel.statisticalClassification.deleted = true"
          + " OR tlevel.statisticalClassification.deleted = true OR"
          + " slevel.statisticalClassification.classification.deleted = true "
          + " OR tlevel.statisticalClassification.classification.deleted = true)"
          + " AND((slevel.statisticalClassification = :version  AND source in :deletedClassificationItems)"
          + "  OR (tlevel.statisticalClassification = :version  AND target in :deletedClassificationItems))")
  Set<CorrespondenceMap> findAllMapsUsingDeletedItems(
      @Param("version") StatisticalClassification classification,
      @Param("deletedClassificationItems") List<ClassificationItem> deletedClassificationItems);
}
