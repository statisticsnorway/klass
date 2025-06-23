package no.ssb.klass.api.migration.dataintegrity.variant;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static no.ssb.klass.api.migration.MigrationTestUtils.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiVariantByIdJsonTest extends AbstractKlassApiVariantTest {

    @Test
    void getOneVariantById() {
        int variantId = 1111;
        String path = getVariantByIdPath(variantId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi( path, null,null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi( path, null,null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(variantId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateItems(sourceResponse, targetResponse, pathNamesVariantById);
            validateList(sourceResponse, targetResponse, PUBLISHED);
            validateList(sourceResponse, targetResponse, CORRESPONDENCE_TABLES);
            validateList(sourceResponse, targetResponse, CHANGELOGS);
            validateList(sourceResponse, targetResponse, LEVELS);
            validateList(sourceResponse, targetResponse, CLASSIFICATION_ITEMS);
        }
    }

}
