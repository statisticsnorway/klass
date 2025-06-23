package no.ssb.klass.auth;

import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.server.SpringVaadinServlet;
import no.ssb.klass.core.model.User;
import no.ssb.klass.designer.user.KlassUserMapperJwt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;


@Component("vaadinServlet")
@WebServlet(urlPatterns = "/*", name = "AuthVaadinServlet", asyncSupported = true)
public class AuthVaadinServlet extends SpringVaadinServlet {
    private static final Logger log = LoggerFactory.getLogger(AuthVaadinServlet.class);

    @Override
    protected void servletInitialized() throws ServletException {
        getService().addSessionInitListener(this::onServletInit);
        super.servletInitialized();
    }

    private void onServletInit(SessionInitEvent sessionInitEvent) {
        VaadinSession session = sessionInitEvent.getSession();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.debug("Got Authentication in Vaadin session {}", authentication.getPrincipal());
        if (authentication.getClass() == JwtAuthenticationToken.class) {
            Jwt jwt = ((JwtAuthenticationToken) authentication).getToken();
            log.debug("Got JWT in Vaadin session {}", jwt);
            User user = new KlassUserMapperJwt(jwt).getUser();
            log.debug("Making user available in session {}", user.getUsername());
            session.setAttribute(User.class, user);
        }
    }
}
