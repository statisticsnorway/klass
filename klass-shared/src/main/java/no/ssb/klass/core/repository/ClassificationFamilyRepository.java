package no.ssb.klass.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import no.ssb.klass.core.model.ClassificationFamily;

@Repository
public interface ClassificationFamilyRepository extends JpaRepository<ClassificationFamily, Long>,
        ClassificationFamilyRepositoryCustom {
    ClassificationFamily findByName(String name);
}
