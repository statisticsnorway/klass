package no.ssb.klass.api.applicationtest;

import static com.jayway.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import org.junit.Test;
import org.springframework.http.HttpStatus;

import com.jayway.restassured.http.ContentType;

/**
 * Testsuite that test the list (all) ssb sections
 */

public class RestApiListSsbSectionIntegrationTest extends AbstractRestApiApplicationTest {
    // @formatter:off
    @Test
    public void restServiceListSsbSectionsFamilies() {
        given().port(port).accept(ContentType.JSON).get(REQUEST_SSB_SECTION)
//                .prettyPeek()
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("_embedded.ssbSections.size()", not(equalTo(0)))

                // links
                .body(JSON_LINKS + ".self.href", containsString(REQUEST_SSB_SECTION));
    }
// @formatter:on
}
