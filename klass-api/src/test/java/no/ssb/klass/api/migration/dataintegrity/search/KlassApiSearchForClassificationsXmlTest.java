package no.ssb.klass.api.migration.dataintegrity.search;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static no.ssb.klass.api.migration.MigrationTestConstants.APPLICATION_XML;
import static no.ssb.klass.api.migration.MigrationTestUtils.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiSearchForClassificationsXmlTest extends AbstractKlassApiSearchTest{

    @Test
    void search() {

        String path = getSearchPath();
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsQuery, APPLICATION_XML);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsQuery, APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();
        } else {
            System.out.println("Search success");
        }
    }

    @Test
    void searchIncludeCodeLists() {

        String path = getSearchPath();
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsQueryIncludeCodeLists, APPLICATION_XML);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsQueryIncludeCodeLists, APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();
        } else {
            System.out.println("Search success");
        }
    }

    @Test
    void searchFilterSsbSection() {

        String path = getSearchPath();
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsQuerySsbSection, APPLICATION_XML);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsQuerySsbSection, APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();
        } else {
            System.out.println("Search success");
        }
    }
}
