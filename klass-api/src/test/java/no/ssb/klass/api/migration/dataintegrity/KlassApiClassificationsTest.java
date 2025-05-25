package no.ssb.klass.api.migration.dataintegrity;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiClassificationsTest extends AbstractKlassApiDataIntegrityTest {

    static List<Map<String, Object>> sourceHostClassifications;
    static List<Map<String, Object>> targetHostClassifications;

    static Response sourceResponse;
    static Response targetResponse;

    static Response sourceResponseCodeLists;
    static Response targetResponseCodeLists;

    static Response sourceResponseChangedSince;
    static Response targetResponseChangedSince;

    static Map<String, Object> paramsIncludeCodeLists = new HashMap<>();
    static Map<String, Object> paramsChangedSince = new HashMap<>();

    static String queryDate;

    @BeforeAll
    static void setUpClassifications() {

        sourceResponse = klassApiMigrationClient.getFromSourceApi(CLASSIFICATIONS_PATH, null);
        targetResponse = klassApiMigrationClient.getFromTargetApi(CLASSIFICATIONS_PATH, null);

        sourceHostClassifications = sourceResponse.path(EMBEDDED_CLASSIFICATIONS);
        targetHostClassifications = targetResponse.path(EMBEDDED_CLASSIFICATIONS);

        queryDate = generateRandomDateTime();
        paramsIncludeCodeLists.put(INCLUDE_CODE_LISTS, TRUE);
        paramsChangedSince.put(CHANGED_SINCE, queryDate);

        sourceResponseCodeLists = klassApiMigrationClient.getFromSourceApi(CLASSIFICATIONS_PATH, paramsIncludeCodeLists);
        targetResponseCodeLists = klassApiMigrationClient.getFromTargetApi(CLASSIFICATIONS_PATH, paramsIncludeCodeLists);

        sourceResponseChangedSince = klassApiMigrationClient.getFromSourceApi(CLASSIFICATIONS_PATH, paramsChangedSince);
        targetResponseChangedSince = klassApiMigrationClient.getFromTargetApi(CLASSIFICATIONS_PATH, paramsChangedSince);
    }

    @Test
    void getClassificationsPage(){

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();
        }
        else {
            Object classificationsPageSource = sourceResponse.path(PAGE);
            Object classificationsPageTarget = targetResponse.path(PAGE);

            assertThat(classificationsPageSource).isNotNull();
            assertThat(classificationsPageSource).isEqualTo(classificationsPageTarget);
        }
    }

    @Test
    void getClassificationsLinks(){
        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();
        }
        else {
            Map<String, Object> sourceLinks = sourceResponse.path(LINKS);
            Map<String, Object> targetLinks = targetResponse.path(LINKS);

            assertThat(sourceLinks).isNotNull();
            assertThat(sourceLinks.size()).isEqualTo(targetLinks.size());


            for(String pathName : pathNamesClassificationsLinks) {
                if (pathName.equals(LINKS_SEARCH_TEMPLATED)) {
                    Boolean sourcePath = sourceResponse.path(LINKS_SEARCH_TEMPLATED);
                    assertThat(sourcePath).isEqualTo(targetResponse.path(LINKS_SEARCH_TEMPLATED));
                } else {
                    String sourcePath = sourceResponse.path(pathName);
                    assertThat(isPathEqualIgnoreHost(sourcePath, targetResponse.path(pathName))).isTrue();
                }
            }
        }

    }

    @Test
    void getClassificationsItems(){
        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();
        }
        else {
            for (int i = 0; i < sourceHostClassifications.size(); i++) {
                Map<String, Object> sourceItem = sourceHostClassifications.get(i);
                Map<String, Object> targetItem = targetHostClassifications.get(i);
                for (String pathName : pathNamesClassificationsPage) {
                    if (pathName.equals(LINKS_SELF_HREF)) {
                        String sourceLink = sourceResponse.path(LINKS_SELF_HREF);
                        String targetLink = targetResponse.path(pathName);
                        assertThat(isPathEqualIgnoreHost(sourceLink, targetLink)).isTrue();
                    } else {
                        assertThat(sourceItem.get(pathName)).isEqualTo(targetItem.get(pathName));
                    }
                }
            }
        }
    }

    @Test
    void getClassificationsIncludeCodeListsPage() {
        assertThat(sourceResponseCodeLists).isNotNull();
        if (sourceResponseCodeLists.getStatusCode() != 200) {
            assertThat(compareError(null, sourceResponseCodeLists, targetResponseCodeLists)).isTrue();
        } else {
            Object classificationsPageSourceHost = sourceResponseCodeLists.path(PAGE);
            Object classificationsPageTargetHost = targetResponseCodeLists.path(PAGE);

            assertThat(classificationsPageTargetHost).isNotNull();

            assertThat(classificationsPageSourceHost).isEqualTo(classificationsPageTargetHost);
        }
    }

    @Test
    void getClassificationsChangedSincePage(){
        if (sourceResponseChangedSince.getStatusCode() != 200) {
            assertThat(compareError(null, sourceResponseChangedSince, targetResponseChangedSince)).isTrue();
        } else {
            Object classificationsPageSourceHost = sourceResponseCodeLists.path(PAGE);
            Object classificationsPageTargetHost = targetResponseCodeLists.path(PAGE);

            assertThat(classificationsPageSourceHost).isNotNull();

            assertThat(classificationsPageSourceHost).isEqualTo(classificationsPageTargetHost);
        }
    }

}
