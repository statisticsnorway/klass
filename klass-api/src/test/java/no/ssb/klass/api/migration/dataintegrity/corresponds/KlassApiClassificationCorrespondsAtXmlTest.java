package no.ssb.klass.api.migration.dataintegrity.corresponds;

import org.junit.jupiter.api.Test;

import static no.ssb.klass.api.migration.MigrationTestConstants.APPLICATION_XML;
import static no.ssb.klass.api.migration.MigrationTestUtils.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiClassificationCorrespondsAtXmlTest extends AbstractKlassApiCorrespondsTest {

    @Test
    void getOneClassificationCorrespondenceAt(){
        int classificationId = 131;

        String path = getCorrespondsAtPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsTargetIdAndDate,APPLICATION_XML);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsTargetIdAndDate,APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateObjectXml(path, sourceResponse, targetResponse);
        }
    }

    @Test
    void getClassificationCorrespondsAtLanguageEn() {
        int classificationId = 131;

        String path = getCorrespondsAtPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsTargetIdAndDateLanguageEn,APPLICATION_XML);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsTargetIdAndDateLanguageEn,APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateObjectXml(path, sourceResponse, targetResponse);
        }
    }

    @Test
    void getClassificationCorrespondsAtLanguageNn() {
        int classificationId = 131;

        String path = getCorrespondsAtPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsTargetIdAndDateLanguageNn,APPLICATION_XML);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsTargetIdAndDateLanguageNn,APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateObjectXml(path, sourceResponse, targetResponse);
        }
    }

    @Test
    void getClassificationCorrespondsAtIncludeFuture() {
        int classificationId = 131;

        String path = getCorrespondsAtPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsTargetIdAndDateIncludeFuture,APPLICATION_XML);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsTargetIdAndDateIncludeFuture,APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateObjectXml(path, sourceResponse, targetResponse);
        }
    }
}
