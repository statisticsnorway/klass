package no.ssb.klass.api.applicationtest;

import static com.jayway.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import no.ssb.klass.testutil.TestDataProvider;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import com.jayway.restassured.http.ContentType;

import no.ssb.klass.core.config.ConfigurationProfiles;

/**
 * @author Mads Lundemo, SSB.
 */
@ActiveProfiles(profiles = { ConfigurationProfiles.H2_INMEMORY }, inheritProfiles = false)
public class RestApiSearchIntegrationTest extends AbstractRestApiApplicationTest {
    // @formatter:off
    @Test
    public void restServiceSearchClassificationsJSON() {
        given().port(port).accept(ContentType.JSON).params("query", "kommune")
                .get(REQUEST_SEARCH)
//                .prettyPeek()
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body(JSON_SEARCH_RESULTS + ".size()", equalTo(2))
                // result 1
                .body(JSON_SEARCH_RESULT1 + ".name", equalTo(TestDataProvider.KOMMUNEINNDELING_NAVN_NO))
                .body(JSON_SEARCH_RESULT1 + ".snippet", containsString("kommune"))
                .body(JSON_SEARCH_RESULT1 + ".searchScore", greaterThan(0.0f))
                .body(JSON_SEARCH_RESULT1 + "._links.self.href", containsString(REQUEST + "/" + kommuneinndeling.getId()))
                //result 2
                .body(JSON_SEARCH_RESULT2 + ".name", equalTo(TestDataProvider.BYDELSINNDELING_NAVN_NO))
                .body(JSON_SEARCH_RESULT2 + ".snippet", containsString("kommune"))
                .body(JSON_SEARCH_RESULT2 + ".searchScore", greaterThan(0.0f))
                .body(JSON_SEARCH_RESULT2 + "._links.self.href", containsString(REQUEST + "/" + bydelsinndeling.getId()))
                // footer
                .body(JSON_LINKS + ".self.href", containsString(REQUEST_SEARCH))
                .body(JSON_PAGE + ".size", equalTo(PAGE_SIZE))
                .body(JSON_PAGE + ".totalElements", equalTo(2))
                .body(JSON_PAGE + ".totalPages", equalTo(1))
                .body(JSON_PAGE + ".number", equalTo(0));

    }

    @Test
    public void restServiceSearchClassificationsXML() {
        given().port(port).accept(ContentType.XML).params("query", "kommuner")
                .get(REQUEST_SEARCH)
//                .prettyPeek()
                .then().assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.XML)
                .body(XML_SEARCH_RESULTS + ".size()", equalTo(2))
                // result 1
                .body(XML_SEARCH_RESULT1 + ".name", equalTo(TestDataProvider.KOMMUNEINNDELING_NAVN_NO))
                .body(XML_SEARCH_RESULT1 + ".snippet", containsString("kommune"))
                .body(XML_SEARCH_RESULT1 + ".searchScore.toFloat()", greaterThan(0.0f))
                .body(XML_SEARCH_RESULT1 + ".links.link.rel", equalTo("self"))
                .body(XML_SEARCH_RESULT1 + ".links.link.href", containsString(REQUEST + "/" + kommuneinndeling.getId()))
                //result 2
                .body(XML_SEARCH_RESULT2 + ".name", equalTo(TestDataProvider.BYDELSINNDELING_NAVN_NO))
                .body(XML_SEARCH_RESULT2 + ".snippet", containsString("kommune"))
                .body(XML_SEARCH_RESULT2 + ".searchScore.toFloat();", greaterThan(0.0f))
                .body(XML_SEARCH_RESULT2 + ".links.link.rel", equalTo("self"))
                .body(XML_SEARCH_RESULT2 + ".links.link.href", containsString(REQUEST + "/" + bydelsinndeling.getId()))
                // footer
                .body(XML_LINKS + ".link.href", containsString(REQUEST_SEARCH))
                .body(XML_PAGE + ".size.toInteger();", equalTo(PAGE_SIZE))
                .body(XML_PAGE + ".totalElements.toInteger();", equalTo(2))
                .body(XML_PAGE + ".totalPages.toInteger();", equalTo(1))
                .body(XML_PAGE + ".number.toInteger();", equalTo(0));

    }

    @Test
    public void restServiceSearchClassificationsShouldNotReturnCodelistsByDefaultJSON() {
        // no result expected when codelists are not included (default behavior)
        given().port(port).accept(ContentType.JSON).params("query", "familie")
                .get(REQUEST_SEARCH)
//                .prettyPeek()
                .then().assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body(JSON_PAGE + ".totalElements", equalTo(0))
                .body(JSON_PAGE + ".totalPages", equalTo(0))
                .body(JSON_PAGE + ".number", equalTo(0));
    }

    @Test
    public void restServiceSearchClassificationsShouldNotReturnCodelistsByDefaultXML() {
        // no result expected when  codelists are not included (default behavior)
        given().port(port).accept(ContentType.XML).params("query", "familie")
                .get(REQUEST_SEARCH)
//                .prettyPeek()
                .then().assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.XML)
                .body(XML_SEARCH_RESULTS + ".size()", equalTo(0))
                .body(XML_PAGE + ".totalElements.toInteger();", equalTo(0))
                .body(XML_PAGE + ".totalPages.toInteger();", equalTo(0))
                .body(XML_PAGE + ".number.toInteger();", equalTo(0));
    }

    @Test
    public void restServiceSearchClassificationsWithCodelistJSON() {

        // one result expected when  codelists are  included
        given().port(port).accept(ContentType.JSON).param("query", "familie").param("includeCodelists", "true")
                .get(REQUEST_SEARCH)
//                .prettyPeek()
                .then().assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body(JSON_SEARCH_RESULTS + ".size()", equalTo(1))
                // result 1
                .body(JSON_SEARCH_RESULT1 + ".name", equalTo(TestDataProvider.FAMILIEGRUPPERING_NAVN_NO))
                .body(JSON_SEARCH_RESULT1 + ".snippet", containsString("familie"))
                .body(JSON_SEARCH_RESULT1 + ".searchScore", greaterThan(0.0f))
                .body(JSON_SEARCH_RESULT1 + "._links.self.href", containsString(REQUEST + "/" + familieGrupperingCodelist.getId()));

    }

    @Test
    public void restServiceSearchClassificationsWithCodelistXML() {

        // one result expected when  codelists are  included
        given().port(port).accept(ContentType.XML).param("query", "familie").param("includeCodelists", "true")
                .get(REQUEST_SEARCH)
//                .prettyPeek()
                .then().assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.XML)
                .body(XML_SEARCH_RESULTS + ".size()", equalTo(1))
                // result 1
                .body(XML_SEARCH_RESULT1 + ".name", equalTo(TestDataProvider.FAMILIEGRUPPERING_NAVN_NO))
                .body(XML_SEARCH_RESULT1 + ".snippet", containsString("familie"))
                .body(XML_SEARCH_RESULT1 + ".searchScore.toFloat();", greaterThan(0.0f))
                .body(XML_SEARCH_RESULT1 + ".links.link.href", containsString(REQUEST + "/" + familieGrupperingCodelist.getId()));

    }
// @formatter:on
}
