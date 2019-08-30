package no.ssb.klass.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import no.ssb.klass.core.model.ClassificationVariant;
import no.ssb.klass.core.model.User;

@Repository
public interface ClassificationVariantRepository extends JpaRepository<ClassificationVariant, Long> {

    List<ClassificationVariant> findByContactPerson(User contactPerson);
}
