package no.ssb.klass.api.applicationtest;

import no.ssb.klass.api.services.IndexService;
import no.ssb.klass.api.services.ReIndexTransactionService;
import no.ssb.klass.core.repository.ClassificationSeriesRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestApiReIndexIntegrationTest {
    @Mock
    private ClassificationSeriesRepository classificationRepository;

    @Mock
    private IndexService indexService;

    @InjectMocks
    private ReIndexTransactionService reIndexTransactionService;

    @Test
    void shouldIndexAllClassifications() {
        List<Long> ids = List.of(1L, 2L, 3L);
        when(classificationRepository.findAllClassificationIds()).thenReturn(ids);

        reIndexTransactionService.runDailyIndexJob();

        ids.forEach(id -> verify(indexService).indexAsync(id));
    }
}
