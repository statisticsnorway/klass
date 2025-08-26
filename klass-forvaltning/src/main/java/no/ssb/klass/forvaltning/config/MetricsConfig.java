package no.ssb.klass.forvaltning.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.spring.autoconfigure.MeterRegistryCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    private static final Logger logger = LoggerFactory.getLogger(MetricsConfig.class);

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        logger.info(">>> MetricsConfig loaded!");
        return registry -> registry.config().commonTags("application", "klass-forvaltning");
    }
}
