package no.ssb.klass.api.services.search;

import no.ssb.klass.api.services.SearchService;
import no.ssb.klass.core.config.ConfigurationProfiles;
import no.ssb.klass.core.repository.ClassificationSeriesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Populates search index at startup of application.
 * <p>
 * Read all classifications ids from database and then indexes them for search.
 */
@Component
@Profile("!" + ConfigurationProfiles.SKIP_INDEXING)
public class SearchIndexPopulator implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(SearchIndexPopulator.class);

    @Autowired
    private ClassificationSeriesRepository classificationSeriesRepository;
    @Autowired
    private SearchService searchService;

    @Override
    public void run(String... args) {
        CompletableFuture.runAsync(() -> {
            List<Long> ids = classificationSeriesRepository.findAllClassificationIds();
            List<Long> testIds = ids.stream().limit(1).toList();
            log.info("Starting to index {} classifications", ids.size());
            testIds.forEach(searchService::indexAsync);
            log.info("Finished indexing for {} classifications", ids.size());
        });
    }
}
