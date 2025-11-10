package no.ssb.klass.core.repository;

import java.math.BigInteger;
import java.util.Optional;
import java.util.Set;
import no.ssb.klass.core.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findOneByUsername(String username);

  Optional<User> findOneByFullname(String fullname);

  // Hibernate does not support UNION and other HQL alternatives might cause
  // performance issues. [Using Native query]
  @Query(
      value =
          "SELECT DISTINCT(u.id) FROM user AS u, classification_series AS c"
              + " WHERE c.deleted = false AND  c.contact_person_id = u.id"
              + " UNION "
              + " select distinct(u.id)"
              + " from user AS u,"
              + "   statistical_classification as variant"
              + "   LEFT join statistical_classification as version on variant.classification_version_id = version.id"
              + "   LEFT join classification_series as classification on version.classification_id = classification.id"
              + " where"
              + "   variant.contact_person_id = u.id"
              + "   AND variant.dtype = 'variant'"
              + "   And classification.deleted = false"
              + "   And variant.deleted = false"
              + "   AND version.deleted = false;",
      nativeQuery = true)
  // Native query returns BigInteger a Set<Long> definition wont change that, only
  // confuse the reader.
  Set<BigInteger> getUserIdsForUsersWithClassifications();
}
