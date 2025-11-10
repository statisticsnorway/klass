package no.ssb.klass.api.applicationtest.config;

import no.ssb.klass.api.services.SearchService;
import no.ssb.klass.api.services.SearchServiceImpl;
import no.ssb.klass.core.repository.ClassificationSeriesRepository;
import org.opensearch.data.client.orhlc.OpenSearchRestTemplate;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class SearchServiceTestConfig {

  @Bean
  public OpenSearchRestTemplate openSearchRestTemplate(
      org.opensearch.client.RestHighLevelClient client) {
    return new OpenSearchRestTemplate(client);
  }

  @Bean
  @Primary
  public SearchService searchService(
      ClassificationSeriesRepository seriesRepository,
      OpenSearchRestTemplate openSearchRestTemplate) {
    return new SearchServiceImpl(seriesRepository, openSearchRestTemplate);
  }
}
