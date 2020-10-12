package no.ssb.klass.api.config;

import no.ssb.klass.api.controllers.validators.CsvFieldsValidator;
import no.ssb.klass.core.service.*;
import no.ssb.klass.api.controllers.ClassificationController;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

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
    private UserService userService;

    @Autowired
    private CsvFieldsValidator csvFieldsValidator;

    @Bean
    public ClassificationService classificationService() {
        return Mockito.mock(ClassificationService.class);
    }

    @Bean
    public SubscriberService subscriberService() {
        return Mockito.mock(SubscriberService.class);
    }

    @Bean
    public SearchService searchService() {
        return Mockito.mock(SearchService.class);
    }

    @Bean
    public StatisticsService statisticsService() {
        return Mockito.mock(StatisticsService.class);
    }
    
    @Bean
    public UserService userService() {
        return Mockito.mock(UserService.class);
    }


    @Bean
    public ClassificationController classificationController() {
        return new ClassificationController(classificationService, subscriberService, searchService, statisticsService, csvFieldsValidator);
    }
}
