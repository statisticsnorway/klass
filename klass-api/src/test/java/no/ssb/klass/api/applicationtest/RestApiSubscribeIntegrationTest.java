package no.ssb.klass.api.applicationtest;

import static io.restassured.RestAssured.*;

import static org.hamcrest.Matchers.*;

import io.restassured.http.ContentType;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

class RestApiSubscribeIntegrationTest extends AbstractRestApiApplicationTest {

    @Test
    void subscriptionErrorsWithMarkupAreReturnedAsPlainText() {
        String email = "<script>alert(\"xss\")</script>@example.com";

        given().port(port)
                .accept(MediaType.TEXT_HTML_VALUE)
                .queryParam("email", email)
                .when()
                .get(REQUEST_WITH_ID + "/removeTracking", kommuneinndeling.getId())
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(MediaType.TEXT_PLAIN_VALUE)
                .header("X-Content-Type-Options", "nosniff")
                .body(equalTo("Unknown email address: " + email));
    }

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
