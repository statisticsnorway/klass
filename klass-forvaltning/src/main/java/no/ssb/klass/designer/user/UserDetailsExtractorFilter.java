package no.ssb.klass.designer.user;


import no.ssb.klass.core.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtIssuerValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;


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
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;
        log.info("Filtering request {}", req.toString());

        if (request.getRequestURI().contains("/ping")) {
            chain.doFilter(req, res);
            return;
        }

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        Enumeration<String> headerNames = httpRequest.getHeaderNames();

        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String name = headerNames.nextElement();
                log.info("Header: {}  value: {}", name, httpRequest.getHeader(name));
            }
        }

        String authHeader = httpRequest.getHeader("authorization").replace("Bearer ", "").replaceAll("\\s+", "");

        NimbusJwtDecoder decoder = NimbusJwtDecoder
                .withJwkSetUri("https://auth.test.ssb.no/realms/ssb/protocol/openid-connect/certs")
                .build();

        decoder.setJwtValidator(new JwtIssuerValidator("https://auth.test.ssb.no/realms/ssb"));

        Jwt jwt = decoder.decode(authHeader);

        log.info("Claims: {}", jwt.getClaims());

        User user = new User(jwt.getClaimAsString("email"), jwt.getClaimAsString("name"), "854");
        log.info("Set User {}", user);
        userContext.setUser(user);

        chain.doFilter(req, res);

    }
}
