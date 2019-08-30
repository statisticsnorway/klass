package no.ssb.klass.designer.admin.tabs;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.vaadin.ui.Button;

import no.ssb.klass.core.ldap.ActiveDirectoryService;
import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.ClassificationVariant;
import no.ssb.klass.core.model.User;
import no.ssb.klass.core.service.ClassificationService;
import no.ssb.klass.core.service.UserService;

/**
 * @author Mads Lundemo, SSB.
 */
public class SectionsTab extends SectionsTabDesign {

    @Autowired
    private ActiveDirectoryService activeDirectoryService;
    @Autowired
    private UserService userService;

    @Autowired
    private ClassificationService classificationService;

    private StringBuffer mainLog;
    private StringBuffer changeLog;
    private StringBuffer notFoundLog;
    private long total;
    private int updated;
    private int ignored;
    private int notFound;

    public SectionsTab() {
        updateButton.addClickListener(this::updateButtonClick);
        logButton.addClickListener(this::logButtonClick);
        logTextArea.setVisible(false);
    }

    public void init() {
        reset();
        total = userService.countUsersWithClassifications();
        usersTotalLabel.setValue(String.valueOf(total));
    }

    private void logButtonClick(Button.ClickEvent clickEvent) {
        logTextArea.setVisible(!logTextArea.isVisible());
        if (logTextArea.isVisible()) {
            logButton.setCaption("Skjul detaljert log");
        } else {
            logButton.setCaption("Vis detaljert log");
        }
    }

    private void updateButtonClick(Button.ClickEvent clickEvent) {
        reset();
        List<User> allContactPersons = userService.getUsersWithClassifications();
        total = allContactPersons.size();
        usersTotalLabel.setValue(String.valueOf(total));

        allContactPersons.stream().forEach(this::updateInfoForUser);
        usersUpdatedLabel.setValue(String.valueOf(updated));
        usersUnchangedLabel.setValue(String.valueOf(ignored));
        usersNotFoundLabel.setValue(String.valueOf(notFound));

        writeSummaryToLog();

        mainLog.append(changeLog.toString()).append('\n');
        mainLog.append(notFoundLog.toString()).append('\n');
        logTextArea.setValue(mainLog.toString());
    }

    private void writeSummaryToLog() {
        mainLog.append("Totalt ").append(total).append(" kontaktpersoner funnet i klass").append('\n');
        mainLog.append(updated).append(" av ").append(total).append(" ble oppdatert").append('\n');
        mainLog.append(ignored).append(" av ").append(total).append(" forble uendret").append('\n');
        mainLog.append(notFound).append(" av ").append(total).append(" ble ikke funnet").append('\n');
        mainLog.append("-------").append('\n');
    }

    private void reset() {
        usersTotalLabel.setValue("?");
        usersUpdatedLabel.setValue("-");
        usersUnchangedLabel.setValue("-");
        usersNotFoundLabel.setValue("-");
        logTextArea.setValue("");

        updated = 0;
        ignored = 0;
        notFound = 0;
        mainLog = new StringBuffer();
        changeLog = new StringBuffer();
        notFoundLog = new StringBuffer();
    }

    private void updateInfoForUser(User user) {
        try {
            List<String> changes = activeDirectoryService.syncActiveDirectory(user);
            if (!changes.isEmpty()) {
                logUpdatedUser(user, changes);
                updated++;
            } else {
                ignored++;
            }
        } catch (UsernameNotFoundException e) {
            notFound++;
            logMissingUser(user);
        }
    }

    private void logUpdatedUser(User user, List<String> changes) {
        changeLog.append("Oppdatert informasjon for ")
                .append(user.getUsername())
                .append(" (").append(user.getFullname()).append(')');

        changes.forEach(s -> changeLog.append("\t\n - ").append(s));
        changeLog.append('\n').append('\n');
    }

    private void logMissingUser(User user) {
        notFoundLog.append("Kontaktperson ikke funnet i AD ")
                .append(user.getUsername())
                .append(" (").append(user.getFullname()).append(')')
                .append(" (Seksjon ").append(user.getSection()).append(')')
                .append('\n');

        List<ClassificationSeries> classificationSeries = classificationService.findClassificationSeriesByContactPerson(
                user);
        List<ClassificationVariant> variants = classificationService.findVariantsByContactPerson(user);

        notFoundLog.append("[Denne brukeren er kontaktperson for ")
                .append(classificationSeries.size()).append(" kodeverk og ")
                .append(variants.size()).append(" varianter").append("]").append('\n');

        classificationSeries.stream().forEach(series -> notFoundLog.append("\t\t")
                .append(series.getNameInPrimaryLanguage()).append('\n'));

        variants.stream().forEach(variant -> notFoundLog.append("\t\t")
                .append(variant.getNameInPrimaryLanguage()).append('\n'));
        notFoundLog.append('\n');
    }

}
