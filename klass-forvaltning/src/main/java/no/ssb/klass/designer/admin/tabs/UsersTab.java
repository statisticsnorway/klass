package no.ssb.klass.designer.admin.tabs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;

import no.ssb.klass.core.ldap.ActiveDirectoryService;
import no.ssb.klass.core.ldap.KlassUserDetails;
import no.ssb.klass.core.model.User;
import no.ssb.klass.core.service.UserService;

/**
 * @author Mads Lundemo, SSB.
 */
public class UsersTab extends UsersTabDesign {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private ActiveDirectoryService activeDirectoryService;
    @Autowired
    private UserService userService;

    public UsersTab() {
        fetchButton.addClickListener(this::fetchUserFromAd);
    }

    private void fetchUserFromAd(Button.ClickEvent clickEvent) {
        String userIdent = fetchTextField.getValue().trim();
        if (userIdent.isEmpty()) {
            Notification.show("", "Vennligst fyll inn brukerens initialer", Notification.Type.WARNING_MESSAGE);
            return;
        }

        User existingUser = userService.getUserByUserName(userIdent);
        if (existingUser != null) {
            Notification.show("Brukeren " + existingUser.getFullname() + " (" + userIdent + ") finnes fra før");
            return;
        }

        UserDetails userDetails;
        try {
            userDetails = userDetailsService.loadUserByUsername(userIdent);
        } catch (UsernameNotFoundException notFoundException) {
            Notification.show("", "Ingen bruker med ident " + userIdent + " funnet", Notification.Type.WARNING_MESSAGE);
            return;
        }

        User newUser = activeDirectoryService.createAndSaveNewUser((KlassUserDetails) userDetails);
        if (newUser != null) {
            Notification.show("Brukeren '" + newUser.getFullname() + "'  er nå opprettet");
        } else {
            Notification.show("Auda!", "Her ser det ut som noe har gått feil", Notification.Type.WARNING_MESSAGE);
            return;
        }

    }

}
