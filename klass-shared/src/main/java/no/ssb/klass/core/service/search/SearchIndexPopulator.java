package no.ssb.klass.core.service.search;

import no.ssb.klass.core.model.ClassificationSeries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import no.ssb.klass.core.config.ConfigurationProfiles;
import no.ssb.klass.core.repository.ClassificationSeriesRepository;
import no.ssb.klass.core.service.SearchService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    @Transactional(readOnly = true)
    public void run(String... args) {
        List<ClassificationSeries> classifications = classificationSeriesRepository.findAll();
        classifications.forEach(classification -> searchService.indexSync(classification));
    }
}
