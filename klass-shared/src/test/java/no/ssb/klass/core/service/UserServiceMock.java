package no.ssb.klass.core.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import no.ssb.klass.core.model.User;
import no.ssb.klass.core.service.UserService;

//@Service
public class UserServiceMock implements UserService {
    private final Set<User> users;

    public UserServiceMock() {
        User dummyUser = new User("ziggy", "Ziggy Stardust", "section");
        users = new HashSet<>();
        users.add(dummyUser);
    }

    @Override
    public User saveUser(User user) {
        users.add(user);
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }
    @Override
    public long getUsersCount() {
        return 1337;
    }

    @Override
    public long countUsersWithClassifications() {
        return 0L;
    }

    @Override
    public List<User> getUsersWithClassifications() {
        return new LinkedList<>();
    }

    @Override
    public User getUserByFullName(String fullName) {
        return null;
    }

    @Override
    public User getUserByUserName(String userName) {
        return null;
    }

    @Override
    public User getUserById(long userId) {
        return null;
    }
}
