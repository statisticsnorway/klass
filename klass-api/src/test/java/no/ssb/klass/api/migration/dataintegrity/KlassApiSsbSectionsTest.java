package no.ssb.klass.api.migration.dataintegrity;

import io.restassured.response.Response;
import no.ssb.klass.api.migration.KlassApiMigrationClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiSsbSectionsTest extends AbstractKlassApiDataIntegrityTest {

    @BeforeAll
    static void beforeAllSections() {
        klassApiMigrationClient = new KlassApiMigrationClient();
    }

    @Test
    void getSsbSections(){
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi("/" + SSB_SECTIONS, null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi("/" + SSB_SECTIONS, null);
        assertThat(targetResponse).isNotNull();
        assertThat(sourceResponse).isNotNull();

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareErrorJsonResponse(null, sourceResponse, targetResponse)).isTrue();
        }
        else{
            Object sourceSections = sourceResponse.path(EMBEDDED_SSB_SECTIONS);
            assertThat(sourceSections).isNotNull();
            String sourceSectionsLinkSelf = sourceResponse.path(LINKS_SELF_HREF);
            assertThat(sourceSectionsLinkSelf).isNotNull();

            assertThat(sourceSections).isEqualTo(targetResponse.path(EMBEDDED_SSB_SECTIONS));
            assertThat(isPathEqualIgnoreHost(sourceSectionsLinkSelf, targetResponse.path(LINKS_SELF_HREF))).isTrue();
        }
    }
}
