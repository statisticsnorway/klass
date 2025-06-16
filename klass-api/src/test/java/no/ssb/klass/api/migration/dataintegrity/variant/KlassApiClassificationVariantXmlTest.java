package no.ssb.klass.api.migration.dataintegrity.variant;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static no.ssb.klass.api.migration.MigrationTestConstants.APPLICATION_XML;
import static no.ssb.klass.api.migration.MigrationTestConstants.CODES;
import static no.ssb.klass.api.migration.MigrationTestUtils.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiClassificationVariantXmlTest extends AbstractKlassApiVariantTest {

    @Test
    void getOneClassificationVariant(){
        Integer classificationId = 84;
        System.out.println("Start test for ID " + classificationId + " at " + Instant.now());

        String path = getVariantPath(classificationId);

        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsVariantDateFrom,APPLICATION_XML);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsVariantDateFrom,APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);


        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateList(sourceResponse, targetResponse, CODES);
        }
    }

}
