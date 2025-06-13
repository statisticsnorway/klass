package no.ssb.klass.designer.user;


import no.ssb.klass.core.model.User;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtIssuerValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Extract User Details from a request with a JWT Bearer token in the Authorization header.
 * <p>
 * This approach is taken because Spring Security 4 does not have OAuth support.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@WebFilter
public class UserDetailsExtractorFilter extends OncePerRequestFilter implements Filter {

    private final UserContext userContext;

    private static final Logger log = LoggerFactory.getLogger(UserDetailsExtractorFilter.class);

    @Autowired
    public UserDetailsExtractorFilter(UserContext userContext) {
        this.userContext = userContext;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getRequestURI().contains("/ping");
    }

    @Override
    public void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
        Jwt jwt = extractJwt(req);

        if (jwt == null) {
            res.sendError(HttpStatus.SC_UNAUTHORIZED, "No authentication credentials provided");
            return;
        }
        try {
            User user = new KlassUserMapperJwt(jwt).getUser();
            log.info("Logged in user: {}", user.getEmail());
            userContext.setUser(user);
        } catch (KlassUserDetailsException e) {
            log.error("Could not log in user", e);
            res.sendError(HttpStatus.SC_UNAUTHORIZED, "Insufficient authentication credentials provided");
        }

        chain.doFilter(req, res);
    }

    private Jwt extractJwt(HttpServletRequest request) {
        String authHeader = request.getHeader("authorization");

        if (authHeader == null) {
            return null;
        }

        NimbusJwtDecoder decoder = NimbusJwtDecoder
                .withJwkSetUri("https://auth.test.ssb.no/realms/ssb/protocol/openid-connect/certs")
                .build();

        decoder.setJwtValidator(new JwtIssuerValidator("https://auth.test.ssb.no/realms/ssb"));

        return decoder.decode(authHeader.replace("Bearer ", "").replaceAll("\\s+", ""));

    }

}
