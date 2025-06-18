package no.ssb.klass.auth;

import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.server.SpringVaadinServlet;
import no.ssb.klass.designer.user.KlassUserMapperJwt;
import no.ssb.klass.designer.user.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
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
        JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        log.debug("Got JWT in Vaadin session {}", jwtAuth.getName());

        UserContext context = session.getAttribute(UserContext.class);
        context.setUser(new KlassUserMapperJwt(jwtAuth.getToken()).getUser());
    }

}
