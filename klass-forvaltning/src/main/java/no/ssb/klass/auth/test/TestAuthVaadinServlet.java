package no.ssb.klass.auth.test;

import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.server.SpringVaadinServlet;
import no.ssb.klass.core.model.Role;
import no.ssb.klass.core.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import java.util.Objects;


@Component("vaadinServlet")
@WebServlet(urlPatterns = "/*", name = "TestAuthVaadinServlet", asyncSupported = true)
@Profile("hardcoded-user")
public class TestAuthVaadinServlet extends SpringVaadinServlet {
    private static final Logger log = LoggerFactory.getLogger(TestAuthVaadinServlet.class);
    private final String userRole;

    private static final User standardUser = new User("ziggy", "Ziggy Stardust", "700");
    private static final User adminUser = new User("admin1", "Admin One", "724");

    public TestAuthVaadinServlet(@Value("${klass.security.hardcoded.user.role}") String userRole) {
        this.userRole = userRole;
    }

    @Override
    protected void servletInitialized() throws ServletException {
        log.warn("Initializing {}", TestAuthVaadinServlet.class.getSimpleName());
        getService().addSessionInitListener(this::onServletInit);
        super.servletInitialized();
    }

    private void onServletInit(SessionInitEvent sessionInitEvent) {
        VaadinSession session = sessionInitEvent.getSession();
        log.warn("Setting hardcoded user. Not for production!");
        session.setAttribute(User.class, getHardcodedUser());
    }

    private User getHardcodedUser() {
        if (Objects.equals(userRole, "ADMIN")) {
            adminUser.setRole(Role.ADMINISTRATOR);
            return adminUser;
        }
        return standardUser;
    }
}
