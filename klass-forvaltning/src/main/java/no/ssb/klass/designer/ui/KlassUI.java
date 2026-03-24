package no.ssb.klass.designer.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    private static final Logger log = LoggerFactory.getLogger(KlassUI.class);
    public static final String PATH = "klassui";
    public static final String KLASS_TITLE = "KlassUI";
    private final ErrorHandler klassErrorHandler = new KlassErrorHandler();
    private final KlassState klassState = new KlassState();

    @Autowired
    private ApplicationContext applicationContext;

    @Value("${klass.env.server}")
    private String klassForvaltningServerName;

    @Value("${klass.security.oauth2.logout.path}")
    private String logoutPath;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        getPage().setTitle(KLASS_TITLE);

        Boolean needsLogout = (Boolean) VaadinSession.getCurrent().getAttribute("redirect-to-logout");
        if (Boolean.TRUE.equals(needsLogout)) {
            log.warn("Auth was null during session init. Redirecting to logout to clear session.");
            getPage().setLocation("https://" + klassForvaltningServerName + logoutPath);
            return;
        }

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