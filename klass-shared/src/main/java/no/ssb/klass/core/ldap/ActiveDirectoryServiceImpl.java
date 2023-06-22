package no.ssb.klass.core.ldap;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import no.ssb.klass.core.config.ConfigurationProfiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.ldap.search.LdapUserSearch;
import org.springframework.stereotype.Service;

import no.ssb.klass.core.model.User;
import no.ssb.klass.core.service.UserService;

/**
 * @author Mads Lundemo, SSB.
 */
@Service
@Profile(value = { ConfigurationProfiles.FRONTEND_ONLY })
public class ActiveDirectoryServiceImpl implements ActiveDirectoryService {

    @Autowired
    private UserService userService;

    @Autowired
    private LdapUserSearch userSearch;
    @Autowired
    private KlassUserDetailsMapper userDetailsMapper;

    public List<String> syncActiveDirectory(User user) {
        KlassUserDetails userDetails = findUserDetails(user.getUsername().trim());
        List<String> changes = new LinkedList<>();

        if (!Objects.equals(userDetails.getSsbSection(), user.getSection())) {
            changes.add("   Seksjon endret fra " + user.getSection() + " til " + userDetails.getSsbSection());
            user.setSection(userDetails.getSsbSection());
        }

        if (!Objects.equals(userDetails.getDisplayName(), user.getFullname())) {
            changes.add("   Navn endret fra " + user.getFullname() + " til " + userDetails.getDisplayName());
            user.setFullname(userDetails.getDisplayName());
        }

        if (!Objects.equals(userDetails.getEmail(), user.getEmail())) {
            changes.add("   Epost endret fra " + user.getEmail() + " til " + userDetails.getEmail());
            user.setEmail(userDetails.getEmail());
        }

        if (!Objects.equals(userDetails.getPhone(), user.getPhone())) {
            changes.add("   Telefon nr. endret fra " + user.getPhone() + " til " + userDetails.getPhone());
            user.setPhone(userDetails.getPhone());
        }
        if (!changes.isEmpty()) {
            userService.saveUser(user);
        }
        return changes;
    }

    private KlassUserDetails findUserDetails(String username) {
        DirContextOperations contextOperations = userSearch.searchForUser(username);
        return (KlassUserDetails) userDetailsMapper.mapUserFromContext(contextOperations, username,
                Collections.emptyList());
    }

    public User updateAndSaveUser(KlassUserDetails userDetails, User user) {
        user.setRole(userDetails.getRole());
        user.setSection(userDetails.getSsbSection());
        user.setFullname(userDetails.getDisplayName());
        user.setEmail(userDetails.getEmail());
        user.setPhone(userDetails.getPhone());
        return userService.saveUser(user);
    }

    public User createAndSaveNewUser(String username) {
        KlassUserDetails userDetails = findUserDetails(username);
        User newUser = new User(userDetails.getUsername(), userDetails.getDisplayName(), userDetails.getSsbSection());
        newUser.setRole(userDetails.getRole());
        newUser.setEmail(userDetails.getEmail());
        newUser.setPhone(userDetails.getPhone());
        return userService.saveUser(newUser);
    }

    public User createAndSaveNewUser(KlassUserDetails userDetails) {
        User newUser = new User(userDetails.getUsername(), userDetails.getDisplayName(), userDetails.getSsbSection());
        newUser.setRole(userDetails.getRole());
        newUser.setEmail(userDetails.getEmail());
        newUser.setPhone(userDetails.getPhone());
        return userService.saveUser(newUser);
    }

}
