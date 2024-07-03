package no.ssb.klass;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Import;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;


@SpringBootApplication
@Import(EmbeddedServletContainerAutoConfiguration.EmbeddedTomcat.class)
public class KlassForvaltningApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(KlassForvaltningApplication.class);
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        servletContext.addListener(new ServletContextListener() {

            @Override
            public void contextInitialized(ServletContextEvent servletContextEvent) {
                // Nothing to do here
            }

            @Override
            public void contextDestroyed(ServletContextEvent sce) {
                // Explicitly deregister the driver to prevent race conditions with Tomcat de-registering the Driver.
                // This is fixed in Spring Boot versions >=2.3.0
                // Ref https://github.com/spring-projects/spring-boot/issues/21221
                org.mariadb.jdbc.Driver.unloadDriver();
            }
        });
        super.onStartup(servletContext);
    }


    public static void main(String[] args) {
        SpringApplication.run(KlassForvaltningApplication.class, args);
    }
}
