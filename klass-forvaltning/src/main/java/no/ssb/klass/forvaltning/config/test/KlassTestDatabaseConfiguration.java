package no.ssb.klass.forvaltning.config.test;

import org.h2.server.web.JakartaWebServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import no.ssb.klass.core.config.ConfigurationProfiles;
import no.ssb.klass.forvaltning.config.production.KlassVaadinConfiguration;

@Configuration
@Profile("!" + ConfigurationProfiles.PRODUCTION)
public class KlassTestDatabaseConfiguration extends KlassVaadinConfiguration {

    @Bean
    @Profile({ ConfigurationProfiles.H2, ConfigurationProfiles.H2_INMEMORY })
    ServletRegistrationBean h2servletRegistration() {
        // H2 console (database) is reached at http://localhost:8080/console
        ServletRegistrationBean registrationBean = new ServletRegistrationBean(new JakartaWebServlet());
        registrationBean.addUrlMappings("/console/*");
        return registrationBean;
    }
}
