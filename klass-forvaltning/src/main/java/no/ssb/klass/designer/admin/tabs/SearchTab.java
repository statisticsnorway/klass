package no.ssb.klass.designer.admin.tabs;

import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

import no.ssb.klass.designer.util.ConfirmationDialog;

/**
 * @author Mads Lundemo, SSB.
 * Note: This tab is hidden and search functionality has been removed.
 */
public class SearchTab extends SearchTabDesign {

    public SearchTab() {
        reindexBtn.addClickListener(this::reindexClick);
    }

    private void reindexClick(Button.ClickEvent clickEvent) {
        ConfirmationDialog dialog = new ConfirmationDialog(
                "Advarsel",
                "Ja",
                "nei", this::dialogResponse,
                "Reindeksering er ikke tilgjengelig."
        );
        UI.getCurrent().addWindow(dialog);
    }

    private void dialogResponse(boolean answerYes) {
        if (answerYes) {
            Notification.show("Reindeksering er ikke tilgjengelig");
        }
    }
}
