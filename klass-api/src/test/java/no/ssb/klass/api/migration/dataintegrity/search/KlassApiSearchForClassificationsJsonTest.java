package no.ssb.klass.api.migration.dataintegrity.search;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
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
            validateObject(sourceResponse, targetResponse, THIRD_SEARCH_RESULT);
            validateList(sourceResponse, targetResponse,SEARCH_RESULTS);
            //validateObject(sourceResponse, targetResponse, PAGE);

        }
    }

    @Test
    void searchPartial1() {

        String path = getSearchPath();
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsQuery2, null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsQuery2, null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();

        } else {
            //validateObject(sourceResponse, targetResponse, PAGE);
            validateObject(sourceResponse, targetResponse, FIRST_SEARCH_RESULT);
            validateObject(sourceResponse, targetResponse, LINKS_SELF_HREF);

        }
    }

    @Test
    void search3() {

        String path = getSearchPath();
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsQuery3, null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsQuery3, null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();

        } else {
            validateObject(sourceResponse, targetResponse, SECOND_SEARCH_RESULT);
            validateObject(sourceResponse, targetResponse, LINKS_SELF_HREF);
            //     validateObject(sourceResponse, targetResponse, PAGE);

        }
    }

    @Test
    void search4() {

        String path = getSearchPath();
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsQuery4, null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsQuery4, null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();

        } else {
            validateList(sourceResponse, targetResponse,SEARCH_RESULTS);
            validateObject(sourceResponse, targetResponse, THIRD_SEARCH_RESULT);
            validateObject(sourceResponse, targetResponse, LINKS_SELF_HREF);
            //     validateObject(sourceResponse, targetResponse, PAGE);

        }
    }

    @Test
    void search5() {

        String path = getSearchPath();
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsQuery5, null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsQuery5, null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();

        } else {
            validateObject(sourceResponse, targetResponse, PAGE);
            validateObject(sourceResponse, targetResponse, FIRST_SEARCH_RESULT);
            validateObject(sourceResponse, targetResponse, LINKS_SELF_HREF);

        }
    }

    @Test
    void search6() {

        String path = getSearchPath();
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsQuery6, null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsQuery6, null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();

        } else {
            validateObject(sourceResponse, targetResponse, PAGE);
            validateObject(sourceResponse, targetResponse, FIRST_SEARCH_RESULT);
            validateObject(sourceResponse, targetResponse, LINKS_SELF_HREF);


        }
    }

    @Test
    void search7() {

        String path = getSearchPath();
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsQuery7, null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsQuery7, null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();

        } else {
            validateObject(sourceResponse, targetResponse, PAGE);
            validateObject(sourceResponse, targetResponse, FIRST_SEARCH_RESULT);
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
            validateObject(sourceResponse, targetResponse, FIRST_SEARCH_RESULT);
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
            validateObject(sourceResponse, targetResponse, LINKS_SELF_HREF);
            validateObject(sourceResponse, targetResponse, PAGE);
            validateObject(sourceResponse, targetResponse, THIRD_SEARCH_RESULT);
        }
    }
}
