package no.ssb.klass.core.repository;

import no.ssb.klass.core.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findOneByUsername(String username);

    User findOneByFullname(String fullname);

}
