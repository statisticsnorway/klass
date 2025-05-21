package no.ssb.klass.api.migration.dataintegrity;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class KlassApiClassificationsTest extends AbstractKlassApiDataIntegrityTest {

    @BeforeEach
    void setUpClassifications() {
        assumeTrue(responseKlassApiSourceHost.getStatusCode() == 200, SOURCE_API_CHECK);
        assumeTrue(responseKlassApiTargetHost.getStatusCode() == 200, TARGET_API_CHECK);
    }

    @Test
    void getClassificationsPage(){

        String classificationsPageSourceHost = responseKlassApiSourceHost.path(EMBEDDED_PAGE);
        String classificationsPageTargetHost = responseKlassApiTargetHost.path(EMBEDDED_PAGE);
        assertThat(classificationsPageSourceHost).isEqualTo(classificationsPageTargetHost);
    }

    @Test
    void getClassificationsLinks(){

        Map<String, Object> sourceLinks = responseKlassApiSourceHost.path(LINKS);
        Map<String, Object> targetLinks = responseKlassApiTargetHost.path(LINKS);

        assertThat(sourceLinks).isNotNull();
        assertThat(sourceLinks.size()).isEqualTo(targetLinks.size());


        for(String pathName : pathNamesClassificationsLinks) {
            if(pathName.equals(LINKS_SEARCH_TEMPLATED)) {
                Boolean sourcePath = responseKlassApiSourceHost.path(LINKS_SEARCH_TEMPLATED);
                assertThat(sourcePath).isEqualTo(responseKlassApiTargetHost.path(LINKS_SEARCH_TEMPLATED));
            }
           else {
                String sourcePath = responseKlassApiSourceHost.path(pathName);
                assertThat(isPathEqualIgnoreHost(sourcePath, responseKlassApiTargetHost.path(pathName))).isTrue();
            }

        }

    }

    @Test
    void getClassificationsItems(){

        for(int i = 0; i < sourceHostClassificationsPage.size(); i++){
           Map<String,Object> sourceItem = sourceHostClassificationsPage.get(i);
           Map<String, Object> targetItem = targetHostClassificationsPage.get(i);
           for(String pathName: pathNamesClassificationsPage){
               if(pathName.equals(LINKS_SELF_HREF)){
                   String sourceLink = responseKlassApiSourceHost.path(LINKS_SELF_HREF);
                   String targetLink = responseKlassApiTargetHost.path(pathName);
                   assertThat(isPathEqualIgnoreHost(sourceLink, targetLink)).isTrue();
               }
               else {
                   assertThat(sourceItem.get(pathName)).isEqualTo(targetItem.get(pathName));
               }
           }
        }
    }

    @Test
    void getClassificationsIncludeCodeListsPage(){
        Response sourceResponse = getClassificationsQueryParamResponse(klassApSourceHostPath, INCLUDE_CODE_LISTS, "true");
        Response targetResponse = getClassificationsQueryParamResponse(klassApiTargetHostPath, INCLUDE_CODE_LISTS, "true");

        String classificationsPageSourceHost = sourceResponse.path(EMBEDDED_PAGE);
        String classificationsPageTargetHost = targetResponse.path(EMBEDDED_PAGE);

        assertThat(classificationsPageSourceHost).isEqualTo(classificationsPageTargetHost);
    }

    @Test
    void getClassificationsChangedSincePage(){
        Response sourceResponse = getClassificationsQueryParamResponse(klassApSourceHostPath, CHANGED_SINCE, "2015-10-31T01:30:00.000-0200");
        Response targetResponse = getClassificationsQueryParamResponse(klassApiTargetHostPath, CHANGED_SINCE, "2015-10-31T01:30:00.000-0200");

        String classificationsPageSourceHost = sourceResponse.path(EMBEDDED_PAGE);
        String classificationsPageTargetHost = targetResponse.path(EMBEDDED_PAGE);

        assertThat(classificationsPageSourceHost).isEqualTo(classificationsPageTargetHost);
    }

    private Response getClassificationsQueryParamResponse(String basePath, String queryParam, String queryValue) {

        return RestAssured.given().queryParam(queryParam, queryValue).get(basePath);

    }

}
