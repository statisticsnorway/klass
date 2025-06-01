package no.ssb.klass.api.migration.dataintegrity.codes;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static no.ssb.klass.api.migration.MigrationTestUtils.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiClassificationCodesAtXmlTest extends AbstractKlassApiCodesTest {

    @Test
    void getOneClassificationCodesAt(){
        int classificationId = 2;
        date = "2001-01-01";

        Map<String, Object> paramDate = new HashMap<>();
        paramDate.put(DATE, date);

        System.out.println("Start test for ID " + classificationId + " at " + Instant.now());

        String path = getCodesAtPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramDate,APPLICATION_XML);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramDate,APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateObjectXml(path, sourceResponse, targetResponse);
        }
    }

    @ParameterizedTest
    @MethodSource("rangeProviderClassificationIds")
    void getClassificationCodesAt(Integer classificationId) {

        System.out.println("Start test for ID " + classificationId + " at " + Instant.now());

        String path = getCodesAtPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsDate,APPLICATION_XML);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsDate,APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateObjectXml(path, sourceResponse, targetResponse);
        }

        System.out.println("End test for ID " + classificationId + " at " + Instant.now());

    }

    @Test
    void getClassificationCodesAtLanguageEn() {

        int classificationId = 11;

        String path = getCodesAtPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsLanguageEnAt,APPLICATION_XML);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsLanguageEnAt,APPLICATION_XML);

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
    void getClassificationCodesAtLanguageNn() {
        int classificationId = 6;

        String path = getCodesAtPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsLanguageNnAt,APPLICATION_XML);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsLanguageNnAt,APPLICATION_XML);

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
    void getClassificationCodesAtIncludeFuture() {

        int classificationId = 6;

        String path = getCodesAtPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsIncludeFutureAt,APPLICATION_XML);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsIncludeFutureAt,APPLICATION_XML);

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
    void getClassificationCodesAtSelectCode() {

        int classificationId = 131;

        String path = getCodesAtPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsSelectCodeAt,APPLICATION_XML);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsSelectCodeAt,APPLICATION_XML);

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
    void getClassificationCodesAtSelectCodes() {

        int classificationId = 131;

        String path = getCodesAtPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsSelectCodesAt,APPLICATION_XML);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsSelectCodesAt,APPLICATION_XML);

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
    void getClassificationCodesAtSelectLevel() {

        int classificationId = 17;

        String path = getCodesAtPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsSelectLevelAt,APPLICATION_XML);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsSelectLevelAt,APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateObjectXml(path, sourceResponse, targetResponse);
        }
    }

    // Nb the presentation name pattern has no values
    @Test
    void getClassificationCodesAtPresentationPattern() {
        int classificationId = 131;

        String path = getCodesAtPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsPresentationCodePatternAt,APPLICATION_XML);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsPresentationCodePatternAt,APPLICATION_XML);

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
