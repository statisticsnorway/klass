package no.ssb.klass.api.migration.dataintegrity;

import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiVariantByIdTest extends AbstractKlassApiDataIntegrityTest {

    static Integer randomId;
    Object sourceField;
    Object targetField;

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

        assertThat(sourceResponse.getStatusCode()).withFailMessage(
                FAIL_MESSAGE, path, sourceResponse.getStatusCode(), targetResponse.getStatusCode()).isEqualTo(targetResponse.getStatusCode());

        if(sourceResponse.getStatusCode() != 200) {
            System.out.println(LOG_MESSAGE_STATUS_CODE + sourceResponse.getStatusCode());
            assertThat(compareError(variantId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            for (String pathName : pathNamesVariantById) {
                sourceField = sourceResponse.path(pathName);
                targetField = targetResponse.path(pathName);
                System.out.println(sourceField + "->" + targetField);
                assertThat(sourceField).withFailMessage(FAIL_MESSAGE, pathName, sourceField, targetField).isEqualTo(targetField);
            }
        }
    }

    String getVariantByIdPath(Integer id) {
        return "/" + VARIANTS + "/" + id;
    }

}
