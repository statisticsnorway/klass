package no.ssb.klass.forvaltning.config.production;

import no.ssb.klass.solr.config.ConfigurationProfiles;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.RememberMeServices;
import org.vaadin.spring.annotation.EnableVaadinExtensions;
import org.vaadin.spring.security.annotation.EnableVaadinSharedSecurity;

import no.ssb.klass.forvaltning.config.test.KlassTestAuthenticationConfiguration;
import no.ssb.klass.forvaltning.controllers.monitor.MonitorController;
import no.ssb.klass.forvaltning.controllers.ping.PingController;
import no.ssb.klass.designer.ui.KlassUI;
import no.ssb.klass.designer.ui.LoginUI;

/**
 * @author Mads Lundemo, SSB.
 */
@Configuration
@EnableVaadinExtensions
@EnableVaadinSharedSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
@Import(KlassTestAuthenticationConfiguration.class)
public class KlassSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private static final String WILDCARD = "**";

    private static final String REMEMBER_ME_KEY = "d571c22b-a106-421d-b551-deae9b130020";

    @NotEmpty
    @Value("${klass.security.ldap.remember}")
    private int remember;

    @Autowired
    private RememberMeServices rememberMeServices;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/VAADIN/" + WILDCARD);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.exceptionHandling().authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/"+LoginUI.PATH));
        http.rememberMe()
                .key(REMEMBER_ME_KEY)
                .rememberMeServices(rememberMeServices)
                .tokenValiditySeconds(remember);
        // .useSecureCookie(true); TODO enable secure cookie when(or if) we switch to https

        http.authorizeRequests()
                .antMatchers("/"+LoginUI.PATH).permitAll()
                .antMatchers("/vaadinServlet/UIDL/" + WILDCARD).permitAll()
                .antMatchers("/vaadinServlet/HEARTBEAT/" + WILDCARD).authenticated()
                .antMatchers("/"+ KlassUI.PATH + "/" + WILDCARD).authenticated()
                .antMatchers("/manage/" + WILDCARD).hasRole("KLASS_ADMINISTRATOR")
                .antMatchers("/" + WILDCARD).permitAll()

                .antMatchers(PingController.PATH).permitAll()
                .antMatchers(MonitorController.PATH).permitAll()
                .anyRequest().authenticated().and().sessionManagement()
                .sessionFixation()
                .migrateSession()
                .and()
                // disable csrf to avoid conflict with vaadins (or else we would have two csrfs)
                .csrf().disable()
                .headers()
                .frameOptions().disable();

    }
}
