package no.ssb.klass.designer.user;


import no.ssb.klass.core.model.User;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtValidators;
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
import java.util.Optional;

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
    private final NimbusJwtDecoder decoder;

    @Autowired
    public UserDetailsExtractorFilter(UserContext userContext) {
        this.userContext = userContext;

        /* TODO https://statistics-norway.atlassian.net/browse/DPMETA-932
         * Configure value with property
         */
        this.decoder = NimbusJwtDecoder
                .withJwkSetUri("https://auth.test.ssb.no/realms/ssb/protocol/openid-connect/certs")
                .build();


        /* TODO https://statistics-norway.atlassian.net/browse/DPMETA-932
         * Configure value with property
         */
        this.decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer("https://auth.test.ssb.no/realms/ssb"));
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        /* TODO https://statistics-norway.atlassian.net/browse/DPMETA-932
         * Configure value with property
         */
        return !request.getRequestURI().contains("/klassui");
    }

    @Override
    public void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
        if (!userContext.hasUser()) {
            Optional<Jwt> jwt = extractJwt(req);

            if (!jwt.isPresent()) {
                res.sendError(HttpStatus.SC_UNAUTHORIZED, "No authentication credentials provided");
                return;
            }
            log.debug("JWT token claims: {}", jwt.get().getClaims());
            try {
                User user = new KlassUserMapperJwt(jwt.get()).getUser();
                log.info("Logged in user: {}", user.getEmail());
                userContext.setUser(user);
            } catch (KlassUserDetailsException e) {
                log.error("Could not log in user", e);
                res.sendError(HttpStatus.SC_UNAUTHORIZED, "Insufficient authentication credentials provided");
                return;
            }
        }

        // Not permitted to continue filter chain in this case, just return.
        if (!res.isCommitted()) {
            chain.doFilter(req, res);
        }
    }

    /**
     * Extract a {@link Jwt} from the Authorization header on a request.
     *
     * @param request The request being processed.
     * @return the decoded JWT token
     */
    private Optional<Jwt> extractJwt(HttpServletRequest request) {
        return getEncodedTokenFromRequest(request).map(this.decoder::decode);
    }

    private Optional<String> getEncodedTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("authorization");

        if (authHeader == null) {
            return Optional.empty();
        }
        return Optional.of(authHeader.replace("Bearer ", "").replaceAll("\\s+", ""));
    }

}
