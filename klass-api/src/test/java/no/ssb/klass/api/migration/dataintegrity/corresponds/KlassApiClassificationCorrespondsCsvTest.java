package no.ssb.klass.api.migration.dataintegrity.corresponds;

import org.junit.jupiter.api.Test;

import static no.ssb.klass.api.migration.MigrationTestConstants.TEXT_CSV;
import static no.ssb.klass.api.migration.MigrationTestUtils.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiClassificationCorrespondsCsvTest extends AbstractKlassApiCorrespondsTest {

    @Test
    void getOneClassificationCorrespondence(){
        int classificationId = 131;

        String path = getCorrespondsPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsTargetIdAndDateFrom,TEXT_CSV);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsTargetIdAndDateFrom,TEXT_CSV);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateCSVDocument(path, sourceResponse, targetResponse);
        }
    }

    @Test
    void getClassificationCorrespondenceTo(){
        int classificationId = 131;

        String path = getCorrespondsPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsTargetIdAndDateFromTo,TEXT_CSV);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsTargetIdAndDateFromTo,TEXT_CSV);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateCSVDocument(path, sourceResponse, targetResponse);
        }
    }

    @Test
    void getClassificationCorrespondsLanguageEn() {
        int classificationId = 131;

        String path = getCorrespondsPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsTargetIdAndDateFromLanguageEn,TEXT_CSV);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsTargetIdAndDateFromLanguageEn,TEXT_CSV);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateCSVDocument(path, sourceResponse, targetResponse);
        }
    }

    @Test
    void getClassificationCorrespondsLanguageNn() {
        int classificationId = 131;

        String path = getCorrespondsPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsTargetIdAndDateFromLanguageNn,TEXT_CSV);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsTargetIdAndDateFromLanguageNn,TEXT_CSV);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateCSVDocument(path, sourceResponse, targetResponse);
        }
    }

    @Test
    void getClassificationCorrespondsIncludeFuture() {
        int classificationId = 131;

        String path = getCorrespondsPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsTargetIdAndDateFromIncludeFuture,TEXT_CSV);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsTargetIdAndDateFromIncludeFuture,TEXT_CSV);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateCSVDocument(path, sourceResponse, targetResponse);
        }
    }

    @Test
    void getClassificationCorrespondsCsvSeparator() {
        int classificationId = 131;

        String path = getCorrespondsPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsTargetIdAndDateFromCsvSeparator,TEXT_CSV);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsTargetIdAndDateFromCsvSeparator,TEXT_CSV);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateCSVDocument(path, sourceResponse, targetResponse);
        }
    }
    @Test
    void getClassificationCorrespondsCsvFields() {
        int classificationId = 131;

        String path = getCorrespondsPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsTargetIdAndDateFromCsvFields,TEXT_CSV);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsTargetIdAndDateFromCsvFields,TEXT_CSV);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateCSVDocument(path, sourceResponse, targetResponse);
        }
    }
}
