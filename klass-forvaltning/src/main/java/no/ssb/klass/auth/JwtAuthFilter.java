package no.ssb.klass.auth;


import org.apache.http.HttpStatus;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Extract User Details from a request with a JWT Bearer token in the Authorization header.
 * <p>
 * This approach is taken because Spring Security 4 does not have OAuth support.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@WebFilter
@Configuration
public class JwtAuthFilter extends OncePerRequestFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);
    private final NimbusJwtDecoder decoder;

    @NotEmpty
    @Value("${klass.security.oauth2.protected.paths}")
    private List<String> protectedPaths;

    public JwtAuthFilter(
            @NotEmpty
            @Value("${klass.security.oauth2.jwt.jwks.uri}")
            String jwksUri,
            @NotEmpty
            @Value("${klass.security.oauth2.jwt.issuer}")
            String jwtIssuer
    ) {

        this.decoder = NimbusJwtDecoder
                .withJwkSetUri(jwksUri)
                .build();

        List<OAuth2TokenValidator<Jwt>> validators = new ArrayList<>();
        validators.add(new JwtTimestampValidator());
        validators.add(new JwtIssuerValidator(jwtIssuer));
        Claims.REQUIRED_CLAIMS.forEach((claim) -> validators.add(new JwtClaimValidator<>(claim, Objects::nonNull)));

        validators.add(new JwtClaimValidator<>("short_username", Objects::nonNull));
        validators.add(new JwtClaimValidator<>("name", Objects::nonNull));
        validators.add(new JwtClaimValidator<>("dapla", Objects::nonNull));
        this.decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(validators));
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return this.protectedPaths.stream().noneMatch((path) -> request.getRequestURI().contains(path));
    }

    @Override
    public void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
        try {
            Optional<Jwt> jwt = extractJwt(req);

            if (!jwt.isPresent()) {
                res.sendError(HttpStatus.SC_UNAUTHORIZED, "No authentication credentials provided");
                return;
            }
            log.debug("JWT token claims: {}", jwt.get().getClaims());
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(new JwtAuthenticationToken(jwt.get()));
            SecurityContextHolder.setContext(context);
        } catch (Exception e) {
            log.error("Could not log in user", e);
            res.sendError(HttpStatus.SC_UNAUTHORIZED, "Insufficient authentication credentials provided");
            return;
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
        String encodedToken = authHeader.replace("Bearer ", "").replaceAll("\\s+", "");
        log.debug("Token {}", encodedToken);
        return Optional.of(encodedToken);
    }

}
