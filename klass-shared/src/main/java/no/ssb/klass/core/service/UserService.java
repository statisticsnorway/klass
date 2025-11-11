package no.ssb.klass.core.service;

import no.ssb.klass.core.model.User;

import java.util.List;

public interface UserService {
    User saveUser(User user);

    List<User> getAllUsers();

    long getUsersCount();

    long countUsersWithClassifications();

    List<User> getUsersWithClassifications();

    User getUserByFullName(String fullName);

    User getUserByUserName(String userName);

    User getUserById(long userId);
}
