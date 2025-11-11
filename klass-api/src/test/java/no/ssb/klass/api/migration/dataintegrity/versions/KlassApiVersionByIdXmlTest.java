package no.ssb.klass.api.migration.dataintegrity.versions;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static no.ssb.klass.api.migration.MigrationTestUtils.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import io.restassured.response.Response;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class KlassApiVersionByIdXmlTest extends AbstractKlassApiVersions {

    @Test
    void getOneVersionById() {

        int classificationId = 683;
        String path = getVersionByIdPath(classificationId);
        Response sourceResponse =
                klassApiMigrationClient.getFromSourceApi(path, null, APPLICATION_XML);
        Response targetResponse =
                klassApiMigrationClient.getFromTargetApi(path, null, APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(
                sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(randomId, sourceResponse, targetResponse)).isTrue();
        } else {
            validateXmlList(
                    path,
                    sourceResponse,
                    targetResponse,
                    CLASSIFICATION_VERSION_PUBLISHED_LANGUAGES);
            validateXmlList(path, sourceResponse, targetResponse, CLASSIFICATION_VERSION_LEVELS);
            validateXmlList(
                    path,
                    sourceResponse,
                    targetResponse,
                    CLASSIFICATION_VERSION_CLASSIFICATION_ITEMS);
            validateXmlItems(sourceResponse, targetResponse, pathNamesClassificationVersionXml);
        }
    }

    @Tag(COMPREHENSIVE)
    @ParameterizedTest
    @MethodSource("rangeProviderVersionIds")
    void getVersionById(int classificationId) {

        String path = getVersionByIdPath(classificationId);
        Response sourceResponse =
                klassApiMigrationClient.getFromSourceApi(path, null, APPLICATION_XML);
        Response targetResponse =
                klassApiMigrationClient.getFromTargetApi(path, null, APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(
                sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(randomId, sourceResponse, targetResponse)).isTrue();
        } else {
            validateXmlList(
                    path,
                    sourceResponse,
                    targetResponse,
                    CLASSIFICATION_VERSION_PUBLISHED_LANGUAGES);
            validateXmlList(path, sourceResponse, targetResponse, CLASSIFICATION_VERSION_LEVELS);
            validateXmlList(
                    path,
                    sourceResponse,
                    targetResponse,
                    CLASSIFICATION_VERSION_CLASSIFICATION_ITEMS);
            validateXmlItems(sourceResponse, targetResponse, pathNamesClassificationVersionXml);
        }
    }

    @Test
    void getVersionByIdLanguageEn() {

        String path = getVersionByIdPath(354);
        Response sourceResponse =
                klassApiMigrationClient.getFromSourceApi(path, paramsLanguageEn, APPLICATION_XML);
        Response targetResponse =
                klassApiMigrationClient.getFromTargetApi(path, paramsLanguageEn, APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(
                sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(randomId, sourceResponse, targetResponse)).isTrue();
        } else {
            validateXmlList(
                    path,
                    sourceResponse,
                    targetResponse,
                    CLASSIFICATION_VERSION_PUBLISHED_LANGUAGES);
            validateXmlList(path, sourceResponse, targetResponse, CLASSIFICATION_VERSION_LEVELS);
            validateXmlList(
                    path,
                    sourceResponse,
                    targetResponse,
                    CLASSIFICATION_VERSION_CLASSIFICATION_ITEMS);
            validateXmlItems(sourceResponse, targetResponse, pathNamesClassificationVersionXml);
        }
    }

    @Test
    void getVersionByIdLanguageNn() {

        String path = getVersionByIdPath(randomId);
        Response sourceResponse =
                klassApiMigrationClient.getFromSourceApi(path, paramsLanguageNn, APPLICATION_XML);
        Response targetResponse =
                klassApiMigrationClient.getFromTargetApi(path, paramsLanguageNn, APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(
                sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(randomId, sourceResponse, targetResponse)).isTrue();
        } else {
            validateXmlList(
                    path,
                    sourceResponse,
                    targetResponse,
                    CLASSIFICATION_VERSION_PUBLISHED_LANGUAGES);
            validateXmlList(path, sourceResponse, targetResponse, CLASSIFICATION_VERSION_LEVELS);
            validateXmlList(
                    path,
                    sourceResponse,
                    targetResponse,
                    CLASSIFICATION_VERSION_CLASSIFICATION_ITEMS);
            validateXmlItems(sourceResponse, targetResponse, pathNamesClassificationVersionXml);
        }
    }

    @Test
    void getVersionByIdIncludingFuture() {
        String path = getVersionByIdPath(randomId);

        Response sourceResponse =
                klassApiMigrationClient.getFromSourceApi(
                        path, paramsIncludeFuture, APPLICATION_XML);
        Response targetResponse =
                klassApiMigrationClient.getFromTargetApi(
                        path, paramsIncludeFuture, APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(
                sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(randomId, sourceResponse, targetResponse)).isTrue();
        } else {
            validateXmlList(
                    path,
                    sourceResponse,
                    targetResponse,
                    CLASSIFICATION_VERSION_PUBLISHED_LANGUAGES);
            validateXmlList(path, sourceResponse, targetResponse, CLASSIFICATION_VERSION_LEVELS);
            validateXmlList(
                    path,
                    sourceResponse,
                    targetResponse,
                    CLASSIFICATION_VERSION_CLASSIFICATION_ITEMS);
            validateXmlItems(sourceResponse, targetResponse, pathNamesClassificationVersionXml);
        }
    }
}
