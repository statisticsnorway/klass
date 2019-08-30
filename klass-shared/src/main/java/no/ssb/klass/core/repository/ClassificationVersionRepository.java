package no.ssb.klass.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import no.ssb.klass.core.model.ClassificationVersion;

@Repository
public interface ClassificationVersionRepository extends JpaRepository<ClassificationVersion, Long> {
}
