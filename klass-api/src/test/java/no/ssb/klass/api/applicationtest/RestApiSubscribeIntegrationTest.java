package no.ssb.klass.api.applicationtest;

import static io.restassured.RestAssured.*;

import static org.hamcrest.Matchers.*;

import io.restassured.http.ContentType;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class RestApiSubscribeIntegrationTest extends AbstractRestApiApplicationTest {

    /**
     * We encountered a bug where unknown emails could subscribe but known emails could not.
     *
     * <p>To provoke this behaviour we run two calls.
     */
    @Test
    void restServiceSubscribe() {

        given().port(port)
                .accept(ContentType.JSON)
                .queryParam("email", "me@example.com")
                .when()
                .post(REQUEST_SUBSCRIBE, kommuneinndeling.getId())
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .assertThat()
                .contentType(ContentType.JSON);

        given().port(port)
                .accept(ContentType.JSON)
                .queryParam("email", "me@example.com")
                .when()
                .post(REQUEST_SUBSCRIBE, kommuneinndeling.getId())
                .prettyPeek()
                .then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .assertThat()
                .contentType(ContentType.JSON)
                .body("message", equalTo("Email already subscribed to classification"));
    }
}
