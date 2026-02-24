package no.ssb.klass.designer.admin.tabs;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

import no.ssb.klass.core.service.SearchService;
import no.ssb.klass.designer.util.ConfirmationDialog;

/**
 * @author Mads Lundemo, SSB.
 */
public class SearchTab extends SearchTabDesign {

    @Autowired
    private SearchService searchService;

    public SearchTab() {
        reindexBtn.addClickListener(this::reindexClick);
    }

    private void reindexClick(Button.ClickEvent clickEvent) {
        ConfirmationDialog dialog = new ConfirmationDialog(
                "Advarsel",
                "Ja",
                "nei", this::dialogResponse,
                "Dette kan ta lang tid (10-15 min minst) og vil medføre at søket fungerer dårlig i dette tidsrommet,"
                        + " er du sikker på at du ønsker å gjøre dette?"

        );
        UI.getCurrent().addWindow(dialog);

    }

    private void dialogResponse(boolean answerYes) {
        if (answerYes) {
            searchService.reindex();
            Notification.show("Reindeksering kjører nå i bakgrunn");
        }
    }

}
