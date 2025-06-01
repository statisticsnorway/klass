package no.ssb.klass.api.migration.dataintegrity.variant;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static no.ssb.klass.api.migration.MigrationTestConstants.CODES;
import static no.ssb.klass.api.migration.MigrationTestUtils.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiClassificationVariantAtTest extends AbstractKlassApiVariantTest {

    @Test
    void getOneClassificationVariantAt(){
        Integer classificationId = 84;
        System.out.println("Start test for ID " + classificationId + " at " + Instant.now());

        String path = getVariantAtPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsVariantDate,null);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsVariantDate,null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(),path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateList(sourceResponse, targetResponse, CODES);
        }
    }
}
