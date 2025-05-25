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

    Object sourceFields;
    Object targetFields;

    Response sourceResponse;
    Response targetResponse;

    @BeforeAll
    static void beforeAllCodes() {
         dateFromToMax = generateRandomDate().format(formatter);
         dateFromInRange = generateRandomDate();
         dateToInRange = generateRandomDate();

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

        sourceResponse = klassApiMigrationClient.getFromSourceApi(CLASSIFICATIONS_PATH + "/"+ classificationId + "/" + CODES, paramsDate);
        targetResponse = klassApiMigrationClient.getFromTargetApi(CLASSIFICATIONS_PATH + "/"+ classificationId + "/" + CODES, paramsDate);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            sourceFields = sourceResponse.path(CODES);
            assertThat(sourceFields).withFailMessage(FAIL_MESSAGE, CODES, sourceFields, targetFields).isEqualTo(targetResponse.path(CODES));
        }
    }


    @ParameterizedTest
    @MethodSource("rangeProviderClassificationIds")
    void getClassificationCodesFromDate(Integer classificationId) {
        assumeTrue(classificationId > 6);

        System.out.println("Start test for ID " + classificationId + " at " + Instant.now());

        System.out.println("Fetch from source api");
        sourceResponse = klassApiMigrationClient.getFromSourceApi(CLASSIFICATIONS_PATH + "/"+ classificationId + "/" + CODES, paramsDate);

        System.out.println("Fetch from target api");
        targetResponse = klassApiMigrationClient.getFromTargetApi(CLASSIFICATIONS_PATH + "/"+ classificationId + "/" + CODES, paramsDate);

        if(sourceResponse.getStatusCode() != 200) {
            System.out.println("Entering error");
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            System.out.println("Entering 200");
            sourceFields = sourceResponse.path(CODES);
            targetFields = targetResponse.path(CODES);
            assertThat(sourceFields).withFailMessage(FAIL_MESSAGE, CODES, sourceFields, targetFields).isEqualTo(targetFields);
        }

        System.out.println("End test for ID " + classificationId + " at " + Instant.now());
    }

    @ParameterizedTest
    @MethodSource("rangeProviderClassificationIds")
    void getClassificationCodesInRangeDate(Integer classificationId) {
        assumeTrue(classificationId > 6);
        System.out.println("Start test for ID " + classificationId + " at " + Instant.now());

        sourceResponse = klassApiMigrationClient.getFromSourceApi(CLASSIFICATIONS_PATH + "/"+ classificationId + "/" + CODES, paramsDateInRange);
        targetResponse = klassApiMigrationClient.getFromTargetApi(CLASSIFICATIONS_PATH + "/"+ classificationId + "/" + CODES, paramsDateInRange);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            sourceFields = sourceResponse.path(CODES);
            assertThat(sourceFields).withFailMessage(FAIL_MESSAGE, CODES, sourceFields, targetFields).isEqualTo(targetResponse.path(CODES));
        }
    }

    @Test
    void getClassificationCodesInvalidOrderDate() {
        Integer classificationId = randomId;
        System.out.println("Start test for ID " + classificationId + " at " + Instant.now());

        String firstDate = (dateFromInRange.isAfter(dateToInRange) ? dateFromInRangeString : dateToInRangeString);
        String secondDate = (dateFromInRange.isBefore(dateToInRange) ? dateFromInRangeString : dateToInRangeString);

        paramsDateInRange.put(RANGE_FROM, firstDate);
        paramsDateInRange.put(RANGE_TO, secondDate);

        sourceResponse = klassApiMigrationClient.getFromSourceApi(CLASSIFICATIONS_PATH + "/"+ classificationId + "/" + CODES, paramsDateInRange);
        targetResponse = klassApiMigrationClient.getFromTargetApi(CLASSIFICATIONS_PATH + "/"+ classificationId + "/" + CODES, paramsDateInRange);

        if (sourceResponse.getStatusCode() != 200) {
                assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        } else {
                sourceFields = sourceResponse.path(CODES);
                assertThat(sourceFields).withFailMessage(FAIL_MESSAGE, CODES, sourceFields, targetFields).isEqualTo(targetResponse.path(CODES));
        }

    }

    static Stream<Integer> rangeProviderClassificationIds() {
        return IntStream.rangeClosed(0, numClassifications + 2).boxed();
    }
}
