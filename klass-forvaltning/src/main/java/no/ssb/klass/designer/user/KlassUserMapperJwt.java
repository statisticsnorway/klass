package no.ssb.klass.designer.user;

import no.ssb.klass.core.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Transform a JWT into a User object.
 * <p>
 * Expects that the JWT contains all the necessary claims for constructing the user
 * object and will throw a {@link KlassUserDetailsException} if they are not present.
 */
public class KlassUserMapperJwt {

    private static final String SHORT_USERNAME_CLAIM = "short_username";
    private static final String EMAIL_CLAIM = "email";
    private static final String NAME_CLAIM = "name";

    private static final Logger log = LoggerFactory.getLogger(KlassUserMapperJwt.class);
    private final String email;
    private final String name;
    private String username;

    public KlassUserMapperJwt(Jwt jwt) throws KlassUserDetailsException {
        if (jwt == null) {
            throw new KlassUserDetailsException("No authentication credentials provided");
        }

        this.username = jwt.getClaimAsString(EMAIL_CLAIM);
        this.email = jwt.getClaimAsString(EMAIL_CLAIM);
        this.name = jwt.getClaimAsString(NAME_CLAIM);

        if (this.username == null || this.email == null || this.name == null) {
            throw new KlassUserDetailsException("Token is missing necessary user detail claims.");
        }

        this.username = this.username.split("@")[0];
    }

    public User getUser() {
        User user = new User(this.username, this.name, "854");
        user.setEmail(this.email);
        return user;
    }
}
