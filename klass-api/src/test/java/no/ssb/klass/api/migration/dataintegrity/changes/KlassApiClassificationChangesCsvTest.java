package no.ssb.klass.api.migration.dataintegrity.changes;

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
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsDate,TEXT_CSV);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsDate,TEXT_CSV);

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
    void getClassificationChanges(Integer classificationId) {

        String path= getChangesPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsDate,TEXT_CSV);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsDate,TEXT_CSV);

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
    void getClassificationChangesDatesInRange(Integer classificationId) {

        String path= getChangesPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsDateInRange,null);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsDateInRange,null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateList(sourceResponse, targetResponse, CODE_CHANGES);
        }
    }

    @Test
    void getOneClassificationIncludeFuture(){
        Integer classificationId = 1;

        String path = getChangesPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsIncludeFuture,TEXT_CSV);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsIncludeFuture,TEXT_CSV);

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
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsLanguageEn,null);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsLanguageEn,null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateList(sourceResponse, targetResponse, CODE_CHANGES);
        }
    }

    @ParameterizedTest
    @MethodSource("rangeProviderClassificationIds")
    void getClassificationChangesNewNorwegian(Integer classificationId) {

        String path= getChangesPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsLanguageNn,null);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsLanguageNn,null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateList(sourceResponse, targetResponse, CODE_CHANGES);
        }
    }

    @ParameterizedTest
    @MethodSource("rangeProviderClassificationIds")
    void getClassificationChangesCsvSeparator(Integer classificationId) {

        String path= getChangesPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsCsvSeparator,TEXT_CSV);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsCsvSeparator,TEXT_CSV);

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
