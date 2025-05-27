package no.ssb.klass.api.migration.dataintegrity;

import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static no.ssb.klass.api.migration.MigrationTestConstants.CODES;
import static no.ssb.klass.api.migration.MigrationTestUtils.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiClassificationVariantAtTest extends AbstractKlassApiDataIntegrityTest{

    static String variantNameId84 = "Klimagasser";
    static String variantDateId84 = "2015-01-01";

    static Map<String, Object> paramsVariantDate = new HashMap<>();

    Response sourceResponse;
    Response targetResponse;


    @BeforeAll
    static void beforeAllVariant() {
        paramsVariantDate.put(VARIANT_NAME, variantNameId84);
        paramsVariantDate.put(DATE, variantDateId84);

    }


    @Test
    void getOneClassificationVariantAt(){
        Integer classificationId = 84;
        System.out.println("Start test for ID " + classificationId + " at " + Instant.now());

        String path = getVariantAtPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsVariantDate);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsVariantDate);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(),path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateList(sourceResponse, targetResponse, CODES);
        }
    }

    String getVariantAtPath(Integer id) {
        return CLASSIFICATIONS_PATH + "/"+ id + "/" + VARIANT_AT;
    }
}
