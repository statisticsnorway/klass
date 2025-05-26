package no.ssb.klass.api.migration.dataintegrity;

import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static no.ssb.klass.api.migration.MigrationTestConstants.CODES;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiClassificationCorrespondsTest extends AbstractKlassApiDataIntegrityTest {


    static Map<String, Object> paramsTargetIdAndDateFrom = new HashMap<>();

    static Integer targetClassificationIdValue;
    static String fromDate;

    Response sourceResponse;
    Response targetResponse;

    List<?> sourceFields;
    List<?> targetFields;

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

        sourceResponse = klassApiMigrationClient.getFromSourceApi(CLASSIFICATIONS_PATH + "/"+ classificationId + "/" + CORRESPONDS, paramsTargetIdAndDateFrom);
        targetResponse = klassApiMigrationClient.getFromTargetApi(CLASSIFICATIONS_PATH + "/"+ classificationId + "/" + CORRESPONDS, paramsTargetIdAndDateFrom);

        if(sourceResponse.getStatusCode() != 200) {
            System.out.println(LOG_MESSAGE_STATUS_CODE + sourceResponse.getStatusCode());
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            System.out.println(LOG_MESSAGE_STATUS_CODE + sourceResponse.getStatusCode());
            sourceFields = sourceResponse.path(CORRESPONDENCE_ITEMS);
            targetFields = targetResponse.path(CORRESPONDENCE_ITEMS);
            System.out.println(sourceFields.size() + "->" + targetFields.size());
            assertThat(sourceFields).withFailMessage(FAIL_MESSAGE, CODES, sourceFields, targetFields).isEqualTo(targetFields);
        }
    }
}
