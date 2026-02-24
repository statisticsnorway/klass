package no.ssb.klass.designer.editing.correspondencetable;

import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;

import no.ssb.klass.designer.components.common.layouts.HorizontalSpacedLayout;
import no.ssb.klass.designer.util.KlassTheme;

public class CorrespondenceTableUserTip extends HorizontalSpacedLayout {
    private static final String SHOW_TIP_STYLENAME = "show-tip";
    private static final String USER_TIP_STYLENAME = "user-tip";
    private static final String HIDE_TIP = "Skjul tips";
    private static final String SHOW_TIP = "Vis bruker tips";
    private boolean displayUsertip;
    private final Label tip;
    private Button displayButton;

    public CorrespondenceTableUserTip() {
        displayButton = new Button();
        displayButton.setPrimaryStyleName(KlassTheme.ACTION_TEXT_BUTTON);
        displayButton.addClickListener(event -> displayUsertip(!displayUsertip));
        tip = new Label(FontAwesome.LIGHTBULB_O.getHtml()
                + " <b>Brukertips</b></br>Du kan dra ett eller flere elementer fra den ene tabellen til den andre for å lage en knytning.",
                ContentMode.HTML);
        addComponents(tip, displayButton);
        setExpandRatio(tip, 1);
        setComponentAlignment(displayButton, Alignment.TOP_RIGHT);
        setMargin(true);
        addStyleName(USER_TIP_STYLENAME);
        displayUsertip(false);
    }

    private void displayUsertip(boolean displayUsertip) {
        tip.setVisible(displayUsertip);
        if (displayUsertip) {
            addStyleName(SHOW_TIP_STYLENAME);
            displayButton.setCaption(HIDE_TIP);
        } else {
            removeStyleName(SHOW_TIP_STYLENAME);
            displayButton.setCaption(SHOW_TIP);
        }
        this.displayUsertip = displayUsertip;
    }
}
