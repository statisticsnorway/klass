package no.ssb.klass.designer.user;

import no.ssb.klass.core.model.Role;

public interface KlassUserDetailsProvider {

    String getDisplayName();

    String getSsbSection();

    String getEmail();

    String getPhone();

    Role getRole();

    String getPassword();

    String getUsername();

}
