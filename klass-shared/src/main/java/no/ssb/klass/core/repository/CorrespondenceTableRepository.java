package no.ssb.klass.core.repository;

import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.model.CorrespondenceTable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CorrespondenceTableRepository extends JpaRepository<CorrespondenceTable, Long> {
    List<CorrespondenceTable> findBySource(ClassificationVersion source);

    List<CorrespondenceTable> findByTarget(ClassificationVersion target);

    List<CorrespondenceTable> findBySourceInAndTargetIn(
            List<ClassificationVersion> sourceVersions, List<ClassificationVersion> targetVersions);

    @Query("SELECT ct FROM CorrespondenceTable ct " +
            "JOIN FETCH ct.source s " +
            "JOIN FETCH s.levels " +
            "JOIN FETCH ct.target t " +
            "JOIN FETCH t.levels " +
            "LEFT JOIN FETCH ct.correspondenceMaps " +
            "LEFT JOIN FETCH ct.changelogs " +
            "WHERE ct.id = :id AND ct.deleted = false")
    Optional<CorrespondenceTable> findFullyInitializedById(@Param("id") Long id);
}
