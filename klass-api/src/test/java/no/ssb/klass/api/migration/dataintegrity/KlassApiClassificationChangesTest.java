package no.ssb.klass.api.migration.dataintegrity;

import io.restassured.response.Response;
import no.ssb.klass.api.migration.MigrationTestUtils;
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

public class KlassApiClassificationChangesTest extends AbstractKlassApiDataIntegrityTest {


    static String dateFromToMax;

    static Map<String, Object> paramsDate = new HashMap<>();

    Response sourceResponse;
    Response targetResponse;

    @BeforeAll
    static void beforeAllCodes() {
        dateFromToMax = MigrationTestUtils.generateRandomDate(
                LocalDate.of(1951, 1, 1),
                LocalDate.of(2024, 12, 31)).format(formatter);


        paramsDate.put(RANGE_FROM, dateFromToMax);

    }

    @Test
    void getOneClassificationChanges(){
        Integer classificationId = 6;

        System.out.println("Start test for ID " + classificationId + " at " + Instant.now());

        String path = getChangesPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsDate);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsDate);

        MigrationTestUtils.assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            System.out.println(LOG_MESSAGE_STATUS_CODE + sourceResponse.getStatusCode());
            assertThat(MigrationTestUtils.compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateList(sourceResponse, targetResponse, CODE_CHANGES);
        }
    }


    @ParameterizedTest
    @MethodSource("rangeProviderClassificationIds")
    void getManyClassificationChanges(Integer classificationId) {

        System.out.println("Start test for ID " + classificationId + " at " + Instant.now());

        String path= getChangesPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsDate);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsDate);

        MigrationTestUtils.assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            System.out.println(LOG_MESSAGE_STATUS_CODE + sourceResponse.getStatusCode());
            assertThat(MigrationTestUtils.compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateList(sourceResponse, targetResponse, CODE_CHANGES);
        }

        System.out.println("End test for ID " + classificationId + " at " + Instant.now());
    }

    static Stream<Integer> rangeProviderClassificationIds() {
        return IntStream.rangeClosed(0, 652).boxed();
    }

    String getChangesPath(Integer id) {
        return CLASSIFICATIONS_PATH + "/"+ id + "/" + CHANGES;
    }
}
