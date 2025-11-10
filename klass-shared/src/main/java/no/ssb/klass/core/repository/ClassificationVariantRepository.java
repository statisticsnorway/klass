package no.ssb.klass.core.repository;

import no.ssb.klass.core.model.ClassificationVariant;
import no.ssb.klass.core.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassificationVariantRepository
        extends JpaRepository<ClassificationVariant, Long> {

    List<ClassificationVariant> findByContactPerson(User contactPerson);

    @Modifying
    @Query(
            "update ClassificationVariant set contactPerson = :newUser where contactPerson = :oldUser and deleted = false")
    void updateUser(@Param("oldUser") User oldUser, @Param("newUser") User newUser);
}
