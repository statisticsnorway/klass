package no.ssb.klass.api.applicationtest.config;

import no.ssb.klass.core.config.ConfigurationProfiles;
import no.ssb.klass.core.repository.ClassificationSeriesRepository;
import no.ssb.klass.search.IndexService;
import no.ssb.klass.search.IndexServiceImpl;

import org.opensearch.data.client.orhlc.OpenSearchRestTemplate;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@TestConfiguration
public class IndexServiceTestConfig {

    @Bean
    @Profile("!" + ConfigurationProfiles.MOCK_SEARCH)
    public OpenSearchRestTemplate openSearchRestTemplate(
            org.opensearch.client.RestHighLevelClient client) {
        return new OpenSearchRestTemplate(client);
    }

    @Bean
    @Primary
    @Profile("!" + ConfigurationProfiles.MOCK_SEARCH)
    public IndexService indexService(
            ClassificationSeriesRepository seriesRepository,
            OpenSearchRestTemplate openSearchRestTemplate) {
        return new IndexServiceImpl(seriesRepository, openSearchRestTemplate);
    }
}
