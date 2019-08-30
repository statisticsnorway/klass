package no.ssb.klass.designer.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.security.VaadinSecurity;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.UI;

import no.ssb.klass.designer.util.KlassTheme;

/**
 * @author Mads Lundemo, SSB.
 */
@Theme(KlassTheme.NAME)
@SpringUI(path = LogoutUI.PATH)
public class LogoutUI extends UI {

    public static final String PATH = "logout";

    @Autowired
    private VaadinSecurity security;

    @Override
    protected void init(VaadinRequest request) {
        getPage().setTitle(KlassUI.KLASS_TITLE);
        security.logout();
    }
}
