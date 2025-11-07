package no.ssb.klass.api.config;

import no.ssb.klass.api.util.RestConstants;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;
import org.springframework.web.accept.PathExtensionContentNegotiationStrategy;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.Map;

/**
 * Prior to Spring Framework v3, path extension mapping was enabled by default.
 *
 * <p>This has been a central way for the Klass API to perform content negotiation and is currently
 * in use by klass-web and presumably other clients.
 *
 * <p>This functionality is now deprecated in Spring and will be removed in a future release. We
 * need to encourage clients to transition to header-based content negotiation if we want to keep
 * pace with future Spring releases.
 */
@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.setUseSuffixPatternMatch(true);
        configurer.setUseRegisteredSuffixPatternMatch(true);
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.strategies(
                List.of(
                        new HeaderContentNegotiationStrategy(),
                        new PathExtensionContentNegotiationStrategy(
                                // Only these path extensions are allowed.
                                Map.of(
                                        "csv",
                                        MediaType.parseMediaType(RestConstants.CONTENT_TYPE_CSV),
                                        "json",
                                        MediaType.APPLICATION_JSON,
                                        "xml",
                                        MediaType.APPLICATION_XML))));
    }
}
