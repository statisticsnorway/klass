package no.ssb.klass.api.migration.dataintegrity.families;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static no.ssb.klass.api.migration.MigrationTestUtils.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiClassificationFamiliesXmlTest extends AbstractKlassApiFamiliesTest {

    @Test
    void getClassificationFamilies() {
        String path = getClassificationFamiliesPath();
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, null,APPLICATION_XML);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, null,APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(),path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validatePathListWithObjectsIterateXml(sourceResponse, targetResponse, ENTITIES_CONTENTS_CONTENT, pathNamesClassificationFamiliesXml);
            validatePathListWithObjectsXml(sourceResponse, targetResponse, ENTITIES, pathNamesXmlLinks);
        }
    }

    @Test
    void getClassificationFamiliesBySsbSection() {

        paramsSsbSection.put(SSB_SECTION, section320 );
        String path = getClassificationFamiliesPath();
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsSsbSection,APPLICATION_XML);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsSsbSection,APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(),path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validatePathListWithObjectsIterateXml(sourceResponse, targetResponse, ENTITIES_CONTENTS_CONTENT, pathNamesClassificationFamiliesXml);
            validatePathListWithObjectsXml(sourceResponse, targetResponse, ENTITIES, pathNamesXmlLinks);
        }
    }

    @Test
    void getClassificationFamiliesIncludeCodeLists() {

        String path = getClassificationFamiliesPath();
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsIncludeCodeLists,APPLICATION_XML);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsIncludeCodeLists,APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(),path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validatePathListWithObjectsIterateXml(sourceResponse, targetResponse, ENTITIES_CONTENTS_CONTENT, pathNamesClassificationFamiliesXml);
            validatePathListWithObjectsXml(sourceResponse, targetResponse, ENTITIES, pathNamesXmlLinks);
        }
    }

    @Test
    void getClassificationFamiliesIncludeLanguageEn() {

        String path = getClassificationFamiliesPath();
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsLanguageEn,APPLICATION_XML);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsLanguageEn,APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(),path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validatePathListWithObjectsIterateXml(sourceResponse, targetResponse, ENTITIES_CONTENTS_CONTENT, pathNamesClassificationFamiliesXml);
            validatePathListWithObjectsXml(sourceResponse, targetResponse, ENTITIES, pathNamesXmlLinks);
        }
    }

    @Test
    void getClassificationFamiliesIncludeLanguageNn() {

        String path = getClassificationFamiliesPath();
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsLanguageNn,APPLICATION_XML);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsLanguageNn,APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(),path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validatePathListWithObjectsIterateXml(sourceResponse, targetResponse, ENTITIES_CONTENTS_CONTENT, pathNamesClassificationFamiliesXml);
            validatePathListWithObjectsXml(sourceResponse, targetResponse, ENTITIES, pathNamesXmlLinks);
        }
    }
}
