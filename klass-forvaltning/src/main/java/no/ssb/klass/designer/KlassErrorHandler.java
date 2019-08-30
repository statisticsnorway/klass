package no.ssb.klass.designer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.server.AbstractErrorMessage;
import com.vaadin.server.DefaultErrorHandler;
import com.vaadin.server.ErrorMessage;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

import no.ssb.klass.designer.editing.ElementNotFoundErrorPageView;
import no.ssb.klass.designer.exceptions.ParameterException;
import no.ssb.klass.designer.util.VaadinUtil;

/**
 * Add your Klass exception to the "if (t instanceof MyKlassException)..." list, and use the default error page
 * (displayDefaultErrorView) or make a custom one.
 * 
 * @author kons-johnsen
 *
 */
@SuppressWarnings("serial")
public class KlassErrorHandler extends DefaultErrorHandler {
    private static final Logger log = LoggerFactory.getLogger(KlassErrorHandler.class);

    private static final String DEFAULT_ERROR_MESSAGE =
            "Det har oppstått en feil. Noter dag og tidspunkt og kontakt systemadministrator";

    @Override
    public void error(com.vaadin.server.ErrorEvent event) {
        boolean showErrorMesage = true;
        StringBuilder errorMessage = new StringBuilder();
        for (Throwable t = event.getThrowable(); t != null; t = t.getCause()) {
            if (t != null) {
                log.error(t.getMessage(), t);
            }
            if (t instanceof ParameterException) {
                ParameterException parameterException = (ParameterException) t;
                String sidenavn = parameterException.getContext();
                if (sidenavn == null) {
                    sidenavn = "siden";
                }
                String parameter = parameterException.getParameterString();
                displayParameterErrorPage(sidenavn, parameter != null ? parameter : " ");
                showErrorMesage = false;
            }
            if (t instanceof InvalidValueException) {
                AbstractComponent component = findAbstractComponent(event);
                if (component != null) {
                    // Shows the error in AbstractComponent
                    ErrorMessage message = AbstractErrorMessage.getErrorMessageForException(t);
                    component.setComponentError(message);
                    showErrorMesage = false;
                }
            }
        }
        if (showErrorMesage) {
            Notification.show(DEFAULT_ERROR_MESSAGE, Type.ERROR_MESSAGE);
        }

        log.error(errorMessage.toString(), event.getThrowable());
        // doDefault(event);
    }

    private void displayParameterErrorPage(String view, String parameter) {
        String previousPage = VaadinUtil.previousPagename();
        if (Strings.isNullOrEmpty(previousPage)) {
            previousPage = "/";
        }
        VaadinUtil.navigateTo(ElementNotFoundErrorPageView.NAME, ImmutableMap.of(
                ElementNotFoundErrorPageView.LABEL_TEXT, view, ElementNotFoundErrorPageView.PARAMETER_TEXT, parameter,
                ElementNotFoundErrorPageView.NEXT_PAGE, previousPage));
    }
}
