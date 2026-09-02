package no.ssb.klass.api.services;

import no.ssb.klass.api.dto.CodeChangeItem;
import no.ssb.klass.api.dto.CodeChangeList;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.util.DateRange;
import no.ssb.klass.core.util.TimeUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MaterializedViewCodeChangesService {
    private static final Logger log =
            LoggerFactory.getLogger(MaterializedViewCodeChangesService.class);

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final boolean enabled;
    private final Set<Long> heavyClassificationIds;

    public MaterializedViewCodeChangesService(
            NamedParameterJdbcTemplate jdbcTemplate,
            @Value("${klass.api.changes.mv.enabled:false}") boolean enabled,
            @Value("${klass.api.changes.mv.classification-ids:}")
                    String heavyClassificationIdsRaw) {
        this.jdbcTemplate = jdbcTemplate;
        this.enabled = enabled;
        this.heavyClassificationIds = parseClassificationIds(heavyClassificationIdsRaw);
    }

    public Optional<CodeChangeList> findChanges(
            Long classificationId,
            DateRange dateRange,
            Language language,
            Boolean includeFuture,
            String csvSeparator) {
        if (!shouldUseMaterializedView(classificationId, includeFuture)) {
            return Optional.empty();
        }

        try {
            boolean hasUpperBound = !TimeUtil.isMaxDate(dateRange.getTo());
            MapSqlParameterSource sqlParameters =
                    new MapSqlParameterSource()
                            .addValue("classificationId", classificationId)
                            .addValue("fromDate", Date.valueOf(dateRange.getFrom()));
            if (hasUpperBound) {
                sqlParameters.addValue("toDate", Date.valueOf(dateRange.getTo()));
            }

            long queryStart = System.currentTimeMillis();
            List<Map<String, Object>> rows =
                    jdbcTemplate.queryForList(
                            queryForLanguage(language, hasUpperBound), sqlParameters);
            long queryMs = System.currentTimeMillis() - queryStart;

            long mapStart = System.currentTimeMillis();
            List<CodeChangeItem> codeChangeItems = new ArrayList<>(rows.size());
            for (Map<String, Object> row : rows) {
                codeChangeItems.add(mapRowToCodeChangeItem(row, language));
            }
            long mapMs = System.currentTimeMillis() - mapStart;

            log.info(
                    "MV changes path used for classificationId={}, language={}, includeFuture={}, from={}, to={}, rows={}, queryMs={}, mapMs={}",
                    classificationId,
                    language,
                    includeFuture,
                    dateRange.getFrom(),
                    hasUpperBound ? dateRange.getTo() : null,
                    codeChangeItems.size(),
                    queryMs,
                    mapMs);

            return Optional.of(new CodeChangeList(csvSeparator.charAt(0), codeChangeItems));
        } catch (RuntimeException ex) {
            log.warn(
                    "Materialized view query failed for classification {}. Falling back to default changes path.",
                    classificationId,
                    ex);
            return Optional.empty();
        }
    }

    boolean shouldUseMaterializedView(Long classificationId, Boolean includeFuture) {
        return enabled
                && Boolean.FALSE.equals(includeFuture)
                && classificationId != null
                && heavyClassificationIds.contains(classificationId);
    }

    private CodeChangeItem mapRowToCodeChangeItem(Map<String, Object> row, Language language) {
        String suffix = languageColumnSuffix(language);
        String oldNameColumn = "old_name_" + suffix;
        String newNameColumn = "new_name_" + suffix;
        String oldShortNameColumn = "old_short_name_" + suffix;
        String newShortNameColumn = "new_short_name_" + suffix;

        LocalDate changeOccurred = readLocalDate(row, "change_occurred");
        return new CodeChangeItem(
                readString(row, "old_code"),
                readString(row, oldNameColumn),
                readString(row, oldShortNameColumn),
                readString(row, "new_code"),
                readString(row, newNameColumn),
                readString(row, newShortNameColumn),
                changeOccurred);
    }

    private LocalDate readLocalDate(Map<String, Object> row, String key) {
        Object value = row.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof LocalDate) {
            return (LocalDate) value;
        }
        if (value instanceof Date) {
            return ((Date) value).toLocalDate();
        }
        throw new IllegalArgumentException(
                "Unsupported date type for " + key + ": " + value.getClass());
    }

    private String readString(Map<String, Object> row, String key) {
        Object value = row.get(key);
        return value == null ? null : value.toString();
    }

    private Set<Long> parseClassificationIds(String heavyClassificationIdsRaw) {
        if (heavyClassificationIdsRaw == null || heavyClassificationIdsRaw.trim().isEmpty()) {
            return Collections.emptySet();
        }

        return Arrays.stream(heavyClassificationIdsRaw.split(","))
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .map(this::toLongOrNull)
                .filter(value -> value != null)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Long toLongOrNull(String value) {
        try {
            return Long.valueOf(value);
        } catch (NumberFormatException ex) {
            log.warn(
                    "Ignoring non-numeric id in klass.api.changes.mv.classification-ids: {}",
                    value);
            return null;
        }
    }

    private String queryForLanguage(Language language, boolean hasUpperBound) {
        String publishedColumn = "published_" + languageColumnSuffix(language);

        String query =
                "SELECT "
                        + "mv.change_occurred, "
                        + "mv.old_code, "
                        + "mv.new_code, "
                        + "mv.old_name_no, mv.new_name_no, mv.old_name_nn, mv.new_name_nn, mv.old_name_en, mv.new_name_en, "
                        + "mv.old_short_name_no, mv.new_short_name_no, mv.old_short_name_nn, mv.new_short_name_nn, mv.old_short_name_en, mv.new_short_name_en "
                        + "FROM klass.mv_heavy_changes mv "
                        + "WHERE mv.classification_id = :classificationId "
                        + "  AND mv.change_occurred >= :fromDate "
                        + "  AND mv."
                        + publishedColumn
                        + " = true ";
        if (hasUpperBound) {
            query += " AND mv.change_occurred < :toDate ";
        }
        return query + "ORDER BY mv.change_occurred, mv.correspondence_map_id";
    }

    private String languageColumnSuffix(Language language) {
        switch (language) {
            case NB:
                return "no";
            case NN:
                return "nn";
            case EN:
                return "en";
            default:
                throw new IllegalArgumentException("Unsupported language: " + language);
        }
    }
}

