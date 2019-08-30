package no.ssb.klass.designer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.vaadin.spring.security.shared.VaadinSharedSecurity;

import no.ssb.klass.core.ldap.ActiveDirectoryService;
import no.ssb.klass.core.ldap.KlassUserDetails;
import no.ssb.klass.core.model.User;
import no.ssb.klass.core.service.UserService;
import no.ssb.klass.designer.user.UserContext;

/**
 * @author Mads Lundemo, SSB.
 */
@Service
public class KlassLoginService {

    @Autowired
    private UserService userService;
    @Autowired
    private ActiveDirectoryService activeDirectoryService;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private VaadinSharedSecurity vaadinSharedSecurity;

    public User login(String username, String password, boolean rememberMe) throws Exception {
        vaadinSharedSecurity.login(username, password, rememberMe);
        User user = getUserFromAuthentication(vaadinSharedSecurity.getAuthentication());
        getUserContext().setUser(user);
        return user;

    }

    public User getUserFromAuthentication(Authentication authentication) {
        KlassUserDetails userDetails = (KlassUserDetails) authentication.getPrincipal();
        User user = findLoggedInUser(userDetails);
        if (user == null) {
            return activeDirectoryService.createAndSaveNewUser(userDetails);
        }
        return activeDirectoryService.updateAndSaveUser(userDetails, user);
    }

    private User findLoggedInUser(KlassUserDetails userDetails) {
        return userService.getUserByUserName(userDetails.getUsername());
    }

    /**
     * getter for session bean (can not and should not be autowired)
     */
    private UserContext getUserContext() {
        return applicationContext.getBean(UserContext.class);

    }
}
