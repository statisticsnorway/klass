package no.ssb.klass.api.migration.dataintegrity.variant;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static no.ssb.klass.api.migration.MigrationTestUtils.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiVariantByIdXmlTest extends AbstractKlassApiVariantTest {

    @Test
    void getOneVariantById() {
        int variantId = 1111;
        String path = getVariantByIdPath(variantId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi( path, null,APPLICATION_XML);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi( path, null,APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(variantId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateXmlList(path, sourceResponse, targetResponse, CLASSIFICATION_VARIANT_CLASSIFICATION_ITEMS);
        }
    }

}
