package no.ssb.klass.api.dataintegrity;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import no.ssb.klass.api.util.RestConstants;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class KlassApiClassificationsTest {
    final String basePath = "/api/klass";
    private final int port = 8080;
    public static final String REQUEST = RestConstants.API_VERSION_V1 + "/classifications";
    public Response result1;
    // Hosts - use env profile?
    // check connection first
    // Run get from host  1
    // Save response
    // Run get from host 2
    // save response
    // Check size (num items, page)
    // Check diff on the whole response
    // Where can it diff?
    // link href first part
    // variable for host
    // How to run json
    // depends on new job
    // concentrate on the non-changeable things first

    @Test
    public void restServiceListClassificationsJSON() {
        String path = "https://data.ssb.no/api/klass/v1/classifications";
        result1 = RestAssured.get(path);
        result1.then()
                .assertThat()
                .statusCode(200)
                .body("page.totalElements", equalTo(148))
                .body("_embedded.classifications.size()", equalTo(20))
                .body("_embedded.classifications[0].name", equalTo("Standard for delområde- og grunnkretsinndeling"));

    }

}
