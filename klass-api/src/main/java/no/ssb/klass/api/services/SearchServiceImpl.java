package no.ssb.klass.api.services;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.opensearch.data.client.orhlc.OpenSearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import no.ssb.klass.core.repository.ClassificationSeriesRepository;
import no.ssb.klass.core.util.TimeUtil;

@Service
@ConditionalOnBean(OpenSearchRestTemplate.class)
public class SearchServiceImpl implements SearchService {
    private static final Logger log = LoggerFactory.getLogger(SearchServiceImpl.class);

    @Value("${klass.env.search.elasticsearch.index:klass}")
    protected String elasticsearchIndex;

    private final ClassificationSeriesRepository classificationRepository;
    private final OpenSearchRestTemplate elasticsearchOperations;

    @Autowired
    public SearchServiceImpl(ClassificationSeriesRepository classificationRepository,
                             OpenSearchRestTemplate elasticsearchOperations) {
        this.classificationRepository = classificationRepository;
        this.elasticsearchOperations = elasticsearchOperations;
    }

    private IndexCoordinates getIndexCoordinates() {
        return IndexCoordinates.of(elasticsearchIndex);
    }

    @Override
    public Page<OpenSearchResult> publicSearch(String query, Pageable pageable, String filterOnSection,
                                               boolean includeCodeLists) {
        return search(PublicSearchQuery.build(query, pageable, filterOnSection, includeCodeLists));
    }

    private Page<OpenSearchResult> search(Query query) {
        Date start = TimeUtil.now();
        SearchHits<OpenSearchResult> searchHits = elasticsearchOperations.search(
                query,
                OpenSearchResult.class,
                getIndexCoordinates()
        );

        List<OpenSearchResult> results = searchHits.getSearchHits().stream()
                .map(hit -> {
                    OpenSearchResult result = hit.getContent();
                    result.setScore(hit.getScore());
                    return result;
                })
                .toList();

        Page<OpenSearchResult> searchResults = new PageImpl<>(
                results,
                query.getPageable(),
                searchHits.getTotalHits()
        );

        log.info("Search for: '{}' resulted in {} hits. Took (ms): {}",
                query, searchResults.getTotalElements(), TimeUtil.millisecondsSince(start));
        return searchResults;
    }
}