package no.ssb.klass.api.migration.dataintegrity;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiSsbSectionsTest extends AbstractKlassApiDataIntegrityTest {

    @Test
    void getSsbSections(){
        String path = getSsbSectionsPath();
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, null);

        assertThat(sourceResponse.getStatusCode()).withFailMessage(
                FAIL_MESSAGE, path, sourceResponse.getStatusCode(), targetResponse.getStatusCode()).isEqualTo(targetResponse.getStatusCode());

        if(sourceResponse.getStatusCode() != 200) {
            System.out.println(LOG_MESSAGE_STATUS_CODE + sourceResponse.getStatusCode());
            assertThat(compareErrorJsonResponse(null, sourceResponse, targetResponse)).isTrue();
        }
        else{
            Object sourceSections = sourceResponse.path(EMBEDDED_SSB_SECTIONS);
            Object targetSections = targetResponse.path(EMBEDDED_SSB_SECTIONS);
            assertThat(sourceSections).isNotNull();

            String sourceSectionsLinkSelf = sourceResponse.path(LINKS_SELF_HREF);
            String targetSectionsLinkSelf = targetResponse.path(LINKS_SELF_HREF);
            assertThat(sourceSectionsLinkSelf).isNotNull();

            assertThat(sourceSections).withFailMessage(FAIL_MESSAGE, EMBEDDED_SSB_SECTIONS, sourceSections, targetSections).isEqualTo(targetSections);
            assertThat(isPathEqualIgnoreHost(sourceSectionsLinkSelf, targetResponse.path(LINKS_SELF_HREF))).withFailMessage(FAIL_MESSAGE, LINKS_SELF_HREF, sourceSectionsLinkSelf, targetSectionsLinkSelf).isTrue();
        }
    }

    String getSsbSectionsPath() {
        return "/" + SSB_SECTIONS;
    }
}
