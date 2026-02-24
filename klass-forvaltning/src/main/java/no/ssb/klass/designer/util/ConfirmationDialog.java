package no.ssb.klass.designer.util;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import no.ssb.klass.designer.components.common.layouts.HorizontalSpacedLayout;
import no.ssb.klass.designer.components.common.layouts.VerticalSpacedLayout;

@SuppressWarnings("serial")
public class ConfirmationDialog extends Window implements Button.ClickListener {
    private Button ok;
    private Button cancel;
    private Callback callback;

    public ConfirmationDialog(String okText, String text, Callback callback) {
        this("Bekreft", okText, "Avbryt", callback, text);
    }

    public ConfirmationDialog(String heading, String okText, String cancelText, Callback callback, String text) {
        super(heading);
        this.callback = callback;
        center();
        setModal(true);

        Label l = new Label(text);
        l.setContentMode(ContentMode.HTML);

        ok = new Button(okText, this);
        cancel = new Button(cancelText, this);
        cancel.addStyleName(ValoTheme.BUTTON_PRIMARY);
        HorizontalLayout buttons = new HorizontalSpacedLayout(cancel, ok);

        VerticalLayout content = new VerticalSpacedLayout(l, buttons);
        content.setSizeFull();
        content.setMargin(true);
        content.setComponentAlignment(buttons, Alignment.MIDDLE_RIGHT);
        content.setExpandRatio(l, 1);

        setHeight("200px");
        setWidth("350px");
        setContent(content);
    }

    @Override
    public void buttonClick(ClickEvent event) {
        close();
        callback.onDialogResult(event.getSource() == ok);
    }

    @FunctionalInterface
    public interface Callback {
        void onDialogResult(boolean answerYes);
    }
}
