package no.ssb.klass.api.migration.dataintegrity.classifications;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static no.ssb.klass.api.migration.MigrationTestUtils.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiClassificationsXmlTest extends AbstractKlassApiClassifications {

    @Test
    void getClassificationsXML()  {
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(
                CLASSIFICATIONS_PATH, null, APPLICATION_XML);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(
                CLASSIFICATIONS_PATH, null, APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), CLASSIFICATIONS_PATH);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();
        }
        else {
            validateXmlNotReady(sourceResponse, targetResponse);

        }
    }

    @Test
    void getClassificationsIncludeCodeListsXml()  {
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(CLASSIFICATIONS_PATH, paramsIncludeCodeLists, APPLICATION_XML);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(CLASSIFICATIONS_PATH, paramsIncludeCodeLists, APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), CLASSIFICATIONS_PATH);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();
        } else {
            validateXmlNotReady(sourceResponse, targetResponse);

        }
    }

    @Test
    void getClassificationsChangedSinceXml() {
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(CLASSIFICATIONS_PATH, paramsChangedSince, APPLICATION_XML);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(CLASSIFICATIONS_PATH, paramsChangedSince, APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), CLASSIFICATIONS_PATH);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();
        } else {

            validateXmlNotReady(sourceResponse, targetResponse);

        }
    }

}
