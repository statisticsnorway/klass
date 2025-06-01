package no.ssb.klass.api.migration.dataintegrity.corresponds;

import org.junit.jupiter.api.Test;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static no.ssb.klass.api.migration.MigrationTestUtils.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiClassificationCorrespondsAtJsonTest extends AbstractKlassApiCorrespondsTest {

    @Test
    void getOneClassificationCorrespondenceAt(){
        int classificationId = 131;

        String path = getCorrespondsAtPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsTargetIdAndDate,null);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsTargetIdAndDate,null);

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
    void getClassificationCorrespondsAtLanguageEn() {
        int classificationId = 131;

        String path = getCorrespondsAtPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsTargetIdAndDateLanguageEn,null);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsTargetIdAndDateLanguageEn,null);

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
    void getClassificationCorrespondsAtLanguageNn() {
        int classificationId = 131;

        String path = getCorrespondsAtPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsTargetIdAndDateLanguageNn,null);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsTargetIdAndDateLanguageNn,null);

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
    void getClassificationCorrespondsAtIncludeFuture() {
        int classificationId = 131;

        String path = getCorrespondsAtPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsTargetIdAndDateIncludeFuture,null);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsTargetIdAndDateIncludeFuture,null);

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
