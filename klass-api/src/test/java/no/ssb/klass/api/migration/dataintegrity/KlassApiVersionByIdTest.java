package no.ssb.klass.api.migration.dataintegrity;

import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static no.ssb.klass.api.migration.MigrationTestUtils.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiVersionByIdTest extends AbstractKlassApiDataIntegrityTest{

    static Integer randomId;

    @BeforeAll
    static void beforeAllVersions() {
        randomId = generateRandomId(2000);
    }

    @Test
    void getVersionById() {

        String path = getVersionByIdPath(randomId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, null);

        assertThat(sourceResponse).withFailMessage("source api returned no content").isNotNull();

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            System.out.println(LOG_MESSAGE_STATUS_CODE + sourceResponse.getStatusCode());
            assertThat(compareError(randomId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateItems(sourceResponse, targetResponse, pathNamesVersionsById);
            validateList(sourceResponse, targetResponse, PUBLISHED);
            validateList(sourceResponse, targetResponse, CHANGELOGS);
            validateList(sourceResponse, targetResponse, LEVELS);
            validateList(sourceResponse, targetResponse, CLASSIFICATIONS_VARIANTS);
            validatePathListWithLinks(sourceResponse, targetResponse, CORRESPONDENCE_TABLES, pathNamesCorrespondenceTableById,ID);
            validatePathListWithLinks(sourceResponse, targetResponse, CLASSIFICATION_ITEMS, pathNamesVersionsClassificationItems, CODE);
        }
    }

    @Test
    void getVersionByIdEnglishLanguage() {

        Map<String, Object> params = new HashMap<>();
        params.put(LANGUAGE, EN);

        String path = getVersionByIdPath(randomId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi( path, params);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi( path, params);

        assertThat(sourceResponse).withFailMessage("source api returned no content").isNotNull();

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            System.out.println(LOG_MESSAGE_STATUS_CODE + sourceResponse.getStatusCode());
            assertThat(compareError(randomId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateItems(sourceResponse, targetResponse, pathNamesVersionsById);
            validateList(sourceResponse, targetResponse, PUBLISHED);
            validateList(sourceResponse, targetResponse, CHANGELOGS);
            validateList(sourceResponse, targetResponse, LEVELS);
            validateList(sourceResponse, targetResponse, CLASSIFICATIONS_VARIANTS);
            validatePathListWithLinks(sourceResponse, targetResponse, CORRESPONDENCE_TABLES, pathNamesCorrespondenceTableById,ID);
            validatePathListWithLinks(sourceResponse, targetResponse, CLASSIFICATION_ITEMS, pathNamesVersionsClassificationItems, CODE);
        }
    }

    @Test
    void getVersionByIdNewNorwegianLanguage() {

        Map<String, Object> params = new HashMap<>();
        params.put(LANGUAGE, NN);

        String path = getVersionByIdPath(randomId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, params);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, params);

        assertThat(sourceResponse).withFailMessage("source api returned no content").isNotNull();

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            System.out.println(LOG_MESSAGE_STATUS_CODE + sourceResponse.getStatusCode());
            assertThat(compareErrorJsonResponse(randomId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateItems(sourceResponse, targetResponse, pathNamesVersionsById);
            validateList(sourceResponse, targetResponse, PUBLISHED);
            validateList(sourceResponse, targetResponse, CHANGELOGS);
            validateList(sourceResponse, targetResponse, LEVELS);
            validateList(sourceResponse, targetResponse, CLASSIFICATIONS_VARIANTS);
            validatePathListWithLinks(sourceResponse, targetResponse, CORRESPONDENCE_TABLES, pathNamesCorrespondenceTableById,ID);
            validatePathListWithLinks(sourceResponse, targetResponse, CLASSIFICATION_ITEMS, pathNamesVersionsClassificationItems, CODE);
        }
    }

    @Test
    void getVersionByIdIncludingFuture() {
        Map<String, Object> params = new HashMap<>();
        params.put(INCLUDE_FUTURE, TRUE);

        String path = getVersionByIdPath(randomId);

        Response sourceResponse = klassApiMigrationClient.getFromSourceApi( path, params);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi( path, params);

        assertThat(sourceResponse).withFailMessage("source api returned no content").isNotNull();

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            System.out.println(LOG_MESSAGE_STATUS_CODE + sourceResponse.getStatusCode());
            assertThat(compareError(randomId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateItems(sourceResponse, targetResponse, pathNamesVersionsById);
            validateList(sourceResponse, targetResponse, PUBLISHED);
            validateList(sourceResponse, targetResponse, CHANGELOGS);
            validateList(sourceResponse, targetResponse, LEVELS);
            validateList(sourceResponse, targetResponse, CLASSIFICATIONS_VARIANTS);
            validatePathListWithLinks(sourceResponse, targetResponse, CORRESPONDENCE_TABLES, pathNamesCorrespondenceTableById,ID);
            validatePathListWithLinks(sourceResponse, targetResponse, CLASSIFICATION_ITEMS, pathNamesVersionsClassificationItems, CODE);
        }
    }

    String getVersionByIdPath(Integer id) {
        return "/" + VERSIONS + "/" + id;
    }
}
