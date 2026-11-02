package no.ssb.klass.api.applicationtest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class TranslatableConverterBenchmarkIsolationTest {
    private final ApplicationContextRunner context =
            new ApplicationContextRunner()
                    .withUserConfiguration(
                            TranslatableConverterHttpBenchmarkTest.ConverterConfiguration.class);

    @Test
    void benchmarkCustomizersAreAbsentUnlessExplicitlyEnabled() {
        context.run(
                application -> {
                    assertFalse(application.containsBean("benchmarkConverter"));
                    assertFalse(application.containsBean("benchmarkAccessLogging"));
                });
    }

    @Test
    void benchmarkCustomizersArePresentWhenExplicitlyEnabled() {
        context.withPropertyValues("klass.converter.e2e=true")
                .run(
                        application -> {
                            assertTrue(application.containsBean("benchmarkConverter"));
                            assertTrue(application.containsBean("benchmarkAccessLogging"));
                        });
    }
}
