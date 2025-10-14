package no.ssb.klass.api.applicationtest.config;

import no.ssb.klass.api.services.SearchService;
import no.ssb.klass.core.config.ConfigurationProfiles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import static org.mockito.Mockito.mock;

/**
 * @author Mads Lundemo, SSB.
 */
@Configuration
@Profile(ConfigurationProfiles.MOCK_SEARCH)
public class KlassSearchMockConfig {

    @Bean
    public SearchService searchService() {
        return mock(SearchService.class);
    }
}
