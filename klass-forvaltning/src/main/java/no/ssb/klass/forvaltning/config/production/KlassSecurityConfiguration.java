package no.ssb.klass.forvaltning.config.production;

import no.ssb.klass.designer.ui.KlassUI;
import no.ssb.klass.designer.ui.LoginUI;
import no.ssb.klass.forvaltning.controllers.monitor.MonitorController;
import no.ssb.klass.forvaltning.controllers.ping.PingController;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.RememberMeServices;

/**
 * @author Mads Lundemo, SSB.
 */

//@Configuration
//@EnableVaadinExtensions
//@EnableVaadinSharedSecurity
//@EnableGlobalMethodSecurity(securedEnabled = true)
//@Import(KlassTestAuthenticationConfiguration.class)
public class KlassSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private static final String WILDCARD = "**";

    @NotEmpty
    @Value("${klass.env.security.ldap.remember.time}")
    private int rememberTime;

    @NotEmpty
    @Value("${klass.env.security.remember.key}")
    private String rememberKey;

    @Autowired
    private RememberMeServices rememberMeServices;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/VAADIN/" + WILDCARD);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.exceptionHandling().authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/" + LoginUI.PATH));
        http.rememberMe()
                .key(rememberKey)
                .rememberMeServices(rememberMeServices)
                .tokenValiditySeconds(rememberTime);
        // .useSecureCookie(true); TODO enable secure cookie when(or if) we switch to
        // https

        http.authorizeRequests()
                .antMatchers("/" + LoginUI.PATH).permitAll()
                .antMatchers("/vaadinServlet/UIDL/" + WILDCARD).permitAll()
                .antMatchers("/vaadinServlet/HEARTBEAT/" + WILDCARD).authenticated()
                .antMatchers("/" + KlassUI.PATH + "/" + WILDCARD).authenticated()
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
