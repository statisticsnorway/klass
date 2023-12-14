package no.ssb.klass;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.TomcatServletWebServerFactoryCustomizer;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Import;

// CHECKSTYLE:OFF
@SpringBootApplication
@Import(TomcatServletWebServerFactoryCustomizer.class)
public class KlassApiApplication extends SpringBootServletInitializer {
    // TODO kmgv if using embedded container (e.g. Tomcat) remove below method and extends SpringBootServletInitializer
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(KlassApiApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(KlassApiApplication.class, args);
    }
}
// CHECKSTYLE:ON