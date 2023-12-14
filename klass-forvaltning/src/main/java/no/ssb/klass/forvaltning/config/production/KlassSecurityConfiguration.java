package no.ssb.klass.forvaltning.config.production;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
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
public class KlassSecurityConfiguration {

    private static final String WILDCARD = "**";

    @NotEmpty
    @Value("${klass.env.security.ldap.remember.time}")
    private int rememberTime;

    @NotEmpty
    @Value("${klass.env.security.remember.key}")
    private String rememberKey;

    @Autowired
    private RememberMeServices rememberMeServices;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/VAADIN/" + WILDCARD);
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.exceptionHandling().authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/" + LoginUI.PATH));
        http.rememberMe()
                .key(rememberKey)
                .rememberMeServices(rememberMeServices)
                .tokenValiditySeconds(rememberTime);
        // .useSecureCookie(true); TODO enable secure cookie when(or if) we switch to
        // https

        http.authorizeRequests()
                .requestMatchers("/" + LoginUI.PATH).permitAll()
                .requestMatchers("/vaadinServlet/UIDL/" + WILDCARD).permitAll()
                .requestMatchers("/vaadinServlet/HEARTBEAT/" + WILDCARD).authenticated()
                .requestMatchers("/" + KlassUI.PATH + "/" + WILDCARD).authenticated()
                .requestMatchers("/manage/" + WILDCARD).hasRole("KLASS_ADMINISTRATOR")
                .requestMatchers("/" + WILDCARD).permitAll()

                .requestMatchers(PingController.PATH).permitAll()
                .requestMatchers(MonitorController.PATH).permitAll()
                .anyRequest().authenticated().and().sessionManagement()
                .sessionFixation()
                .migrateSession()
                .and()
                // disable csrf to avoid conflict with vaadins (or else we would have two csrfs)
                .csrf().disable()
                .headers()
                .frameOptions().disable();
        return http.build();
    }
}
