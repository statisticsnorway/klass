package no.ssb.klass;

import no.ssb.klass.core.model.User;
import no.ssb.klass.designer.user.UserContextImpl;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class UserContextTest {

    private static final User user = new User("kno", "Kari Nordmann", "724");

    @Test
    public void userRoleAdmin() {
        assertThat(
                UserContextImpl.shouldHaveAdminRole(
                        user,
                        new String[]{"kno", "aaa", "bbb"}
                ), is(true));
    }

    @Test
    public void userRoleStandard() {
        assertThat(
                UserContextImpl.shouldHaveAdminRole(
                        user,
                        new String[]{"aaa", "bbb"}
                ), is(false));
    }
}
