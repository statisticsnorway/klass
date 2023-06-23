package no.ssb.klass.forvaltning.config.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.authentication.AuthenticationManagerBeanDefinitionParser;

import no.ssb.klass.core.config.ConfigurationProfiles;
import no.ssb.klass.forvaltning.config.production.KlassAuthenticationConfiguration;

/**
 * Configuration for local testing and test environment
 *
 * @author Mads Lundemo, SSB.
 */
@Configuration
@Profile(value = "!" + ConfigurationProfiles.PRODUCTION + "&" + ConfigurationProfiles.FRONTEND_ONLY)
public class KlassTestAuthenticationConfiguration extends KlassAuthenticationConfiguration {

    @Autowired
    protected AuthenticationProvider authenticationProvider;

    /**
     * Dummy authenticator replacing adAuthenticationProvider for offline
     * support<br/>
     * Init method will configure support for test users from LDIF file
     *
     * @see KlassTestAuthenticationConfiguration#init
     */
    @Bean
    @Profile(ConfigurationProfiles.AD_AUTHENTICATE_OFFLINE)
    public AuthenticationProvider nullAuthenticationProvider() {
        return new AuthenticationManagerBeanDefinitionParser.NullAuthenticationProvider();
    }

    /**
     * Access to SSBs Active Directory server is required for this
     * AuthenticationProvider to work
     */
    @Bean
    @Override
    @Profile("!" + ConfigurationProfiles.AD_AUTHENTICATE_OFFLINE)
    public AuthenticationProvider adAuthenticationProvider() {
        return super.adAuthenticationProvider();
    }

    @Override
    public void init(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider);
        auth.ldapAuthentication()
                .userDetailsContextMapper(userDetailsMapper())
                .userDnPatterns("uid={0},ou=people")
                .groupSearchBase("ou=groups")
                .contextSource()
                .ldif("classpath:klassTestUsers.ldif");
    }

}
