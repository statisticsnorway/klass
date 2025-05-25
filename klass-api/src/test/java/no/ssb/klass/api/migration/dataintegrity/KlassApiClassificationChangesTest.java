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
import static no.ssb.klass.api.migration.MigrationTestConstants.CODES;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class KlassApiClassificationChangesTest extends AbstractKlassApiDataIntegrityTest {


    static String dateFromToMax;

    static Map<String, Object> paramsDate = new HashMap<>();

    List<?> sourceCodeChanges;
    List<?> targetCodeChanges;

    Response sourceResponse;
    Response targetResponse;

    @BeforeAll
    static void beforeAllCodes() {
        dateFromToMax = generateRandomDate(
                LocalDate.of(1951, 1, 1),
                LocalDate.of(2026, 12, 31)).format(formatter);


        paramsDate.put(RANGE_FROM, dateFromToMax);

    }

    @Test
    void getOneClassificationWithChanges(){
        Integer classificationId = 6;
        System.out.println("Start test for ID " + classificationId + " at " + Instant.now());

        sourceResponse = klassApiMigrationClient.getFromSourceApi(CLASSIFICATIONS_PATH + "/"+ classificationId + "/" + CHANGES, paramsDate);
        targetResponse = klassApiMigrationClient.getFromTargetApi(CLASSIFICATIONS_PATH + "/"+ classificationId + "/" + CHANGES, paramsDate);

        if(sourceResponse.getStatusCode() != 200) {
            System.out.println(LOG_MESSAGE_STATUS_CODE + sourceResponse.getStatusCode());
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            System.out.println(LOG_MESSAGE_STATUS_CODE + sourceResponse.getStatusCode());
            sourceCodeChanges = sourceResponse.path(CODE_CHANGES);
            targetCodeChanges = targetResponse.path(CODE_CHANGES);
            System.out.println(sourceCodeChanges.size() + "->" + targetCodeChanges.size());
            assertThat(sourceCodeChanges).withFailMessage(FAIL_MESSAGE, CODES, sourceCodeChanges, targetCodeChanges).isEqualTo(targetCodeChanges);
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
            System.out.println(LOG_MESSAGE_STATUS_CODE + sourceResponse.getStatusCode());
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            System.out.println(LOG_MESSAGE_STATUS_CODE + sourceResponse.getStatusCode());
            sourceCodeChanges = sourceResponse.path(CODE_CHANGES);
            targetCodeChanges = targetResponse.path(CODE_CHANGES);
            assertThat(sourceCodeChanges).withFailMessage(FAIL_MESSAGE, CODES, sourceCodeChanges, targetCodeChanges).isEqualTo(targetCodeChanges);
        }

        System.out.println("End test for ID " + classificationId + " at " + Instant.now());
    }

    static Stream<Integer> rangeProviderClassificationIds() {
        return IntStream.rangeClosed(0, 652).boxed();
    }
}
