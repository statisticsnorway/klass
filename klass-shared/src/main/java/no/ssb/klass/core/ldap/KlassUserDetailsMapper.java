package no.ssb.klass.core.ldap;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.LdapUserDetails;
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Mads Lundemo, SSB.
 */
public class KlassUserDetailsMapper extends LdapUserDetailsMapper {

    private static final Logger log = LoggerFactory.getLogger(KlassUserDetailsMapper.class);

    public static final String NAME_ATTRIBUTE = "displayName";
    public static final String SECTION_ATTRIBUTE = "department";
    public static final String DESCRIPTION_ATTRIBUTE = "description";

    public static final String MAIL_ATTRIBUTE = "mail";
    public static final String MOBILE_PHONE_ATTRIBUTE = "mobile";
    public static final String LANDLINE_PHONE_ATTRIBUTE = "telephoneNumber";
    private static final String PREFIX = "ROLE_";

    private static final Pattern SECTION_REGEX =
            Pattern.compile("((o|k)\\s*)?(?<nummer>\\d+)([^\\w]*)(?<navn>.+?)$", Pattern.CASE_INSENSITIVE);

    public KlassUserDetailsMapper() {
        setRolePrefix(PREFIX);
    }

    @Override
    public UserDetails mapUserFromContext(DirContextOperations ctx, String username,
            Collection<? extends GrantedAuthority> authorities) {

        List<SimpleGrantedAuthority> prefixed = PrefixAuthorities(authorities);

        LdapUserDetails ldapUserDetails = (LdapUserDetails) super.mapUserFromContext(ctx, username, prefixed);
        String displayName = getDisplayNameFromContext(ctx, username);
        String ssbSection = getSsbSectionFromContext(ctx, username);
        String email = getOptionalValueFromContext(ctx, MAIL_ATTRIBUTE);
        String mobile = getOptionalValueFromContext(ctx, MOBILE_PHONE_ATTRIBUTE);
        String landline = getOptionalValueFromContext(ctx, LANDLINE_PHONE_ATTRIBUTE);
        // if no mobile phone number is found fall back on landline numbers
        String phone = mobile != null ? mobile : landline;
        KlassUserDetails klassUserDetails = new KlassUserDetails(ldapUserDetails, displayName, ssbSection, email,
                phone);

        return klassUserDetails;
    }

    private List<SimpleGrantedAuthority> PrefixAuthorities(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .map(o -> o.getAuthority().startsWith(PREFIX)
                        ? new SimpleGrantedAuthority(o.getAuthority().toUpperCase())
                        : new SimpleGrantedAuthority(PREFIX + o.getAuthority().toUpperCase()))
                .collect(Collectors.toList());
    }

    private String getDisplayNameFromContext(DirContextOperations ctx, String username) {
        try {
            Attribute attribute = ctx.getAttributes().get(NAME_ATTRIBUTE);
            if (attribute != null && !Strings.isNullOrEmpty((String) attribute.get(0))) {
                return (String) attribute.get(0);
            }
        } catch (NamingException e) {
            log.warn("unable to extract name, reason", e);

        }
        throw new KlassUserDetailsException("Kunne ikke hente navn for bruker " + username);
    }

    private String getOptionalValueFromContext(DirContextOperations ctx, String valueName) {
        try {
            Attribute attribute = ctx.getAttributes().get(valueName);
            if (attribute != null && !Strings.isNullOrEmpty((String) attribute.get(0))) {
                return (String) attribute.get(0);
            }
        } catch (NamingException e) {
            log.info("unable to extract " + valueName + ", reason", e);
        }
        return null;
    }

    private String getSsbSectionFromContext(DirContextOperations ctx, String username) {
        try {
            Attribute section = ctx.getAttributes().get(SECTION_ATTRIBUTE);
            Attribute description = ctx.getAttributes().get(DESCRIPTION_ATTRIBUTE);
            if (section != null) {
                String sectionRaw = (String) section.get(0);
                Matcher matcher = SECTION_REGEX.matcher(sectionRaw);
                if (matcher.matches()) {
                    return matcher.group("nummer") + " - " + matcher.group("navn");
                } else {
                    return sectionRaw;
                }
                // keeping this as its to much hassle to change apacheDS to include custom attribute
            } else if (description != null) {
                String raw = (String) description.get(0);
                if (!Strings.isNullOrEmpty(raw) && raw.length() > 2) {
                    return raw.substring(0, 3);
                } else {
                    log.warn("unable to extract Section; Description field to short, data:" + raw);
                }
            } else {
                log.warn("unable to extract Section; Description field is empty");
            }
        } catch (NamingException e) {
            log.warn("unable to extract Section, reason", e);

        }
        throw new KlassUserDetailsException("Kunne ikke finne seksjonstilhørighet for bruker " + username);
    }
}
