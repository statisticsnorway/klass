package no.ssb.klass.api.migration.dataintegrity.sections;

import io.restassured.response.Response;
import no.ssb.klass.api.migration.dataintegrity.AbstractKlassApiDataIntegrityTest;
import org.junit.jupiter.api.Test;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static no.ssb.klass.api.migration.MigrationTestUtils.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiSsbSectionsTest extends AbstractKlassApiSections {

    @Test
    void getSsbSections(){
        String path = getSsbSectionsPath();
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, null, null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, null, null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateList(sourceResponse, targetResponse, EMBEDDED_SSB_SECTIONS);
            validateObject(sourceResponse, targetResponse, LINKS_SELF_HREF);
        }
    }

    @Test
    void getSsbSectionsXML() throws Exception {
        String path = getSsbSectionsPath();
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, null, APPLICATION_XML);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, null, APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateObjectXml(path, sourceResponse, targetResponse);
            validateLinksXml(path, sourceResponse, targetResponse);
        }
    }

    String getSsbSectionsPath() {
        return "/" + SSB_SECTIONS;
    }
}
