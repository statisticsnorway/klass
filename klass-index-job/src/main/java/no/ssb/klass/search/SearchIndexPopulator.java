package no.ssb.klass.search;

import no.ssb.klass.core.repository.ClassificationSeriesRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Populates search index at startup of application.
 *
 * <p>Read all classifications ids from database and then indexes them for search.
 */
@ConfigurationPropertiesScan
@EnableJpaRepositories(basePackages = {"no.ssb.klass.core.repository"})
@EntityScan(basePackages = {"no.ssb.klass.core.model"})
@SpringBootApplication(
        scanBasePackages = {"no.ssb.klass.search"},
        exclude = {
            ElasticsearchDataAutoConfiguration.class,
            ElasticsearchRepositoriesAutoConfiguration.class,
        })
public class SearchIndexPopulator implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SearchIndexPopulator.class);

    private final ClassificationSeriesRepository classificationSeriesRepository;
    private final IndexService indexService;

    public SearchIndexPopulator(
            ClassificationSeriesRepository classificationSeriesRepository,
            IndexService indexService) {
        this.classificationSeriesRepository = classificationSeriesRepository;
        this.indexService = indexService;
    }

    public static void main(String[] args) {
        SpringApplication.run(SearchIndexPopulator.class, args);
    }

    @Override
    public void run(String... args) {
        CompletableFuture.runAsync(
                () -> {
                    List<Long> ids = classificationSeriesRepository.findAllClassificationIds();
                    log.info("Starting to index {} classifications", ids.size());
                    ids.forEach(indexService::indexAsync);
                    log.info("Finished indexing for {} classifications", ids.size());
                });
    }
}
