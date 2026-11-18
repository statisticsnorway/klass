package no.ssb.klass.api.applicationtest;

import static io.restassured.RestAssured.*;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

import io.restassured.http.ContentType;

import no.ssb.klass.api.util.RestConstants;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.stream.Stream;

class RestApiErrorHandlingIntegrationTest extends AbstractRestApiApplicationTest {

    private static Stream<Arguments> contentTypes() {
        return Stream.of(
                Arguments.of(MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_XML_VALUE),
                Arguments.of(MediaType.TEXT_XML_VALUE, MediaType.TEXT_XML_VALUE),
                Arguments.of(MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE),
                Arguments.of(RestConstants.CONTENT_TYPE_CSV, RestConstants.CONTENT_TYPE_CSV));
    }

    private static Stream<Arguments> badRequests() {
        return Stream.of(
                Arguments.of("/api/klass/v1/classifications/139/changes"),
                Arguments.of("/api/klass/v1/classifications/2%3Flanguage=en"),
                Arguments.of("/api/klass/v1/classifications/null"),
                Arguments.of(
                        "/api/klass/v1/classifications/1/correspondsAt?targetClassificationId=2"),
                Arguments.of("/api/klass/v1/classifications/145/variantAt"));
    }

    private static Stream<Arguments> notFounds() {
        return Stream.of(
                Arguments.of("/unknown/path"),
                Arguments.of("/api/klass/v1/classifications/99999"),
                Arguments.of("/api/klass/v1/versions/99999"),
                Arguments.of(
                        "/api/klass/v1/classifications/1/correspondsAt?targetClassificationId=99999&date=2020-01-01"));
    }

    private static Stream<Arguments> missingResourceMessages() {
        return Stream.of(
                Arguments.of("/variants/99999", "Classification Variant not found with id = 99999"),
                Arguments.of("/versions/99999", "Classification Version not found with id = 99999"),
                Arguments.of(
                        "/correspondencetables/99999",
                        "Correspondence Table not found with id = 99999"),
                Arguments.of(
                        "/classificationfamilies/99999",
                        "ClassificationFamily not found with id = 99999"));
    }

    @ParameterizedTest
    @MethodSource("missingResourceMessages")
    void resourceNotFoundMessagesArePreserved(String path, String message) {
        given().port(port)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(RestConstants.CONTEXT_AND_VERSION_V1 + path)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("detail", equalTo(message));
    }

    private static Stream<Arguments> invalidParameters() {
        return Stream.of(
                Arguments.of("csvSeparator", "::", "Separator must be a single character"),
                Arguments.of(
                        "csvFields",
                        "unknownField",
                        "CorrespondenceList does not contain the following field(s): unknownField"),
                Arguments.of("selectCodes", "1".repeat(201), "selectCodes too long"),
                Arguments.of("selectCodes", "<script>", "selectCodes contains illegal characters"),
                Arguments.of(
                        "selectCodes", "01,".repeat(20) + "01", "Too many selectCodes parameters"));
    }

    @ParameterizedTest
    @MethodSource("invalidParameters")
    void validationMessagesArePreserved(String parameter, String value, String message) {
        given().port(port)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .param("from", "2014-01-01")
                .param("to", "2015-01-01")
                .param(parameter, value)
                .when()
                .get(REQUEST_WITH_ID_AND_CODES, kommuneinndeling.getId())
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("detail", equalTo(message));
    }

    @Test
    void invalidDateRangeRetainsDetails() {
        given().port(port)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .param("from", "2020-01-02")
                .param("to", "2020-01-01")
                .when()
                .get(REQUEST_WITH_ID_AND_CODES, kommuneinndeling.getId())
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(
                        "detail",
                        equalTo("From is equal or after to. From: 2020-01-02 To: 2020-01-01"));
    }

    @Test
    void csvErrorNegotiationByPathStillTakesPrecedenceOverAccept() {
        given().port(port)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .param("from", "2014-01-01")
                .param("to", "2015-01-01")
                .param("csvFields", "unknownField")
                .when()
                .get(REQUEST_WITH_ID_AND_CODES + ".csv", kommuneinndeling.getId())
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(RestConstants.CONTENT_TYPE_CSV)
                .header("X-Content-Type-Options", "nosniff")
                .body(
                        equalTo(
                                "CorrespondenceList does not contain the following field(s): unknownField"));
    }

    @ParameterizedTest
    @MethodSource("contentTypes")
    void classificationNotFound(String acceptedContentType, String responseContentType) {
        given().port(port)
                .accept(acceptedContentType)
                .param("from", "2014-01-01")
                .param("to", "2015-01-01")
                .when()
                .get(REQUEST_WITH_ID_AND_CODES, 99999)
                .prettyPeek()
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .contentType(responseContentType)
                .body(containsString("Classification not found with id = 99999"));
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

    @ParameterizedTest
    @MethodSource("badRequests")
    void badRequests(String path) {
        given().port(port)
                .contentType(ContentType.JSON)
                .when()
                .get(path)
                .prettyPeek()
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @ParameterizedTest
    @MethodSource("notFounds")
    void notFounds(String path) {
        given().port(port)
                .contentType(ContentType.JSON)
                .when()
                .get(path)
                .prettyPeek()
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
                MediaType.APPLICATION_JSON_VALUE,
                MediaType.APPLICATION_XML_VALUE,
                RestConstants.CONTENT_TYPE_CSV
            })
    void missingSwaggerResourceDoesNotReflectHtml(String acceptedContentType) {
        given().port(port)
                .urlEncodingEnabled(false)
                .accept(acceptedContentType)
                .when()
                .get("/api/klass/swagger-ui%3Cscript%3Ealert%28%22xss%22%29%3C/script%3E")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(not(containsString("script")))
                .body(not(containsString("xss")))
                .body(not(containsString("No static resource")));
    }
}
