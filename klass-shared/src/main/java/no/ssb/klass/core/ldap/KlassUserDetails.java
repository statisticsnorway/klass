package no.ssb.klass.core.ldap;

import java.util.Collection;
import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.ldap.userdetails.LdapUserDetails;

import no.ssb.klass.core.model.Role;

/**
 * @author Mads Lundemo, SSB.
 */
public class KlassUserDetails implements LdapUserDetails {

    private final LdapUserDetails ldapUserDetails;

    private final String displayName;
    private final String ssbSection;
    private final String email;
    private final String phone;

    public KlassUserDetails(LdapUserDetails ldapUserDetails, String displayName,
            String ssbSection, String email, String phone) {
        this.ldapUserDetails = ldapUserDetails;
        this.displayName = displayName;
        this.ssbSection = ssbSection;
        this.email = email;
        this.phone = phone;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getSsbSection() {
        return ssbSection;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public Role getRole() {
        for (GrantedAuthority grantedAuthority : ldapUserDetails.getAuthorities()) {
            if (Objects.equals(grantedAuthority.getAuthority(), ActiveDirectoryRoles.KLASS_ADMINISTRATOR)) {
                return Role.ADMINISTRATOR;
            }
        }
        return Role.STANDARD;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return ldapUserDetails.getAuthorities();
    }

    @Override
    public String getPassword() {
        return ldapUserDetails.getPassword();
    }

    @Override
    public String getUsername() {
        return ldapUserDetails.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return ldapUserDetails.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return ldapUserDetails.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return ldapUserDetails.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return ldapUserDetails.isEnabled();
    }

    @Override
    public String getDn() {
        return ldapUserDetails.getDn();
    }
}
