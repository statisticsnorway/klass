package no.ssb.klass.api.services;

import no.ssb.klass.core.repository.ClassificationSeriesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *  Service responsible for re-indexing classification series into the search index.
 */
@Service
public class ReIndexService {
    private static final Logger log = LoggerFactory.getLogger(ReIndexService.class);

    private final ClassificationSeriesRepository classificationRepository;
    private final IndexService indexService;

    @Autowired
    public ReIndexService(ClassificationSeriesRepository classificationRepository, IndexService indexService) {
        this.classificationRepository = classificationRepository;
        this.indexService = indexService;
    }

    /**
     * Executes a daily scheduled job
     * <p>
     * This method retrieves all classification IDs from repository and triggers
     * asynchronous indexing for each one. The job is scheduled to run every day at 23:30.
     * </p>
     */
    @Scheduled(cron = "0 0 * * * *")
    public void runDailyIndexJob() {
        log.info("Starting scheduled index job at 23:30");

        List<Long> ids = classificationRepository.findAllClassificationIds();
        for (Long id : ids) {
            indexService.indexAsync(id);
        }

        log.info("Finished scheduled indexing for {} classifications", ids.size());
    }
}
