package no.ssb.klass.api.services;

import no.ssb.klass.core.repository.ClassificationSeriesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReIndexTransactionService {
    private static final Logger log = LoggerFactory.getLogger(ReIndexTransactionService.class);

    private final ClassificationSeriesRepository classificationRepository;
    private final IndexService indexService;

    @Autowired
    public ReIndexTransactionService(ClassificationSeriesRepository classificationRepository, IndexService indexService) {
        this.classificationRepository = classificationRepository;
        this.indexService = indexService;
    }

    @Scheduled(cron = "0 30 23 * * *")
    public void runDailyIndexJob() {
        log.info("Starting scheduled index job at 23:30");

        List<Long> ids = classificationRepository.findAllClassificationIds();
        for (Long id : ids) {
            indexService.indexAsync(id);
        }

        log.info("Finished scheduled indexing for {} classifications", ids.size());
    }
}
