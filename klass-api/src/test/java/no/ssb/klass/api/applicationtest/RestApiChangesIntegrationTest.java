package no.ssb.klass.api.applicationtest;

import static io.restassured.RestAssured.*;

import static org.hamcrest.Matchers.*;

import io.restassured.http.ContentType;

import no.ssb.klass.testutil.TestDataProvider;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

/**
 * @author Mads Lundemo, SSB.
 */
public class RestApiChangesIntegrationTest extends AbstractRestApiApplicationTest {
    @Test
    public void restServiceChangesJSON() {
        given().port(port)
                .accept(ContentType.JSON)
                .param("from", "2013-01-01")
                .get(REQUEST_WITH_ID_AND_CHANGES, kommuneinndeling.getId())
                //                .prettyPeek()
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("codeChanges.size()", equalTo(2))
                // 1
                .body("codeChanges[0].oldCode", equalTo("1739"))
                .body("codeChanges[0].oldName", equalTo("Røyrvik"))
                .body("codeChanges[0].oldShortName", equalTo(""))
                .body("codeChanges[0].newCode", equalTo("1739"))
                .body("codeChanges[0].newName", equalTo("Raarvihke Røyrvik"))
                .body("codeChanges[0].newShortName", equalTo(""))
                .body("codeChanges[0].changeOccurred", equalTo("2014-01-01"))
                // 2
                .body("codeChanges[1].oldCode", equalTo("1939"))
                .body("codeChanges[1].oldName", equalTo("Storfjord"))
                .body("codeChanges[1].oldShortName", equalTo(""))
                .body("codeChanges[1].newCode", equalTo("1939"))
                .body("codeChanges[1].newName", equalTo("Omasvuotna Storfjord Omasvuonon"))
                .body("codeChanges[1].newShortName", equalTo(""))
                .body("codeChanges[1].changeOccurred", equalTo("2014-01-01"));
    }

    @Test
    public void restServiceChangesIncludeFutureVersionJSON() {
        given().port(port)
                .accept(ContentType.JSON)
                .param("from", "2013-01-01")
                .param("includeFuture", true)
                .get(REQUEST_WITH_ID_AND_CHANGES, kommuneinndeling.getId())
                //               .prettyPeek()
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("codeChanges.size()", equalTo(3))
                // 1
                .body("codeChanges[0].oldCode", equalTo("1739"))
                .body("codeChanges[0].oldName", equalTo("Røyrvik"))
                .body("codeChanges[0].oldShortName", equalTo(""))
                .body("codeChanges[0].newCode", equalTo("1739"))
                .body("codeChanges[0].newName", equalTo("Raarvihke Røyrvik"))
                .body("codeChanges[0].newShortName", equalTo(""))
                .body("codeChanges[0].changeOccurred", equalTo("2014-01-01"))
                // 2
                .body("codeChanges[1].oldCode", equalTo("1939"))
                .body("codeChanges[1].oldName", equalTo("Storfjord"))
                .body("codeChanges[1].oldShortName", equalTo(""))
                .body("codeChanges[1].newCode", equalTo("1939"))
                .body("codeChanges[1].newName", equalTo("Omasvuotna Storfjord Omasvuonon"))
                .body("codeChanges[1].newShortName", equalTo(""))
                .body("codeChanges[1].changeOccurred", equalTo("2014-01-01"))
                // 2
                .body("codeChanges[2].oldCode", equalTo("1739"))
                .body("codeChanges[2].oldName", equalTo("Raarvihke Røyrvik"))
                .body("codeChanges[2].oldShortName", equalTo(""))
                .body("codeChanges[2].newCode", equalTo("5043"))
                .body("codeChanges[2].newName", equalTo("Raarvihke Røyrvik"))
                .body("codeChanges[2].newShortName", equalTo(""))
                .body(
                        "codeChanges[2].changeOccurred",
                        equalTo(TestDataProvider.TEN_YEARS_LATER_DATE));
    }

