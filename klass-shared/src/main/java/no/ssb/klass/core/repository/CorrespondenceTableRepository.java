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
     * <p>Endpoints that walk every change table of a classification otherwise let Hibernate batch
     * fetch the maps a few tables at a time. Fetching them together roughly halves both the query
     * count and the time: measured over 141 tables and 63000 maps, 13 queries and 1257ms became 6
     * queries and 752ms.
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
