package no.ssb.klass.api.config;

import no.ssb.klass.api.controllers.validators.CsvFieldsValidator;
import no.ssb.klass.core.service.*;
import no.ssb.klass.api.controllers.ClassificationController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.*;

public class MockConfig {

    @Autowired
    private ClassificationService classificationService;

    @Autowired
    private SubscriberService subscriberService;

    @Autowired
    private SearchService searchService;

    @Autowired
    private StatisticsService statisticsService;

    @Autowired
    private CsvFieldsValidator csvFieldsValidator;

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
    private CsvFieldsValidator csvFieldsValidator() {
        return new CsvFieldsValidator();
    }


    @Bean
    public ClassificationController classificationController() {
        return new ClassificationController(classificationService, subscriberService, searchService, statisticsService, csvFieldsValidator);
    }
}
