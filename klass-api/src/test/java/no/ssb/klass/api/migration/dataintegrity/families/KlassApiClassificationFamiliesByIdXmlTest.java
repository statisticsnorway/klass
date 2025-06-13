package no.ssb.klass.api.migration.dataintegrity.families;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static no.ssb.klass.api.migration.MigrationTestUtils.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiClassificationFamiliesByIdXmlTest extends AbstractKlassApiFamiliesTest {

    @Test
    void getOneClassificationFamilyById() {
        int classificationFamilyId = 11;
        String path = getClassificationFamilyByIdPath(classificationFamilyId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, null,APPLICATION_XML);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path , null,APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validatePathListWithObjectsXml(
                    sourceResponse, targetResponse,CLASSIFICATION_FAMILY, pathNamesClassificationFamilyByIdXml
            );
        }
    }

    @ParameterizedTest
    @MethodSource("rangeProviderClassificationFamilyIds")
    void getClassificationFamilyById(int classificationFamilyId) {
        String path = getClassificationFamilyByIdPath(classificationFamilyId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, null, APPLICATION_XML);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path , null, APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validatePathListWithObjectsXml(
                    sourceResponse, targetResponse,CLASSIFICATION_FAMILY, pathNamesClassificationFamilyByIdXml
            );
        }
    }

    @ParameterizedTest
    @MethodSource("rangeProviderClassificationFamilyIds")
    void getClassificationFamilyIncludeCodeLists(int classificationFamilyId) {
        String path = getClassificationFamilyByIdPath(classificationFamilyId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsIncludeCodeLists,APPLICATION_XML);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path , paramsIncludeCodeLists,APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validatePathListWithObjectsXml(
                    sourceResponse, targetResponse,CLASSIFICATION_FAMILY, pathNamesClassificationFamilyByIdXml
            );
        }
    }

    @ParameterizedTest
    @MethodSource("rangeProviderClassificationFamilyIds")
    void getClassificationFamilyLanguageEn(int classificationFamilyId) {
        String path = getClassificationFamilyByIdPath(classificationFamilyId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsLanguageEn,APPLICATION_XML);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path , paramsLanguageEn,APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validatePathListWithObjectsXml(
                    sourceResponse, targetResponse,CLASSIFICATION_FAMILY, pathNamesClassificationFamilyByIdXml
            );
        }
    }

    @ParameterizedTest
    @MethodSource("rangeProviderClassificationFamilyIds")
    void getClassificationFamilyLanguageNn(int classificationFamilyId) {
        String path = getClassificationFamilyByIdPath(classificationFamilyId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsLanguageNn, APPLICATION_XML);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path , paramsLanguageNn,APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validatePathListWithObjectsXml(
                    sourceResponse, targetResponse,CLASSIFICATION_FAMILY, pathNamesClassificationFamilyByIdXml
            );
        }
    }

    @ParameterizedTest
    @MethodSource("rangeProviderClassificationFamilyIds")
    void getClassificationFamilySsbSection(int classificationFamilyId) {
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
            validatePathListWithObjectsXml(
                    sourceResponse, targetResponse,CLASSIFICATION_FAMILY, pathNamesClassificationFamilyByIdXml
            );
        }
    }
}
