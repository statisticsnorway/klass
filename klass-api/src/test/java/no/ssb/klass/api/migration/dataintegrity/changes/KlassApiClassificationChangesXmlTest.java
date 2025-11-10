package no.ssb.klass.api.migration.dataintegrity.changes;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static no.ssb.klass.api.migration.MigrationTestUtils.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import io.restassured.response.Response;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Instant;

public class KlassApiClassificationChangesXmlTest extends AbstractKlassApiChanges {

    @Test
    void getOneClassificationChanges() {
        Integer classificationId = industry_classification_standard;

        System.out.println("Start test for ID " + classificationId + " at " + Instant.now());

        String path = getChangesPath(classificationId);
        Response sourceResponse =
                klassApiMigrationClient.getFromSourceApi(path, paramsDate, APPLICATION_XML);
        Response targetResponse =
                klassApiMigrationClient.getFromTargetApi(path, paramsDate, APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(
                sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        } else {
            validateXmlList(
                    path, sourceResponse, targetResponse, CODE_CHANGE_LIST_CODE_CHANGE_ITEM);
        }
    }

    @ParameterizedTest
    @MethodSource("rangeProviderClassificationIds")
    void getClassificationChanges(int classificationId) {

        String path = getChangesPath(classificationId);
        Response sourceResponse =
                klassApiMigrationClient.getFromSourceApi(path, paramsDate, APPLICATION_XML);
        Response targetResponse =
                klassApiMigrationClient.getFromTargetApi(path, paramsDate, APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(
                sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        } else {
            validateXmlList(
                    path, sourceResponse, targetResponse, CODE_CHANGE_LIST_CODE_CHANGE_ITEM);
        }
    }

    @ParameterizedTest
    @MethodSource("rangeProviderClassificationIds")
    void getClassificationChangesDatesInRange(int classificationId) {

        String path = getChangesPath(classificationId);
        Response sourceResponse =
                klassApiMigrationClient.getFromSourceApi(path, paramsDateInRange, APPLICATION_XML);
        Response targetResponse =
                klassApiMigrationClient.getFromTargetApi(path, paramsDateInRange, APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(
                sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        } else {
            validateXmlList(
                    path, sourceResponse, targetResponse, CODE_CHANGE_LIST_CODE_CHANGE_ITEM);
        }
    }

    @ParameterizedTest
    @MethodSource("rangeProviderClassificationIds")
    void getClassificationChangesLanguageEn(int classificationId) {
        String path = getChangesPath(classificationId);
        Response sourceResponse =
                klassApiMigrationClient.getFromSourceApi(path, paramsLanguageEn, APPLICATION_XML);
        Response targetResponse =
                klassApiMigrationClient.getFromTargetApi(path, paramsLanguageEn, APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(
                sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        } else {
            validateXmlList(
                    path, sourceResponse, targetResponse, CODE_CHANGE_LIST_CODE_CHANGE_ITEM);
        }
    }

    @ParameterizedTest
    @MethodSource("rangeProviderClassificationIds")
    void getClassificationChangesLanguageNn(int classificationId) {
        String path = getChangesPath(classificationId);
        Response sourceResponse =
                klassApiMigrationClient.getFromSourceApi(path, paramsLanguageNn, APPLICATION_XML);
        Response targetResponse =
                klassApiMigrationClient.getFromTargetApi(path, paramsLanguageNn, APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(
                sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        } else {
            validateXmlList(
                    path, sourceResponse, targetResponse, CODE_CHANGE_LIST_CODE_CHANGE_ITEM);
        }
    }

    @Test
    void getOneClassificationChangesIncludeFuture() {
        int classificationId = industry_classification_standard;

        String path = getChangesPath(classificationId);
        Response sourceResponse =
                klassApiMigrationClient.getFromSourceApi(
                        path, paramsIncludeFuture, APPLICATION_XML);
        Response targetResponse =
                klassApiMigrationClient.getFromTargetApi(
                        path, paramsIncludeFuture, APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(
                sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        } else {
            validateXmlList(
                    path, sourceResponse, targetResponse, CODE_CHANGE_LIST_CODE_CHANGE_ITEM);
        }
    }
}
