package no.ssb.klass.api.applicationtest;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import io.restassured.http.ContentType;
import no.ssb.klass.testutil.TestDataProvider;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

/**
 * @author Mads Lundemo, SSB.
 *     <p>Testsuite that test the list (all) classifications (tests with JSON and XML)
 */
public class RestApiListClassificationIntegrationTest extends AbstractRestApiApplicationTest {
  // @formatter:off
  @Test
  public void restServiceListClassificationsJSON() {
    given()
        .port(port)
        .accept(ContentType.JSON)
        .get(REQUEST)
        //                .prettyPeek()
        .then()
        .statusCode(HttpStatus.OK.value())
        .contentType(ContentType.JSON)
        .body(JSON_CLASSIFICATIONS + ".size()", equalTo(2))
        // result 1
        .body(JSON_CLASSIFICATION1 + ".name", equalTo(TestDataProvider.KOMMUNEINNDELING_NAVN_NO))
        .body(
            JSON_CLASSIFICATION1 + "._links.self.href",
            containsString(REQUEST + "/" + kommuneinndeling.getId()))
        // result 2
        .body(JSON_CLASSIFICATION2 + ".name", equalTo(TestDataProvider.BYDELSINNDELING_NAVN_NO))
        .body(
            JSON_CLASSIFICATION2 + "._links.self.href",
            containsString(REQUEST + "/" + bydelsinndeling.getId()))
        // links
        .body(JSON_LINKS + ".self.href", containsString(REQUEST))
        .body(JSON_LINKS + ".search.href", containsString(REQUEST_SEARCH))
        // paging
        .body(JSON_PAGE + ".size", equalTo(20))
        .body(JSON_PAGE + ".totalElements", equalTo(2))
        .body(JSON_PAGE + ".totalPages", equalTo(1))
        .body(JSON_PAGE + ".number", equalTo(0));
  }

  @Test
  public void restServiceListClassificationsIncludeCodelistsJSON() {
    given()
        .port(port)
        .accept(ContentType.JSON)
        .param("includeCodelists", "true")
        .get(REQUEST)
        .prettyPeek()
        .then()
        .statusCode(HttpStatus.OK.value())
        .contentType(ContentType.JSON)
        .body(JSON_CLASSIFICATIONS + ".size()", equalTo(4))
        // result 1
        .body(JSON_CLASSIFICATION1 + ".name", equalTo(TestDataProvider.FAMILIEGRUPPERING_NAVN_NO))
        .body(
            JSON_CLASSIFICATION1 + "._links.self.href",
            containsString(REQUEST + "/" + familieGrupperingCodelist.getId()))
        // result 2
        .body(JSON_CLASSIFICATION3 + ".name", equalTo(TestDataProvider.KOMMUNEINNDELING_NAVN_NO))
        .body(
            JSON_CLASSIFICATION3 + "._links.self.href",
            containsString(REQUEST + "/" + kommuneinndeling.getId()))
        // result 3
        .body(JSON_CLASSIFICATION4 + ".name", equalTo(TestDataProvider.BYDELSINNDELING_NAVN_NO))
        .body(
            JSON_CLASSIFICATION4 + "._links.self.href",
            containsString(REQUEST + "/" + bydelsinndeling.getId()))

        // links
        .body(JSON_LINKS + ".self.href", containsString(REQUEST))
        .body(JSON_LINKS + ".search.href", containsString(REQUEST_SEARCH))
        // paging
        .body(JSON_PAGE + ".size", equalTo(20))
        .body(JSON_PAGE + ".totalElements", equalTo(4))
        .body(JSON_PAGE + ".totalPages", equalTo(1))
        .body(JSON_PAGE + ".number", equalTo(0));
  }

  @Test
  public void restServiceListClassificationsChangedSinceJSON() {
    // Spring's DateTimeFormat parser har changed the timezone offset format from ssZ to SSSXXX.
    given()
        .port(port)
        .accept(ContentType.JSON)
        .param("changedSince", "2015-10-31T01:30:00.000-02:00")
        .get(REQUEST)
        .then()
        .statusCode(HttpStatus.OK.value());

    given()
        .port(port)
        .accept(ContentType.JSON)
        .param("changedSince", "2015-10-31T01:30:00.000-0200")
        .get(REQUEST)
        .then()
        .statusCode(HttpStatus.OK.value())
        .contentType(ContentType.JSON)
        .body(JSON_CLASSIFICATIONS + ".size()", equalTo(1))
        // result 1
        .body(JSON_CLASSIFICATION1 + ".name", equalTo(TestDataProvider.BYDELSINNDELING_NAVN_NO))
        .body(
            JSON_CLASSIFICATION1 + "._links.self.href",
            containsString(REQUEST + "/" + bydelsinndeling.getId()))
        // links
        .body(JSON_LINKS + ".self.href", containsString(REQUEST))
        .body(JSON_LINKS + ".search.href", containsString(REQUEST_SEARCH))
        // paging
        .body(JSON_PAGE + ".size", equalTo(20))
        .body(JSON_PAGE + ".totalElements", equalTo(1))
        .body(JSON_PAGE + ".totalPages", equalTo(1))
        .body(JSON_PAGE + ".number", equalTo(0));
  }

