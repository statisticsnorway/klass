package no.ssb.klass.api.migration.dataintegrity.changes;

import no.ssb.klass.api.migration.dataintegrity.AbstractKlassApiDataIntegrityTest;
import org.junit.jupiter.api.BeforeAll;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static no.ssb.klass.api.migration.MigrationTestConstants.CSV_SEPARATOR;
import static no.ssb.klass.api.migration.MigrationTestUtils.formatter;
import static no.ssb.klass.api.migration.MigrationTestUtils.generateRandomDate;

public class AbstractKlassApiChanges extends AbstractKlassApiDataIntegrityTest {
    static LocalDate date;

    static String dateFrom;
    static String dateTo;

    static Map<String, Object> paramsLanguageEn = new HashMap<>();
    static Map<String, Object> paramsLanguageNn = new HashMap<>();

    static Map<String, Object> paramsDate = new HashMap<>();
    static Map<String, Object> paramsDateInRange = new HashMap<>();
    static Map<String, Object> paramsCsvSeparator = new HashMap<>();
    static Map<String, Object> paramsIncludeFuture = new HashMap<>();

    String getChangesPath(Integer id) {
        return CLASSIFICATIONS_PATH + "/"+ id + "/" + CHANGES;
    }

    @BeforeAll
    static void beforeAllCodes() {
        date = generateRandomDate(
                LocalDate.of(1951, 1, 1),
                LocalDate.of(2024, 12, 31));
        dateFrom = date.format(formatter);
        dateTo = generateRandomDate((date.plusDays(1)), LocalDate.of(2024, 12, 31)).format(formatter);

        paramsDate.put(FROM, dateFrom);
        paramsDateInRange.putAll(Map.of(FROM, dateFrom, TO, dateTo));
        paramsLanguageEn.putAll(
                Map.of(
                        FROM, dateFrom,
                        LANGUAGE, EN
                )
        );
        paramsLanguageNn.putAll(
                Map.of(
                        FROM, dateFrom,
                        LANGUAGE, NN
                )
        );
        paramsCsvSeparator.putAll(
                Map.of(
                        FROM, dateFrom,
                        CSV_SEPARATOR, ";"
                )
        );

        paramsIncludeFuture.putAll(
                Map.of(
                        FROM, dateFrom,
                        INCLUDE_FUTURE, TRUE
                )
        );

    }
}
