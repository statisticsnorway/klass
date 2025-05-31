package no.ssb.klass.api.migration.dataintegrity;

import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static no.ssb.klass.api.migration.MigrationTestUtils.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiClassificationByIdTest extends AbstractKlassApiDataIntegrityTest {

    static Map<String, Object> paramsLanguageEn = new HashMap<>();
    static Map<String, Object> paramsLanguageNn = new HashMap<>();
    static Map<String, Object> paramsIncludeFuture = new HashMap<>();

    Response sourceResponse;
    Response targetResponse;

    @BeforeAll
    static void setUpClassification() {
        paramsLanguageEn.put(LANGUAGE, EN);
        paramsLanguageNn.put(LANGUAGE, NN);
        paramsIncludeFuture.put(INCLUDE_FUTURE, TRUE);
    }

    @Test
    void getOneClassification() {

        int classificationId = sourceResponseIdentifiers.get(11);
        String path = getClassificationByIdPath(classificationId);

        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, null, null);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, null, null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else {
            validateItems(sourceResponse, targetResponse, pathNamesClassification);
            validateList(sourceResponse, targetResponse, STATISTICAL_UNITS);
            validatePathListWithObjects(sourceResponse, targetResponse, VERSIONS, pathNamesVersion, ID);
        }
    }

    @ParameterizedTest
    @MethodSource("rangeProviderClassificationIds")
    void getClassification(Integer classificationId) {

        String path = getClassificationByIdPath(classificationId);

        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, null, null);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, null, null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else {
            validateItems(sourceResponse, targetResponse, pathNamesClassification);
            validateList(sourceResponse, targetResponse, STATISTICAL_UNITS);
            validatePathListWithObjects(sourceResponse, targetResponse, VERSIONS, pathNamesVersion, ID);
        }
    }

    @ParameterizedTest
    @MethodSource("rangeProviderClassificationIds")
    void getClassificationXml(Integer classificationId) {

        String path = getClassificationByIdPath(classificationId);

        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, null, APPLICATION_XML);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, null, APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else {
           validateXml(sourceResponse, targetResponse);
        }
    }


    @ParameterizedTest
    @MethodSource("rangeProviderClassificationIds")
    void getClassificationEnglish(Integer classificationId) {

        String path = getClassificationByIdPath(classificationId);

        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsLanguageEn, null);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsLanguageEn, null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        } else {
            validateItems(sourceResponse, targetResponse, pathNamesClassification);
            validateList(sourceResponse, targetResponse, STATISTICAL_UNITS);
            validatePathListWithObjects(sourceResponse, targetResponse, VERSIONS, pathNamesVersion, ID);
        }
    }

    @ParameterizedTest
    @MethodSource("rangeProviderClassificationIds")
    void getClassificationEnglishXml(Integer classificationId) {

        String path = getClassificationByIdPath(classificationId);

        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsLanguageEn, APPLICATION_XML);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsLanguageEn, APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        } else {
            validateXml(sourceResponse, targetResponse);
        }
    }

    @ParameterizedTest
    @MethodSource("rangeProviderClassificationIds")
    void getClassificationNewNorwegian(Integer classificationId) {

        String path = CLASSIFICATIONS_PATH + "/" + classificationId;

        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsLanguageNn, null);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsLanguageNn, null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
            }
        else {
            validateItems(sourceResponse, targetResponse, pathNamesClassification);
            validateList(sourceResponse, targetResponse, STATISTICAL_UNITS);
            validatePathListWithObjects(sourceResponse, targetResponse, VERSIONS, pathNamesVersion, ID);
        }
    }

    @ParameterizedTest
    @MethodSource("rangeProviderClassificationIds")
    void getClassificationNewNorwegianXml(Integer classificationId) {

        String path = CLASSIFICATIONS_PATH + "/" + classificationId;

        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsLanguageNn, APPLICATION_XML);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsLanguageNn, APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else {
            validateXml(sourceResponse, targetResponse);
        }
    }


    @ParameterizedTest
    @MethodSource("rangeProviderClassificationIds")
    void getClassificationIncludeFuture(Integer classificationId) {

        String path = getClassificationByIdPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsIncludeFuture, null);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsIncludeFuture, null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }else {
            validateItems(sourceResponse, targetResponse, pathNamesClassification);
            validateList(sourceResponse, targetResponse, STATISTICAL_UNITS);
            validatePathListWithObjects(sourceResponse, targetResponse, VERSIONS, pathNamesVersion, ID);
        }
    }

    @ParameterizedTest
    @MethodSource("rangeProviderClassificationIds")
    void getClassificationIncludeFutureXml(Integer classificationId) {

        String path = getClassificationByIdPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsIncludeFuture, APPLICATION_XML);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsIncludeFuture, APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }else {
            validateXml(sourceResponse, targetResponse);
        }
    }


    static Stream<Integer> rangeProviderClassificationIds() {
        return IntStream.rangeClosed(0, lastClassificationId).boxed();
    }

    String getClassificationByIdPath(Integer id) {
        return CLASSIFICATIONS_PATH + "/" + id;
    }
}
