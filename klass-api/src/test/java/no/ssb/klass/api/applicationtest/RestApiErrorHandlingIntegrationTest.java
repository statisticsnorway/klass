package no.ssb.klass.api.applicationtest;

import static com.jayway.restassured.RestAssured.*;

import org.junit.Test;
import org.springframework.http.HttpStatus;

import com.jayway.restassured.http.ContentType;

/**
 * @author Mads Lundemo, SSB.
 *         <p>
 *         Testsuite that test how the Rest Api handles unexpected sutuations (tests with JSON and XML)
 */
public class RestApiErrorHandlingIntegrationTest extends AbstractRestApiApplicationTest {
    // @formatter:off
    @Test
    public void restServiceShouldReturn404WhenClassificationNotFoundJSON() {
        given()
                .port(port)
                .accept(ContentType.JSON)
                .when()
                .get(REQUEST_WITH_ID, -1)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .contentType(ContentType.JSON);
    }

    @Test
    public void restServiceShouldReturn404WhenClassificationNotFoundXML() {
        given()
                .port(port)
                .accept(ContentType.XML)
                .when()
                .get(REQUEST_WITH_ID, -1)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .contentType(ContentType.XML);
    }

    @Test
    public void restServiceShouldReturn400AndErrorWhenRequestIsMalformedJSON() {
        given()
                .port(port)
                .accept(ContentType.JSON)
                .when()
                .get(REQUEST_WITH_ID, "bogusInput")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(ContentType.JSON);
    }

    @Test
    public void restServiceShouldReturn400AndErrorWhenRequestIsMalformedXML() {
        given()
                .port(port)
                .accept(ContentType.XML)
                .when()
                .get(REQUEST_WITH_ID, "bogusInput")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(ContentType.XML);
    }
// @formatter:on
}
