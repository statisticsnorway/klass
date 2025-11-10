package no.ssb.klass.api.applicationtest;

import static io.restassured.RestAssured.*;

import no.ssb.klass.api.util.RestConstants;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.stream.Stream;

/**
 * @author Mads Lundemo, SSB.
 *     <p>Testsuite that test how the Rest Api handles unexpected sutuations (tests with JSON and
 *     XML)
 */
class RestApiErrorHandlingIntegrationTest extends AbstractRestApiApplicationTest {

    private static Stream<Arguments> contentTypes() {
        return Stream.of(
                Arguments.of(MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_XML_VALUE),
                Arguments.of(MediaType.TEXT_XML_VALUE, MediaType.TEXT_XML_VALUE),
                Arguments.of(MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE),
                Arguments.of(RestConstants.CONTENT_TYPE_CSV, RestConstants.CONTENT_TYPE_CSV));
    }

    @ParameterizedTest
    @MethodSource("contentTypes")
    void classificationNotFound(String acceptedContentType, String responseContentType) {
        given().port(port)
                .accept(acceptedContentType)
                .param("from", "2014-01-01")
                .param("to", "2015-01-01")
                .when()
                .get(REQUEST_WITH_ID_AND_CODES, -1)
                .prettyPeek()
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .contentType(responseContentType);
    }

    @ParameterizedTest
    @MethodSource("contentTypes")
    void invalidPathParameter(String acceptedContentType, String responseContentType) {
        given().port(port)
                .accept(acceptedContentType)
                .param("from", "2014-01-01")
                .param("to", "2015-01-01")
                .when()
                .get(REQUEST_WITH_ID_AND_CODES, "bogusInput")
                .prettyPeek()
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(responseContentType);
    }
}
