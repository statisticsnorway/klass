package no.ssb.klass.api.migration.dataintegrity.variant;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static no.ssb.klass.api.migration.MigrationTestUtils.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiVariantByIdCsvTest extends AbstractKlassApiVariantTest {

    @Test
    void getOneVariantById() {
        int variantId = 1111;
        String path = getVariantByIdPath(variantId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi( path, null,TEXT_CSV);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi( path, null,TEXT_CSV);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(variantId, sourceResponse, targetResponse)).isTrue();
        }
        else{
           validateCSVDocument(path, sourceResponse, targetResponse);
        }
    }

}
