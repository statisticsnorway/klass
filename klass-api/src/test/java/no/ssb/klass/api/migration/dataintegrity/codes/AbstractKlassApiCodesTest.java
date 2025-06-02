package no.ssb.klass.api.migration.dataintegrity.codes;

import no.ssb.klass.api.migration.dataintegrity.AbstractKlassApiDataIntegrityTest;
import org.junit.jupiter.api.BeforeAll;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static no.ssb.klass.api.migration.MigrationTestConstants.PRESENTATION_NAME_PATTERN;
import static no.ssb.klass.api.migration.MigrationTestUtils.*;

public abstract class AbstractKlassApiCodesTest extends AbstractKlassApiDataIntegrityTest {

    static String dateFromToMax;

    static LocalDate dateOne;
    static LocalDate dateTwo;
    static String dateFromInRangeString ;
    static String dateToInRangeString;

    static String selectedCode = "3004";
    static String selectedCodes = "3004-3400";
    static String presentationNamePattern = "%7Bcode%7D-%7Buppercase(name)%7D";

    static Integer randomId;

    static String date;

    static Map<String, Object> paramsFrom = new HashMap<>();
    static Map<String, Object> paramsDateInRange = new HashMap<>();
    static Map<String, Object> paramsLanguageNn = new HashMap<>();
    static Map<String, Object> paramsLanguageEn = new HashMap<>();
    static Map<String, Object> paramsIncludeFuture = new HashMap<>();

    static Map<String, Object> paramsLanguageNnAt = new HashMap<>();
    static Map<String, Object> paramsLanguageEnAt = new HashMap<>();
    static Map<String, Object> paramsIncludeFutureAt = new HashMap<>();

    static Map<String, Object> paramsSelectLevel= new HashMap<>();
    static Map<String, Object> paramsSelectLevelAt= new HashMap<>();
    static Map<String, Object> paramsSelectCode = new HashMap<>();
    static Map<String, Object> paramsSelectCodes = new HashMap<>();

    static Map<String, Object> paramsSelectCodeAt = new HashMap<>();
    static Map<String, Object> paramsSelectCodesAt = new HashMap<>();

    static Map<String, Object> paramsDate = new HashMap<>();

    static Map<String, Object> paramsCsvSeparator = new HashMap<>();
    static Map<String, Object> paramsPresentationCodePattern = new HashMap<>();
    static Map<String, Object> paramsCsvFields = new HashMap<>();

    static Map<String, Object> paramsCsvSeparatorAt = new HashMap<>();
    static Map<String, Object> paramsPresentationCodePatternAt = new HashMap<>();
    static Map<String, Object> paramsCsvFieldsAt = new HashMap<>();

    String getCodesPath(Integer id) {
        return CLASSIFICATIONS_PATH + "/"+ id + "/" + CODES;
    }

    String getCodesAtPath(Integer id) {
        return CLASSIFICATIONS_PATH + "/"+ id + "/" + CODES_AT;
    }

    @BeforeAll
    static void beforeAllCodes() {
        dateFromToMax = generateRandomDate(
                LocalDate.of(1900, 1, 1),
                LocalDate.of(2030, 12, 31)).format(formatter);
        dateOne = generateRandomDate(
                LocalDate.of(1900, 1, 1),
                LocalDate.of(2030, 12, 31)
        );
        dateTwo = generateRandomDate(
                LocalDate.of(1900, 1, 1),
                LocalDate.of(2030, 12, 31)
        );

        dateFromInRangeString = dateOne.isBefore(dateTwo) ? dateOne.format(formatter) : dateTwo.format(formatter);
        dateToInRangeString = dateOne.isAfter(dateTwo) ? dateOne.format(formatter) : dateTwo.format(formatter);

        randomId = generateRandomId(lastClassificationId);

        paramsFrom.put(FROM, dateFromToMax);
        paramsDateInRange.putAll(Map.of(FROM, dateFromInRangeString, TO, dateToInRangeString));

        date = generateRandomDate(
                LocalDate.of(1980, 1, 1),
                LocalDate.of(2020, 12, 31)).format(formatter);
        paramsDate.put(DATE, date);

        paramsLanguageEn.putAll(Map.of(FROM, dateFromToMax, LANGUAGE, EN));
        paramsLanguageNn.putAll(Map.of(FROM, dateFromToMax, LANGUAGE, NN));
        paramsIncludeFuture.putAll(Map.of(FROM, dateFromToMax,INCLUDE_FUTURE, TRUE));

        paramsLanguageEnAt.putAll(Map.of(DATE,date, LANGUAGE, EN));
        paramsLanguageNnAt.putAll(Map.of(DATE,date, LANGUAGE, NN));
        paramsIncludeFutureAt.putAll(Map.of(DATE, date,INCLUDE_FUTURE, TRUE));

        paramsSelectCode.putAll(Map.of(FROM, dateFromToMax, SELECT_CODES, selectedCode));
        paramsSelectCodes.putAll(Map.of(FROM, dateFromToMax, SELECT_CODES, selectedCodes));

        paramsSelectLevel.putAll(Map.of(FROM, dateFromToMax, SELECT_LEVEL, 3));
        paramsSelectLevelAt.putAll(Map.of(DATE, date, SELECT_LEVEL, 3));

        paramsSelectCodeAt.putAll(Map.of(DATE, date, SELECT_CODES, selectedCode));
        paramsSelectCodesAt.putAll(Map.of(DATE, date, SELECT_CODES, selectedCodes));

        paramsCsvSeparator.putAll(Map.of(FROM, dateFromToMax, CSV_SEPARATOR, ";"));
        paramsCsvFields.putAll(Map.of(FROM, dateFromToMax, CSV_FIELDS, "name,code"));

        paramsPresentationCodePattern.putAll(Map.of(FROM, dateFromInRangeString, PRESENTATION_NAME_PATTERN, presentationNamePattern));

        paramsCsvSeparatorAt.putAll(Map.of(DATE, date, CSV_SEPARATOR, ";"));
        paramsCsvFieldsAt.putAll(Map.of(DATE, date, CSV_FIELDS, "name,code"));

        paramsPresentationCodePatternAt.putAll(Map.of(DATE, date, PRESENTATION_NAME_PATTERN, presentationNamePattern));

    }
}
