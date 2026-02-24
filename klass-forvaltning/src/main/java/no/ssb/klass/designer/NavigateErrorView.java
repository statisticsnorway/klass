package no.ssb.klass.designer;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import no.ssb.klass.designer.util.VaadinUtil;

@SuppressWarnings("serial")
public class NavigateErrorView extends VerticalLayout implements ErrorView {
    private static final String WRONG_URL_TEXT = "Den siden som du prøver å navigere til finnes ikke.";
    private static final String WRONG_URL_BUTTON_TEXT = "Gå til hovedside";
    private static final String WRONG_URL_URL = ClassificationFamilyView.NAME;

    public NavigateErrorView() {
        VerticalLayout layout = new VerticalLayout();
        Label label = new Label(WRONG_URL_TEXT);
        label.setStyleName("v-label-bold");
        Button button = new Button(WRONG_URL_BUTTON_TEXT);
        layout.addComponent(label);
        layout.addComponent(button);
        layout.setMargin(true);
        addComponent(layout);
        button.addClickListener(okEvent -> VaadinUtil.navigateTo(WRONG_URL_URL));
    }

    @Override
    public void enter(ViewChangeEvent event) {

    }

}
