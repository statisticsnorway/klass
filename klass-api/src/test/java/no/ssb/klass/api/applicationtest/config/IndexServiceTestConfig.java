package no.ssb.klass.api.applicationtest.config;

import no.ssb.klass.api.services.IndexService;
import no.ssb.klass.api.services.IndexServiceImpl;
import no.ssb.klass.core.repository.ClassificationSeriesRepository;
import org.opensearch.data.client.orhlc.OpenSearchRestTemplate;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class IndexServiceTestConfig {

  @Bean
  public OpenSearchRestTemplate openSearchRestTemplate(
      org.opensearch.client.RestHighLevelClient client) {
    return new OpenSearchRestTemplate(client);
  }

  @Bean
  @Primary
  public IndexService indexService(
      ClassificationSeriesRepository seriesRepository,
      OpenSearchRestTemplate openSearchRestTemplate) {
    return new IndexServiceImpl(seriesRepository, openSearchRestTemplate);
  }
}
