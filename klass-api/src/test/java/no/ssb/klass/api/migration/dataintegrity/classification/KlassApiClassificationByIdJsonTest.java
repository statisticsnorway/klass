package no.ssb.klass.api.migration.dataintegrity.classification;

import io.restassured.response.Response;
import no.ssb.klass.core.model.Language;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static no.ssb.klass.api.migration.MigrationTestUtils.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiClassificationByIdJsonTest extends AbstractKlassApiClassificationTest {

    @Test
    void getOneClassification() {

        int classificationId = sourceResponseIdentifiers.get(11);
        String path = getClassificationByIdPath(classificationId);

        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, null, null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, null, null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else {
            validateItems(sourceResponse, targetResponse, pathNamesClassification, null);
            validateList(sourceResponse, targetResponse, STATISTICAL_UNITS);
            validatePathListWithObjects(sourceResponse, targetResponse, VERSIONS, pathNamesVersion, ID, null);
        }
    }

    @ParameterizedTest
    @MethodSource("rangeProviderClassificationIds")
    void getClassification(Integer classificationId) {

        String path = getClassificationByIdPath(classificationId);

        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, null, null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, null, null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else {
            validateItems(sourceResponse, targetResponse, pathNamesClassification, null);
            validateList(sourceResponse, targetResponse, STATISTICAL_UNITS);
            validatePathListWithObjects(sourceResponse, targetResponse, VERSIONS, pathNamesVersion, ID, null);
        }
    }

    @ParameterizedTest
    @MethodSource("rangeProviderClassificationIds")
    void getClassificationEnglish(Integer classificationId) {

        String path = getClassificationByIdPath(classificationId);

        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsLanguageEn, null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsLanguageEn, null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        } else {
            validateItems(sourceResponse, targetResponse, pathNamesClassification, Language.EN);
            validateList(sourceResponse, targetResponse, STATISTICAL_UNITS);
            validatePathListWithObjects(sourceResponse, targetResponse, VERSIONS, pathNamesVersion, ID, Language.EN);
        }
    }

    @Test
    void getOneClassificationEnglish() {

        int classificationId = marital_status_standard_id;
        String path = getClassificationByIdPath(classificationId);

        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsLanguageEn, null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsLanguageEn, null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        } else {
            validateItems(sourceResponse, targetResponse, pathNamesClassification, Language.EN);
            validateList(sourceResponse, targetResponse, STATISTICAL_UNITS);
            validatePathListWithObjects(sourceResponse, targetResponse, VERSIONS, pathNamesVersion, ID, Language.EN);
        }
    }

    @ParameterizedTest
    @MethodSource("rangeProviderClassificationIds")
    void getClassificationNewNorwegian(Integer classificationId) {

        String path = CLASSIFICATIONS_PATH + "/" + classificationId;

        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsLanguageNn, null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsLanguageNn, null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
            }
        else {
            validateItems(sourceResponse, targetResponse, pathNamesClassification, Language.NN);
            validateList(sourceResponse, targetResponse, STATISTICAL_UNITS);
            validatePathListWithObjects(sourceResponse, targetResponse, VERSIONS, pathNamesVersion, ID, Language.NN);
        }
    }

    @Test
    void getOneClassificationNewNorwegian() {

        int classificationId = gender_standard_id;
        String path = CLASSIFICATIONS_PATH + "/" + classificationId;

        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsLanguageNn, null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsLanguageNn, null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else {
            validateItems(sourceResponse, targetResponse, pathNamesClassification, Language.NN);
            validateList(sourceResponse, targetResponse, STATISTICAL_UNITS);
            validatePathListWithObjects(sourceResponse, targetResponse, VERSIONS, pathNamesVersion, ID, Language.NN);
        }
    }

    @ParameterizedTest
    @MethodSource("rangeProviderClassificationIds")
    void getClassificationIncludeFuture(Integer classificationId) {

        String path = getClassificationByIdPath(classificationId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsIncludeFuture, null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsIncludeFuture, null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }else {
            validateItems(sourceResponse, targetResponse, pathNamesClassification, null);
            validateList(sourceResponse, targetResponse, STATISTICAL_UNITS);
            validatePathListWithObjects(sourceResponse, targetResponse, VERSIONS, pathNamesVersion, ID, null);
        }
    }

    @Test
    void getOneClassificationIncludeFuture() {

        int classificationId = gender_standard_id;
        String path = getClassificationByIdPath(classificationId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsIncludeFuture, null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsIncludeFuture, null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }else {
            validateItems(sourceResponse, targetResponse, pathNamesClassification, null);
            validateList(sourceResponse, targetResponse, STATISTICAL_UNITS);
            validatePathListWithObjects(sourceResponse, targetResponse, VERSIONS, pathNamesVersion, ID, null);
        }
    }
}
