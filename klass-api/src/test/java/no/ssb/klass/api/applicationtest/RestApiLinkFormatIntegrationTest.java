package no.ssb.klass.api.applicationtest;

import io.restassured.http.ContentType;
import io.restassured.http.Header;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;

public class RestApiLinkFormatIntegrationTest extends AbstractRestApiApplicationTest {
    @Test
    public void hateoasLinksNoForwardedHeaders() {
        given().port(port).accept(ContentType.JSON)
                .when().get(REQUEST)
                .then()
                .body(JSON_LINKS + ".self.href", containsString(REQUEST))
                .body(JSON_LINKS + ".self.href", startsWith("http://localhost"));
    }

    @Test
    public void hateoasLinksWithCustomForwardedHostHeader() {
        given().port(port).accept(ContentType.JSON)
                .header(new Header("X-Forwarded-Proto", "https"))
                .header(new Header("X-SSB-Forwarded-Host", "klass.ssb.no"))
                .header(new Header("X-Forwarded-Host", "ignored"))
                .when().get(REQUEST)
                .then()
                .body(JSON_LINKS + ".self.href", containsString(REQUEST))
                .body(JSON_LINKS + ".self.href", startsWith("https://klass.ssb.no"));
    }
}
