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
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, null,null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, null,null);

        assertThat(sourceResponse).withFailMessage("source api returned no content").isNotNull();

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(randomId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateItems(sourceResponse, targetResponse, pathNamesVersionsById);
            validateList(sourceResponse, targetResponse, PUBLISHED);
            validateList(sourceResponse, targetResponse, CHANGELOGS);
            validateList(sourceResponse, targetResponse, LEVELS);
            validateList(sourceResponse, targetResponse, CLASSIFICATIONS_VARIANTS);
            validatePathListWithObjects(sourceResponse, targetResponse, CORRESPONDENCE_TABLES, pathNamesCorrespondenceTableVersions,ID);
            validatePathListWithObjects(sourceResponse, targetResponse, CLASSIFICATION_ITEMS, pathNamesVersionsClassificationItems, CODE);
        }
    }

    @Test
    void getVersionByIdXML() throws Exception {

        String path = getVersionByIdPath(randomId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, null, APPLICATION_XML);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, null, APPLICATION_XML);

        assertThat(sourceResponse).withFailMessage("source api returned no content").isNotNull();

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(randomId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateObjectXml(path, sourceResponse, targetResponse);
            validateLinksXml(path,sourceResponse, targetResponse);
        }
    }

    @Test
    void getVersionByIdEnglishLanguage() {

        Map<String, Object> params = new HashMap<>();
        params.put(LANGUAGE, EN);

        String path = getVersionByIdPath(randomId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi( path, params,null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi( path, params,null);

        assertThat(sourceResponse).withFailMessage("source api returned no content").isNotNull();

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(randomId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateItems(sourceResponse, targetResponse, pathNamesVersionsById);
            validateList(sourceResponse, targetResponse, PUBLISHED);
            validateList(sourceResponse, targetResponse, CHANGELOGS);
            validateList(sourceResponse, targetResponse, LEVELS);
            validateList(sourceResponse, targetResponse, CLASSIFICATIONS_VARIANTS);
            validatePathListWithObjects(sourceResponse, targetResponse, CORRESPONDENCE_TABLES, pathNamesCorrespondenceTableVersions,ID);
            validatePathListWithObjects(sourceResponse, targetResponse, CLASSIFICATION_ITEMS, pathNamesVersionsClassificationItems, CODE);
        }
    }

    @Test
    void getVersionByIdNewNorwegianLanguage() {

        Map<String, Object> params = new HashMap<>();
        params.put(LANGUAGE, NN);

        String path = getVersionByIdPath(randomId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, params,null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, params,null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(randomId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateItems(sourceResponse, targetResponse, pathNamesVersionsById);
            validateList(sourceResponse, targetResponse, PUBLISHED);
            validateList(sourceResponse, targetResponse, CHANGELOGS);
            validateList(sourceResponse, targetResponse, LEVELS);
            validateList(sourceResponse, targetResponse, CLASSIFICATIONS_VARIANTS);
            validatePathListWithObjects(sourceResponse, targetResponse, CORRESPONDENCE_TABLES, pathNamesCorrespondenceTableVersions,ID);
            validatePathListWithObjects(sourceResponse, targetResponse, CLASSIFICATION_ITEMS, pathNamesVersionsClassificationItems, CODE);
        }
    }

    @Test
    void getVersionByIdIncludingFuture() {
        Map<String, Object> params = new HashMap<>();
        params.put(INCLUDE_FUTURE, TRUE);

        String path = getVersionByIdPath(randomId);

        Response sourceResponse = klassApiMigrationClient.getFromSourceApi( path, params,null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi( path, params,null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(randomId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateItems(sourceResponse, targetResponse, pathNamesVersionsById);
            validateList(sourceResponse, targetResponse, PUBLISHED);
            validateList(sourceResponse, targetResponse, CHANGELOGS);
            validateList(sourceResponse, targetResponse, LEVELS);
            validateList(sourceResponse, targetResponse, CLASSIFICATIONS_VARIANTS);
            validatePathListWithObjects(sourceResponse, targetResponse, CORRESPONDENCE_TABLES, pathNamesCorrespondenceTableVersions,ID);
            validatePathListWithObjects(sourceResponse, targetResponse, CLASSIFICATION_ITEMS, pathNamesVersionsClassificationItems, CODE);
        }
    }

    String getVersionByIdPath(Integer id) {
        return "/" + VERSIONS + "/" + id;
    }
}
