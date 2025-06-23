package no.ssb.klass.auth;

import com.vaadin.server.VaadinSession;
import no.ssb.klass.core.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Collections;
import java.util.Objects;

@Component
public class KlassJwtAuthenticationProvider implements AuthenticationProvider {

    private static final Logger log = LoggerFactory.getLogger(KlassJwtAuthenticationProvider.class);

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.debug("Got authentication request {}", authentication.getPrincipal());
        User user = VaadinSession.getCurrent().getAttribute(User.class);
        log.debug("Got user from Vaadin session {}", user.getUsername());
        authentication.setAuthenticated(true);
        if (Objects.equals(((Principal) authentication.getPrincipal()).getName(), user.getUsername())) {
            return new AbstractAuthenticationToken(Collections.singleton((GrantedAuthority) () -> "ROLE_KLASS_STANDARD")) {
                @Override
                public Object getCredentials() {
                    return null;
                }

                @Override
                public Object getPrincipal() {
                    return user;
                }
            };
        } else {
            return null;
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return false;
    }

}
