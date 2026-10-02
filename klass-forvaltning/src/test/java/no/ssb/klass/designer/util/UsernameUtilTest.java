package no.ssb.klass.designer.components.metadata;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import no.ssb.klass.core.model.User;
import no.ssb.klass.designer.util.UsernameUtil;

public class UsernameUtilTest {

    @Test
    public void filterOutEmailUsernames_removesUsersWithAtSignInUsername() {
        User shortName = new User("kno", "Kari Nordmann", "320");
        User emailStyle = new User("kno@ssb.no", "Kari Nordmann", "320");
        User another = new User("ola", "Ola Nordmann", "320");

        List<User> result = UsernameUtil
                .filterOutEmailUsernames(Arrays.asList(shortName, emailStyle, another));

        assertThat(usernamesOf(result), contains("kno", "ola"));
    }

    @Test
    public void filterOutEmailUsernames_returnsEmptyWhenAllUsernamesAreEmail() {
        User a = new User("a@ssb.no", "A", "320");
        User b = new User("b@ssb.no", "B", "320");

        List<User> result = UsernameUtil.filterOutEmailUsernames(Arrays.asList(a, b));

        assertThat(result, empty());
    }

    @Test
    public void filterOutEmailUsernames_returnsEmptyForEmptyInput() {
        assertThat(UsernameUtil.filterOutEmailUsernames(Collections.<User>emptyList()), empty());
    }

    @Test
    public void filterOutEmailUsernames_keepsAllWhenNoUsernameContainsAt() {
        User a = new User("kno", "Kari Nordmann", "320");
        User b = new User("ola", "Ola Nordmann", "320");

        List<User> result = UsernameUtil.filterOutEmailUsernames(Arrays.asList(a, b));

        assertThat(usernamesOf(result), contains("kno", "ola"));
    }

    private static List<String> usernamesOf(List<User> users) {
        return users.stream().map(User::getUsername).collect(Collectors.toList());
    }
}

