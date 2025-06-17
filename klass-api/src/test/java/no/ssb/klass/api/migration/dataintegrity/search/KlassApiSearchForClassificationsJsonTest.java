package no.ssb.klass.api.migration.dataintegrity.search;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static no.ssb.klass.api.migration.MigrationTestConstants.LINKS_SELF_HREF;
import static no.ssb.klass.api.migration.MigrationTestConstants.PAGE;
import static no.ssb.klass.api.migration.MigrationTestUtils.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiSearchForClassificationsJsonTest extends AbstractKlassApiSearchTest {

    @Test
    void search() {

        String path = getSearchPath();
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsQuery, null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsQuery, null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();

        } else {
            validateObject(sourceResponse, targetResponse, PAGE);
            validateObject(sourceResponse, targetResponse, LINKS_SELF_HREF);

        }
    }

    @Test
    void searchIncludeCodeLists() {

        String path = getSearchPath();
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsQueryIncludeCodeLists, null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsQueryIncludeCodeLists, null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();
        } else {
            validateObject(sourceResponse, targetResponse, PAGE);
            validateObject(sourceResponse, targetResponse, LINKS_SELF_HREF);
        }
    }

    @Test
    void searchFilterSsbSection() {

        String path = getSearchPath();
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsQuerySsbSection, null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsQuerySsbSection, null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();
        } else {
            validateObject(sourceResponse, targetResponse, PAGE);
            validateObject(sourceResponse, targetResponse, LINKS_SELF_HREF);
        }
    }
}
