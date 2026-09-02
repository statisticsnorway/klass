package no.ssb.klass.api.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import no.ssb.klass.api.controllers.validators.CsvFieldsValidator;
import no.ssb.klass.api.dto.CodeChangeItem;
import no.ssb.klass.api.dto.CodeChangeList;
import no.ssb.klass.api.services.MaterializedViewCodeChangesService;
import no.ssb.klass.api.services.SearchService;
import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.service.ClassificationService;
import no.ssb.klass.core.service.StatisticsService;
import no.ssb.klass.core.service.SubscriberService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ClassificationControllerChangesRoutingTest {

    @Mock private ClassificationService classificationService;
    @Mock private SubscriberService subscriberService;
    @Mock private SearchService searchService;
    @Mock private StatisticsService statisticsService;
    @Mock private MaterializedViewCodeChangesService materializedViewCodeChangesService;

    @Test
    void shouldUseMaterializedViewPathWhenPresent() {
        ClassificationController controller =
                new ClassificationController(
                        classificationService,
                        subscriberService,
                        searchService,
                        statisticsService,
                        new CsvFieldsValidator(),
                        materializedViewCodeChangesService);

        CodeChangeList mvResult =
                new CodeChangeList(
                        ',',
                        List.of(
                                new CodeChangeItem(
                                        "01",
                                        "Old",
                                        "Old short",
                                        "02",
                                        "New",
                                        "New short",
                                        LocalDate.parse("1950-01-01"))));
        when(materializedViewCodeChangesService.findChanges(
                        eq(131L), any(), eq(Language.NB), eq(false), eq(",")))
                .thenReturn(Optional.of(mvResult));

        CodeChangeList response =
                controller.changes(
                        131L,
                        LocalDate.parse("1900-01-01"),
                        LocalDate.parse("2100-01-01"),
                        ",",
                        "",
                        Language.NB,
                        false);

        assertThat(response.getCodeChanges()).hasSize(1);
        assertThat(response.getCodeChanges().get(0).getOldCode()).isEqualTo("01");
        verify(classificationService, never()).getClassificationSeries(any());
    }

    @Test
    void shouldFallBackToDefaultPathWhenMaterializedViewPathIsEmpty() {
        ClassificationController controller =
                new ClassificationController(
                        classificationService,
                        subscriberService,
                        searchService,
                        statisticsService,
                        new CsvFieldsValidator(),
                        materializedViewCodeChangesService);

        ClassificationSeries classificationSeries =
                org.mockito.Mockito.mock(ClassificationSeries.class);
        when(materializedViewCodeChangesService.findChanges(
                        eq(131L), any(), eq(Language.NB), eq(false), eq(",")))
                .thenReturn(Optional.empty());
        when(classificationService.getClassificationSeries(131L)).thenReturn(classificationSeries);
        when(classificationSeries.getChangeTables(any(), eq(false))).thenReturn(List.of());

        CodeChangeList response =
                controller.changes(
                        131L,
                        LocalDate.parse("1900-01-01"),
                        LocalDate.parse("2100-01-01"),
                        ",",
                        "",
                        Language.NB,
                        false);

        assertThat(response.getCodeChanges()).isEmpty();
        verify(classificationService).getClassificationSeries(131L);
    }
}
