package no.ssb.klass.core.ldap;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.core.Is.*;
import static org.hamcrest.core.IsNull.*;
import static org.hamcrest.core.StringContains.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.ldap.search.LdapUserSearch;
import org.springframework.security.ldap.userdetails.LdapUserDetailsImpl;

import no.ssb.klass.core.model.User;
import no.ssb.klass.core.service.UserService;

/**
 * @author Mads Lundemo, SSB.
 */
@RunWith(MockitoJUnitRunner.class)
public class ActiveDirectoryServiceTest {

    @Mock
    private UserService userService;
    @Mock
    private LdapUserSearch userSearch;
    @Spy
    private KlassUserDetailsMapper userDetailsMapper;

    @InjectMocks
    private ActiveDirectoryServiceImpl adService;

    @Before
    public void configureUserServiceMock() {
        when(userService.saveUser(any(User.class))).thenAnswer(invocation -> invocation.getArguments()[0]);
    }

    @Test
    public void shouldUpdateSectionIfDifferent() {

        User userWithOldSection = new User("tes", "test", "100");

        DirContextAdapter ldapResponse = new DirContextAdapter();
        ldapResponse.setAttributeValue(KlassUserDetailsMapper.NAME_ATTRIBUTE, "test");
        ldapResponse.setAttributeValue(KlassUserDetailsMapper.SECTION_ATTRIBUTE, "200 - test");
        when(userSearch.searchForUser("tes")).thenReturn(ldapResponse);

        List<String> changes = adService.syncActiveDirectory(userWithOldSection);

        ArgumentCaptor<User> argument = ArgumentCaptor.forClass(User.class);
        verify(userService, times(1)).saveUser(argument.capture());

        assertThat(changes.isEmpty(), is(false));
        assertThat(changes.get(0), containsString("200 - test"));
        assertThat(argument.getValue().getSection(), is("200 - test"));
        assertThat(argument.getValue().getUsername(), is("tes"));
        assertThat(argument.getValue().getFullname(), is("test"));

    }

    @Test
    public void shouldNotUpdateSectionIfEqual() {
        User userWithOldSection = new User("tes", "test", "100 - test");

        DirContextAdapter ldapResponse = new DirContextAdapter();
        ldapResponse.setAttributeValue(KlassUserDetailsMapper.NAME_ATTRIBUTE, "test");
        ldapResponse.setAttributeValue(KlassUserDetailsMapper.SECTION_ATTRIBUTE, "100 - test");
        when(userSearch.searchForUser("tes")).thenReturn(ldapResponse);

        List<String> changes = adService.syncActiveDirectory(userWithOldSection);

        assertThat(changes.isEmpty(), is(true));
        verify(userService, never()).saveUser(any());
    }

    @Test
    public void shouldUpdateFullNameIfDifferent() {
        User userWithOldSection = new User("tes", "test", "100 - test");

        DirContextAdapter ldapResponse = new DirContextAdapter();
        ldapResponse.setAttributeValue(KlassUserDetailsMapper.NAME_ATTRIBUTE, "nytt navn");
        ldapResponse.setAttributeValue(KlassUserDetailsMapper.SECTION_ATTRIBUTE, "100 - test");

        when(userSearch.searchForUser("tes")).thenReturn(ldapResponse);

        List<String> changes = adService.syncActiveDirectory(userWithOldSection);

        ArgumentCaptor<User> argument = ArgumentCaptor.forClass(User.class);
        verify(userService, times(1)).saveUser(argument.capture());

        assertThat(changes.isEmpty(), is(false));
        assertThat(changes.get(0), containsString("nytt navn"));

        assertThat(argument.getValue().getSection(), is("100 - test"));
        assertThat(argument.getValue().getUsername(), is("tes"));
        assertThat(argument.getValue().getFullname(), is("nytt navn"));
    }

    @Test
    public void shouldNotUpdateFullNameIfEqual() {
        User userWithOldSection = new User("tes", "test", "100 - test");

        DirContextAdapter ldapResponse = new DirContextAdapter();
        ldapResponse.setAttributeValue(KlassUserDetailsMapper.NAME_ATTRIBUTE, "test");
        ldapResponse.setAttributeValue(KlassUserDetailsMapper.SECTION_ATTRIBUTE, "100 - test");

        when(userSearch.searchForUser("tes")).thenReturn(ldapResponse);

        List<String> changes = adService.syncActiveDirectory(userWithOldSection);

        verify(userService, never()).saveUser(any(User.class));

        assertThat(changes.isEmpty(), is(true));

    }

    @Test
    public void shouldCreateAndSaveNewUserIfItExistsInAd() {

        DirContextAdapter ldapResponse = new DirContextAdapter();
        ldapResponse.setAttributeValue(KlassUserDetailsMapper.NAME_ATTRIBUTE, "test");
        ldapResponse.setAttributeValue(KlassUserDetailsMapper.SECTION_ATTRIBUTE, "100 - test");

        when(userSearch.searchForUser("xxx")).thenReturn(ldapResponse);

        User user = adService.createAndSaveNewUser("xxx");

        assertThat(user, notNullValue());
        assertThat(user.getSection(), is("100 - test"));
        assertThat(user.getUsername(), is("xxx"));
        assertThat(user.getFullname(), is("test"));

    }

    @Test
    public void shouldCreateAndSaveNewUser() {
        LdapUserDetailsImpl userDetails = Mockito.mock(LdapUserDetailsImpl.class);
        when(userDetails.getUsername()).thenReturn("xxx");
        KlassUserDetails newDetails = new KlassUserDetails(userDetails, "full name", "200", "e@mail.com", "22225555");

        User user = adService.createAndSaveNewUser(newDetails);

        assertThat(user, notNullValue());
        assertThat(user.getSection(), is("200"));
        assertThat(user.getUsername(), is("xxx"));
        assertThat(user.getFullname(), is("full name"));

    }

    @Test(expected = UsernameNotFoundException.class)
    public void shouldThrowExceptionIfUserDoesNotExistsInAd() {
        when(userSearch.searchForUser("xxx")).thenThrow(new UsernameNotFoundException(""));
        adService.createAndSaveNewUser("xxx");
    }

    @Test
    public void shouldUpdateUserAndSave() {

        LdapUserDetailsImpl userDetails = Mockito.mock(LdapUserDetailsImpl.class);
        User userWithOldData = new User("tes", "test", "100");
        KlassUserDetails newDetails = new KlassUserDetails(userDetails, "full name", "200", "e@mail.com", "22225555");

        User savedUser = adService.updateAndSaveUser(newDetails, userWithOldData);

        verify(userService, times(1)).saveUser(any(User.class));

        assertThat(savedUser, notNullValue());
        assertThat(savedUser.getSection(), is("200"));
        assertThat(savedUser.getUsername(), is("tes"));
        assertThat(savedUser.getFullname(), is("full name"));
    }
}
