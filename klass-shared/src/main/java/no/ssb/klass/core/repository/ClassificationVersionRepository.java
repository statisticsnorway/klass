package no.ssb.klass.core.repository;

import no.ssb.klass.core.model.ClassificationVersion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassificationVersionRepository
        extends JpaRepository<ClassificationVersion, Long> {}
