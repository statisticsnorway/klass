package no.ssb.klass.designer.user;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import no.ssb.klass.core.model.User;
import no.ssb.klass.core.service.UserService;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class UserContextImplTest {

    @Test
    public void createsSeparateUsersWhenUsernameFormatChanges() {
        UserService userService = mock(UserService.class);

        User shortUsernameUser = new User("kno", "Kari Nordmann", "320");
        User emailStyleUsernameUser = new User("kno@ssb.no", "Kari Nordmann", "320");

        when(userService.getUserByUserName("kno")).thenReturn(null);
        when(userService.getUserByUserName("kno@ssb.no")).thenReturn(null);
        when(userService.saveUser(shortUsernameUser)).thenReturn(shortUsernameUser);
        when(userService.saveUser(emailStyleUsernameUser)).thenReturn(emailStyleUsernameUser);

        User first = UserContextImpl.updateOrCreateUser(userService, shortUsernameUser);
        User second = UserContextImpl.updateOrCreateUser(userService, emailStyleUsernameUser);

        assertSame(shortUsernameUser, first);
        assertSame(emailStyleUsernameUser, second);

        verify(userService).saveUser(shortUsernameUser);
        verify(userService).saveUser(emailStyleUsernameUser);
        verify(userService).getUserByUserName("kno");
        verify(userService).getUserByUserName("kno@ssb.no");

        List<String> persistedUsernames =
                Arrays.asList(shortUsernameUser.getUsername(), emailStyleUsernameUser.getUsername());
        assertThat(persistedUsernames, containsInAnyOrder("kno", "kno@ssb.no"));
    }
}


