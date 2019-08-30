package no.ssb.klass.designer.admin.tabs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.google.common.base.Strings;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

import no.ssb.klass.core.ldap.ActiveDirectoryService;
import no.ssb.klass.core.model.User;
import no.ssb.klass.core.service.AdminService;
import no.ssb.klass.core.service.UserService;
import no.ssb.klass.designer.util.ConfirmationDialog;

/**
 * @author Mads Lundemo, SSB.
 */
public class OwnershipTab extends OwnershipTabDesign {

    private static final Logger log = LoggerFactory.getLogger(OwnershipTab.class);

    @Autowired
    private ActiveDirectoryService activeDirectoryService;
    @Autowired
    private UserService userService;
    @Autowired
    private AdminService adminService;

    public OwnershipTab() {
        transferUsers.addClickListener(e -> transferUsers());
    }

    private void transferUsers() {
        String newContactPersonName = newContactPerson.getValue().trim();
        String oldContactPersonName = oldContactPerson.getValue().trim();
        if (Strings.isNullOrEmpty(oldContactPersonName)) {
            Notification.show("Nåværende kontaktperson må fylles ut", Notification.Type.WARNING_MESSAGE);
            return;
        }
        if (Strings.isNullOrEmpty(newContactPersonName)) {
            Notification.show("Ny kontaktperson må fylles ut", Notification.Type.WARNING_MESSAGE);
            return;
        }
        if (newContactPersonName.equals(oldContactPersonName)) {
            Notification.show("Ny kontaktperson er lik nåværende kontaktperson", Notification.Type.WARNING_MESSAGE);
            return;
        }
        User newContactPersonUser = getOrCreateUser(newContactPersonName);
        if (newContactPersonUser == null) {
            Notification.show(newContactPersonName + " finnes ikke", Notification.Type.WARNING_MESSAGE);
            return;
        }
        User oldContactPersonUser = userService.getUserByUserName(oldContactPersonName);
        if (oldContactPersonUser == null) {
            Notification.show(oldContactPersonName + " finnes ikke", Notification.Type.WARNING_MESSAGE);
            return;
        }
        int numberOfClassifications = adminService.getNumberOfClassificationForUser(oldContactPersonUser);
        if (numberOfClassifications == 0) {
            Notification.show("Fant ingen klassifikasjoner for " + oldContactPersonName,
                    Notification.Type.WARNING_MESSAGE);
            return;
        }
        StringBuilder confirmationText = new StringBuilder("Er du sikker på at du ønsker å overføre ");
        confirmationText.append(numberOfClassifications).append(" kodeverk fra ");
        confirmationText.append(oldContactPersonUser.getFullname()).append(" (").append(oldContactPersonName).append(
                ") til ");
        confirmationText.append(newContactPersonUser.getFullname()).append(" (").append(newContactPersonName).append(
                ")?");
        ConfirmationDialog confWindow = new ConfirmationDialog("Overfør kodeverk", "Ja, Overfør", "Nei, avbryt", (
                answerYes) -> {
            if (answerYes) {
                adminService.updateUser(oldContactPersonUser, newContactPersonUser);
            }
        }, confirmationText.toString());
        UI.getCurrent().addWindow(confWindow);
    }

    private User getOrCreateUser(String newContactPersonName) {
        User newContactPersonUser = userService.getUserByUserName(newContactPersonName);
        if (newContactPersonUser == null) {
            try {
                return activeDirectoryService.createAndSaveNewUser(newContactPersonName);
            } catch (UsernameNotFoundException e) {
                log.warn("User " + newContactPersonName + " not found in AD.");
                return null;
            }
        }
        return newContactPersonUser;
    }
}
