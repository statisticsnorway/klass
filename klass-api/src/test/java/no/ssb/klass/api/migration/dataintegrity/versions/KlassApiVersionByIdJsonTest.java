package no.ssb.klass.api.migration.dataintegrity.versions;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static no.ssb.klass.api.migration.MigrationTestUtils.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiVersionByIdJsonTest extends AbstractKlassApiVersions {

    @Test
    void getVersionById() {

        String path = getVersionByIdPath(randomId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, null,null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, null,null);

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
    void getVersionByIdLanguageEn() {

        String path = getVersionByIdPath(randomId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi( path, paramsLanguageEn,null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi( path, paramsLanguageEn,null);

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
    void getVersionByIdLanguageNn() {

        String path = getVersionByIdPath(randomId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsLanguageNn,null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsLanguageNn,null);

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
    void getVersionByIdIncludeFuture() {

        String path = getVersionByIdPath(randomId);

        Response sourceResponse = klassApiMigrationClient.getFromSourceApi( path, paramsIncludeFuture,null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi( path, paramsIncludeFuture,null);

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
}
