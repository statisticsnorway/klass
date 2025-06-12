package no.ssb.klass.designer.user;


import no.ssb.klass.core.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtIssuerValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Extract User Details from a request with a JWT Bearer token in the Authorization header.
 * <p>
 * This approach is taken because Spring Security 4 does not have OAuth support.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class UserDetailsExtractorFilter extends GenericFilterBean implements Filter {

    private final UserContext userContext;

    private static final Logger log = LoggerFactory.getLogger(UserDetailsExtractorFilter.class);

    @Autowired
    public UserDetailsExtractorFilter(UserContext userContext) {
        this.userContext = userContext;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) req;

        if (isUnauthenticatedPath(request)) {
            chain.doFilter(req, res);
            return;
        }

        Jwt jwt = extractJwt(request);

        User user = new User(jwt.getClaimAsString("email"), jwt.getClaimAsString("name"), "854");
        user.setEmail(jwt.getClaimAsString("email"));
        log.info("Active User: {}", user.getUsername());
        userContext.setUser(user);

        chain.doFilter(req, res);
    }

    private boolean isUnauthenticatedPath(HttpServletRequest request) {
        return request.getRequestURI().contains("/ping");
    }

    private Jwt extractJwt(HttpServletRequest request) {
        String authHeader = request.getHeader("authorization");

        if (authHeader == null) {
            throw new AuthenticationCredentialsNotFoundException("Request is unauthenticated");
        }

        NimbusJwtDecoder decoder = NimbusJwtDecoder
                .withJwkSetUri("https://auth.test.ssb.no/realms/ssb/protocol/openid-connect/certs")
                .build();

        decoder.setJwtValidator(new JwtIssuerValidator("https://auth.test.ssb.no/realms/ssb"));

        return decoder.decode(authHeader.replace("Bearer ", "").replaceAll("\\s+", ""));

    }
}
