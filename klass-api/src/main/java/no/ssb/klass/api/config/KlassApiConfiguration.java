package no.ssb.klass.api.config;

import no.ssb.klass.api.controllers.PingController;
import no.ssb.klass.api.util.RestConstants;
import no.ssb.klass.core.config.ConfigurationProfiles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @author Mads Lundemo, SSB.
 */
@Configuration
@Profile(value = {ConfigurationProfiles.API_ONLY}) // makes config disabled for tests
public class KlassApiConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                // block sensitive endpoints (actuator)
                .antMatchers("/manage**").denyAll() // alt. hasIpAddress("127.0.0.1")
                .antMatchers("/manage/**").denyAll()

                // MLO: Some endpoints might be useful, consider allowing non sensitive ones.
                // .antMatchers("/manage/metrics").permitAll()

                // allow rest API and health checks
                .antMatchers(RestConstants.API_VERSION_V1 + "/**").permitAll()
                .antMatchers(PingController.PATH).permitAll()

                .and()
                .csrf().disable()
                .headers()
                .addHeaderWriter((request, response) -> {
                    if (request.getServletPath().startsWith(RestConstants.PREFIX_AND_API_VERSION_V1)) {
                        // Workaround to Force CORS header all the time for API
                        response.addHeader("Access-Control-Allow-Origin", "*");
                        // Header telling cache server what is varying in our responses
                        response.addHeader("Vary", "Accept");
                        response.addHeader("Vary", "X-Forwarded-Proto");
                    }
                })
                .frameOptions().disable();
        return http.build();
    }
}
