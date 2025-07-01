package no.ssb.klass.core.service.search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import no.ssb.klass.core.config.ConfigurationProfiles;
import no.ssb.klass.core.repository.ClassificationSeriesRepository;
import no.ssb.klass.core.service.SearchService;

/**
 * Populates search index at startup of application.
 * <p>
 * Read all classifications ids from database and then indexes them for search.
 */
@Component
@Profile("!" + ConfigurationProfiles.SKIP_INDEXING)
public class SearchIndexPopulator implements CommandLineRunner {
    @Autowired
    private ClassificationSeriesRepository classificationSeriesRepository;
    @Autowired
    private SearchService searchService;

    @Override
    public void run(String... args) {
        classificationSeriesRepository.findAllClassificationIds().forEach(id -> searchService.indexAsync(id));
    }
}