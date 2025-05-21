package no.ssb.klass.api.migration.dataintegrity;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static no.ssb.klass.api.migration.MigrationTestConstants.SEARCH;

public class KlassApiClassificationsSearchTest extends AbstractKlassApiDataIntegrityTest {

    @Test
    void shouldSearchClassifications() {
        String query = "kommuner";
        Response sourceResponse = getSearchResponse(klassApSourceHostPath, query);
        Response targetResponse = getSearchResponse(klassApiTargetHostPath, query);

    }

    private Response getSearchResponse(String basePath, String query) {
        Response response = RestAssured.get(basePath + "/" + SEARCH + "query=" + query);
        response.then().assertThat().statusCode(200);
        return response;
    }
}
