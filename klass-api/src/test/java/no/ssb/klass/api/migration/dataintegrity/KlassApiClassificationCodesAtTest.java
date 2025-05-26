package no.ssb.klass.api.migration.dataintegrity;


import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiClassificationCodesAtTest extends AbstractKlassApiDataIntegrityTest{

    static Map<String, Object> paramsDate = new HashMap<>();

    List<?> sourceFields;
    List<?> targetFields;

    Response sourceResponse;
    Response targetResponse;

    static String date;

    @BeforeAll
    static void beforeAllCodesAt() {
        date = generateRandomDate(
                LocalDate.of(1980, 1, 1),
                LocalDate.of(2020, 12, 31)).format(formatter);
        paramsDate.put(DATE, date);
    }

    @Test
    void getOneClassificationCodesAt(){
        Integer classificationId = 2;
        date = "2001-01-01";

        Map<String, Object> paramDate = new HashMap<>();
        paramDate.put(DATE, date);

        System.out.println("Start test for ID " + classificationId + " at " + Instant.now());

        sourceResponse = klassApiMigrationClient.getFromSourceApi(CLASSIFICATIONS_PATH + "/"+ classificationId + "/" + CODES_AT, paramDate);
        targetResponse = klassApiMigrationClient.getFromTargetApi(CLASSIFICATIONS_PATH + "/"+ classificationId + "/" + CODES_AT, paramDate);

        if(sourceResponse.getStatusCode() != 200) {
            System.out.println(LOG_MESSAGE_STATUS_CODE + sourceResponse.getStatusCode());
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            System.out.println(LOG_MESSAGE_STATUS_CODE + sourceResponse.getStatusCode());
            sourceFields = sourceResponse.path(CODES);
            targetFields = targetResponse.path(CODES);
            System.out.println(sourceFields.size() + "->" + targetFields.size());
            assertThat(sourceFields).withFailMessage(FAIL_MESSAGE, CODES, sourceFields, targetFields).isEqualTo(targetFields);
        }
    }

    @ParameterizedTest
    @MethodSource("rangeProviderClassificationIds")
    void getClassificationCodesAt(Integer classificationId) {

        System.out.println("Start test for ID " + classificationId + " at " + Instant.now());

        System.out.println("Fetch from source api");
        sourceResponse = klassApiMigrationClient.getFromSourceApi(CLASSIFICATIONS_PATH + "/"+ classificationId + "/" + CODES_AT, paramsDate);

        System.out.println("Fetch from target api");
        targetResponse = klassApiMigrationClient.getFromTargetApi(CLASSIFICATIONS_PATH + "/"+ classificationId + "/" + CODES_AT, paramsDate);

        if(sourceResponse.getStatusCode() != 200) {
            System.out.println(LOG_MESSAGE_STATUS_CODE + sourceResponse.getStatusCode());
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            System.out.println(LOG_MESSAGE_STATUS_CODE + sourceResponse.getStatusCode());
            sourceFields = sourceResponse.path(CODES);
            targetFields = targetResponse.path(CODES);
            System.out.println(sourceFields.size() + "->" + targetFields.size());
            assertThat(sourceFields).withFailMessage(FAIL_MESSAGE, CODES, sourceFields, targetFields).isEqualTo(targetFields);
        }

        System.out.println("End test for ID " + classificationId + " at " + Instant.now());

    }

    static Stream<Integer> rangeProviderClassificationIds() {
        return IntStream.rangeClosed(0, 652).boxed();
    }

    String getCodesAtPath(Integer id) {
        return CLASSIFICATIONS_PATH + "/" + id;
    }

}
