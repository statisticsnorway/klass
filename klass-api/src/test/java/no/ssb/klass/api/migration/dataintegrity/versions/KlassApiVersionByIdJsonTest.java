package no.ssb.klass.api.migration.dataintegrity.versions;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static no.ssb.klass.api.migration.MigrationTestUtils.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import io.restassured.response.Response;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class KlassApiVersionByIdJsonTest extends AbstractKlassApiVersions {

    @ParameterizedTest
    @MethodSource("rangeProviderVersionIds")
    void getVersionById(int classificationId) {

        String path = getVersionByIdPath(classificationId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, null, null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, null, null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(
                sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(randomId, sourceResponse, targetResponse)).isTrue();
        } else {
            validateItems(sourceResponse, targetResponse, pathNamesVersionsById);
            validateList(sourceResponse, targetResponse, PUBLISHED);
            validateList(sourceResponse, targetResponse, CHANGELOGS);
            validateList(sourceResponse, targetResponse, LEVELS);
            validatePathListWithObjects(
                    sourceResponse,
                    targetResponse,
                    CLASSIFICATIONS_VARIANTS,
                    pathNamesClassificationVariants,
                    ID);
            validatePathListWithObjects(
                    sourceResponse,
                    targetResponse,
                    CORRESPONDENCE_TABLES,
                    pathNamesCorrespondenceTableVersions,
                    ID);
            validatePathListWithObjects(
                    sourceResponse,
                    targetResponse,
                    CLASSIFICATION_ITEMS,
                    pathNamesVersionsClassificationItems,
                    CODE);
        }
    }

    @Test
    void getVersionByIdLanguageEn() {

        String path = getVersionByIdPath(randomId);
        Response sourceResponse =
                klassApiMigrationClient.getFromSourceApi(path, paramsLanguageEn, null);
        Response targetResponse =
                klassApiMigrationClient.getFromTargetApi(path, paramsLanguageEn, null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(
                sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(randomId, sourceResponse, targetResponse)).isTrue();
        } else {
            validateItems(sourceResponse, targetResponse, pathNamesVersionsById);
            validateList(sourceResponse, targetResponse, PUBLISHED);
            validateList(sourceResponse, targetResponse, CHANGELOGS);
            validateList(sourceResponse, targetResponse, LEVELS);
            validatePathListWithObjects(
                    sourceResponse,
                    targetResponse,
                    CLASSIFICATIONS_VARIANTS,
                    pathNamesClassificationVariants,
                    ID);
            validatePathListWithObjects(
                    sourceResponse,
                    targetResponse,
                    CORRESPONDENCE_TABLES,
                    pathNamesCorrespondenceTableVersions,
                    ID);
            validatePathListWithObjects(
                    sourceResponse,
                    targetResponse,
                    CLASSIFICATION_ITEMS,
                    pathNamesVersionsClassificationItems,
                    CODE);
        }
    }

    @Test
    void getVersionByIdLanguageNn() {

        String path = getVersionByIdPath(randomId);
        Response sourceResponse =
                klassApiMigrationClient.getFromSourceApi(path, paramsLanguageNn, null);
        Response targetResponse =
                klassApiMigrationClient.getFromTargetApi(path, paramsLanguageNn, null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(
                sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(randomId, sourceResponse, targetResponse)).isTrue();
        } else {
            validateItems(sourceResponse, targetResponse, pathNamesVersionsById);
            validateList(sourceResponse, targetResponse, PUBLISHED);
            validateList(sourceResponse, targetResponse, CHANGELOGS);
            validateList(sourceResponse, targetResponse, LEVELS);
            validatePathListWithObjects(
                    sourceResponse,
                    targetResponse,
                    CLASSIFICATIONS_VARIANTS,
                    pathNamesClassificationVariants,
                    ID);
            validatePathListWithObjects(
                    sourceResponse,
                    targetResponse,
                    CORRESPONDENCE_TABLES,
                    pathNamesCorrespondenceTableVersions,
                    ID);
            validatePathListWithObjects(
                    sourceResponse,
                    targetResponse,
                    CLASSIFICATION_ITEMS,
                    pathNamesVersionsClassificationItems,
                    CODE);
        }
    }

    @Test
    void getVersionByIdIncludeFuture() {

        String path = getVersionByIdPath(randomId);

        Response sourceResponse =
                klassApiMigrationClient.getFromSourceApi(path, paramsIncludeFuture, null);
        Response targetResponse =
                klassApiMigrationClient.getFromTargetApi(path, paramsIncludeFuture, null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(
                sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(randomId, sourceResponse, targetResponse)).isTrue();
        } else {
            validateItems(sourceResponse, targetResponse, pathNamesVersionsById);
            validateList(sourceResponse, targetResponse, PUBLISHED);
            validateList(sourceResponse, targetResponse, CHANGELOGS);
            validateList(sourceResponse, targetResponse, LEVELS);
            validatePathListWithObjects(
                    sourceResponse,
                    targetResponse,
                    CLASSIFICATIONS_VARIANTS,
                    pathNamesClassificationVariants,
                    ID);
            validatePathListWithObjects(
                    sourceResponse,
                    targetResponse,
                    CORRESPONDENCE_TABLES,
                    pathNamesCorrespondenceTableVersions,
                    ID);
            validatePathListWithObjects(
                    sourceResponse,
                    targetResponse,
                    CLASSIFICATION_ITEMS,
                    pathNamesVersionsClassificationItems,
                    CODE);
        }
    }
}
