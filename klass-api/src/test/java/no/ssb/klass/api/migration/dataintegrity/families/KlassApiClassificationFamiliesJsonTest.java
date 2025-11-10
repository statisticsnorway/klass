package no.ssb.klass.api.migration.dataintegrity.families;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static no.ssb.klass.api.migration.MigrationTestUtils.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import io.restassured.response.Response;

import org.junit.jupiter.api.Test;

public class KlassApiClassificationFamiliesJsonTest extends AbstractKlassApiFamiliesTest {

    @Test
    void getClassificationFamilies() {
        String path = getClassificationFamiliesPath();
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, null, null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, null, null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(
                sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();
        } else {
            validateObject(sourceResponse, targetResponse, LINKS_SELF_HREF);
            validatePathListWithObjects(
                    sourceResponse,
                    targetResponse,
                    EMBEDDED_CLASSIFICATION_FAMILIES,
                    pathNamesClassificationFamilies,
                    ID);
        }
    }

    @Test
    void getClassificationFamiliesBySsbSection() {

        paramsSsbSection.put(SSB_SECTION, section320);
        String path = getClassificationFamiliesPath();
        Response sourceResponse =
                klassApiMigrationClient.getFromSourceApi(path, paramsSsbSection, null);
        Response targetResponse =
                klassApiMigrationClient.getFromTargetApi(path, paramsSsbSection, null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(
                sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();
        } else {
            validateObject(sourceResponse, targetResponse, LINKS_SELF_HREF);
            validatePathListWithObjects(
                    sourceResponse,
                    targetResponse,
                    EMBEDDED_CLASSIFICATION_FAMILIES,
                    pathNamesClassificationFamilies,
                    ID);
        }
    }

    @Test
    void getClassificationFamiliesNoMatchingSection() {

        paramsSsbSection.put(SSB_SECTION, "no section");
        String path = getClassificationFamiliesPath();
        Response sourceResponse =
                klassApiMigrationClient.getFromSourceApi(path, paramsSsbSection, null);
        Response targetResponse =
                klassApiMigrationClient.getFromTargetApi(path, paramsSsbSection, null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(
                sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();
        } else {
            validateObject(sourceResponse, targetResponse, LINKS_SELF_HREF);
            validatePathListWithObjects(
                    sourceResponse,
                    targetResponse,
                    EMBEDDED_CLASSIFICATION_FAMILIES,
                    pathNamesClassificationFamilies,
                    ID);
        }
    }

    @Test
    void getClassificationFamiliesIncludeCodeLists() {

        String path = getClassificationFamiliesPath();
        Response sourceResponse =
                klassApiMigrationClient.getFromSourceApi(path, paramsIncludeCodeLists, null);
        Response targetResponse =
                klassApiMigrationClient.getFromTargetApi(path, paramsIncludeCodeLists, null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(
                sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();
        } else {
            validateObject(sourceResponse, targetResponse, LINKS_SELF_HREF);
            validatePathListWithObjects(
                    sourceResponse,
                    targetResponse,
                    EMBEDDED_CLASSIFICATION_FAMILIES,
                    pathNamesClassificationFamilies,
                    ID);
        }
    }

    @Test
    void getClassificationFamiliesIncludeLanguageEn() {

        String path = getClassificationFamiliesPath();
        Response sourceResponse =
                klassApiMigrationClient.getFromSourceApi(path, paramsLanguageEn, null);
        Response targetResponse =
                klassApiMigrationClient.getFromTargetApi(path, paramsLanguageEn, null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(
                sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();
        } else {
            validateObject(sourceResponse, targetResponse, LINKS_SELF_HREF);
            validatePathListWithObjects(
                    sourceResponse,
                    targetResponse,
                    EMBEDDED_CLASSIFICATION_FAMILIES,
                    pathNamesClassificationFamilies,
                    ID);
        }
    }

    @Test
    void getClassificationFamiliesIncludeLanguageNn() {

        String path = getClassificationFamiliesPath();
        Response sourceResponse =
                klassApiMigrationClient.getFromSourceApi(path, paramsLanguageNn, null);
        Response targetResponse =
                klassApiMigrationClient.getFromTargetApi(path, paramsLanguageNn, null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(
                sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();
        } else {
            validateObject(sourceResponse, targetResponse, LINKS_SELF_HREF);
            validatePathListWithObjects(
                    sourceResponse,
                    targetResponse,
                    EMBEDDED_CLASSIFICATION_FAMILIES,
                    pathNamesClassificationFamilies,
                    ID);
        }
    }
}
