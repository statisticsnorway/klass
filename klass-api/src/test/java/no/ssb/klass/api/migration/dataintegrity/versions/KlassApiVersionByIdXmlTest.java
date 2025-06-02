package no.ssb.klass.api.migration.dataintegrity.versions;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static no.ssb.klass.api.migration.MigrationTestUtils.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiVersionByIdXmlTest extends AbstractKlassApiVersions {

    @ParameterizedTest
    @MethodSource("rangeProviderVersionIds")
    void getVersionById(int classificationId) throws Exception {

        String path = getVersionByIdPath(classificationId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, null, APPLICATION_XML);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, null, APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(randomId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateObjectXml(path, sourceResponse, targetResponse);
            validateLinksXml(path,sourceResponse, targetResponse);
        }
    }

    @Test
    void getVersionByIdLanguageEn() throws Exception {

        String path = getVersionByIdPath(randomId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi( path, paramsLanguageEn,APPLICATION_XML);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi( path, paramsLanguageEn,APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(randomId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateObjectXml(path, sourceResponse, targetResponse);
            validateLinksXml(path,sourceResponse, targetResponse);
        }
    }

    @Test
    void getVersionByIdLanguageNn() throws Exception {

        String path = getVersionByIdPath(randomId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsLanguageNn,APPLICATION_XML);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsLanguageNn,APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(randomId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateObjectXml(path, sourceResponse, targetResponse);
            validateLinksXml(path,sourceResponse, targetResponse);
        }
    }

    @Test
    void getVersionByIdIncludingFuture() throws Exception {
        String path = getVersionByIdPath(randomId);

        Response sourceResponse = klassApiMigrationClient.getFromSourceApi( path, paramsIncludeFuture,APPLICATION_XML);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi( path, paramsIncludeFuture,APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(randomId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateObjectXml(path, sourceResponse, targetResponse);
            validateLinksXml(path,sourceResponse, targetResponse);
        }
    }
}
