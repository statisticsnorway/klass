package no.ssb.klass.api.applicationtest;

import static io.restassured.RestAssured.*;

import io.restassured.http.ContentType;

import no.ssb.klass.api.util.RestConstants;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
}
