package no.ssb.klass.forvaltning.config.production;

import com.vaadin.ui.declarative.Design;
import no.ssb.klass.core.config.ConfigurationProfiles;
import no.ssb.klass.designer.vaadin.VaadinSpringComponentFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@Configuration
@Profile(value = ConfigurationProfiles.PRODUCTION)
public class KlassVaadinConfiguration {

    @Bean
    public static VaadinSpringComponentFactory componentFactory() {
        VaadinSpringComponentFactory componentFactory = new VaadinSpringComponentFactory();
        Design.setComponentFactory(componentFactory);
        return componentFactory;
    }
}
