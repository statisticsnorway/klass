package no.ssb.klass.designer.user;

import no.ssb.klass.core.model.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class KlassUserDetailsProviderJwt implements KlassUserDetailsProvider {

    private static final Logger log = LoggerFactory.getLogger(KlassUserDetailsProvider.class);

    private String getUserDetailsFromAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Authentication: {}", authentication.toString());
        if (authentication == null) {
            throw new KlassUserDetailsException("Could not retrieve user details. Authentication object was null.");
        }
        log.info("Principal: {}", authentication.getPrincipal());
        return (String) authentication.getPrincipal();

    }

    @Override
    public String getDisplayName() {
        return "";
    }

    @Override
    public String getSsbSection() {
        return "";
    }

    @Override
    public String getEmail() {
        return "";
    }

    @Override
    public String getPhone() {
        return "";
    }

    @Override
    public Role getRole() {
        return null;
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return this.getUserDetailsFromAuthentication();
    }
}
