package no.ssb.klass.api.migration.dataintegrity;

import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static no.ssb.klass.api.migration.MigrationTestUtils.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiClassificationChangesTest extends AbstractKlassApiDataIntegrityTest {

    static LocalDate date;

    static String dateFrom;
    static String dateTo;

    static Map<String, Object> paramsLanguageEn = new HashMap<>();
    static Map<String, Object> paramsLanguageNn = new HashMap<>();

    static Map<String, Object> paramsDate = new HashMap<>();
    static Map<String, Object> paramsDateInRange = new HashMap<>();
    static Map<String, Object> paramsCsvSeparator = new HashMap<>();

    Response sourceResponse;
    Response targetResponse;

    @BeforeAll
    static void beforeAllCodes() {
        date = generateRandomDate(
                LocalDate.of(1951, 1, 1),
                LocalDate.of(2024, 12, 31));
        dateFrom = date.format(formatter);
        dateTo = generateRandomDate(date, LocalDate.of(2024, 12, 31)).format(formatter);
        paramsDateInRange.putAll(Map.of(FROM, dateFrom, TO, dateTo));
        paramsDate.put(FROM, dateFrom);
        paramsLanguageEn.put(LANGUAGE, EN);
        paramsLanguageNn.put(LANGUAGE, NN);
        paramsCsvSeparator.putAll(Map.of(FROM, dateFrom, CSV_SEPARATOR, ";"));

    }

    @Test
    void getOneClassificationChanges(){
        Integer classificationId = 6;

        System.out.println("Start test for ID " + classificationId + " at " + Instant.now());

        String path = getChangesPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsDate,null);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsDate,null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateList(sourceResponse, targetResponse, CODE_CHANGES);
        }
    }

    @Test
    void getOneClassificationChangesCsv(){
        Integer classificationId = 6;

        System.out.println("Start test for ID " + classificationId + " at " + Instant.now());

        String path = getChangesPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsDate,TEXT_CSV);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsDate,TEXT_CSV);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateCSV(sourceResponse, targetResponse);
        }
    }


    @ParameterizedTest
    @MethodSource("rangeProviderClassificationIds")
    void getClassificationChanges(Integer classificationId) {

        System.out.println("Start test for ID " + classificationId + " at " + Instant.now());

        String path= getChangesPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsDate,null);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsDate,null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateList(sourceResponse, targetResponse, CODE_CHANGES);
        }

        System.out.println("End test for ID " + classificationId + " at " + Instant.now());
    }

    @ParameterizedTest
    @MethodSource("rangeProviderClassificationIds")
    void getClassificationChangesXml(Integer classificationId) {

        String path= getChangesPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsDate, APPLICATION_XML);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsDate, APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateXml(sourceResponse, targetResponse);
        }

    }

    @ParameterizedTest
    @MethodSource("rangeProviderClassificationIds")
    void getClassificationChangesCsv(Integer classificationId) {

        String path= getChangesPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsDate,TEXT_CSV);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsDate,TEXT_CSV);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateCSV(sourceResponse, targetResponse);
        }

    }

    @ParameterizedTest
    @MethodSource("rangeProviderClassificationIds")
    void getClassificationChangesCsvParams(Integer classificationId) {

        String path= getChangesPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsCsvSeparator,TEXT_CSV);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsCsvSeparator,TEXT_CSV);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateCSV(sourceResponse, targetResponse);
        }

    }

    @ParameterizedTest
    @MethodSource("rangeProviderClassificationIds")
    void getClassificationChangesDatesInRange(Integer classificationId) {

        String path= getChangesPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsDateInRange,null);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsDateInRange,null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateList(sourceResponse, targetResponse, CODE_CHANGES);
        }
    }


    @ParameterizedTest
    @MethodSource("rangeProviderClassificationIds")
    void getClassificationChangesEnglish(Integer classificationId) {

        String path= getChangesPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsLanguageEn,null);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsLanguageEn,null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateList(sourceResponse, targetResponse, CODE_CHANGES);
        }
    }

    @ParameterizedTest
    @MethodSource("rangeProviderClassificationIds")
    void getClassificationChangesNewNorwegian(Integer classificationId) {

        String path= getChangesPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsLanguageNn,null);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsLanguageNn,null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateList(sourceResponse, targetResponse, CODE_CHANGES);
        }
    }

    static Stream<Integer> rangeProviderClassificationIds() {
        return IntStream.rangeClosed(0, lastClassificationId).boxed();
    }

    String getChangesPath(Integer id) {
        return CLASSIFICATIONS_PATH + "/"+ id + "/" + CHANGES;
    }
}
