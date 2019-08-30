package no.ssb.klass.core.service;

import no.ssb.klass.core.model.User;

public interface AdminService {
    Integer getNumberOfClassificationForUser(User user);
    void updateUser(User oldUser, User newUser);
}
