package no.ssb.klass.api.migration.dataintegrity;

import io.restassured.RestAssured;
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
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class KlassApiClassificationCodesTest extends AbstractKlassApiDataIntegrityTest {

    static String dateFromToMax;

    static LocalDate dateFromInRange ;
    static LocalDate dateToInRange;
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
                 LocalDate.of(1800, 1, 1),
                 LocalDate.of(2030, 12, 31)).format(formatter);
         dateFromInRange = generateRandomDate(
                 LocalDate.of(1800, 1, 1),
                 LocalDate.of(2030, 12, 31)
         );
         dateToInRange = generateRandomDate(
                 LocalDate.of(1800, 1, 1),
                 LocalDate.of(2030, 12, 31)
         );

         dateToInRangeString = dateFromInRange.format(formatter);
         dateToInRangeString = dateToInRange.format(formatter);

         randomId = generateRandomId(150);

         paramsDate.put(RANGE_FROM, dateFromToMax);
         paramsDateInRange.put(RANGE_FROM, dateFromInRangeString);
         paramsDateInRange.put(RANGE_TO, dateToInRangeString);

        RestAssured
                .given()
                .baseUri("http://localhost:8081")
                .basePath("/api/klass/v1")
                .get("/classifications")
                .then()
                .statusCode(200);

        RestAssured
                .given()
                .baseUri("http://localhost:8080")
                .basePath("/api/klass/v1")
                .get("/classifications")
                .then()
                .statusCode(200);
    }

    @Test
    void getOneClassificationWithCodes(){
        Integer classificationId = 6;
        System.out.println("Start test for ID " + classificationId + " at " + Instant.now());

        String path = getCodesPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsDate);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsDate);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            System.out.println(LOG_MESSAGE_STATUS_CODE + sourceResponse.getStatusCode());
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateList(sourceResponse, targetResponse, CODES);
        }
    }


    @ParameterizedTest
    @MethodSource("rangeProviderClassificationIds")
    void getClassificationCodesFromDate(Integer classificationId) {
        // For now skipping because of some items size
        assumeTrue(classificationId > 6);

        System.out.println("Start test for ID " + classificationId + " at " + Instant.now());

        String path = getCodesPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsDate);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsDate);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            System.out.println(LOG_MESSAGE_STATUS_CODE + sourceResponse.getStatusCode());
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateList(sourceResponse, targetResponse, CODES);
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
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsDateInRange);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsDateInRange);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            System.out.println(LOG_MESSAGE_STATUS_CODE + sourceResponse.getStatusCode());
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

        paramsDateInRange.put(RANGE_FROM, firstDate);
        paramsDateInRange.put(RANGE_TO, secondDate);

        String path = getCodesPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsDateInRange);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsDateInRange);

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
