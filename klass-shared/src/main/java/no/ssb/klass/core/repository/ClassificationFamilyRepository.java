package no.ssb.klass.core.repository;

import no.ssb.klass.core.model.ClassificationFamily;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassificationFamilyRepository extends JpaRepository<ClassificationFamily, Long> {
  ClassificationFamily findByName(String name);
}
