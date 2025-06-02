package no.ssb.klass.api.migration.dataintegrity.corresponds;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static no.ssb.klass.api.migration.MigrationTestConstants.APPLICATION_XML;
import static no.ssb.klass.api.migration.MigrationTestUtils.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiClassificationCorrespondsXmlTest extends AbstractKlassApiCorrespondsTest {

    @Test
    void getOneClassificationCorrespondence(){
        int classificationId = 131;

        String path = getCorrespondsPath(classificationId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsTargetIdAndDateFrom,APPLICATION_XML);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsTargetIdAndDateFrom,APPLICATION_XML);

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
    void getClassificationCorrespondenceTo(){
        int classificationId = 131;

        String path = getCorrespondsPath(classificationId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsTargetIdAndDateFromTo,APPLICATION_XML);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsTargetIdAndDateFromTo,APPLICATION_XML);

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
    void getClassificationCorrespondsLanguageEn() {
        int classificationId = 131;

        String path = getCorrespondsPath(classificationId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsTargetIdAndDateFromLanguageEn,APPLICATION_XML);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsTargetIdAndDateFromLanguageEn,APPLICATION_XML);

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
    void getClassificationCorrespondsLanguageNn() {
        int classificationId = 131;

        String path = getCorrespondsPath(classificationId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsTargetIdAndDateFromLanguageNn,APPLICATION_XML);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsTargetIdAndDateFromLanguageNn,APPLICATION_XML);

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
    void getClassificationCorrespondsIncludeFuture() {
        int classificationId = 131;

        String path = getCorrespondsPath(classificationId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsTargetIdAndDateFromIncludeFuture,APPLICATION_XML);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsTargetIdAndDateFromIncludeFuture,APPLICATION_XML);

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
