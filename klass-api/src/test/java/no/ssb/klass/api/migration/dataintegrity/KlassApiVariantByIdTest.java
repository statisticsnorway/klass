package no.ssb.klass.api.migration.dataintegrity;

import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiVariantByIdTest extends AbstractKlassApiDataIntegrityTest {

    static Integer randomId;

    @BeforeAll
    static void beforeAllVersions() {
        randomId = generateRandomId(2000);
    }

    @Test
    void getOneVariantById() {
        int variantId = 1111;
        String path = getVariantByIdPath(variantId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi( path, null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi( path, null);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            System.out.println(LOG_MESSAGE_STATUS_CODE + sourceResponse.getStatusCode());
            assertThat(compareError(variantId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateItems(sourceResponse, targetResponse, pathNamesVariantById);
            validateList(sourceResponse, targetResponse, PUBLISHED);
            validateList(sourceResponse, targetResponse, CORRESPONDENCE_TABLES);
            validateList(sourceResponse, targetResponse, CHANGELOGS);
            validateList(sourceResponse, targetResponse, LEVELS);
            validateList(sourceResponse, targetResponse, CLASSIFICATION_ITEMS);
            validateSelfLink(sourceResponse, targetResponse);
        }
    }

    String getVariantByIdPath(Integer id) {
        return "/" + VARIANTS + "/" + id;
    }

}
