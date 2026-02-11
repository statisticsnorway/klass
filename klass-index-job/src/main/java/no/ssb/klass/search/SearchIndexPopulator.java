package no.ssb.klass.search;

import no.ssb.klass.core.repository.ClassificationSeriesRepository;
import no.ssb.klass.core.service.ClassificationServiceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Populates search index at startup of application.
 *
 * <p>Read all classifications ids from database and then indexes them for search.
 */
@SpringBootApplication(
        scanBasePackages = {
            "no.ssb.klass.search",
            "no.ssb.klass.core.repository",
            "no.ssb.klass.core.util",
        },
        exclude = {
            ElasticsearchDataAutoConfiguration.class,
            ElasticsearchRepositoriesAutoConfiguration.class,
        })
@Configuration
@ConfigurationPropertiesScan
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {"no.ssb.klass.core.repository"})
@EntityScan(basePackages = {"no.ssb.klass.core.model", "no.ssb.klass.core.util"})
@Import(ClassificationServiceImpl.class)
public class SearchIndexPopulator implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SearchIndexPopulator.class);

    private final ClassificationSeriesRepository classificationRepository;
    private final IndexService indexService;

    public SearchIndexPopulator(
            ClassificationSeriesRepository classificationRepository, IndexService indexService) {
        this.classificationRepository = classificationRepository;
        this.indexService = indexService;
    }

    public static void main(String[] args) {
        SpringApplication.run(SearchIndexPopulator.class, args);
    }

    @Override
    public void run(String... args) {
        log.info("Collecting classifications to index");
        CompletableFuture.runAsync(
                () -> {
                    List<Long> ids = classificationRepository.findPublishedClassificationIds();
                    log.info("Starting to index {} classifications", ids.size());
                    ids.forEach(indexService::indexAsync);
                    log.info("Finished indexing for {} classifications", ids.size());
                });
    }
}
