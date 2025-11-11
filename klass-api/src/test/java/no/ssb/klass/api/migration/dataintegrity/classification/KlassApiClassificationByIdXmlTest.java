package no.ssb.klass.api.migration.dataintegrity.classification;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static no.ssb.klass.api.migration.MigrationTestUtils.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import io.restassured.response.Response;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class KlassApiClassificationByIdXmlTest extends AbstractKlassApiClassificationTest {

    @Test
    void getOneClassificationXml() {

        int classificationId = sourceResponseIdentifiers.get(11);
        String path = getClassificationByIdPath(classificationId);

        Response sourceResponse =
                klassApiMigrationClient.getFromSourceApi(path, null, APPLICATION_XML);
        Response targetResponse =
                klassApiMigrationClient.getFromTargetApi(path, null, APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(
                sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        } else {
            validateXmlItems(sourceResponse, targetResponse, pathNamesClassificationXml);
        }
    }

    @Tag(COMPREHENSIVE)
    @ParameterizedTest
    @MethodSource("rangeProviderClassificationIds")
    void getClassificationXml(Integer classificationId) {

        String path = getClassificationByIdPath(classificationId);

        Response sourceResponse =
                klassApiMigrationClient.getFromSourceApi(path, null, APPLICATION_XML);
        Response targetResponse =
                klassApiMigrationClient.getFromTargetApi(path, null, APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(
                sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        } else {
            validateXmlItems(sourceResponse, targetResponse, pathNamesClassificationXml);
        }
    }

    @Tag(COMPREHENSIVE)
    @ParameterizedTest
    @MethodSource("rangeProviderClassificationIds")
    void getClassificationEnglishXml(Integer classificationId) {

        String path = getClassificationByIdPath(classificationId);

        Response sourceResponse =
                klassApiMigrationClient.getFromSourceApi(path, paramsLanguageEn, APPLICATION_XML);
        Response targetResponse =
                klassApiMigrationClient.getFromTargetApi(path, paramsLanguageEn, APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(
                sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        } else {
            validateXmlItems(sourceResponse, targetResponse, pathNamesClassificationXml);
        }
    }

    @Tag(COMPREHENSIVE)
    @ParameterizedTest
    @MethodSource("rangeProviderClassificationIds")
    void getClassificationNewNorwegianXml(Integer classificationId) {

        String path = CLASSIFICATIONS_PATH + "/" + classificationId;

        Response sourceResponse =
                klassApiMigrationClient.getFromSourceApi(path, paramsLanguageNn, APPLICATION_XML);
        Response targetResponse =
                klassApiMigrationClient.getFromTargetApi(path, paramsLanguageNn, APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(
                sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        } else {
            validateXmlItems(sourceResponse, targetResponse, pathNamesClassificationXml);
        }
    }

    @Tag(COMPREHENSIVE)
    @ParameterizedTest
    @MethodSource("rangeProviderClassificationIds")
    void getClassificationIncludeFutureXml(Integer classificationId) {

        String path = getClassificationByIdPath(classificationId);
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
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        } else {
            validateXmlItems(sourceResponse, targetResponse, pathNamesClassificationXml);
        }
    }
}
