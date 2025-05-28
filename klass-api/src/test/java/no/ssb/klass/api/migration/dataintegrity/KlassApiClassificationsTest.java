package no.ssb.klass.api.migration.dataintegrity;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static no.ssb.klass.api.migration.MigrationTestUtils.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiClassificationsTest extends AbstractKlassApiDataIntegrityTest {

    static List<Map<String, Object>> sourceHostClassifications;
    static List<Map<String, Object>> targetHostClassifications;

    static Response sourceResponse;
    static Response targetResponse;

    static Map<String, Object> paramsIncludeCodeLists = new HashMap<>();
    static Map<String, Object> paramsChangedSince = new HashMap<>();

    static String queryDate;

    @BeforeAll
    static void setUpClassifications() {
        sourceResponse = sourceResponseClassifications;
        targetResponse = targetResponseClassifications;

        sourceHostClassifications = sourceResponse.path(EMBEDDED_CLASSIFICATIONS);
        targetHostClassifications = targetResponse.path(EMBEDDED_CLASSIFICATIONS);

        queryDate = "2015-03-01T01:30:00.000-0200";
        paramsIncludeCodeLists.put(INCLUDE_CODE_LISTS, TRUE);
        paramsChangedSince.put(CHANGED_SINCE, queryDate);
    }

    @Test
    void getClassifications(){
        sourceResponse = sourceResponseClassifications;
        targetResponse = targetResponseClassifications;

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), CLASSIFICATIONS_PATH);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();
        }
        else {
            validateObject(sourceResponse, targetResponse, PAGE);
            int totalPages = sourceResponse.path(PAGE_TOTAL_ELEMENTS);
            for(int i=0; i < totalPages; i++) {
                validatePathListWithObjects(
                        sourceResponse, targetResponse, EMBEDDED_CLASSIFICATIONS, pathNamesClassificationsPage, ID);
                validateItems(sourceResponse, targetResponse, pathNamesClassificationsLinks);

                if(sourceResponse.path(LINKS_NEXT_HREF) == null) {
                    return;
                }
                    sourceResponse =
                            klassApiMigrationClient.getFromSourceApi(sourceResponse.path(LINKS_NEXT_HREF), null,null);
                    targetResponse = klassApiMigrationClient.getFromTargetApi(targetResponse.path(LINKS_NEXT_HREF), null,null);

            }

        }
    }

    @Test
    void getClassificationsXML(){
        sourceResponse = klassApiMigrationClient.getFromSourceApi(
                CLASSIFICATIONS_PATH, null, TEXT_XML);
        targetResponse = klassApiMigrationClient.getFromTargetApi(
                CLASSIFICATIONS_PATH, null, TEXT_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), CLASSIFICATIONS_PATH);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();
        }
        else {
            validateObject(sourceResponse, targetResponse, PAGE);
            int totalPages = sourceResponse.path(PAGE_TOTAL_ELEMENTS);
            for(int i=0; i < totalPages; i++) {
                validatePathListWithObjects(
                        sourceResponse, targetResponse, EMBEDDED_CLASSIFICATIONS, pathNamesClassificationsPage, ID);
                validateItems(sourceResponse, targetResponse, pathNamesClassificationsLinks);

                if(sourceResponse.path(LINKS_NEXT_HREF) == null) {
                    return;
                }
                sourceResponse =
                        klassApiMigrationClient.getFromSourceApi(sourceResponse.path(LINKS_NEXT_HREF), null,null);
                targetResponse = klassApiMigrationClient.getFromTargetApi(targetResponse.path(LINKS_NEXT_HREF), null,null);

            }

        }
    }

    @Test
    void getClassificationsIncludeCodeLists() {
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(CLASSIFICATIONS_PATH, paramsIncludeCodeLists,null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(CLASSIFICATIONS_PATH, paramsIncludeCodeLists,null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), CLASSIFICATIONS_PATH);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();
        } else {

            validateObject(sourceResponse, targetResponse, PAGE);
            validatePathListWithObjects(
                    sourceResponse, targetResponse, EMBEDDED_CLASSIFICATIONS, pathNamesClassificationsPage, ID);
            validateItems(sourceResponse, targetResponse, pathNamesClassificationsLinks);

        }
    }

    @Test
    void getClassificationsChangedSince(){
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(CLASSIFICATIONS_PATH, paramsChangedSince,null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(CLASSIFICATIONS_PATH, paramsChangedSince,null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), CLASSIFICATIONS_PATH);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();
        } else {

            validateObject(sourceResponse, targetResponse, PAGE);
            validatePathListWithObjects(
                    sourceResponse, targetResponse, EMBEDDED_CLASSIFICATIONS, pathNamesClassificationsPage, ID);
            validateItems(sourceResponse, targetResponse, pathNamesClassificationsLinks);

        }
    }

}
