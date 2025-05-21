package no.ssb.klass.api.migration.dataintegrity;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static no.ssb.klass.api.migration.MigrationTestConstants.PAGE;
import static no.ssb.klass.api.migration.MigrationTestConstants.SEARCH;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiClassificationsSearchTest extends AbstractKlassApiDataIntegrityTest {

    @Test
    void shouldSearchClassifications() {
        String query = "kommuner";
        Response sourceResponse = getSearchResponse(klassApSourceHostPath, query);
        Response targetResponse = getSearchResponse(klassApiTargetHostPath, query);

        Object sourcePage = sourceResponse.getBody().jsonPath().get(PAGE);
        Object targetPage = targetResponse.getBody().jsonPath().get(PAGE);
        assertThat(sourcePage).isEqualTo(targetPage);

    }

    private Response getSearchResponse(String basePath, String query) {
        Response response = RestAssured.get(basePath + "/" + SEARCH + "query=" + query);
        response.then().assertThat().statusCode(200);
        return response;
    }
}
