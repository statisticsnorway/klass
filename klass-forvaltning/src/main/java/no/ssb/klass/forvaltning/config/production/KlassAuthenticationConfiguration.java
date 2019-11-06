package no.ssb.klass.forvaltning.config.production;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.ldap.userdetails.LdapUserDetailsService;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.InMemoryTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;

import no.ssb.klass.solr.config.ConfigurationProfiles;
import no.ssb.klass.core.ldap.KlassUserDetailsMapper;

/**
 * Configuration for production environment, all test beans are excluded
 *
 * @author Mads Lundemo, SSB.
 */
@Configuration
@Profile(value = ConfigurationProfiles.PRODUCTION)
public class KlassAuthenticationConfiguration extends GlobalAuthenticationConfigurerAdapter {

    public static final String SEARCH_BASE = "OU=Brukere,OU=SSB,DC=ssb,DC=no";
    public static final String SEARCH_FILTER = "(&(objectClass=user)(sAMAccountName={0}))";

    /**
     * "secret" hashing salt for remember me service
     */
    public static final String REMEMBER_ME_KEY = "d571c22b-a106-421d-b551-deae9b130020";

    @NotEmpty
    @Value("${klass.security.ldap.domain}")
    protected String domain;
    
    @NotEmpty
    @Value("${klass.security.ldap.url}")
    protected String url;

    @NotEmpty
    @Value("${klass.security.ldap.user}")
    private String user;

    @NotEmpty
    @Value("${klass.security.ldap.password}")
    private String password;

//    @Autowired
//    protected KlassUserDetailsMapper userDetailsMapper;

    @Bean
    public PasswordEncoder passwordEncoder() {
        // just making sure we wont use weak MD5 or SHA encoder for anything (remember me service etc)
        // as far as i know default behavior for remember me service would be
        // MD5(username + expirationTime + password + REMEMBER_ME_KEY)
        return new BCryptPasswordEncoder();
    }

    @Bean
    public LdapContextSource ldapContext() {
        LdapContextSource source = new LdapContextSource();
        source.setUrl(url);
        source.setUserDn(user);
        source.setPassword(password);
        return source;
    }

    @Bean
    public FilterBasedLdapUserSearch ldapUserSearch(LdapContextSource ldapContext) {
        FilterBasedLdapUserSearch search = new FilterBasedLdapUserSearch(SEARCH_BASE, SEARCH_FILTER, ldapContext);
        String[] attributes = { KlassUserDetailsMapper.SECTION_ATTRIBUTE,
                KlassUserDetailsMapper.NAME_ATTRIBUTE,
                KlassUserDetailsMapper.MAIL_ATTRIBUTE,
                KlassUserDetailsMapper.MOBILE_PHONE_ATTRIBUTE,
                KlassUserDetailsMapper.LANDLINE_PHONE_ATTRIBUTE };
        search.setReturningAttributes(attributes);
        search.setSearchSubtree(true);
        return search;
    }

    @Bean
    public LdapUserDetailsService ldapUserDetailsService(FilterBasedLdapUserSearch search) {
        LdapUserDetailsService ldapUserDetailsService = new LdapUserDetailsService(search);
        ldapUserDetailsService.setUserDetailsMapper(userDetailsMapper());
        return ldapUserDetailsService;
    }

    @Bean
    public RememberMeServices rememberMeServices(LdapUserDetailsService rememberMeUserDetailsService) {
        // Using in memory tokens for simplicity
        // (We might want to change this if we start using multiple servers and load balancing)
        InMemoryTokenRepositoryImpl rememberMeTokenRepository = new InMemoryTokenRepositoryImpl();
        PersistentTokenBasedRememberMeServices services = new PersistentTokenBasedRememberMeServices(
                REMEMBER_ME_KEY, rememberMeUserDetailsService, rememberMeTokenRepository);
        services.setAlwaysRemember(false);
        return services;
    }

    @Bean
    public AuthenticationProvider adAuthenticationProvider() {
        ActiveDirectoryLdapAuthenticationProvider provider =
                new ActiveDirectoryLdapAuthenticationProvider(domain, url);
        provider.setUserDetailsContextMapper(userDetailsMapper());
        return provider;
    }

    @Bean
    public KlassUserDetailsMapper userDetailsMapper() {
        return new KlassUserDetailsMapper();
    }

    @Override
    public void init(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(adAuthenticationProvider());
    }

}
