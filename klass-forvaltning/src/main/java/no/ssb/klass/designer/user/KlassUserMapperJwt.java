package no.ssb.klass.designer.user;

import no.ssb.klass.auth.Claims;
import no.ssb.klass.core.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Map;

/**
 * Transform a JWT into a User object.
 * <p>
 * Expects that the JWT contains all the necessary claims for constructing the user
 * object and will throw a {@link KlassUserDetailsException} if they are not present.
 */
public class KlassUserMapperJwt {

    private static final Logger log = LoggerFactory.getLogger(KlassUserMapperJwt.class);
    private final String email;
    private final String name;
    private final String username;
    private final String sectionCode;

    public KlassUserMapperJwt(Jwt jwt) throws KlassUserDetailsException {
        if (jwt == null) {
            throw new KlassUserDetailsException("No authentication credentials provided");
        }

        this.username = jwt.getClaimAsString(Claims.SHORT_USERNAME);
        this.email = jwt.getClaimAsString(Claims.EMAIL);
        this.name = jwt.getClaimAsString(Claims.NAME);

        this.sectionCode = this.extractSectionCode(jwt);

        if (this.username == null || this.email == null || this.name == null || this.sectionCode == null) {
            throw new KlassUserDetailsException("Token is missing necessary user detail claims.");
        }
    }

    private String extractSectionCode(Jwt jwt) {
        Map<String, Object> dapla = jwt.getClaimAsMap(Claims.DAPLA);
        if (dapla == null) return null;

        return (String) dapla.get(Claims.SECTION_CODE);
    }

    public User getUser() {
        User user = new User(this.username, this.name, this.sectionCode);
        user.setEmail(this.email);
        return user;
    }
}
