package no.ssb.klass.api.applicationtest.config;

import no.ssb.klass.api.services.SearchService;
import no.ssb.klass.core.service.UserService;
import no.ssb.klass.core.service.UserServiceMock;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientAutoConfiguration;
import org.springframework.context.annotation.*;

/**
 * @author Mads Lundemo, SSB.
 */
@Configuration
@EnableAutoConfiguration(exclude = {
        ElasticsearchRestClientAutoConfiguration.class,
        ElasticsearchDataAutoConfiguration.class
})
@ComponentScan(basePackageClasses = {
        no.ssb.klass.api.ApiDocumentation.class,
        no.ssb.klass.core.service.UserService.class,
        no.ssb.klass.core.repository.ChangelogRepository.class},
        excludeFilters = @ComponentScan.Filter(value = SearchService.class, type = FilterType.ASSIGNABLE_TYPE))
@EntityScan(basePackageClasses = no.ssb.klass.core.model.BaseEntity.class)
public class ApplicationTestConfig {

    @Bean
    public UserService userService() {
        return new UserServiceMock();
    }

    @Bean
    @ConditionalOnMissingBean
    public SearchService searchService() {
        // For integration tests with mock-search profile, return a fully mocked SearchService
        return Mockito.mock(SearchService.class);
    }
}
