package no.ssb.klass.api.migration.dataintegrity;

import io.restassured.response.Response;
import no.ssb.klass.api.util.RestConstants;
import org.junit.jupiter.api.Test;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiSsbSectionsTest extends AbstractKlassApiDataIntegrityTest {

    @Test
    void getSsbSections(){
        Response sourceResponse = getResponse(sourceHost + BASE_PATH + RestConstants.API_VERSION_V1  + "/" + SSB_SECTIONS);
        Response targetResponse = getResponse(targetHost + BASE_PATH + RestConstants.API_VERSION_V1 + "/" + SSB_SECTIONS);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();
        }
        else{
            Object sourceSections = sourceResponse.path(EMBEDDED_SSB_SECTIONS);
            String sourceSectionsLinkSelf = sourceResponse.path(LINKS_SELF_HREF);

            assertThat(sourceSections).isEqualTo(targetResponse.path(EMBEDDED_SSB_SECTIONS));
            assertThat(isPathEqualIgnoreHost(sourceSectionsLinkSelf, targetResponse.path(LINKS_SELF_HREF))).isTrue();
        }
    }
}
