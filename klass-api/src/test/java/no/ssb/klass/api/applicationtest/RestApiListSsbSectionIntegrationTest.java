package no.ssb.klass.api.applicationtest;

import static io.restassured.RestAssured.*;

import static org.hamcrest.Matchers.*;

import io.restassured.http.ContentType;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

/** Testsuite that test the list (all) ssb sections */
public class RestApiListSsbSectionIntegrationTest extends AbstractRestApiApplicationTest {
    @Test
    public void restServiceListSsbSectionsFamilies() {
        given().port(port)
                .accept(ContentType.JSON)
                .get(REQUEST_SSB_SECTION)
                //                .prettyPeek()
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("_embedded.ssbSections.size()", not(equalTo(0)))

                // links
                .body(JSON_LINKS + ".self.href", containsString(REQUEST_SSB_SECTION));
    }
}
