package no.ssb.klass;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.TomcatServletWebServerFactoryCustomizer;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(TomcatServletWebServerFactoryCustomizer.class)
@ConfigurationPropertiesScan
public class KlassMailService extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(KlassMailService.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(KlassMailService.class, args);
    }
}
