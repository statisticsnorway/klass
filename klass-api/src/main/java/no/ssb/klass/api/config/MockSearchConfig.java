package no.ssb.klass.api.config;

import no.ssb.klass.api.services.SearchService;
import no.ssb.klass.core.config.ConfigurationProfiles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;

@Configuration
public class MockSearchConfig {

  @Bean
  @Profile(ConfigurationProfiles.MOCK_SEARCH)
  public SearchService searchService() {
    return (query, pageable, filterOnSection, includeCodeLists) -> Page.empty();
  }
}
