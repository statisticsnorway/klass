package no.ssb.klass.api.migration.dataintegrity;

import io.restassured.response.Response;
import no.ssb.klass.api.migration.MigrationTestUtils;
import org.junit.jupiter.api.Test;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiClassificationFamiliesTest extends KlassApiClassificationCodesTest{


    @Test
    void getClassificationFamilies() {
        String path = getClassificationFamiliesPath();
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, null);

        MigrationTestUtils.assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(),path);

        if(sourceResponse.getStatusCode() != 200) {
            System.out.println(LOG_MESSAGE_STATUS_CODE + sourceResponse.getStatusCode());
            assertThat(MigrationTestUtils.compareError(null, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateSelfLink(sourceResponse, targetResponse);
            validatePathListWithLinks(sourceResponse, targetResponse, EMBEDDED_CLASSIFICATION_FAMILIES, pathNamesClassificationFamilies);
        }
    }

    String getClassificationFamiliesPath() {
        return "/" + CLASSIFICATION_FAMILIES;
    }
}
