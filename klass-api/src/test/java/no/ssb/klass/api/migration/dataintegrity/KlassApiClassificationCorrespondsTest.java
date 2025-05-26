package no.ssb.klass.api.migration.dataintegrity;

import io.restassured.response.Response;
import no.ssb.klass.api.migration.MigrationTestUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiClassificationCorrespondsTest extends AbstractKlassApiDataIntegrityTest {


    static Map<String, Object> paramsTargetIdAndDateFrom = new HashMap<>();

    static Integer targetClassificationIdValue;
    static String fromDate;

    Response sourceResponse;
    Response targetResponse;

    @BeforeAll
    static void beforeAllCorrespondence() {
        fromDate = "2020-01-01";
        targetClassificationIdValue = 103;
        paramsTargetIdAndDateFrom.put(TARGET_CLASSIFICATION_ID, targetClassificationIdValue);
        paramsTargetIdAndDateFrom.put(RANGE_FROM, fromDate);

    }

    @Test
    void getOneClassificationCorrespondence(){
        int classificationId = 131;
        System.out.println("Start test for ID " + classificationId + " at " + Instant.now());

        String path = getCorrespondsPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsTargetIdAndDateFrom);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsTargetIdAndDateFrom);

        MigrationTestUtils.assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            System.out.println(LOG_MESSAGE_STATUS_CODE + sourceResponse.getStatusCode());
            assertThat(MigrationTestUtils.compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateList(sourceResponse, targetResponse, CORRESPONDENCE_ITEMS);
        }
    }

    String getCorrespondsPath(Integer id) {
        return CLASSIFICATIONS_PATH + "/"+ id + "/" + CORRESPONDS;
    }
}
