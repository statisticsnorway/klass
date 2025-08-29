package no.ssb.klass.api.migration.dataintegrity.codes;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Instant;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static no.ssb.klass.api.migration.MigrationTestConstants.APPLICATION_XML;
import static no.ssb.klass.api.migration.MigrationTestUtils.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiClassificationCodesXmlTest extends AbstractKlassApiCodesTest {

    @Test
    void getOneClassificationWithCodes() {
        int classificationId = industry_classification_standard;

        String path = getCodesPath(classificationId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsFrom, APPLICATION_XML);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsFrom, APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateXmlList(path, sourceResponse, targetResponse, CODE_LIST_CODE_ITEM);
        }
    }

    // Optional params
    @ParameterizedTest
    @MethodSource("rangeProviderClassificationIds")
    void getClassificationWithCodes(Integer classificationId) {

        System.out.println("Start test for ID " + classificationId + " at " + Instant.now());

        String path = getCodesPath(classificationId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsFrom, APPLICATION_XML);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsFrom, APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateXmlList(path, sourceResponse, targetResponse, CODE_LIST_CODE_ITEM);
        }

        System.out.println("End test for ID " + classificationId + " at " + Instant.now());
    }

    @ParameterizedTest
    @MethodSource("rangeProviderClassificationIds")
    void getClassificationWithCodesInRange(Integer classificationId) {
        System.out.println("Start test for ID " + classificationId + " at " + Instant.now());

        String path = getCodesPath(classificationId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsDateInRange,APPLICATION_XML);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsDateInRange,APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateXmlList(path, sourceResponse, targetResponse, CODE_LIST_CODE_ITEM);
        }
    }

    @Test
    void getClassificationWithCodesLanguageEn() {

        int classificationId = industry_classification_standard;

        String path = getCodesPath(classificationId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsLanguageEn,APPLICATION_XML);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsLanguageEn,APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateXmlList(path, sourceResponse, targetResponse, CODE_LIST_CODE_ITEM);
        }
    }

    @Test
    void getClassificationWithCodesLanguageNn() {
        int classificationId = industry_classification_standard;

        String path = getCodesPath(classificationId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsLanguageNn,APPLICATION_XML);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsLanguageNn,APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateXmlList(path, sourceResponse, targetResponse, CODE_LIST_CODE_ITEM);
        }
    }

    @Test
    void getClassificationWithCodesIncludeFuture() {

        int classificationId = industry_classification_standard;

        String path = getCodesPath(classificationId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsIncludeFuture,APPLICATION_XML);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsIncludeFuture,APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateXmlList(path, sourceResponse, targetResponse, CODE_LIST_CODE_ITEM);
        }
    }

    @Test
    void getClassificationWithCodesSelectCode() {

        int classificationId = 131;

        String path = getCodesPath(classificationId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsSelectCode,APPLICATION_XML);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsSelectCode,APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateXmlList(path, sourceResponse, targetResponse, CODE_LIST_CODE_ITEM);
        }
    }

    @Test
    void getClassificationWithCodesSelectLevel() {
        int classificationId = industry_classification_standard;

        String path = getCodesPath(classificationId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsDateInRange,APPLICATION_XML);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsDateInRange,APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateXmlList(path, sourceResponse, targetResponse, CODE_LIST_CODE_ITEM);
        }
    }

    // Nb the presentation name pattern has no values
    @Test
    void getClassificationWithCodesPresentationPattern() {
        int classificationId = 131;

        String path = getCodesPath(classificationId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsPresentationCodePattern,APPLICATION_XML);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsPresentationCodePattern,APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateXmlList(path, sourceResponse, targetResponse, CODE_LIST_CODE_ITEM);
        }
    }

    @Test
    void getClassificationWithCodesInvalidOrder() {
        int classificationId = 11;

        String firstDate = "2025-01-01";
        String secondDate = "1995-11-12";

        paramsDateInRange.put(FROM, firstDate);
        paramsDateInRange.put(TO, secondDate);

        String path = getCodesPath(classificationId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsDateInRange, APPLICATION_XML);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsDateInRange, APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);
        assertThat(sourceResponse.getStatusCode()).isEqualTo(400);
        assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
    }
}
