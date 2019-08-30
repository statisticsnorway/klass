package no.ssb.klass.designer.editing;

import java.util.Map;

import org.vaadin.spring.annotation.PrototypeScope;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import no.ssb.klass.designer.ErrorView;
import no.ssb.klass.designer.util.ParameterUtil;
import no.ssb.klass.designer.util.VaadinUtil;

@PrototypeScope
@SpringView(name = ElementNotFoundErrorPageView.NAME)
@SuppressWarnings("serial")
public class ElementNotFoundErrorPageView extends ElementNotFoundErrorPage implements ErrorView {
    public static final String NAME = "ElementNotFoundErrorPage";
    public static final String NEXT_PAGE = "nextText";
    public static final String LABEL_TEXT = "labelText";
    public static final String PARAMETER_TEXT = "Parameter";

    @Override
    public void enter(ViewChangeEvent event) {
        Map<String, String> parameters = ParameterUtil.decodeParameters(event.getParameters());
        String nextPage = parameters.get(NEXT_PAGE);
        reason.setValue(reason.getValue() + ' ' + parameters.get(LABEL_TEXT) + '.');
        String parameter = parameters.get(PARAMETER_TEXT);
        if (parameter != null) {
            parameterText.setValue(parameterText.getValue() + " Parameterverdi som feilet er: " + parameter);
        }
        if (nextPage == null) {
            nextPage = "";
        }
        okButton.addClickListener(new EscapeClickListener(nextPage));
    }

    public static class EscapeClickListener implements ClickListener {
        private final String nextPage;

        public EscapeClickListener(String nextPage) {
            this.nextPage = nextPage;
        }

        @Override
        public void buttonClick(ClickEvent event) {
            VaadinUtil.navigateTo(nextPage);
        }
    }
}
