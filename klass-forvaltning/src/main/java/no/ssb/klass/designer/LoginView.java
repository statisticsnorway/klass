package no.ssb.klass.designer;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.vaadin.spring.annotation.PrototypeScope;

import com.vaadin.server.Page;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

import no.ssb.klass.solr.config.ConfigurationProfiles;
import no.ssb.klass.core.ldap.KlassUserDetailsException;
import no.ssb.klass.designer.listeners.EnterListener;
import no.ssb.klass.designer.service.KlassLoginService;
import no.ssb.klass.designer.ui.KlassUI;

/**
 * @author Mads Lundemo, SSB.
 */
@PrototypeScope
@SpringComponent
public class LoginView extends LoginDesign {

    private static final Logger log = LoggerFactory.getLogger(LoginView.class);

    @Autowired
    private KlassLoginService klassLoginService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    public LoginView(Environment environment) {
        UI.getCurrent().setFocusedComponent(usernameTextField);
        loginButton.addClickListener(this::onButtonClick);
        loginButton.addShortcutListener(new EnterListener() {
            @Override
            protected void enterPressed() {
                login();
            }
        });
        showOrHideTestUserInfo(environment);
        checkForLogout();

        adminBtn.addClickListener(event -> {
            usernameTextField.setValue("admin1");
            passwordTextField.setValue("admin1");
            login();
        });
        ziggyBtn.addClickListener(event -> {
            usernameTextField.setValue("ziggy");
            passwordTextField.setValue("ziggy");
            login();
        });
        bowieBtn.addClickListener(event -> {
            usernameTextField.setValue("bowie");
            passwordTextField.setValue("bowie");
            login();
        });
    }

    // sooner or later we will probably remove the test user info
    // but for now we just hide it when we are not using Test profile
    private void showOrHideTestUserInfo(Environment environment) {
        testUserLayout.setVisible(environment.acceptsProfiles("!" + ConfigurationProfiles.PRODUCTION));
    }

    private void onButtonClick(Button.ClickEvent event) {
        login();
    }

    private void login() {
        String username = usernameTextField.getValue();
        String password = passwordTextField.getValue();
        boolean rememberMe = rememberMeCheckbox.getValue();

        try {
            klassLoginService.login(username, password, rememberMe);
            Page.getCurrent().setLocation(KlassUI.PATH + getFragments());

        } catch (BadCredentialsException e) {
            Notification.show("Du har tastet in feil brukernavn eller passord", Notification.Type.WARNING_MESSAGE);
        } catch (KlassUserDetailsException e) {
            Notification.show(e.getMessage(), Notification.Type.ERROR_MESSAGE);
        } catch (Exception e) {
            Notification.show("En ukjent feil har oppstått",
                    "Du kan prøve igjen senere eller ta kontakt med de som er ansvarlig for applikasjonen",
                    Notification.Type.ERROR_MESSAGE);
            log.error("Login error, cause:" + e.getMessage(), e);
        }
    }

    private void checkForLogout() {
        String query = Page.getCurrent().getLocation().getQuery();
        if (Objects.equals(query, "logout")) {
            Notification.show("Du er nå logget ut", "", Notification.Type.WARNING_MESSAGE);
        }
    }

    private String getFragments() {
        String fragments = Page.getCurrent().getUriFragment();
        if (fragments == null) {
            return "";
        }
        return "#" + fragments;
    }

}
