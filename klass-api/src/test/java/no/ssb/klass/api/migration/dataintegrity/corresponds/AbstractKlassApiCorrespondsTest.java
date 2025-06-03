package no.ssb.klass.api.migration.dataintegrity.corresponds;

import no.ssb.klass.api.migration.dataintegrity.AbstractKlassApiDataIntegrityTest;
import org.junit.jupiter.api.BeforeAll;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;

public class AbstractKlassApiCorrespondsTest extends AbstractKlassApiDataIntegrityTest {

    String getCorrespondsAtPath(Integer id) {
        return CLASSIFICATIONS_PATH + "/"+ id + "/" + CORRESPONDS_AT;
    }

    String getCorrespondsPath(Integer id) {
        return CLASSIFICATIONS_PATH + "/"+ id + "/" + CORRESPONDS;
    }

    static Map<String, Object> paramsTargetIdAndDate = new HashMap<>();
    static Map<String, Object> paramsTargetIdAndDateLanguageEn = new HashMap<>();
    static Map<String, Object> paramsTargetIdAndDateLanguageNn = new HashMap<>();
    static Map<String, Object> paramsTargetIdAndDateIncludeFuture = new HashMap<>();
    static Map<String, Object> paramsTargetIdAndDateCsvFields = new HashMap<>();
    static Map<String, Object> paramsTargetIdAndDateCsvSeparator = new HashMap<>();

    static Map<String, Object> paramsTargetIdAndDateFrom = new HashMap<>();

    static Map<String, Object> paramsTargetIdAndDateFromTo = new HashMap<>();

    static Map<String, Object> paramsTargetIdAndDateFromLanguageEn = new HashMap<>();
    static Map<String, Object> paramsTargetIdAndDateFromLanguageNn = new HashMap<>();
    static Map<String, Object> paramsTargetIdAndDateFromIncludeFuture = new HashMap<>();
    static Map<String, Object> paramsTargetIdAndDateFromCsvFields = new HashMap<>();
    static Map<String, Object> paramsTargetIdAndDateFromCsvSeparator = new HashMap<>();

    static Integer targetClassificationIdValue;
    static String fromDate;

    static String date;

    static Stream<Integer> correspondenceIdRangeProvider() {
        return IntStream.rangeClosed(0, 1500).boxed();
    }

    String getCorrespondenceTableByIdPath(Integer id) {
        return "/" + CORRESPONDENCE_TABLES + "/"  + id;
    }

    @BeforeAll
    static void beforeAllCorrespondence() {
        date = "2018-01-01";
        fromDate = "2020-01-01";
        targetClassificationIdValue = 103;

        paramsTargetIdAndDate.putAll(
                Map.of(
                        DATE, date,
                        TARGET_CLASSIFICATION_ID, targetClassificationIdValue
                )
        );
        paramsTargetIdAndDateLanguageEn.putAll(
                Map.of(
                      DATE, date,
                      TARGET_CLASSIFICATION_ID, targetClassificationIdValue,
                      LANGUAGE, EN
                )
        );
        paramsTargetIdAndDateLanguageNn.putAll(
                Map.of(
                        DATE, date,
                        TARGET_CLASSIFICATION_ID, targetClassificationIdValue,
                        LANGUAGE, NN
                )
        );
        paramsTargetIdAndDateCsvSeparator.putAll(
                Map.of(
                        DATE, date,
                        TARGET_CLASSIFICATION_ID, targetClassificationIdValue,
                        CSV_SEPARATOR, ";"

                )
        );
        paramsTargetIdAndDateCsvFields.putAll(
                Map.of(
                        DATE, date,
                        TARGET_CLASSIFICATION_ID, targetClassificationIdValue,
                        CSV_FIELDS, "sourceName,targetName"

                )
        );
        paramsTargetIdAndDateIncludeFuture.putAll(
                Map.of(
                        DATE, date,
                        TARGET_CLASSIFICATION_ID, targetClassificationIdValue,
                        INCLUDE_FUTURE, TRUE
                )
        );

        paramsTargetIdAndDateFrom.putAll(
                Map.of(
                        FROM, fromDate,
                        TARGET_CLASSIFICATION_ID, targetClassificationIdValue
                )
        );

        paramsTargetIdAndDateFromTo.putAll(
                Map.of(
                        FROM, fromDate,
                        TARGET_CLASSIFICATION_ID, targetClassificationIdValue,
                        TO, "2024-11-03"
                )
        );

        paramsTargetIdAndDateFromIncludeFuture.putAll(
                Map.of(
                        FROM, fromDate,
                        TARGET_CLASSIFICATION_ID, targetClassificationIdValue,
                        INCLUDE_FUTURE, TRUE
                )
        );

        paramsTargetIdAndDateFromLanguageEn.putAll(
                Map.of(
                        FROM, fromDate,
                        TARGET_CLASSIFICATION_ID, targetClassificationIdValue,
                        LANGUAGE, EN
                )
        );
        paramsTargetIdAndDateFromLanguageNn.putAll(
                Map.of(
                        FROM, fromDate,
                        TARGET_CLASSIFICATION_ID, targetClassificationIdValue,
                        LANGUAGE, NN
                )
        );

        paramsTargetIdAndDateFromCsvSeparator.putAll(
                Map.of(
                        FROM, fromDate,
                        TARGET_CLASSIFICATION_ID, targetClassificationIdValue,
                        CSV_SEPARATOR, "*"
                )
        );
        paramsTargetIdAndDateFromCsvFields.putAll(
                Map.of(
                        FROM, fromDate,
                        TARGET_CLASSIFICATION_ID, targetClassificationIdValue,
                        CSV_FIELDS,"sourceName,targetName"
                )
        );
    }
}
