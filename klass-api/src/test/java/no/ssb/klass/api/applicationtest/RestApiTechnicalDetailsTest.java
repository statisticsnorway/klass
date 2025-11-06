package no.ssb.klass.api.applicationtest;

import static io.restassured.RestAssured.given;

import static org.hamcrest.Matchers.*;

import io.restassured.http.ContentType;
import io.restassured.http.Header;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests for general technical details in the API, which are not related to data or business logic,
 * but are essential for the functioning of the API based on the documented contract.
 */
class RestApiTechnicalDetailsTest extends AbstractRestApiApplicationTest {
    @Test
    void hateoasLinksNoForwardedHeaders() {
        given().port(port)
                .accept(ContentType.JSON)
                .when()
                .get(REQUEST)
                .then()
                .body(JSON_LINKS + ".self.href", containsString(REQUEST))
                .body(JSON_LINKS + ".self.href", startsWith("http://localhost"));
    }

    @Test
    void hateoasLinksWithCustomForwardedHostHeader() {
        given().port(port)
                .accept(ContentType.JSON)
                .header(new Header("X-Forwarded-Proto", "https"))
                .header(new Header("X-SSB-Forwarded-Host", "klass.ssb.no"))
                .header(new Header("X-Forwarded-Host", "ignored"))
                .when()
                .get(REQUEST)
                .then()
                .body(JSON_LINKS + ".self.href", containsString(REQUEST))
                .body(JSON_LINKS + ".self.href", startsWith("https://klass.ssb.no"));
    }

    @Test
    public void restServiceAllowCors() {

        given().port(port)
                .accept(ContentType.JSON)
                .header("Origin", "https://www.ssb.no/klass")
                .get(REQUEST_WITH_ID, kommuneinndeling.getId())
                .prettyPeek()
                .then()
                .assertThat()
                .header("Access-Control-Allow-Origin", equalTo("*"));
    }

    @ParameterizedTest
    @ValueSource(strings = {".xml", ".json", ".csv"})
    public void pathExtensionContentNegotiation(String pathExtension) {
        given().port(port)
                .noContentType() // Make sure the path extension is the sole specifier
                .when()
                .get(REQUEST_WITH_ID_AND_CODES + pathExtension, kommuneinndeling.getId())
                .prettyPeek()
                .then()
                .statusCode(200)
                .contentType("application/" + pathExtension.replace(".", ""))
                .body(notNullValue());
    }
}
