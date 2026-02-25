package no.ssb.klass.designer.admin.tabs;

import com.vaadin.ui.Button;
import no.ssb.klass.core.service.ClassificationService;
import no.ssb.klass.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Mads Lundemo, SSB.
 */
public class SectionsTab extends SectionsTabDesign {

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
        // Disable the update button
        // Functionality no longer available after transitioning away from LDAP
        updateButton.setEnabled(false);

        logButton.addClickListener(this::logButtonClick);
        logTextArea.setVisible(false);
    }

    // user service also null
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

}
