package no.ssb.klass.api.applicationtest;

import static io.restassured.RestAssured.given;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

import no.ssb.klass.api.util.RestConstants;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

/**
 * Responses must declare how long they are, so that a body cut short in transit is detectable by
 * the client rather than silently accepted as complete.
 */
class RestApiContentLengthIntegrationTest extends AbstractRestApiApplicationTest {

    @ParameterizedTest
    @ValueSource(
            strings = {
                MediaType.APPLICATION_JSON_VALUE,
                MediaType.APPLICATION_XML_VALUE,
                RestConstants.CONTENT_TYPE_CSV
            })
    void codesResponseDeclaresContentLengthMatchingTheBody(String acceptedContentType) {
        Response response =
                given().port(port)
                        .accept(acceptedContentType)
                        .param("from", "2008-01-01")
                        .param("to", "2015-01-01")
                        .get(REQUEST_WITH_ID_AND_CODES, kommuneinndeling.getId())
                        .then()
                        .statusCode(HttpStatus.OK.value())
                        .extract()
                        .response();

        assertThat(response.getHeader(HttpHeaders.CONTENT_LENGTH))
                .as("a truncated body is undetectable without a declared length")
                .isNotNull()
                .asLong()
                .isEqualTo(response.asByteArray().length);
        assertThat(response.getHeader(HttpHeaders.TRANSFER_ENCODING)).isNull();
    }

    @Test
    void errorResponsesAlsoDeclareContentLength() {
        Response response =
                given().port(port)
                        .accept(ContentType.JSON)
                        .param("from", "2014-01-01")
                        .param("to", "2015-01-01")
                        .get(REQUEST_WITH_ID_AND_CODES, -1)
                        .then()
                        .statusCode(HttpStatus.NOT_FOUND.value())
                        .extract()
                        .response();

        assertThat(response.getHeader(HttpHeaders.CONTENT_LENGTH))
                .isNotNull()
                .asLong()
                .isEqualTo(response.asByteArray().length);
    }
}
