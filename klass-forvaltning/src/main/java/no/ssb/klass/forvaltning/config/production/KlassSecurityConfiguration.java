package no.ssb.klass.forvaltning.config.production;

import no.ssb.klass.designer.ui.LoginUI;
import no.ssb.klass.forvaltning.config.test.KlassTestAuthenticationConfiguration;
import no.ssb.klass.forvaltning.controllers.monitor.MonitorController;
import no.ssb.klass.forvaltning.controllers.ping.PingController;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.vaadin.spring.annotation.EnableVaadinExtensions;
import org.vaadin.spring.security.annotation.EnableVaadinSharedSecurity;

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

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/VAADIN/" + WILDCARD);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/" + LoginUI.PATH).permitAll()
                .antMatchers("/vaadinServlet/UIDL/" + WILDCARD).permitAll()
//                .antMatchers("/vaadinServlet/HEARTBEAT/" + WILDCARD).authenticated()
//                .antMatchers("/" + KlassUI.PATH + "/" + WILDCARD).authenticated()
                .antMatchers("/manage/" + WILDCARD).hasRole("KLASS_ADMINISTRATOR")
                .antMatchers("/" + WILDCARD).permitAll()

                .antMatchers(PingController.PATH).permitAll()
                .antMatchers(MonitorController.PATH).permitAll()
//                .anyRequest().authenticated().and().sessionManagement()
//                .sessionFixation()
//                .migrateSession()
                .and()
                // disable csrf to avoid conflict with vaadins (or else we would have two csrfs)
                .csrf().disable()
                .headers()
                .frameOptions().disable();

    }
}
