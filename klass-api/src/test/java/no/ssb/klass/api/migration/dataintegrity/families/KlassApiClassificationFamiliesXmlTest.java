package no.ssb.klass.api.migration.dataintegrity.families;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static no.ssb.klass.api.migration.MigrationTestUtils.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiClassificationFamiliesXmlTest extends AbstractKlassApiFamiliesTest {

    @Test
    void getClassificationFamilies() {
        String path = getClassificationFamiliesPath();
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, null,APPLICATION_XML);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, null,null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(),path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateXmlNotReady(sourceResponse, targetResponse, path);
        }
    }

    @Test
    void getClassificationFamiliesBySsbSection() {

        paramsSsbSection.put(SSB_SECTION, "320 - Seksjon for befolkningsstatistikk");
        String path = getClassificationFamiliesPath();
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsSsbSection,APPLICATION_XML);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsSsbSection,APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(),path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateXmlNotReady(sourceResponse, targetResponse, path);
        }
    }
}
