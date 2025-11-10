package no.ssb.klass.api.dto.hal;

import no.ssb.klass.core.model.User;

public class ContactPersonResource {
    private final String name;
    private final String email;
    private final String phone;

    public ContactPersonResource(User contactPerson) {
        name = contactPerson.getFullname();
        email = contactPerson.getEmail();
        phone = contactPerson.getPhone();
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }
}
