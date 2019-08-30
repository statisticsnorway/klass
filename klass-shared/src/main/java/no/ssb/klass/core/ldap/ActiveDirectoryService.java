package no.ssb.klass.core.ldap;

import java.util.List;

import no.ssb.klass.core.model.User;

/**
 * @author Mads Lundemo, SSB.
 */
public interface ActiveDirectoryService {


    List<String> syncActiveDirectory(User user);

    User updateAndSaveUser(KlassUserDetails userDetails, User user);

    User createAndSaveNewUser(String username);

    User createAndSaveNewUser(KlassUserDetails userDetails);
}
