package org.springframework.boot.context.embedded;

import javax.servlet.Servlet;

/**
 * To allow Vaadin 7 to operate with Spring Boot 1.5, we need to make
 * this class available at the deprecated package location.
 */
public class ServletRegistrationBean extends org.springframework.boot.web.servlet.ServletRegistrationBean {
    public ServletRegistrationBean() {
    }

    public ServletRegistrationBean(Servlet servlet, String... urlMappings) {
        super(servlet, urlMappings);
    }

    public ServletRegistrationBean(Servlet servlet, boolean alwaysMapUrl, String... urlMappings) {
        super(servlet, alwaysMapUrl, urlMappings);
    }
}
