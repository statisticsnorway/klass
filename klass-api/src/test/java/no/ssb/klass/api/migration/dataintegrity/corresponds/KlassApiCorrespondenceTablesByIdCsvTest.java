package no.ssb.klass.api.migration.dataintegrity.corresponds;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static no.ssb.klass.api.migration.MigrationTestUtils.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import io.restassured.response.Response;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class KlassApiCorrespondenceTablesByIdCsvTest extends AbstractKlassApiCorrespondsTest {

    @Test
    void getOneCorrespondenceTableById() {
        int correspondenceTableId = 1111;

        String path = getCorrespondenceTableByIdPath(correspondenceTableId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, null, TEXT_CSV);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, null, TEXT_CSV);

        assertThat(sourceResponse).withFailMessage("source api returned no content").isNotNull();

        assertStatusCodesEqual(
                sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if (sourceResponse.getStatusCode() != 200) {
            System.out.println(LOG_MESSAGE_STATUS_CODE + sourceResponse.getStatusCode());
            assertThat(compareError(correspondenceTableId, sourceResponse, targetResponse))
                    .isTrue();
        } else {
            validateCSVDocument(path, sourceResponse, targetResponse);
        }
    }

    @Tag(COMPREHENSIVE)
    @ParameterizedTest
    @MethodSource("correspondenceIdRangeProvider")
    void getCorrespondenceTable(int correspondenceTableId) {

        String path = getCorrespondenceTableByIdPath(correspondenceTableId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, null, TEXT_CSV);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, null, TEXT_CSV);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(
                sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(correspondenceTableId, sourceResponse, targetResponse))
                    .isTrue();
        } else {
            validateCSVDocument(path, sourceResponse, targetResponse);
        }
    }

    @Tag(COMPREHENSIVE)
    @ParameterizedTest
    @MethodSource("correspondenceIdRangeProvider")
    void getCorrespondenceTableLanguageEn(int correspondenceTableId) {
        String path = getCorrespondenceTableByIdPath(correspondenceTableId);
        Response sourceResponse =
                klassApiMigrationClient.getFromSourceApi(path, paramsLanguageEn, TEXT_CSV);
        Response targetResponse =
                klassApiMigrationClient.getFromTargetApi(path, paramsLanguageEn, TEXT_CSV);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(
                sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(correspondenceTableId, sourceResponse, targetResponse))
                    .isTrue();
        } else {
            validateCSVDocument(path, sourceResponse, targetResponse);
        }
    }

    @Tag(COMPREHENSIVE)
    @ParameterizedTest
    @MethodSource("correspondenceIdRangeProvider")
    void getCorrespondenceTableLanguageNn(int correspondenceTableId) {

        String path = getCorrespondenceTableByIdPath(correspondenceTableId);
        Response sourceResponse =
                klassApiMigrationClient.getFromSourceApi(path, paramsLanguageNn, TEXT_CSV);
        Response targetResponse =
                klassApiMigrationClient.getFromTargetApi(path, paramsLanguageNn, TEXT_CSV);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(
                sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(correspondenceTableId, sourceResponse, targetResponse))
                    .isTrue();
        } else {
            validateCSVDocument(path, sourceResponse, targetResponse);
        }
    }
}
