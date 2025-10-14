package no.ssb.klass.api.applicationtest.config;

import no.ssb.klass.core.service.UserService;
import no.ssb.klass.core.service.UserServiceMock;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.*;

/**
 * @author Mads Lundemo, SSB.
 */
@EnableAutoConfiguration
@ComponentScan(basePackageClasses = {
        no.ssb.klass.api.ApiDocumentation.class,
        no.ssb.klass.core.service.UserService.class,
        no.ssb.klass.core.repository.ChangelogRepository.class},
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = no.ssb.klass.api.config.MockConfig.class)
        })

@EntityScan(basePackageClasses = no.ssb.klass.core.model.BaseEntity.class)
public class ApplicationTestConfig {

    @Bean
    public UserService userService() {
        return new UserServiceMock();
    }
    
}
