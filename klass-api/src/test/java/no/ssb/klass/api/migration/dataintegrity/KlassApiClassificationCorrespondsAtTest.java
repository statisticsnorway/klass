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

public class KlassApiClassificationCorrespondsAtTest extends KlassApiClassificationCorrespondsTest {

    static Map<String, Object> paramsTargetIdAndDate = new HashMap<>();

    static Integer targetClassificationIdValue;
    static String fromDate;
    static String date;

    Response sourceResponse;
    Response targetResponse;

    List<?> sourceFields;
    List<?> targetFields;

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

        sourceResponse = klassApiMigrationClient.getFromSourceApi(CLASSIFICATIONS_PATH + "/"+ classificationId + "/" + CORRESPONDS_AT, paramsTargetIdAndDate);
        targetResponse = klassApiMigrationClient.getFromTargetApi(CLASSIFICATIONS_PATH + "/"+ classificationId + "/" + CORRESPONDS_AT, paramsTargetIdAndDate);

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
