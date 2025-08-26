package no.ssb.klass.forvaltning.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.spring.autoconfigure.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        System.out.println(">>> MetricsConfig loaded!"); // debug
        return registry -> registry.config().commonTags("application", "klass-forvaltning");
    }
}
