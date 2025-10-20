package no.ssb.klass.api.config;

import no.ssb.klass.api.services.SearchService;
import no.ssb.klass.api.services.search.OpenSearchResult;
import no.ssb.klass.core.model.ClassificationSeries;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Configuration
@Profile("mock-search")
public class MockSearchConfig {

    @Bean
    public SearchService searchService() {
        return new SearchService() {

            @Override
            public Page<OpenSearchResult> publicSearch(String query, Pageable pageable, String filterOnSection,
                                                       boolean includeCodeLists) {
                return Page.empty();
            }

            @Override
            public void indexAsync(Long classificationSeriesId) {}

            @Override
            public void indexSync(ClassificationSeries classificationSeries) {}
        };
    }
}
