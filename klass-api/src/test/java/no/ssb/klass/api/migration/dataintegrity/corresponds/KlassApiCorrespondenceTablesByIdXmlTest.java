package no.ssb.klass.api.migration.dataintegrity.corresponds;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static no.ssb.klass.api.migration.MigrationTestUtils.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiCorrespondenceTablesByIdXmlTest extends AbstractKlassApiCorrespondsTest {

    @Test
    void getOneCorrespondenceTableById() throws Exception {
        int correspondenceTableId = 1111;

        String path = getCorrespondenceTableByIdPath(correspondenceTableId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi( path, null,APPLICATION_XML);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi( path, null,APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            System.out.println(LOG_MESSAGE_STATUS_CODE + sourceResponse.getStatusCode());
            assertThat(compareError(correspondenceTableId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateObjectXml(path, sourceResponse, targetResponse);
            validateLinksXml(path, sourceResponse, targetResponse);
        }
    }

    @ParameterizedTest
    @MethodSource("correspondenceIdRangeProvider")
    void getCorrespondenceTable(Integer correspondenceTableId) throws Exception {

        String path = getCorrespondenceTableByIdPath(correspondenceTableId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, null,APPLICATION_XML);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, null,APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(correspondenceTableId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateObjectXml(path, sourceResponse, targetResponse);
            validateLinksXml(path, sourceResponse, targetResponse);
        }
    }


    @ParameterizedTest
    @MethodSource("correspondenceIdRangeProvider")
    void getCorrespondenceTableLanguageEn(int correspondenceTableId) throws Exception {
        String path = getCorrespondenceTableByIdPath(correspondenceTableId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsLanguageEn,APPLICATION_XML);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsLanguageEn,APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(correspondenceTableId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateObjectXml(path, sourceResponse, targetResponse);
            validateLinksXml(path, sourceResponse, targetResponse);
        }
    }



    @ParameterizedTest
    @MethodSource("correspondenceIdRangeProvider")
    void getCorrespondenceTableLanguageNn(int correspondenceTableId) throws Exception {

        String path = getCorrespondenceTableByIdPath(correspondenceTableId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsLanguageNn,APPLICATION_XML);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsLanguageNn,APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(correspondenceTableId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateObjectXml(path, sourceResponse, targetResponse);
            validateLinksXml(path, sourceResponse, targetResponse);
        }

    }
}
