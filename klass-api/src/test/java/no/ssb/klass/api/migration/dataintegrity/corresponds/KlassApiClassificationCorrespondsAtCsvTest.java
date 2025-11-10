package no.ssb.klass.api.migration.dataintegrity.corresponds;

import static no.ssb.klass.api.migration.MigrationTestConstants.TEXT_CSV;
import static no.ssb.klass.api.migration.MigrationTestUtils.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import io.restassured.response.Response;

import org.junit.jupiter.api.Test;

public class KlassApiClassificationCorrespondsAtCsvTest extends AbstractKlassApiCorrespondsTest {

    @Test
    void getOneClassificationCorrespondenceAt() {
        int classificationId = 131;

        String path = getCorrespondsAtPath(classificationId);
        Response sourceResponse =
                klassApiMigrationClient.getFromSourceApi(path, paramsTargetIdAndDate, TEXT_CSV);
        Response targetResponse =
                klassApiMigrationClient.getFromTargetApi(path, paramsTargetIdAndDate, TEXT_CSV);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(
                sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        } else {
            validateCSVDocument(path, sourceResponse, targetResponse);
        }
    }

    @Test
    void getClassificationCorrespondsAtLanguageEn() {
        int classificationId = 131;

        String path = getCorrespondsAtPath(classificationId);
        Response sourceResponse =
                klassApiMigrationClient.getFromSourceApi(
                        path, paramsTargetIdAndDateLanguageEn, TEXT_CSV);
        Response targetResponse =
                klassApiMigrationClient.getFromTargetApi(
                        path, paramsTargetIdAndDateLanguageEn, TEXT_CSV);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(
                sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        } else {
            validateCSVDocument(path, sourceResponse, targetResponse);
        }
    }

    @Test
    void getClassificationCorrespondsAtLanguageNn() {
        int classificationId = 131;

        String path = getCorrespondsAtPath(classificationId);
        Response sourceResponse =
                klassApiMigrationClient.getFromSourceApi(
                        path, paramsTargetIdAndDateLanguageNn, TEXT_CSV);
        Response targetResponse =
                klassApiMigrationClient.getFromTargetApi(
                        path, paramsTargetIdAndDateLanguageNn, TEXT_CSV);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(
                sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        } else {
            validateCSVDocument(path, sourceResponse, targetResponse);
        }
    }

    @Test
    void getClassificationCorrespondsAtIncludeFuture() {
        int classificationId = 131;

        String path = getCorrespondsAtPath(classificationId);
        Response sourceResponse =
                klassApiMigrationClient.getFromSourceApi(
                        path, paramsTargetIdAndDateIncludeFuture, TEXT_CSV);
        Response targetResponse =
                klassApiMigrationClient.getFromTargetApi(
                        path, paramsTargetIdAndDateIncludeFuture, TEXT_CSV);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(
                sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        } else {
            validateCSVDocument(path, sourceResponse, targetResponse);
        }
    }

    @Test
    void getClassificationCorrespondsAtCsvSeparator() {
        int classificationId = 131;

        String path = getCorrespondsAtPath(classificationId);
        Response sourceResponse =
                klassApiMigrationClient.getFromSourceApi(
                        path, paramsTargetIdAndDateCsvSeparator, TEXT_CSV);
        Response targetResponse =
                klassApiMigrationClient.getFromTargetApi(
                        path, paramsTargetIdAndDateCsvSeparator, TEXT_CSV);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(
                sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        } else {
            validateCSVDocument(path, sourceResponse, targetResponse);
        }
    }

    @Test
    void getClassificationCorrespondsAtCsvFields() {
        int classificationId = 131;

        String path = getCorrespondsAtPath(classificationId);
        Response sourceResponse =
                klassApiMigrationClient.getFromSourceApi(
                        path, paramsTargetIdAndDateCsvFields, TEXT_CSV);
        Response targetResponse =
                klassApiMigrationClient.getFromTargetApi(
                        path, paramsTargetIdAndDateCsvFields, TEXT_CSV);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(
                sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        } else {
            validateCSVDocument(path, sourceResponse, targetResponse);
        }
    }
}
