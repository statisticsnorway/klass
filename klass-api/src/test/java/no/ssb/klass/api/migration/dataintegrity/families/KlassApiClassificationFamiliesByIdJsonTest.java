package no.ssb.klass.api.migration.dataintegrity.families;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static no.ssb.klass.api.migration.MigrationTestUtils.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiClassificationFamiliesByIdJsonTest extends AbstractKlassApiFamiliesTest {

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

    // Add test som viser at "210" er ok i nye
    @Test
    void getOneClassificationFamilySsbSection() {
        paramsSsbSection.put(SSB_SECTION, "210 - Seksjon for nasjonalregnskap");
        String path = getClassificationFamilyByIdPath(11);
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
}
