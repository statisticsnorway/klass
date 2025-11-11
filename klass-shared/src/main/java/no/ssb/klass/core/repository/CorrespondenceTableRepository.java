package no.ssb.klass.core.repository;

import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.model.CorrespondenceTable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CorrespondenceTableRepository extends JpaRepository<CorrespondenceTable, Long> {
    List<CorrespondenceTable> findBySource(ClassificationVersion source);

    List<CorrespondenceTable> findByTarget(ClassificationVersion target);

    List<CorrespondenceTable> findBySourceInAndTargetIn(
            List<ClassificationVersion> sourceVersions, List<ClassificationVersion> targetVersions);
}
