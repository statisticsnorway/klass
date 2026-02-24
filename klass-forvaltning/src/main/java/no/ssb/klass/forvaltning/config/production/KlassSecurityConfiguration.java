package no.ssb.klass.forvaltning.config.production;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class KlassSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private static final Logger log = LoggerFactory.getLogger(KlassSecurityConfiguration.class);

    @Override
    public void configure(WebSecurity web) throws Exception {
        // Authorization is enforced by a login proxy external to the app and a filter.
        web.ignoring().antMatchers("/**");
    }
}
