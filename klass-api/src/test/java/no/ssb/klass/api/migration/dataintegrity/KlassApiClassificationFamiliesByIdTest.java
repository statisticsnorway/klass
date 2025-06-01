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

public class KlassApiClassificationFamiliesByIdTest extends KlassApiClassificationCorrespondsAtTest {

    static Map<String, Object> paramsIncludeCodeLists= new HashMap<>();
    static Map<String, Object> paramsLanguageEn = new HashMap<>();
    static Map<String, Object> paramsLanguageNn = new HashMap<>();
    static Map<String, Object> paramsSsbSection = new HashMap<>();

    static int randomSsbSectionId;

    @BeforeAll
    static void beforeAllClassificationFamiliesById() {
        paramsIncludeCodeLists.put(INCLUDE_CODE_LISTS, TRUE);
        paramsLanguageEn.put(LANGUAGE, EN);
        paramsLanguageNn.put(LANGUAGE, NN);

        randomSsbSectionId = generateRandomId(ssbSectionNames.size());
    }
    @Test
    void getOneClassificationFamilyById() {
        int classificationFamilyId = 11;
        String path = getClassificationFamilyByIdPath(classificationFamilyId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, null,null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path , null,null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateItems(sourceResponse, targetResponse, pathNamesClassificationFamilyById);
            validatePathListWithObjects(sourceResponse, targetResponse, CLASSIFICATIONS, pathNamesClassificationsPage, ID);
        }
    }

    @ParameterizedTest
    @MethodSource("rangeProviderClassificationFamilyIds")
    void getClassificationFamilyById(int classificationFamilyId) {
        String path = getClassificationFamilyByIdPath(classificationFamilyId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, null,null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path , null,null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateItems(sourceResponse, targetResponse, pathNamesClassificationFamilyById);
            validatePathListWithObjects(sourceResponse, targetResponse, CLASSIFICATIONS, pathNamesClassificationsPage, ID);
        }
    }

    @ParameterizedTest
    @MethodSource("rangeProviderClassificationFamilyIds")
    void getClassificationFamilyByIdXml(int classificationFamilyId) {
        String path = getClassificationFamilyByIdPath(classificationFamilyId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, null, APPLICATION_XML);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path , null, APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();
        }
        else{
           validateXml(sourceResponse, targetResponse);
        }
    }

    @ParameterizedTest
    @MethodSource("rangeProviderClassificationFamilyIds")
    void getClassificationFamilyIncludeCodeLists(int classificationFamilyId) {
        String path = getClassificationFamilyByIdPath(classificationFamilyId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsIncludeCodeLists,null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path , paramsIncludeCodeLists,null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateItems(sourceResponse, targetResponse, pathNamesClassificationFamilyById);
            validatePathListWithObjects(sourceResponse, targetResponse, CLASSIFICATIONS, pathNamesClassificationsPage, ID);
        }
    }

    @ParameterizedTest
    @MethodSource("rangeProviderClassificationFamilyIds")
    void getClassificationFamilyIncludeCodeListsXml(int classificationFamilyId) {
        String path = getClassificationFamilyByIdPath(classificationFamilyId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsIncludeCodeLists, APPLICATION_XML);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path , paramsIncludeCodeLists, APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();
        }
        else{
           validateXml(sourceResponse, targetResponse);
        }
    }

    @ParameterizedTest
    @MethodSource("rangeProviderClassificationFamilyIds")
    void getClassificationFamilyEnglish(int classificationFamilyId) {
        String path = getClassificationFamilyByIdPath(classificationFamilyId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsLanguageEn,null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path , paramsLanguageEn,null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateItems(sourceResponse, targetResponse, pathNamesClassificationFamilyById);
            validatePathListWithObjects(sourceResponse, targetResponse, CLASSIFICATIONS, pathNamesClassificationsPage, ID);
        }
    }

    @ParameterizedTest
    @MethodSource("rangeProviderClassificationFamilyIds")
    void getClassificationFamilyEnglishXml(int classificationFamilyId) {
        String path = getClassificationFamilyByIdPath(classificationFamilyId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsLanguageEn, APPLICATION_XML);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path , paramsLanguageEn, APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateXml(sourceResponse, targetResponse);
        }
    }

    @ParameterizedTest
    @MethodSource("rangeProviderClassificationFamilyIds")
    void getClassificationFamilyNewNorwegian(int classificationFamilyId) {
        String path = getClassificationFamilyByIdPath(classificationFamilyId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsLanguageNn, null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path , paramsLanguageNn,null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateItems(sourceResponse, targetResponse, pathNamesClassificationFamilyById);
            validatePathListWithObjects(sourceResponse, targetResponse, CLASSIFICATIONS, pathNamesClassificationsPage, ID);
        }
    }

    @ParameterizedTest
    @MethodSource("rangeProviderClassificationFamilyIds")
    void getClassificationFamilyNewNorwegianXml(int classificationFamilyId) throws Exception {
        String path = getClassificationFamilyByIdPath(classificationFamilyId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsLanguageNn, APPLICATION_XML);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path , paramsLanguageNn, APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateObjectXml(CLASSIFICATIONS_PATH, sourceResponse, targetResponse);
            validateLinksXml(CLASSIFICATIONS_PATH,sourceResponse, targetResponse);
        }
    }

    @ParameterizedTest
    @MethodSource("rangeProviderClassificationFamilyIds")
    void getClassificationFamilySsbSection(int classificationFamilyId) {

        paramsSsbSection.put(SSB_SECTION, ssbSectionNames.get(randomSsbSectionId));
        String path = getClassificationFamilyByIdPath(classificationFamilyId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsSsbSection, null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path , paramsSsbSection,null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateItems(sourceResponse, targetResponse, pathNamesClassificationFamilyById);
            validatePathListWithObjects(sourceResponse, targetResponse, CLASSIFICATIONS, pathNamesClassificationsPage, ID);
        }
    }

    @ParameterizedTest
    @MethodSource("rangeProviderClassificationFamilyIds")
    void getClassificationFamilySsbSectionXml(int classificationFamilyId) throws Exception {
        paramsSsbSection.put(SSB_SECTION, ssbSectionNames.get(randomSsbSectionId));

        String path = getClassificationFamilyByIdPath(classificationFamilyId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsSsbSection, APPLICATION_XML);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path , paramsSsbSection, APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateObjectXml(path, sourceResponse, targetResponse);
            validateLinksXml(CLASSIFICATIONS_PATH,sourceResponse, targetResponse);
        }
    }

    static Stream<Integer> rangeProviderClassificationFamilyIds() {
        return IntStream.rangeClosed(0, 30).boxed();
    }

    String getClassificationFamilyByIdPath(Integer id) {
        return "/" + CLASSIFICATION_FAMILIES + "/" + id;
    }
}
