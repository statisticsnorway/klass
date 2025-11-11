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
import java.util.HashMap;
import java.util.Map;

class KlassApiClassificationCodesAtJsonTest extends AbstractKlassApiCodesTest {

    @Test
    void getOneClassificationCodesAt() {
        int classificationId = genderStandardId;
        date = "2001-01-01";

        Map<String, Object> paramDate = new HashMap<>();
        paramDate.put(DATE, date);

        System.out.println("Start test for ID " + classificationId + " at " + Instant.now());

        String path = getCodesAtPath(classificationId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramDate, null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramDate, null);

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
    void getClassificationCodesAt(Integer classificationId) {

        System.out.println("Start test for ID " + classificationId + " at " + Instant.now());

        String path = getCodesAtPath(classificationId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsDate, null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsDate, null);

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

    @Test
    void getClassificationCodesAtLanguageEn() {

        int classificationId = 11;

        String path = getCodesAtPath(classificationId);
        Response sourceResponse =
                klassApiMigrationClient.getFromSourceApi(path, paramsLanguageEnAt, null);
        Response targetResponse =
                klassApiMigrationClient.getFromTargetApi(path, paramsLanguageEnAt, null);

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
    void getClassificationCodesAtLanguageNn() {
        int classificationId = industryClassificationStandard;

        String path = getCodesAtPath(classificationId);
        Response sourceResponse =
                klassApiMigrationClient.getFromSourceApi(path, paramsLanguageNnAt, null);
        Response targetResponse =
                klassApiMigrationClient.getFromTargetApi(path, paramsLanguageNnAt, null);

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
    void getClassificationCodesAtIncludeFuture() {

        int classificationId = industryClassificationStandard;

        String path = getCodesAtPath(classificationId);
        Response sourceResponse =
                klassApiMigrationClient.getFromSourceApi(path, paramsIncludeFutureAt, null);
        Response targetResponse =
                klassApiMigrationClient.getFromTargetApi(path, paramsIncludeFutureAt, null);

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
    void getClassificationCodesAtSelectCode() {

        int classificationId = 131;

        String path = getCodesAtPath(classificationId);
        Response sourceResponse =
                klassApiMigrationClient.getFromSourceApi(path, paramsSelectCodeAt, null);
        Response targetResponse =
                klassApiMigrationClient.getFromTargetApi(path, paramsSelectCodeAt, null);

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
    void getClassificationCodesAtSelectCodes() {

        int classificationId = 131;

        String path = getCodesAtPath(classificationId);
        Response sourceResponse =
                klassApiMigrationClient.getFromSourceApi(path, paramsSelectCodesAt, null);
        Response targetResponse =
                klassApiMigrationClient.getFromTargetApi(path, paramsSelectCodesAt, null);

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
    void getClassificationCodesAtSelectLevel() {

        int classificationId = 17;

        String path = getCodesAtPath(classificationId);
        Response sourceResponse =
                klassApiMigrationClient.getFromSourceApi(path, paramsSelectLevelAt, null);
        Response targetResponse =
                klassApiMigrationClient.getFromTargetApi(path, paramsSelectLevelAt, null);

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
    void getClassificationCodesAtPresentationPattern() {
        int classificationId = 131;

        String path = getCodesAtPath(classificationId);
        Response sourceResponse =
                klassApiMigrationClient.getFromSourceApi(
                        path, paramsPresentationCodePatternAt, null);
        Response targetResponse =
                klassApiMigrationClient.getFromTargetApi(
                        path, paramsPresentationCodePatternAt, null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(
                sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        } else {
            validateList(sourceResponse, targetResponse, CODES);
        }
    }
}
