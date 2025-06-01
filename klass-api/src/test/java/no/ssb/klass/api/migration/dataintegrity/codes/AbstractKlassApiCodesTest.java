package no.ssb.klass.api.migration.dataintegrity.codes;

import io.restassured.response.Response;
import no.ssb.klass.api.migration.dataintegrity.AbstractKlassApiDataIntegrityTest;
import org.junit.jupiter.api.BeforeAll;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static no.ssb.klass.api.migration.MigrationTestConstants.PRESENTATION_NAME_PATTERN;
import static no.ssb.klass.api.migration.MigrationTestUtils.*;

public class AbstractKlassApiCodesTest extends AbstractKlassApiDataIntegrityTest {

    static String dateFromToMax;

    static LocalDate dateOne;
    static LocalDate dateTwo;
    static String dateFromInRangeString ;
    static String dateToInRangeString;

    static String selectedCode = "3004";
    static String selectedCodes = "3004-3400";
    static String presentationNamePattern = "%7Bcode%7D-%7Buppercase(name)%7D";

    static Integer randomId;

    static Map<String, Object> paramsDate = new HashMap<>();
    static Map<String, Object> paramsDateInRange = new HashMap<>();
    static Map<String, Object> paramsLanguageNn = new HashMap<>();
    static Map<String, Object> paramsLanguageEn = new HashMap<>();
    static Map<String, Object> paramsIncludeFuture = new HashMap<>();

    static Map<String, Object> paramsSelectLevel= new HashMap<>();
    static Map<String, Object> paramsSelectCode = new HashMap<>();
    static Map<String, Object> paramsSelectCodes = new HashMap<>();

    static Map<String, Object> paramsCsvSeparator = new HashMap<>();

    static Map<String, Object> paramsPresentationCodePattern = new HashMap<>();
    static Map<String, Object> paramsCsvFields = new HashMap<>();


    Response sourceResponse;
    Response targetResponse;

    static Stream<Integer> rangeProviderClassificationIds() {
        return IntStream.rangeClosed(0, lastClassificationId).boxed();
    }

    static String getCodesPath(Integer id) {
        return CLASSIFICATIONS_PATH + "/"+ id + "/" + CODES;
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

        paramsDate.put(FROM, dateFromToMax);
        paramsDateInRange.putAll(Map.of(FROM, dateFromInRangeString, TO, dateToInRangeString));

        paramsLanguageEn.putAll(Map.of(FROM, dateFromToMax, LANGUAGE, EN));
        paramsLanguageNn.putAll(Map.of(FROM, dateFromToMax, LANGUAGE, NN));
        paramsIncludeFuture.putAll(Map.of(FROM, dateFromToMax,INCLUDE_FUTURE, TRUE));
        paramsSelectCode.putAll(Map.of(FROM, dateFromToMax, SELECT_CODES, selectedCode));
        paramsSelectCodes.putAll(Map.of(FROM, dateFromToMax, SELECT_CODES, selectedCodes));
        paramsSelectLevel.putAll(Map.of(FROM, dateFromToMax, SELECT_LEVEL, 3));

        paramsPresentationCodePattern.putAll(Map.of(FROM, dateFromInRangeString, PRESENTATION_NAME_PATTERN, presentationNamePattern));

    }
}
