package no.ssb.klass.api.migration.dataintegrity.corresponds;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static no.ssb.klass.api.migration.MigrationTestUtils.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiClassificationCorrespondsJsonTest extends AbstractKlassApiCorrespondsTest {

    @Test
    void getOneClassificationCorrespondence(){
        int classificationId = 131;

        String path = getCorrespondsPath(classificationId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsTargetIdAndDateFrom,null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsTargetIdAndDateFrom,null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateList(sourceResponse, targetResponse, CORRESPONDENCE_ITEMS);
        }
    }

    @Test
    void getClassificationCorrespondenceTo(){
        int classificationId = 131;

        String path = getCorrespondsPath(classificationId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsTargetIdAndDateFromTo,null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsTargetIdAndDateFromTo,null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateList(sourceResponse, targetResponse, CORRESPONDENCE_ITEMS);
        }
    }

    @Test
    void getClassificationCorrespondsLanguageEn() {
        int classificationId = 131;

        String path = getCorrespondsPath(classificationId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsTargetIdAndDateFromLanguageEn,null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsTargetIdAndDateFromLanguageEn,null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateList(sourceResponse, targetResponse, CORRESPONDENCE_ITEMS);
        }
    }

    @Test
    void getClassificationCorrespondsLanguageNn() {
        int classificationId = 131;

        String path = getCorrespondsPath(classificationId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsTargetIdAndDateFromLanguageNn,null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsTargetIdAndDateFromLanguageNn,null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateList(sourceResponse, targetResponse, CORRESPONDENCE_ITEMS);
        }
    }

    @Test
    void getClassificationCorrespondsIncludeFuture() {
        int classificationId = 131;

        String path = getCorrespondsPath(classificationId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsTargetIdAndDateFromIncludeFuture,null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsTargetIdAndDateFromIncludeFuture,null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateList(sourceResponse, targetResponse, CORRESPONDENCE_ITEMS);
        }
    }
}
