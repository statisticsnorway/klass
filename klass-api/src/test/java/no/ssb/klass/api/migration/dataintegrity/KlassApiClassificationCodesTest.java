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
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class KlassApiClassificationCodesTest extends AbstractKlassApiDataIntegrityTest {

    static String dateFromToMax;

    static LocalDate dateOne;
    static LocalDate dateTwo;
    static String dateFromInRangeString ;
    static String dateToInRangeString;

    static Integer randomId;

    static Map<String, Object> paramsDate = new HashMap<>();
    static Map<String, Object> paramsDateInRange = new HashMap<>();

    Response sourceResponse;
    Response targetResponse;

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

         randomId = generateRandomId(150);

         paramsDate.put(FROM, dateFromToMax);
         paramsDateInRange.put(FROM, dateFromInRangeString);
         paramsDateInRange.put(TO, dateToInRangeString);
    }

    @Test
    void getOneClassificationWithCodes(){
        Integer classificationId = 6;
        System.out.println("Start test for ID " + classificationId + " at " + Instant.now());

        String path = getCodesPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsDate,null);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsDate,null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateList(sourceResponse, targetResponse, CODES);
        }
    }

    @Test
    void getOneClassificationWithCodesXml(){
        Integer classificationId = 6;
        System.out.println("Start test for ID " + classificationId + " at " + Instant.now());

        String path = getCodesPath(classificationId);
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

    @Test
    void getOneClassificationWithCodesCsv(){
        Integer classificationId = 6;
        System.out.println("Start test for ID " + classificationId + " at " + Instant.now());

        String path = getCodesPath(classificationId);
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
    void getClassificationCodesFromDate(Integer classificationId) {
        // For now skipping because of some items size
        assumeTrue(classificationId > 6);

        System.out.println("Start test for ID " + classificationId + " at " + Instant.now());

        String path = getCodesPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsDate,null);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsDate,null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateList(sourceResponse, targetResponse, CODES);
        }

        System.out.println("End test for ID " + classificationId + " at " + Instant.now());
    }

    @ParameterizedTest
    @MethodSource("rangeProviderClassificationIds")
    void getClassificationCodesFromDateXml(Integer classificationId) {
        // For now skipping because of some items size
        assumeTrue(classificationId > 6);

        System.out.println("Start test for ID " + classificationId + " at " + Instant.now());

        String path = getCodesPath(classificationId);
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

        System.out.println("End test for ID " + classificationId + " at " + Instant.now());
    }

    @ParameterizedTest
    @MethodSource("rangeProviderClassificationIds")
    void getClassificationCodesFromDateCsv(Integer classificationId) {
        // For now skipping because of some items size
        assumeTrue(classificationId > 6);

        System.out.println("Start test for ID " + classificationId + " at " + Instant.now());

        String path = getCodesPath(classificationId);
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

        System.out.println("End test for ID " + classificationId + " at " + Instant.now());
    }

    @ParameterizedTest
    @MethodSource("rangeProviderClassificationIds")
    void getClassificationCodesInRangeDate(Integer classificationId) {

        // For now skipping because of some items size
        assumeTrue(classificationId > 6);
        System.out.println("Start test for ID " + classificationId + " at " + Instant.now());

        String path = getCodesPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsDateInRange,null);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsDateInRange,null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateList(sourceResponse, targetResponse, CODES);
        }
    }

    @Test
    void getClassificationCodesInvalidOrderDate() {
        Integer classificationId = 11;
        System.out.println("Start test for ID " + classificationId + " at " + Instant.now());

        String firstDate = "2025-01-01";
        String secondDate = "1995-11-12";

        paramsDateInRange.put(FROM, firstDate);
        paramsDateInRange.put(TO, secondDate);

        String path = getCodesPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsDateInRange,null);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsDateInRange,null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);
        assertThat(sourceResponse.getStatusCode()).isEqualTo(400);
        assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
    }

    @Test
    void getClassificationCodesInvalidOrderDateXml() {
        Integer classificationId = 11;
        System.out.println("Start test for ID " + classificationId + " at " + Instant.now());

        String firstDate = "2025-01-01";
        String secondDate = "1995-11-12";

        paramsDateInRange.put(FROM, firstDate);
        paramsDateInRange.put(TO, secondDate);

        String path = getCodesPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsDateInRange, APPLICATION_XML);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsDateInRange, APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);
        assertThat(sourceResponse.getStatusCode()).isEqualTo(400);
        assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
    }

    @Test
    void getClassificationCodesInvalidOrderDateCsv() {
        Integer classificationId = 11;
        System.out.println("Start test for ID " + classificationId + " at " + Instant.now());

        String firstDate = "2025-01-01";
        String secondDate = "1995-11-12";

        paramsDateInRange.put(FROM, firstDate);
        paramsDateInRange.put(TO, secondDate);

        String path = getCodesPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsDateInRange,TEXT_CSV);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsDateInRange,TEXT_CSV);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);
        assertThat(sourceResponse.getStatusCode()).isEqualTo(400);
        assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
    }



    static Stream<Integer> rangeProviderClassificationIds() {
        return IntStream.rangeClosed(0, 652).boxed();
    }

    String getCodesPath(Integer id) {
        return CLASSIFICATIONS_PATH + "/"+ id + "/" + CODES;
    }
}
