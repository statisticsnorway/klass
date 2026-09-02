package no.ssb.klass.api.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import no.ssb.klass.api.dto.CodeChangeItem;
import no.ssb.klass.api.dto.CodeChangeList;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.util.DateRange;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class MaterializedViewCodeChangesServiceTest {

    @Mock private NamedParameterJdbcTemplate jdbcTemplate;

    @Test
    void shouldReturnEmptyWhenFeatureDisabled() {
        MaterializedViewCodeChangesService service =
                new MaterializedViewCodeChangesService(jdbcTemplate, false, "131,6");

        Optional<CodeChangeList> result =
                service.findChanges(
                        131L,
                        DateRange.create(
                                LocalDate.parse("1900-01-01"), LocalDate.parse("2100-01-01")),
                        Language.NB,
                        false,
                        ",");

        assertThat(result).isEmpty();
        verify(jdbcTemplate, never()).queryForList(anyString(), any(MapSqlParameterSource.class));
    }

    @Test
    void shouldReturnEmptyWhenIncludeFutureIsTrue() {
        MaterializedViewCodeChangesService service =
                new MaterializedViewCodeChangesService(jdbcTemplate, true, "131");

        Optional<CodeChangeList> result =
                service.findChanges(
                        131L,
                        DateRange.create(
                                LocalDate.parse("1900-01-01"), LocalDate.parse("2100-01-01")),
                        Language.NB,
                        true,
                        ",");

        assertThat(result).isEmpty();
        verify(jdbcTemplate, never()).queryForList(anyString(), any(MapSqlParameterSource.class));
    }

    @Test
    void shouldMapRowsFromMaterializedView() {
        MaterializedViewCodeChangesService service =
                new MaterializedViewCodeChangesService(jdbcTemplate, true, "131");
        when(jdbcTemplate.queryForList(anyString(), any(MapSqlParameterSource.class)))
                .thenReturn(
                        List.of(
                                Map.ofEntries(
                                        Map.entry("change_occurred", Date.valueOf("1950-01-01")),
                                        Map.entry("old_code", "B"),
                                        Map.entry("new_code", "A"),
                                        Map.entry("old_name_no", "Gammel B"),
                                        Map.entry("new_name_no", "Gammel A"),
                                        Map.entry("old_name_nn", "Gamall B"),
                                        Map.entry("new_name_nn", "Gamall A"),
                                        Map.entry("old_name_en", "Old B"),
                                        Map.entry("new_name_en", "Old A"),
                                        Map.entry("old_short_name_no", "GB"),
                                        Map.entry("new_short_name_no", "GA"),
                                        Map.entry("old_short_name_nn", "GBN"),
                                        Map.entry("new_short_name_nn", "GAN"),
                                        Map.entry("old_short_name_en", "GBE"),
                                        Map.entry("new_short_name_en", "GAE"))));

        Optional<CodeChangeList> result =
                service.findChanges(
                        131L,
                        DateRange.create(
                                LocalDate.parse("1900-01-01"), LocalDate.parse("2100-01-01")),
                        Language.NB,
                        false,
                        ",");

        assertThat(result).isPresent();
        List<CodeChangeItem> items = result.get().getCodeChanges();
        assertThat(items).hasSize(1);
        assertThat(items.get(0).getOldCode()).isEqualTo("B");
        assertThat(items.get(0).getOldName()).isEqualTo("Gammel B");
        assertThat(items.get(0).getOldShortName()).isEqualTo("GB");
        assertThat(items.get(0).getNewCode()).isEqualTo("A");
        assertThat(items.get(0).getNewName()).isEqualTo("Gammel A");
        assertThat(items.get(0).getNewShortName()).isEqualTo("GA");
        assertThat(items.get(0).getChangeOccurred()).isEqualTo(LocalDate.parse("1950-01-01"));
    }

    @Test
    void shouldSupportOpenEndedDateRange() {
        MaterializedViewCodeChangesService service =
                new MaterializedViewCodeChangesService(jdbcTemplate, true, "131");
        when(jdbcTemplate.queryForList(anyString(), any(MapSqlParameterSource.class)))
                .thenReturn(
                        List.of(
                                Map.ofEntries(
                                        Map.entry("change_occurred", Date.valueOf("1950-01-01")),
                                        Map.entry("old_code", "B"),
                                        Map.entry("new_code", "A"),
                                        Map.entry("old_name_no", "Gammel B"),
                                        Map.entry("new_name_no", "Gammel A"),
                                        Map.entry("old_name_nn", "Gamall B"),
                                        Map.entry("new_name_nn", "Gamall A"),
                                        Map.entry("old_name_en", "Old B"),
                                        Map.entry("new_name_en", "Old A"),
                                        Map.entry("old_short_name_no", "GB"),
                                        Map.entry("new_short_name_no", "GA"),
                                        Map.entry("old_short_name_nn", "GBN"),
                                        Map.entry("new_short_name_nn", "GAN"),
                                        Map.entry("old_short_name_en", "GBE"),
                                        Map.entry("new_short_name_en", "GAE"))));

        Optional<CodeChangeList> result =
                service.findChanges(
                        131L,
                        DateRange.create(LocalDate.parse("1950-01-01"), null),
                        Language.NB,
                        false,
                        ",");

        assertThat(result).isPresent();
        assertThat(result.get().getCodeChanges()).hasSize(1);
    }
}
