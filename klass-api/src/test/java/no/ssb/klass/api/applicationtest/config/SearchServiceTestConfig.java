package no.ssb.klass.api.applicationtest.config;

import no.ssb.klass.api.services.SearchService;
import no.ssb.klass.api.services.SearchServiceImpl;
import no.ssb.klass.core.config.ConfigurationProfiles;
import no.ssb.klass.core.repository.ClassificationSeriesRepository;

import org.opensearch.data.client.orhlc.OpenSearchRestTemplate;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@TestConfiguration
public class SearchServiceTestConfig {

    @Bean
    @Profile("!" + ConfigurationProfiles.MOCK_SEARCH)
    public OpenSearchRestTemplate openSearchRestTemplate(
            org.opensearch.client.RestHighLevelClient client) {
        return new OpenSearchRestTemplate(client);
    }

    @Bean
    @Profile("!" + ConfigurationProfiles.MOCK_SEARCH)
    public SearchService searchService(
            ClassificationSeriesRepository seriesRepository,
            OpenSearchRestTemplate openSearchRestTemplate) {
        return new SearchServiceImpl(seriesRepository, openSearchRestTemplate);
    }
}
