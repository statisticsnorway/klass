package no.ssb.klass.core.repository;

import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.model.CorrespondenceTable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface CorrespondenceTableRepository extends JpaRepository<CorrespondenceTable, Long> {
    List<CorrespondenceTable> findBySource(ClassificationVersion source);

    List<CorrespondenceTable> findByTarget(ClassificationVersion target);

    List<CorrespondenceTable> findBySourceInAndTargetIn(
            List<ClassificationVersion> sourceVersions, List<ClassificationVersion> targetVersions);

    /**
     * Loads the given correspondence tables together with their maps and the classification items
     * each map points at.
     *
     * <p>Reading them one table at a time costs three queries per table, which dominates endpoints
     * that walk every change table of a classification. Standard for kommuneinndeling has 142
     * versions, so {@code /changes?from=1838-01-01} visits 141 tables: 423 round trips, against a
     * handful when they are fetched together.
     *
     * <p>The items' levels are fetched too. {@code ClassificationItem.level} is an eager
     * {@code @ManyToOne}, so leaving it out simply moves the round trips rather than removing them:
     * one per distinct level, and each drags in the owning classification and its contact person.
     */
    @Query(
            """
            select distinct correspondenceTable from CorrespondenceTable correspondenceTable
            left join fetch correspondenceTable.correspondenceMaps correspondenceMap
            left join fetch correspondenceMap.source source
            left join fetch source.level
            left join fetch correspondenceMap.target target
            left join fetch target.level
            where correspondenceTable.id in :ids
            """)
    List<CorrespondenceTable> findAllByIdWithMaps(@Param("ids") Collection<Long> ids);
}
