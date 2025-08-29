package no.ssb.klass.api.migration.dataintegrity.changes;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Instant;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static no.ssb.klass.api.migration.MigrationTestUtils.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiClassificationChangesJsonTest extends AbstractKlassApiChanges {

    @Test
    void getOneClassificationChanges(){
        Integer classificationId = 6;

        String path = getChangesPath(classificationId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsDate,null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsDate,null);

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
    void getClassificationChanges(int classificationId) {
        System.out.println("Start test for ID " + classificationId + " at " + Instant.now());

        String path= getChangesPath(classificationId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsDate,null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsDate,null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateList(sourceResponse, targetResponse, CODE_CHANGES);
        }

        System.out.println("End test for ID " + classificationId + " at " + Instant.now());
    }

    @ParameterizedTest
    @MethodSource("rangeProviderClassificationIds")
    void getOneClassificationChangesDatesInRange(int classificationId) {
        String path= getChangesPath(classificationId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsDateInRange,null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsDateInRange,null);

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
    void getClassificationChangesEnglish(int classificationId) {
        String path= getChangesPath(classificationId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsLanguageEn,null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsLanguageEn,null);

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
    void getClassificationChangesNewNorwegian(int classificationId) {
        String path= getChangesPath(classificationId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsLanguageNn,null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsLanguageNn,null);

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
    void getClassificationChangesIncludeFuture(int classificationId) {
        String path= getChangesPath(classificationId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsIncludeFuture,null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsIncludeFuture,null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateList(sourceResponse, targetResponse, CODE_CHANGES);
        }
    }
}
