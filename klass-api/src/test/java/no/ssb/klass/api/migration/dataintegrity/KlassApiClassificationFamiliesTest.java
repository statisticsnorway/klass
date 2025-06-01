package no.ssb.klass.api.migration.dataintegrity;

import io.restassured.response.Response;
import no.ssb.klass.api.migration.dataintegrity.codes.KlassApiClassificationCodesJsonTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static no.ssb.klass.api.migration.MigrationTestUtils.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiClassificationFamiliesTest extends KlassApiClassificationCodesJsonTest {

    static Map<String, Object> paramsSsbSection = new HashMap<>();

    @BeforeAll
    static void initFamilies() {
        paramsSsbSection.put(SSB_SECTION, "320 - Seksjon for befolkningsstatistikk");
    }
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
            validatePathListWithObjects(sourceResponse, targetResponse, EMBEDDED_CLASSIFICATION_FAMILIES, pathNamesClassificationFamilies, ID);
        }
    }

    @Test
    void getClassificationFamiliesBySsbSection() {
        String path = getClassificationFamiliesPath();
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsSsbSection,null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsSsbSection,null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(),path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validatePathListWithObjects(sourceResponse, targetResponse, EMBEDDED_CLASSIFICATION_FAMILIES, pathNamesClassificationFamilies, ID);
        }
    }

    String getClassificationFamiliesPath() {
        return "/" + CLASSIFICATION_FAMILIES;
    }
}
