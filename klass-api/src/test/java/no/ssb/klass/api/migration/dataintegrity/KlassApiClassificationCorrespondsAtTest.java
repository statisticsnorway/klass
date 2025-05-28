package no.ssb.klass.api.migration.dataintegrity;

import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static no.ssb.klass.api.migration.MigrationTestUtils.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiClassificationCorrespondsAtTest extends KlassApiClassificationCorrespondsTest {

    static Map<String, Object> paramsTargetIdAndDate = new HashMap<>();

    static Integer targetClassificationIdValue;
    static String date;

    Response sourceResponse;
    Response targetResponse;

    @BeforeAll
    static void beforeAllCorrespondence() {
        date = "2018-01-01";
        targetClassificationIdValue = 103;
        paramsTargetIdAndDate.put(TARGET_CLASSIFICATION_ID, targetClassificationIdValue);
        paramsTargetIdAndDate.put(DATE, date);

    }

    @Test
    void getOneClassificationCorrespondenceAt(){
        int classificationId = 131;
        System.out.println("Start test for ID " + classificationId + " at " + Instant.now());

        String path = getCorrespondsAtPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsTargetIdAndDate,null);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsTargetIdAndDate,null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateList(sourceResponse, targetResponse, CORRESPONDENCE_ITEMS);
        }
    }

    String getCorrespondsAtPath(Integer id) {
        return CLASSIFICATIONS_PATH + "/"+ id + "/" + CORRESPONDS_AT;
    }
}
