package no.ssb.klass.designer.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.UI;

import no.ssb.klass.designer.LoginView;
import no.ssb.klass.designer.util.KlassTheme;

/**
 * @author Mads Lundemo, SSB.
 */
@Theme(KlassTheme.NAME)
@SpringUI(path = LoginUI.PATH)
public class LoginUI extends UI {

    public static final String PATH = "login";

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    protected void init(VaadinRequest request) {
        getPage().setTitle(KlassUI.KLASS_TITLE);
        LoginView view = applicationContext.getBean(LoginView.class);
        setContent(view);
        setSizeFull();
    }
}
