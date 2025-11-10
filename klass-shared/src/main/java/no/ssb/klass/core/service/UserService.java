package no.ssb.klass.core.service;

import java.util.List;
import no.ssb.klass.core.model.User;

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
