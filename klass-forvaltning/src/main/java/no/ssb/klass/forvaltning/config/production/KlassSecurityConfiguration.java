package no.ssb.klass.forvaltning.config.production;

import no.ssb.klass.auth.JwtAuthFilter;
import no.ssb.klass.auth.KlassJwtAuthenticationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity
public class KlassSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private static final Logger log = LoggerFactory.getLogger(KlassSecurityConfiguration.class);
    private final JwtAuthFilter jwtAuthFilter;

    public KlassSecurityConfiguration(@Autowired JwtAuthFilter jwtAuthFilter) {
        super();
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public AuthenticationProvider authenticationProvider(@Autowired KlassJwtAuthenticationProvider klassJwtAuthenticationProvider) {
        log.debug("Got authentication provider");
        return klassJwtAuthenticationProvider;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest().permitAll()
                .antMatchers("/klassui").authenticated()
                .antMatchers("/manage/**").hasRole("KLASS_ADMINISTRATOR")
        ;
        http.addFilterBefore(
                jwtAuthFilter, BasicAuthenticationFilter.class);
    }

}
