package no.ssb.klass.core.repository;

import no.ssb.klass.core.model.ClassificationItem;
import no.ssb.klass.core.model.ReferencingClassificationItem;
import no.ssb.klass.core.model.StatisticalClassification;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

@org.springframework.stereotype.Repository
public interface ReferencingClassificationItemRepository
        extends Repository<ReferencingClassificationItem, Long> {

    /**
     * Finds ReferencingClassificationItems that references a specific classificationItem
     *
     * @param classificationItem classificationItem to match
     * @param deleted flag to indicate if we want to look for soft deleted items (only deleted /
     *     only not deleted)
     * @return list of ReferencingClassificationItem referencing input classificationItem
     */
    @Query(
            "from no.ssb.klass.core.model.ReferencingClassificationItem item "
                    + "where item.reference = :item and item.level.statisticalClassification.deleted = :deleted")
    List<ReferencingClassificationItem> findByReference(
            @Param("item") ClassificationItem classificationItem,
            @Param("deleted") boolean deleted);

    /**
     * Finds ReferencingClassificationItems that references classificationItems in a list
     *
     * @param classificationItems list of classificationItems to match
     * @param deleted flag to indicate if we want to look for soft deleted items (only deleted /
     *     only not deleted)
     * @return list of ReferencingClassificationItem referencing input classificationItem list
     */
    @Query(
            "from no.ssb.klass.core.model.ReferencingClassificationItem item "
                    + "where item.reference in :items and item.level.statisticalClassification.deleted = :deleted")
    List<ReferencingClassificationItem> findByReferenceInList(
            @Param("items") List<ClassificationItem> classificationItems,
            @Param("deleted") boolean deleted);

    /**
     * Finds all ReferencingClassificationItems that references classificationItems in given
     * StatisticalClassification
     *
     * @param statisticalClassification StatisticalClassification with items to find references for
     * @param deleted flag to indicate if we want to look for soft deleted Referencing items (only
     *     deleted / only not deleted)
     * @return list of ReferencingClassificationItem referencing classificationItems in
     *     StatisticalClassification
     */
    @Query(
            "SELECT item FROM no.ssb.klass.core.model.ReferencingClassificationItem item "
                    + " INNER JOIN item.level level"
                    + " INNER JOIN level.statisticalClassification s"
                    + " WHERE s.deleted = :deleted "
                    + " AND level.statisticalClassification.classificationVersion = :classification")
    List<ReferencingClassificationItem> findItemReferences(
            @Param("classification") StatisticalClassification statisticalClassification,
            @Param("deleted") boolean deleted);
}
