package no.ssb.klass;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Import;


@SpringBootApplication
@Import(EmbeddedServletContainerAutoConfiguration.EmbeddedTomcat.class)
public class KlassForvaltningApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(KlassForvaltningApplication.class);
    }


    public static void main(String[] args) {
        SpringApplication.run(KlassForvaltningApplication.class, args);
    }
}
