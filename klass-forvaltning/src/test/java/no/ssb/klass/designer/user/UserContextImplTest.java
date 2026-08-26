package no.ssb.klass.designer.user;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertSame;

import no.ssb.klass.core.model.User;
import no.ssb.klass.core.service.UserService;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserContextImplTest {

    @Test
    public void createsSeparateUsersWhenUsernameFormatDiffer() {
        InMemoryUserService userService = new InMemoryUserService();

        User shortUsernameUser = new User("kno", "Kari Nordmann", "320");
        User emailStyleUsernameUser = new User("kno@ssb.no", "Kari Nordmann", "320");

        User first = UserContextImpl.updateOrCreateUser(userService, shortUsernameUser);
        User second = UserContextImpl.updateOrCreateUser(userService, emailStyleUsernameUser);

        assertSame(shortUsernameUser, first);
        assertSame(emailStyleUsernameUser, second);

        List<String> persistedUsernames =
                Arrays.asList(shortUsernameUser.getUsername(), emailStyleUsernameUser.getUsername());
        assertThat(persistedUsernames, containsInAnyOrder("kno", "kno@ssb.no"));
        assertThat(
                userService.getSavedUsernames(),
                containsInAnyOrder("kno", "kno@ssb.no"));
    }

    private static class InMemoryUserService implements UserService {
        private final Map<String, User> usersByUsername = new HashMap<String, User>();

        @Override
        public User saveUser(User user) {
            usersByUsername.put(user.getUsername(), user);
            return user;
        }

        @Override
        public List<User> getAllUsers() {
            return Collections.emptyList();
        }

        @Override
        public long getUsersCount() {
            return usersByUsername.size();
        }

        @Override
        public long countUsersWithClassifications() {
            return 0;
        }

        @Override
        public List<User> getUsersWithClassifications() {
            return Collections.emptyList();
        }

        @Override
        public User getUserByFullName(String fullName) {
            return null;
        }

        @Override
        public User getUserByUserName(String userName) {
            return usersByUsername.get(userName);
        }

        @Override
        public User getUserById(long userId) {
            return null;
        }

        List<String> getSavedUsernames() {
            return Arrays.asList(usersByUsername.keySet().toArray(new String[0]));
        }
    }
}


