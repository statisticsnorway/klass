package no.ssb.klass.designer.windows;

import java.util.Optional;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import no.ssb.klass.core.model.Changelog;
import no.ssb.klass.designer.components.common.layouts.HorizontalSpacedLayout;
import no.ssb.klass.designer.components.common.layouts.VerticalSpacedLayout;
import no.ssb.klass.designer.user.UserContext;
import no.ssb.klass.designer.util.KlassTheme;

public class DescriptionOfChangeWindow extends Window {
    private final TextArea textArea;
    private final Callback callback;
    private final UserContext userContext;

    public DescriptionOfChangeWindow(UserContext userContext, Callback callback) {
        super("Beskrivelse av endring");
        this.callback = callback;
        this.userContext = userContext;
        center();
        setModal(true);
        setHeight("400px");
        setWidth("600px");

        Button okButton = new Button("Ok", (event) -> okClicked());
        Label label = new Label("eller");
        Button notRelevantButton = new Button("ikke relevant", (event) -> notRelevantClicked());
        notRelevantButton.setPrimaryStyleName(KlassTheme.ACTION_TEXT_BUTTON);
        notRelevantButton.setDescription(
                "Velg <i>ikke relevant</i> hvis endringen gjelder et ikke publisert språk. Abonnenter vil da ikke bli informert om endringen.");
        HorizontalLayout buttons = new HorizontalSpacedLayout(okButton, label, notRelevantButton);
        buttons.setComponentAlignment(label, Alignment.MIDDLE_CENTER);
        buttons.setComponentAlignment(notRelevantButton, Alignment.MIDDLE_CENTER);

        textArea = new TextArea();
        textArea.setInputPrompt("Skriv inn en beskrivelse av endring.\n"
                + "Velg ikke relevant hvis endringen gjelder et ikke publisert språk. Abonnenter vil da ikke bli informert om endringen.");
        textArea.setSizeFull();
        VerticalLayout layout = new VerticalSpacedLayout(textArea, buttons);
        layout.setMargin(true);
        layout.setExpandRatio(textArea, 1);
        layout.setSizeFull();

        setContent(layout);
    }

    private void notRelevantClicked() {
        finish(Optional.empty());
    }

    private void okClicked() {
        if (textArea.getValue().trim().isEmpty()) {
            Notification.show("Beskrivelse av endring må være fylt ut", Type.ERROR_MESSAGE);
        } else {
            Changelog changelog = new Changelog(userContext.getUsername(), textArea.getValue());
            finish(Optional.of(changelog));
        }
    }

    private void finish(Optional<Changelog> changelog) {
        close();
        callback.onDescriptionEntered(changelog);
    }

    @FunctionalInterface
    public interface Callback {
        void onDescriptionEntered(Optional<Changelog> changelog);
    }
}
