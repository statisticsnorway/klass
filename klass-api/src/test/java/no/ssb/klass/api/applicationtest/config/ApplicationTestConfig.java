package no.ssb.klass.api.applicationtest.config;

import no.ssb.klass.solr.config.KlassSearchTestConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import no.ssb.klass.core.repository.ClassificationSeriesRepository;
import no.ssb.klass.core.service.SearchService;
import no.ssb.klass.core.service.SearchServiceImpl;
import no.ssb.klass.core.service.UserService;
import no.ssb.klass.core.service.UserServiceMock;
import no.ssb.klass.api.applicationtest.utils.ApplicationTestUtil;

/**
 * @author Mads Lundemo, SSB.
 */
@EnableAutoConfiguration
@ComponentScan( basePackageClasses = {
        no.ssb.klass.api.ApiDocumentation.class, 
        no.ssb.klass.core.service.UserService.class, 
        no.ssb.klass.core.repository.ChangelogRepository.class},
        excludeFilters = @ComponentScan.Filter(value = SearchService.class, type = FilterType.ASSIGNABLE_TYPE))
@EntityScan(basePackageClasses = no.ssb.klass.core.model.BaseEntity.class)
@Import({KlassSearchTestConfiguration.class})
public class ApplicationTestConfig {

    @Bean
    public UserService userService() {
        return new UserServiceMock();
    }

    @Bean
    @ConditionalOnMissingBean
    public SearchService searchService(ClassificationSeriesRepository seriesRepository) {
        return new SearchServiceImpl(seriesRepository);
    }

    @Bean
    public ApplicationTestUtil applicationTestUtil() {
        return new ApplicationTestUtil();
    }

    @Configuration
    static class TestSecurityConfiguration extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests().antMatchers("/**").permitAll();
        }
    }
}
