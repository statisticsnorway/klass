package no.ssb.klass.api.migration.dataintegrity.codes;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static no.ssb.klass.api.migration.MigrationTestUtils.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import io.restassured.response.Response;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Instant;

class KlassApiClassificationCodesJsonTest extends AbstractKlassApiCodesTest {

    @Test
    void getOneClassificationWithCodes() {
        int classificationId = industryClassificationStandard;
        System.out.println("Start test for ID " + classificationId + " at " + Instant.now());

        String path = getCodesPath(classificationId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsFrom, null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsFrom, null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(
                sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        } else {
            validateList(sourceResponse, targetResponse, CODES);
        }
    }

    @Tag(COMPREHENSIVE)
    @ParameterizedTest
    @MethodSource("rangeProviderClassificationIds")
    void getClassificationWithCodes(Integer classificationId) {
        System.out.println("Start test for ID " + classificationId + " at " + Instant.now());

        String path = getCodesPath(classificationId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsFrom, null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsFrom, null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(
                sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        } else {
            validateList(sourceResponse, targetResponse, CODES);
        }

        System.out.println("End test for ID " + classificationId + " at " + Instant.now());
    }

    @Tag(COMPREHENSIVE)
    @ParameterizedTest
    @MethodSource("rangeProviderClassificationIds")
    void getClassificationWithCodesInRange(Integer classificationId) {
        System.out.println("Start test for ID " + classificationId + " at " + Instant.now());

        String path = getCodesPath(classificationId);
        Response sourceResponse =
                klassApiMigrationClient.getFromSourceApi(path, paramsDateInRange, null);
        Response targetResponse =
                klassApiMigrationClient.getFromTargetApi(path, paramsDateInRange, null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(
                sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        } else {
            validateList(sourceResponse, targetResponse, CODES);
        }
    }

    @Test
    void getOneClassificationWithCodesInRange() {

        int classificationId = 36;
        String path = getCodesPath(classificationId);
        Response sourceResponse =
                klassApiMigrationClient.getFromSourceApi(path, paramsDateInRange, null);
        Response targetResponse =
                klassApiMigrationClient.getFromTargetApi(path, paramsDateInRange, null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(
                sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        } else {
            validateList(sourceResponse, targetResponse, CODES);
        }
    }

    @Test
    void getClassificationWithCodesLanguageEn() {

        int classificationId = 11;

        String path = getCodesPath(classificationId);
        Response sourceResponse =
                klassApiMigrationClient.getFromSourceApi(path, paramsLanguageEn, null);
        Response targetResponse =
                klassApiMigrationClient.getFromTargetApi(path, paramsLanguageEn, null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(
                sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        } else {
            validateList(sourceResponse, targetResponse, CODES);
        }
    }

    @Test
    void getClassificationWithCodesLanguageNn() {
        int classificationId = industryClassificationStandard;

        String path = getCodesPath(classificationId);
        Response sourceResponse =
                klassApiMigrationClient.getFromSourceApi(path, paramsLanguageNn, null);
        Response targetResponse =
                klassApiMigrationClient.getFromTargetApi(path, paramsLanguageNn, null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(
                sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        } else {
            validateList(sourceResponse, targetResponse, CODES);
        }
    }

    @Test
    void getClassificationWithCodesIncludeFuture() {

        int classificationId = industryClassificationStandard;

        String path = getCodesPath(classificationId);
        Response sourceResponse =
                klassApiMigrationClient.getFromSourceApi(path, paramsIncludeFuture, null);
        Response targetResponse =
                klassApiMigrationClient.getFromTargetApi(path, paramsIncludeFuture, null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(
                sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        } else {
            validateList(sourceResponse, targetResponse, CODES);
        }
    }

    @Test
    void getClassificationWithCodesSelectCode() {

        int classificationId = 131;

        String path = getCodesPath(classificationId);
        Response sourceResponse =
                klassApiMigrationClient.getFromSourceApi(path, paramsSelectCode, null);
        Response targetResponse =
                klassApiMigrationClient.getFromTargetApi(path, paramsSelectCode, null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(
                sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        } else {
            validateList(sourceResponse, targetResponse, CODES);
        }
    }

    @Test
    void getClassificationWithCodesSelectCodes() {

        int classificationId = 131;

        String path = getCodesPath(classificationId);
        Response sourceResponse =
                klassApiMigrationClient.getFromSourceApi(path, paramsSelectCodes, null);
        Response targetResponse =
                klassApiMigrationClient.getFromTargetApi(path, paramsSelectCodes, null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(
                sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        } else {
            validateList(sourceResponse, targetResponse, CODES);
        }
    }

    @Test
    void getClassificationWithCodesSelectLevel() {

        int classificationId = 17;

        String path = getCodesPath(classificationId);
        Response sourceResponse =
                klassApiMigrationClient.getFromSourceApi(path, paramsSelectLevel, null);
        Response targetResponse =
                klassApiMigrationClient.getFromTargetApi(path, paramsSelectLevel, null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(
                sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        } else {
            validateList(sourceResponse, targetResponse, CODES);
        }
    }

    // Nb the presentation name pattern has no values
    @Test
    void getClassificationWithCodesPresentationPattern() {
        int classificationId = 131;

        String path = getCodesPath(classificationId);
        Response sourceResponse =
                klassApiMigrationClient.getFromSourceApi(path, paramsPresentationCodePattern, null);
        Response targetResponse =
                klassApiMigrationClient.getFromTargetApi(path, paramsPresentationCodePattern, null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(
                sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        } else {
            validateList(sourceResponse, targetResponse, CODES);
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
        Response sourceResponse =
                klassApiMigrationClient.getFromSourceApi(path, paramsDateInRange, null);
        Response targetResponse =
                klassApiMigrationClient.getFromTargetApi(path, paramsDateInRange, null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(
                sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);
        assertThat(sourceResponse.getStatusCode()).isEqualTo(400);
        assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
    }
}
