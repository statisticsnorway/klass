package no.ssb.klass.api.migration.dataintegrity.families;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static no.ssb.klass.api.migration.MigrationTestUtils.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiClassificationFamiliesJsonTest extends AbstractKlassApiFamiliesTest {

    @Test
    void getClassificationFamilies() {
        String path = getClassificationFamiliesPath();
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, null,null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, null,null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(),path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateObject(sourceResponse, targetResponse, LINKS_SELF_HREF);
            validatePathListWithObjects(sourceResponse, targetResponse, EMBEDDED_CLASSIFICATION_FAMILIES, pathNamesClassificationFamilies, ID);
        }
    }

    @Test
    void getClassificationFamiliesBySsbSection() {

        paramsSsbSection.put(SSB_SECTION, "320 - Seksjon for befolkningsstatistikk");
        String path = getClassificationFamiliesPath();
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsSsbSection,null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsSsbSection,null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(),path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateObject(sourceResponse, targetResponse, LINKS_SELF_HREF);
            validatePathListWithObjects(sourceResponse, targetResponse, EMBEDDED_CLASSIFICATION_FAMILIES, pathNamesClassificationFamilies, ID);
        }
    }

    @Test
    void getClassificationFamiliesNoMatchingSection() {

        paramsSsbSection.put(SSB_SECTION, "no section");
        String path = getClassificationFamiliesPath();
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsSsbSection,null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsSsbSection,null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(),path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateObject(sourceResponse, targetResponse, LINKS_SELF_HREF);
            validatePathListWithObjects(sourceResponse, targetResponse, EMBEDDED_CLASSIFICATION_FAMILIES, pathNamesClassificationFamilies, ID);
        }
    }
}
