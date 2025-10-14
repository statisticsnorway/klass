package no.ssb.klass.api.config;

import no.ssb.klass.api.controllers.validators.CsvFieldsValidator;
import no.ssb.klass.api.services.SearchService;
import no.ssb.klass.core.service.*;
import no.ssb.klass.api.controllers.ClassificationController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import static org.mockito.Mockito.*;

@Configuration
@Profile("api-documentation-test")
public class MockConfig {

    @Bean
    public ClassificationService classificationService() {
        return mock(ClassificationService.class);
    }

    @Bean
    public SubscriberService subscriberService() {
        return mock(SubscriberService.class);
    }

    @Bean
    public SearchService searchService() {
        return mock(SearchService.class);
    }

    @Bean
    public StatisticsService statisticsService() {
        return mock(StatisticsService.class);
    }
    
    @Bean
    public UserService userService() {
        return mock(UserService.class);
    }

    @Bean
    public CsvFieldsValidator csvFieldsValidator() {
        return new CsvFieldsValidator();
    }

    @Bean
    public ClassificationController classificationController() {
        return new ClassificationController(
                classificationService(),
                subscriberService(),
                searchService(),
                statisticsService(),
                csvFieldsValidator()
        );
    }
}