    @Test
    public void restServiceChangesXML() {
        given().port(port)
                .accept(ContentType.XML)
                .param("from", "2013-01-01")
                .get(REQUEST_WITH_ID_AND_CHANGES, kommuneinndeling.getId())
                //                .prettyPeek()
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.XML)
                .body("codeChangeList.codeChangeItem.size()", equalTo(2))
                // 1
                .body("codeChangeList.codeChangeItem[0].oldCode", equalTo("1739"))
                .body("codeChangeList.codeChangeItem[0].oldName", equalTo("Røyrvik"))
                .body("codeChangeList.codeChangeItem[0].oldShortName", equalTo(""))
                .body("codeChangeList.codeChangeItem[0].newCode", equalTo("1739"))
                .body("codeChangeList.codeChangeItem[0].newName", equalTo("Raarvihke Røyrvik"))
                .body("codeChangeList.codeChangeItem[0].newShortName", equalTo(""))
                .body("codeChangeList.codeChangeItem[0].changeOccurred", equalTo("2014-01-01"))
                // 2
                .body("codeChangeList.codeChangeItem[1].oldCode", equalTo("1939"))
                .body("codeChangeList.codeChangeItem[1].oldName", equalTo("Storfjord"))
                .body("codeChangeList.codeChangeItem[1].oldShortName", equalTo(""))
                .body("codeChangeList.codeChangeItem[1].newCode", equalTo("1939"))
                .body(
                        "codeChangeList.codeChangeItem[1].newName",
                        equalTo("Omasvuotna Storfjord Omasvuonon"))
                .body("codeChangeList.codeChangeItem[1].newShortName", equalTo(""))
                .body("codeChangeList.codeChangeItem[1].changeOccurred", equalTo("2014-01-01"));
    }

    @Test
    public void restServiceChangesIncludeFutureVersionXML() {
        given().port(port)
                .accept(ContentType.XML)
                .param("from", "2013-01-01")
                .param("includeFuture", true)
                .get(REQUEST_WITH_ID_AND_CHANGES, kommuneinndeling.getId())
                .prettyPeek()
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.XML)
                .body("codeChangeList.codeChangeItem.size()", equalTo(3))
                // 1
                .body("codeChangeList.codeChangeItem[0].oldCode", equalTo("1739"))
                .body("codeChangeList.codeChangeItem[0].oldName", equalTo("Røyrvik"))
                .body("codeChangeList.codeChangeItem[0].oldShortName", equalTo(""))
                .body("codeChangeList.codeChangeItem[0].newCode", equalTo("1739"))
                .body("codeChangeList.codeChangeItem[0].newName", equalTo("Raarvihke Røyrvik"))
                .body("codeChangeList.codeChangeItem[0].newShortName", equalTo(""))
                .body("codeChangeList.codeChangeItem[0].changeOccurred", equalTo("2014-01-01"))
                // 2
                .body("codeChangeList.codeChangeItem[1].oldCode", equalTo("1939"))
                .body("codeChangeList.codeChangeItem[1].oldName", equalTo("Storfjord"))
                .body("codeChangeList.codeChangeItem[1].oldShortName", equalTo(""))
                .body("codeChangeList.codeChangeItem[1].newCode", equalTo("1939"))
                .body(
                        "codeChangeList.codeChangeItem[1].newName",
                        equalTo("Omasvuotna Storfjord Omasvuonon"))
                .body("codeChangeList.codeChangeItem[1].newShortName", equalTo(""))
                .body("codeChangeList.codeChangeItem[1].changeOccurred", equalTo("2014-01-01"))
                // 3
                .body("codeChangeList.codeChangeItem[2].oldCode", equalTo("1739"))
                .body("codeChangeList.codeChangeItem[2].oldName", equalTo("Raarvihke Røyrvik"))
                .body("codeChangeList.codeChangeItem[2].oldShortName", equalTo(""))
                .body("codeChangeList.codeChangeItem[2].newCode", equalTo("5043"))
                .body("codeChangeList.codeChangeItem[2].newName", equalTo("Raarvihke Røyrvik"))
                .body("codeChangeList.codeChangeItem[2].newShortName", equalTo(""))
                .body(
                        "codeChangeList.codeChangeItem[2].changeOccurred",
                        equalTo(TestDataProvider.TEN_YEARS_LATER_DATE));
    }
}
