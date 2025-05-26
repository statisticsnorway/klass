package no.ssb.klass.api.migration.dataintegrity;

import io.restassured.response.Response;
import no.ssb.klass.api.migration.MigrationTestUtils;
import org.junit.jupiter.api.Test;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiSsbSectionsTest extends AbstractKlassApiDataIntegrityTest {

    @Test
    void getSsbSections(){
        String path = getSsbSectionsPath();
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, null);

        MigrationTestUtils.assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            System.out.println(LOG_MESSAGE_STATUS_CODE + sourceResponse.getStatusCode());
            assertThat(MigrationTestUtils.compareErrorJsonResponse(null, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateList(sourceResponse, targetResponse, EMBEDDED_SSB_SECTIONS);
            validateSelfLink(sourceResponse, targetResponse);
        }
    }

    String getSsbSectionsPath() {
        return "/" + SSB_SECTIONS;
    }
}
