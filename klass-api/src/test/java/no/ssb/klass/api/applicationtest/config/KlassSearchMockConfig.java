package no.ssb.klass.api.applicationtest.config;

import no.ssb.klass.core.config.ConfigurationProfiles;
import no.ssb.klass.api.services.SearchService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Primary;

@Configuration
@Profile(ConfigurationProfiles.MOCK_SEARCH)
public class KlassSearchMockConfig {

    @Bean
    @Primary
    public SearchService searchServiceMock() {
        return Mockito.mock(SearchService.class);
    }
}