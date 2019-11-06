package no.ssb.klass.api.config;

import no.ssb.klass.solr.config.ConfigurationProfiles;
import no.ssb.klass.api.controllers.MonitorController;
import no.ssb.klass.api.controllers.PingController;
import no.ssb.klass.api.util.RestConstants;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @author Mads Lundemo, SSB.
 */
@Configuration
@Profile(value = {ConfigurationProfiles.API_ONLY})//makes config disabled for tests
public class KlassApiConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                //block sensitive endpoints (actuator)
                .antMatchers("/manage**").denyAll() //alt. hasIpAddress("127.0.0.1")
                .antMatchers("/manage/**").denyAll()
                
                //MLO: Some endpoints might be useful, consider allowing non sensitive ones.
//                .antMatchers("/manage/metrics").permitAll()
                
                //allow rest API and health checks
                .antMatchers(RestConstants.REST_PREFIX+"/**").permitAll()
                .antMatchers(PingController.PATH).permitAll()
                .antMatchers(MonitorController.PATH).permitAll()
                
                .and()
                .csrf().disable()
                .headers()
                .addHeaderWriter((request, response) -> {
                    if (request.getServletPath().startsWith(RestConstants.REST_PREFIX)) {
                        // Workaround to Force CORS header all the time for API
                        response.addHeader("Access-Control-Allow-Origin", "*");
                        // Header telling cache server what is varying in our responses
                        response.addHeader("Vary", "Accept");
                        response.addHeader("Vary", "X-Forwarded-Proto");
                    }
                })
                .frameOptions().disable();

    }
}
