package no.ssb.klass.api.migration.dataintegrity.changes;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;


import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static no.ssb.klass.api.migration.MigrationTestUtils.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiClassificationChangesCsvTest extends AbstractKlassApiChanges {

    @Test
    void getOneClassificationChanges(){
        Integer classificationId = 6;

        String path = getChangesPath(classificationId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsDate,TEXT_CSV);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsDate,TEXT_CSV);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateCSVDocument(path, sourceResponse, targetResponse);
        }
    }

    @ParameterizedTest
    @MethodSource("rangeProviderClassificationIds")
    void getClassificationChanges(int classificationId) {
        String path= getChangesPath(classificationId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsDate,TEXT_CSV);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsDate,TEXT_CSV);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateCSVDocument(path, sourceResponse, targetResponse);
        }

    }

    @ParameterizedTest
    @MethodSource("rangeProviderClassificationIds")
    void getClassificationChangesDatesInRange(int classificationId) {
        // Temp start at id 7 because of heavy requests to some ids
        String path= getChangesPath(classificationId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsDateInRange,TEXT_CSV);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsDateInRange,TEXT_CSV);

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
    void getOneClassificationIncludeFuture(){
        int classificationId = 1;

        String path = getChangesPath(classificationId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsIncludeFuture,TEXT_CSV);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsIncludeFuture,TEXT_CSV);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateCSVDocument(path, sourceResponse, targetResponse);
        }
    }

    @ParameterizedTest
    @MethodSource("rangeProviderClassificationIds")
    void getClassificationChangesEnglish(Integer classificationId) {
        String path= getChangesPath(classificationId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsLanguageEn,TEXT_CSV);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsLanguageEn,TEXT_CSV);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateCSVDocument(path, sourceResponse, targetResponse);
        }
    }

    @ParameterizedTest
    @MethodSource("rangeProviderClassificationIds")
    void getClassificationChangesNewNorwegian(Integer classificationId) {
        String path= getChangesPath(classificationId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsLanguageNn,TEXT_CSV);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsLanguageNn,TEXT_CSV);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateCSVDocument(path, sourceResponse, targetResponse);
        }
    }

    @ParameterizedTest
    @MethodSource("rangeProviderClassificationIds")
    void getClassificationChangesCsvSeparator(Integer classificationId) {
        String path= getChangesPath(classificationId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsCsvSeparator,TEXT_CSV);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsCsvSeparator,TEXT_CSV);

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
