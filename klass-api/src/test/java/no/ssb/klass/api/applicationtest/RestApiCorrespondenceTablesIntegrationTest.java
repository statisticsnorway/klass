package no.ssb.klass.api.applicationtest;

import static io.restassured.RestAssured.given;

import static org.hamcrest.Matchers.*;

import io.restassured.http.ContentType;

import no.ssb.klass.api.util.RestConstants;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

public class RestApiCorrespondenceTablesIntegrationTest extends AbstractRestApiApplicationTest {
    @Test
    public void restServiceCorrespondenceTablesJSON() {
        given().port(port)
                .accept(ContentType.JSON)
                .param("language", "nb")
                .get(REQUEST_CORRESPONDENCE_TABLES, correspondenceTable.getId())
                .then()
                .contentType(ContentType.JSON)
                .statusCode(HttpStatus.OK.value())
                .body("name", is("Kommuneinndeling 2014 - Bydelsinndeling 2014"))
                .body("contactPerson", notNullValue())
                .body("owningSection", is("section"))
                .body("source", is("Kommuneinndeling 2014"))
                .body("sourceId", is(correspondenceTable.getSource().getId().intValue()))
                .body("target", is("Bydelsinndeling 2014"))
                .body("targetId", is(correspondenceTable.getTarget().getId().intValue()))
                .body("changeTable", notNullValue())
                .body("published", notNullValue())
                .body("sourceLevel", isEmptyOrNullString())
                .body("targetLevel", isEmptyOrNullString())
                .body("changelogs", notNullValue())
                .body("correspondenceMaps.size()", is(3))
                .body("correspondenceMaps[0].sourceCode", is("0301"))
                .body("correspondenceMaps[0].sourceName", is("Oslo"))
                .body("correspondenceMaps[0].targetCode", is("030101"))
                .body("correspondenceMaps[0].targetName", is("Gamle Oslo"))
                .body(
                        JSON_LINKS + ".self.href",
                        endsWith("/correspondencetables/" + correspondenceTable.getId().intValue()))
                .body(
                        JSON_LINKS + ".source.href",
                        endsWith(
                                "/versions/"
                                        + correspondenceTable.getSource().getId().intValue()
                                        + "{?language,includeFuture}"))
                .body(
                        JSON_LINKS + ".target.href",
                        endsWith(
                                "/versions/"
                                        + correspondenceTable.getTarget().getId().intValue()
                                        + "{?language,includeFuture}"));
    }

    @Test
    public void restServiceCorrespondenceTablesXML() {
        given().port(port)
                .accept(ContentType.XML)
                //                .param("language", "nb")
                .get(REQUEST_CORRESPONDENCE_TABLES, correspondenceTable.getId())
                .then()
                .contentType(ContentType.XML)
                .statusCode(HttpStatus.OK.value())
                .body(
                        XML_CORRESPONDENCETABLE + ".name",
                        is("Kommuneinndeling 2014 - Bydelsinndeling 2014"))
                .body(XML_CORRESPONDENCETABLE + ".contactPerson", notNullValue())
                .body(XML_CORRESPONDENCETABLE + ".owningSection", is("section"))
                .body(XML_CORRESPONDENCETABLE + ".source", is("Kommuneinndeling 2014"))
                .body(
                        XML_CORRESPONDENCETABLE + ".sourceId",
                        is(String.valueOf(correspondenceTable.getSource().getId().intValue())))
                .body(XML_CORRESPONDENCETABLE + ".target", is("Bydelsinndeling 2014"))
                .body(
                        XML_CORRESPONDENCETABLE + ".targetId",
                        is(String.valueOf(correspondenceTable.getTarget().getId().intValue())))
                .body(XML_CORRESPONDENCETABLE + ".changeTable", notNullValue())
                .body(XML_CORRESPONDENCETABLE + ".published", notNullValue())
                .body(XML_CORRESPONDENCETABLE + ".sourceLevel", isEmptyOrNullString())
                .body(XML_CORRESPONDENCETABLE + ".targetLevel", isEmptyOrNullString())
                .body(XML_CORRESPONDENCETABLE + ".changelogs", notNullValue())
                .body(XML_CORRESPONDENCETABLE_MAP + ".size()", is(3))
                .body(XML_CORRESPONDENCETABLE_MAP + "[0].sourceCode", is("0301"))
                .body(XML_CORRESPONDENCETABLE_MAP + "[0].sourceName", is("Oslo"))
                .body(XML_CORRESPONDENCETABLE_MAP + "[0].targetCode", is("030101"))
                .body(XML_CORRESPONDENCETABLE_MAP + "[0].targetName", is("Gamle Oslo"))
                .body(XML_CORRESPONDENCETABLE + ".link[0].rel", is("self"))
                .body(
                        XML_CORRESPONDENCETABLE + ".link[0].href",
                        endsWith("/correspondencetables/" + correspondenceTable.getId().intValue()))
                .body(XML_CORRESPONDENCETABLE + ".link[1].rel", is("source"))
                .body(
                        XML_CORRESPONDENCETABLE + ".link[1].href",
                        endsWith(
                                "/versions/"
                                        + correspondenceTable.getSource().getId().intValue()
                                        + "{?language,includeFuture}"))
                .body(XML_CORRESPONDENCETABLE + ".link[2].rel", is("target"))
                .body(
                        XML_CORRESPONDENCETABLE + ".link[2].href",
                        endsWith(
                                "/versions/"
                                        + correspondenceTable.getTarget().getId().intValue()
                                        + "{?language,includeFuture}"));
    }

    @Test
    public void restServiceCorrespondenceTablesCSV() {
        given().port(port)
                .accept(RestConstants.CONTENT_TYPE_CSV)
                .get(REQUEST_CORRESPONDENCE_TABLES, correspondenceTable.getId())
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(RestConstants.CONTENT_TYPE_CSV)
                .body(
                        containsString(
                                "\"sourceCode\";\"sourceName\";\"targetCode\";\"targetName\"\n"
                                        + "\"0301\";\"Oslo\";\"030101\";\"Gamle Oslo\"\n"
                                        + "\"0301\";\"Oslo\";\"030102\";\"Grünerløkka\"\n"
                                        + "\"0301\";\"Oslo\";\"030103\";\"Sagene\""));
    }
}
