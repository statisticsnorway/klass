package no.ssb.klass.designer;

import java.util.Map;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import no.ssb.klass.designer.util.ParameterUtil;
import no.ssb.klass.designer.util.VaadinUtil;

@SpringView(name = DefaultErrorView.NAME)
@SuppressWarnings("serial")
public class DefaultErrorView extends VerticalLayout implements ErrorView {
    public static final String NAME = "DefaultErrorPage";
    public static final String LABEL_TEXT = "labelText";
    public static final String BUTTON_TEXT = "buttonText";
    public static final String NEXT_PAGE = "nextText";

    private Label errorText;
    private Button buttonText;

    private EscapeClickListener listener;

    public DefaultErrorView() {
        errorText = new Label();
        buttonText = new Button();
        VerticalLayout layout = new VerticalLayout();
        errorText.setStyleName("v-label-bold");
        layout.addComponent(errorText);
        layout.addComponent(buttonText);
        layout.setMargin(true);
        addComponent(layout);
    }

    @Override
    public void enter(ViewChangeEvent event) {
        buttonText.removeClickListener(listener);
        Map<String, String> parameters = ParameterUtil.decodeParameters(event.getParameters());
        errorText.setValue(parameters.get(LABEL_TEXT));
        buttonText.setCaption(parameters.get(BUTTON_TEXT));
        String nextPage = parameters.get(NEXT_PAGE);
        if (nextPage == null) {
            nextPage = "";
        }
        listener = new EscapeClickListener(nextPage);
        buttonText.addClickListener(listener);
    }

    public class EscapeClickListener implements ClickListener {

        private String nextPage;

        public EscapeClickListener(String nextPage) {
            this.nextPage = nextPage;
        }

        @Override
        public void buttonClick(ClickEvent event) {
            VaadinUtil.navigateTo(nextPage);
        }

    }

}