  @Test
  public void restServiceListClassificationsXML() {
    given().port(port).accept(ContentType.XML).get(REQUEST).prettyPrint();

    given()
        .port(port)
        .accept(ContentType.XML)
        .get(REQUEST)
        //                .prettyPeek()
        .prettyPeek()
        .then()
        .statusCode(HttpStatus.OK.value())
        .contentType(ContentType.XML)
        .body(XML_CLASSIFICATIONS + ".size()", equalTo(2))
        // result 1
        .body(XML_CLASSIFICATION1 + ".name", equalTo(TestDataProvider.KOMMUNEINNDELING_NAVN_NO))
        .body(XML_CLASSIFICATION1 + ".link.rel", equalTo("self"))
        .body(
            XML_CLASSIFICATION1 + ".link.href",
            containsString(REQUEST + "/" + kommuneinndeling.getId()))
        // result 2
        .body(XML_CLASSIFICATION2 + ".name", equalTo(TestDataProvider.BYDELSINNDELING_NAVN_NO))
        .body(XML_CLASSIFICATION2 + ".link.rel", equalTo("self"))
        .body(
            XML_CLASSIFICATION2 + ".link.href",
            containsString(REQUEST + "/" + bydelsinndeling.getId()))
        // links
        .body(XML_ROOT + ".link[0].rel", equalTo("self"))
        .body(XML_ROOT + ".link[0].href", containsString(REQUEST))
        .body(XML_ROOT + ".link[1].rel", equalTo("search"))
        .body(XML_ROOT + ".link[1].href", containsString(REQUEST_SEARCH))
        // paging
        .body(XML_PAGE + ".size.toInteger();", equalTo(20))
        .body(XML_PAGE + ".totalElements.toInteger();", equalTo(2))
        .body(XML_PAGE + ".totalPages.toInteger();", equalTo(1))
        .body(XML_PAGE + ".number.toInteger();", equalTo(0));
  }

  @Test
  public void restServiceListClassificationsIncludeCodelistXML() {
    given()
        .port(port)
        .accept(ContentType.XML)
        .param("includeCodelists", "true")
        .get(REQUEST)
        .prettyPeek()
        .then()
        .statusCode(HttpStatus.OK.value())
        .contentType(ContentType.XML)
        .body(XML_CLASSIFICATIONS + ".size()", equalTo(4))
        // result 1
        .body(XML_CLASSIFICATION1 + ".name", equalTo(TestDataProvider.FAMILIEGRUPPERING_NAVN_NO))
        .body(XML_CLASSIFICATION1 + ".link.rel", equalTo("self"))
        .body(
            XML_CLASSIFICATION1 + ".link.href",
            containsString(REQUEST + "/" + familieGrupperingCodelist.getId()))
        // result 2
        .body(XML_CLASSIFICATION3 + ".name", equalTo(TestDataProvider.KOMMUNEINNDELING_NAVN_NO))
        .body(XML_CLASSIFICATION3 + ".link.rel", equalTo("self"))
        .body(
            XML_CLASSIFICATION3 + ".link.href",
            containsString(REQUEST + "/" + kommuneinndeling.getId()))
        // result 3
        .body(XML_CLASSIFICATION4 + ".name", equalTo(TestDataProvider.BYDELSINNDELING_NAVN_NO))
        .body(XML_CLASSIFICATION4 + ".link.rel", equalTo("self"))
        .body(
            XML_CLASSIFICATION4 + ".link.href",
            containsString(REQUEST + "/" + bydelsinndeling.getId()))

        // links
        .body(XML_ROOT + ".link[0].rel", equalTo("self"))
        .body(XML_ROOT + ".link[0].href", containsString(REQUEST))
        .body(XML_ROOT + ".link[1].rel", equalTo("search"))
        .body(XML_ROOT + ".link[1].href", containsString(REQUEST_SEARCH))
        // paging
        .body(XML_PAGE + ".size.toInteger();", equalTo(20))
        .body(XML_PAGE + ".totalElements.toInteger();", equalTo(4))
        .body(XML_PAGE + ".totalPages.toInteger();", equalTo(1))
        .body(XML_PAGE + ".number.toInteger();", equalTo(0));
  }

  @Test
  public void restServiceListClassificationsChangedSinceXML() {
    given()
        .port(port)
        .accept(ContentType.XML)
        .param("changedSince", "2015-10-31T01:30:00.000-0200")
        .get(REQUEST)
        //                .prettyPeek()
        .then()
        .statusCode(HttpStatus.OK.value())
        .contentType(ContentType.XML)
        .body(XML_CLASSIFICATIONS + ".size()", equalTo(1))
        // result 1
        .body(XML_CLASSIFICATION1 + ".name", equalTo(TestDataProvider.BYDELSINNDELING_NAVN_NO))
        .body(XML_CLASSIFICATION1 + ".link.rel", equalTo("self"))
        .body(
            XML_CLASSIFICATION1 + ".link.href",
            containsString(REQUEST + "/" + bydelsinndeling.getId()))
        // links
        .body(XML_ROOT + ".link[0].rel", equalTo("self"))
        .body(XML_ROOT + ".link[0].href", containsString(REQUEST))
        .body(XML_ROOT + ".link[1].rel", equalTo("search"))
        .body(XML_ROOT + ".link[1].href", containsString(REQUEST_SEARCH))
        // paging
        .body(XML_PAGE + ".size.toInteger();", equalTo(20))
        .body(XML_PAGE + ".totalElements.toInteger();", equalTo(1))
        .body(XML_PAGE + ".totalPages.toInteger();", equalTo(1))
        .body(XML_PAGE + ".number.toInteger();", equalTo(0));
  }
  // @formatter:on
}
