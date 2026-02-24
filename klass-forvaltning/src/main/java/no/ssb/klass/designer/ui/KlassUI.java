package no.ssb.klass.designer.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.vaadin.annotations.Theme;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.UI;

import no.ssb.klass.designer.KlassErrorHandler;
import no.ssb.klass.designer.KlassSystemMessageProvider;
import no.ssb.klass.designer.MainView;
import no.ssb.klass.designer.exceptions.KlassRuntimeException;
import no.ssb.klass.designer.util.KlassState;
import no.ssb.klass.designer.util.KlassTheme;

@SuppressWarnings("serial")
@Theme(KlassTheme.NAME)
@SpringUI(path = KlassUI.PATH)
public class KlassUI extends UI {
    public static final String PATH = "klassui";
    public static final String KLASS_TITLE = "KlassUI";
    private final ErrorHandler klassErrorHandler = new KlassErrorHandler();
    private final KlassState klassState = new KlassState();

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        getPage().setTitle(KLASS_TITLE);
        configureErrorHandler();

        VaadinService.getCurrent().setSystemMessagesProvider(new KlassSystemMessageProvider());
        MainView main = applicationContext.getBean(MainView.class);
        setContent(main);
        setSizeFull();
    }

    private void configureErrorHandler() {
        UI.getCurrent().setErrorHandler(klassErrorHandler);
        VaadinSession.getCurrent().setErrorHandler(klassErrorHandler);
    }

    // Catch klass specific errors that occurs during UI and view init
    @Override
    public void doInit(VaadinRequest request, int uiId, String embedId) {
        try {
            super.doInit(request, uiId, embedId);
        } catch (KlassRuntimeException ex) {
            klassErrorHandler.error(new com.vaadin.server.ErrorEvent(ex));
        }
    }

    public KlassState getKlassState() {
        return klassState;
    }
}