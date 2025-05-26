package no.ssb.klass.api.migration.dataintegrity;

import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiClassificationVariantTest extends AbstractKlassApiDataIntegrityTest {

    static String variantNameId84 = "Klimagasser";
    static String variantDateId84 = "2015-01-01";

    static Map<String, Object> paramsVariantDateFrom = new HashMap<>();

    List<?> sourceFields;
    List<?> targetFields;

    Response sourceResponse;
    Response targetResponse;


    @BeforeAll
    static void beforeAllVariant() {
        paramsVariantDateFrom.put(VARIANT_NAME, variantNameId84);
        paramsVariantDateFrom.put(RANGE_FROM, variantDateId84);

    }


    @Test
    void getOneClassificationVariant(){
        Integer classificationId = 84;
        System.out.println("Start test for ID " + classificationId + " at " + Instant.now());

        String path = getVariantPath(classificationId);

        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsVariantDateFrom);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsVariantDateFrom);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        assertThat(sourceResponse.getStatusCode()).withFailMessage(
                FAIL_MESSAGE, path, sourceResponse.getStatusCode(), targetResponse.getStatusCode()).isEqualTo(targetResponse.getStatusCode());

        if(sourceResponse.getStatusCode() != 200) {
            System.out.println(LOG_MESSAGE_STATUS_CODE + sourceResponse.getStatusCode());
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            sourceFields = sourceResponse.path(CODES);
            targetFields = targetResponse.path(CODES);
            System.out.println(sourceFields.size() + "->" + targetFields.size());
            assertThat(sourceFields).withFailMessage(FAIL_MESSAGE, CODES, sourceFields, targetFields).isEqualTo(targetFields);
        }
    }

    String getVariantPath(Integer id) {
        return CLASSIFICATIONS_PATH + "/"+ id + "/" + VARIANT;
    }

}
