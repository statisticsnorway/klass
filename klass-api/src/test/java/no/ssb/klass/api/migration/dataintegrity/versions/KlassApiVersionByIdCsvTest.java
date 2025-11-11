package no.ssb.klass.api.migration.dataintegrity.versions;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static no.ssb.klass.api.migration.MigrationTestUtils.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import io.restassured.response.Response;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class KlassApiVersionByIdCsvTest extends AbstractKlassApiVersions {

    @Tag(COMPREHENSIVE)
    @ParameterizedTest
    @MethodSource("rangeProviderVersionIds")
    void getVersionById(int classificationId) {

        String path = getVersionByIdPath(classificationId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, null, TEXT_CSV);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, null, TEXT_CSV);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(
                sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(randomId, sourceResponse, targetResponse)).isTrue();
        } else {
            validateCSVDocument(path, sourceResponse, targetResponse);
        }
    }

    @Test
    void getVersionByIdLanguageEn() {

        String path = getVersionByIdPath(randomId);
        Response sourceResponse =
                klassApiMigrationClient.getFromSourceApi(path, paramsLanguageEn, TEXT_CSV);
        Response targetResponse =
                klassApiMigrationClient.getFromTargetApi(path, paramsLanguageEn, TEXT_CSV);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(
                sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(randomId, sourceResponse, targetResponse)).isTrue();
        } else {
            validateCSVDocument(path, sourceResponse, targetResponse);
        }
    }

    @Test
    void getVersionByIdLanguageNn() {

        String path = getVersionByIdPath(randomId);
        Response sourceResponse =
                klassApiMigrationClient.getFromSourceApi(path, paramsLanguageNn, TEXT_CSV);
        Response targetResponse =
                klassApiMigrationClient.getFromTargetApi(path, paramsLanguageNn, TEXT_CSV);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(
                sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(randomId, sourceResponse, targetResponse)).isTrue();
        } else {
            validateCSVDocument(path, sourceResponse, targetResponse);
        }
    }

    @Test
    void getVersionByIdIncludeFuture() {

        String path = getVersionByIdPath(randomId);

        Response sourceResponse =
                klassApiMigrationClient.getFromSourceApi(path, paramsIncludeFuture, TEXT_CSV);
        Response targetResponse =
                klassApiMigrationClient.getFromTargetApi(path, paramsIncludeFuture, TEXT_CSV);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(
                sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(randomId, sourceResponse, targetResponse)).isTrue();
        } else {
            validateCSVDocument(path, sourceResponse, targetResponse);
        }
    }
}
