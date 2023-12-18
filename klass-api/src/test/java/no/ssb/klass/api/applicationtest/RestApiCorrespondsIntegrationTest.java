package no.ssb.klass.api.applicationtest;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import no.ssb.klass.testutil.TestDataProvider;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.http.ContentType;

/**
 * @author Mads Lundemo, SSB.
 */
public class RestApiCorrespondsIntegrationTest extends AbstractRestApiApplicationTest {
    // @formatter:off
    @Test
    public void restServiceCorrespondsJSON() {
        given().port(port).accept(ContentType.JSON)
                .param("targetClassificationId", bydelsinndeling.getId())
                .param("from", "2015-01-01")
                .get(REQUEST_WITH_ID_AND_CORRESPONDS, kommuneinndeling.getId())
//                .prettyPeek()
                .then()
                .contentType(ContentType.JSON)
                .statusCode(HttpStatus.OK.value())
                .body(JSON_CORRESPONDENCES + ".size()", equalTo(3))
                //1
                .body(JSON_CORRESPONDENCES + "[0].sourceCode", equalTo("0301"))
                .body(JSON_CORRESPONDENCES + "[0].sourceName", equalTo("Oslo"))
                .body(JSON_CORRESPONDENCES + "[0].sourceShortName", equalTo(""))
                .body(JSON_CORRESPONDENCES + "[0].targetCode", equalTo("030101"))
                .body(JSON_CORRESPONDENCES + "[0].targetName", equalTo("Gamle Oslo"))
                .body(JSON_CORRESPONDENCES + "[0].targetShortName", equalTo(""))
                .body(JSON_CORRESPONDENCES + "[0].validFrom", equalTo("2015-01-01"))
                .body(JSON_CORRESPONDENCES + "[0].validTo", equalTo(null))
                //...
                //3
                .body(JSON_CORRESPONDENCES + "[2].sourceCode", equalTo("0301"))
                .body(JSON_CORRESPONDENCES + "[2].sourceName", equalTo("Oslo"))
                .body(JSON_CORRESPONDENCES + "[2].sourceShortName", equalTo(""))
                .body(JSON_CORRESPONDENCES + "[2].targetCode", equalTo("030103"))
                .body(JSON_CORRESPONDENCES + "[2].targetName", equalTo("Sagene"))
                .body(JSON_CORRESPONDENCES + "[2].targetShortName", equalTo(""))
                .body(JSON_CORRESPONDENCES + "[2].validFrom", equalTo("2015-01-01"))
                .body(JSON_CORRESPONDENCES + "[2].validTo", equalTo(null));

    }

    @Test
    public void restServiceCorrespondsIncludeFutureVersionJSON() {
        given().port(port).accept(ContentType.JSON).param("includeFuture", true)
                .param("targetClassificationId", bydelsinndeling.getId())
                .param("from", "2015-01-01")
                .get(REQUEST_WITH_ID_AND_CORRESPONDS, kommuneinndeling.getId())
//                .prettyPeek()
                .then()
                .contentType(ContentType.JSON)
                .statusCode(HttpStatus.OK.value())
                .body(JSON_CORRESPONDENCES + ".size()", equalTo(4))
                //1
                .body(JSON_CORRESPONDENCES + "[0].sourceCode", equalTo("0301"))
                .body(JSON_CORRESPONDENCES + "[0].sourceName", equalTo("Oslo"))
                .body(JSON_CORRESPONDENCES + "[0].sourceShortName", equalTo(""))
                .body(JSON_CORRESPONDENCES + "[0].targetCode", equalTo("030101"))
                .body(JSON_CORRESPONDENCES + "[0].targetName", equalTo("Gamle Oslo"))
                .body(JSON_CORRESPONDENCES + "[0].targetShortName", equalTo(""))
                .body(JSON_CORRESPONDENCES + "[0].validFrom", equalTo("2015-01-01"))
                .body(JSON_CORRESPONDENCES + "[0].validTo", equalTo(null))
                //...
                //3
                .body(JSON_CORRESPONDENCES + "[2].sourceCode", equalTo("0301"))
                .body(JSON_CORRESPONDENCES + "[2].sourceName", equalTo("Oslo"))
                .body(JSON_CORRESPONDENCES + "[2].sourceShortName", equalTo(""))
                .body(JSON_CORRESPONDENCES + "[2].targetCode", equalTo("030103"))
                .body(JSON_CORRESPONDENCES + "[2].targetName", equalTo("Sagene"))
                .body(JSON_CORRESPONDENCES + "[2].targetShortName", equalTo(""))
                .body(JSON_CORRESPONDENCES + "[2].validFrom", equalTo("2015-01-01"))
                .body(JSON_CORRESPONDENCES + "[2].validTo", equalTo(TestDataProvider.TEN_YEARS_LATER_DATE))
                //3
                .body(JSON_CORRESPONDENCES + "[3].sourceCode", equalTo("0301"))
                .body(JSON_CORRESPONDENCES + "[3].sourceName", equalTo("Oslo"))
                .body(JSON_CORRESPONDENCES + "[3].sourceShortName", equalTo(""))
                .body(JSON_CORRESPONDENCES + "[3].targetCode", equalTo("030103"))
                .body(JSON_CORRESPONDENCES + "[3].targetName", equalTo("Sagene ny"))
                .body(JSON_CORRESPONDENCES + "[3].targetShortName", equalTo(""))
                .body(JSON_CORRESPONDENCES + "[3].validFrom", equalTo(TestDataProvider.TEN_YEARS_LATER_DATE))
                .body(JSON_CORRESPONDENCES + "[3].validTo", equalTo(null));

    }

    @Test
    public void restServiceCorrespondsXML() {
        given().port(port).accept(ContentType.XML)
                .param("targetClassificationId", bydelsinndeling.getId())
                .param("from", "2015-01-01")
                .get(REQUEST_WITH_ID_AND_CORRESPONDS, kommuneinndeling.getId())
//                .prettyPeek()
                .then()
                .contentType(ContentType.XML)
                .statusCode(HttpStatus.OK.value())
                .body(XML_CORRESPONDENCES + ".size()", equalTo(3))
                //1
                .body(XML_CORRESPONDENCES + "[0].sourceCode", equalTo("0301"))
                .body(XML_CORRESPONDENCES + "[0].sourceName", equalTo("Oslo"))
                .body(XML_CORRESPONDENCES + "[0].sourceShortName", equalTo(""))
                .body(XML_CORRESPONDENCES + "[0].targetCode", equalTo("030101"))
                .body(XML_CORRESPONDENCES + "[0].targetName", equalTo("Gamle Oslo"))
                .body(XML_CORRESPONDENCES + "[0].targetShortName", equalTo(""))
                .body(XML_CORRESPONDENCES + "[0].validFrom", equalTo("2015-01-01"))
                .body(XML_CORRESPONDENCES + "[0].validTo", equalTo(""))
                //...
                //3
                .body(XML_CORRESPONDENCES + "[2].sourceCode", equalTo("0301"))
                .body(XML_CORRESPONDENCES + "[2].sourceName", equalTo("Oslo"))
                .body(XML_CORRESPONDENCES + "[2].sourceShortName", equalTo(""))
                .body(XML_CORRESPONDENCES + "[2].targetCode", equalTo("030103"))
                .body(XML_CORRESPONDENCES + "[2].targetName", equalTo("Sagene"))
                .body(XML_CORRESPONDENCES + "[2].targetShortName", equalTo(""))
                .body(XML_CORRESPONDENCES + "[2].validFrom", equalTo("2015-01-01"))
                .body(XML_CORRESPONDENCES + "[2].validTo", equalTo(""));

    }

    @Test
    public void restServiceCorrespondsIncludeFutureVersionXML() {
        given().port(port).accept(ContentType.XML).param("includeFuture", true)
                .param("targetClassificationId", bydelsinndeling.getId())
                .param("from", "2015-01-01")
                .get(REQUEST_WITH_ID_AND_CORRESPONDS, kommuneinndeling.getId())
//                .prettyPeek()
                .then()
                .contentType(ContentType.XML)
                .statusCode(HttpStatus.OK.value())
                .body(XML_CORRESPONDENCES + ".size()", equalTo(4))
                //1
                .body(XML_CORRESPONDENCES + "[0].sourceCode", equalTo("0301"))
                .body(XML_CORRESPONDENCES + "[0].sourceName", equalTo("Oslo"))
                .body(XML_CORRESPONDENCES + "[0].sourceShortName", equalTo(""))
                .body(XML_CORRESPONDENCES + "[0].targetCode", equalTo("030101"))
                .body(XML_CORRESPONDENCES + "[0].targetName", equalTo("Gamle Oslo"))
                .body(XML_CORRESPONDENCES + "[0].targetShortName", equalTo(""))
                .body(XML_CORRESPONDENCES + "[0].validFrom", equalTo("2015-01-01"))
                .body(XML_CORRESPONDENCES + "[0].validTo", equalTo(""))
                //...
                //3
                .body(XML_CORRESPONDENCES + "[2].sourceCode", equalTo("0301"))
                .body(XML_CORRESPONDENCES + "[2].sourceName", equalTo("Oslo"))
                .body(XML_CORRESPONDENCES + "[2].sourceShortName", equalTo(""))
                .body(XML_CORRESPONDENCES + "[2].targetCode", equalTo("030103"))
                .body(XML_CORRESPONDENCES + "[2].targetName", equalTo("Sagene"))
                .body(XML_CORRESPONDENCES + "[2].targetShortName", equalTo(""))
                .body(XML_CORRESPONDENCES + "[2].validFrom", equalTo("2015-01-01"))
                .body(XML_CORRESPONDENCES + "[2].validTo", equalTo(TestDataProvider.TEN_YEARS_LATER_DATE))
                //4
                .body(XML_CORRESPONDENCES + "[3].sourceCode", equalTo("0301"))
                .body(XML_CORRESPONDENCES + "[3].sourceName", equalTo("Oslo"))
                .body(XML_CORRESPONDENCES + "[3].sourceShortName", equalTo(""))
                .body(XML_CORRESPONDENCES + "[3].targetCode", equalTo("030103"))
                .body(XML_CORRESPONDENCES + "[3].targetName", equalTo("Sagene ny"))
                .body(XML_CORRESPONDENCES + "[3].targetShortName", equalTo(""))
                .body(XML_CORRESPONDENCES + "[3].validFrom", equalTo(TestDataProvider.TEN_YEARS_LATER_DATE))
                .body(XML_CORRESPONDENCES + "[3].validTo", equalTo(""));

    }

    @Test
    public void restServiceCorrespondsCSV() {
        given().port(port).accept(CONTENT_TYPE_CSV)
                .param("targetClassificationId", bydelsinndeling.getId())
                .param("from", "2015-01-01")
                .get(REQUEST_WITH_ID_AND_CORRESPONDS, kommuneinndeling.getId())
//                .prettyPeek()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(containsString(
                        "\"sourceCode\",\"sourceName\",\"sourceShortName\",\"targetCode\",\"targetName\","
                                + "\"targetShortName\",\"validFrom\",\"validTo\"\n"
                                + "\"0301\",\"Oslo\",\"\",\"030101\",\"Gamle Oslo\",\"\",\"2015-01-01\",\n"
                                + "\"0301\",\"Oslo\",\"\",\"030102\",\"Grünerløkka\",\"\",\"2015-01-01\",\n"
                                + "\"0301\",\"Oslo\",\"\",\"030103\",\"Sagene\",\"\",\"2015-01-01\","
                ));

    }

    @Test
    public void restServiceCorrespondsIncludeFutureVersionCSV() {
        given().port(port).accept(CONTENT_TYPE_CSV).param("includeFuture", true)
                .param("targetClassificationId", bydelsinndeling.getId())
                .param("from", "2015-01-01")
                .get(REQUEST_WITH_ID_AND_CORRESPONDS, kommuneinndeling.getId())
//                .prettyPeek()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(containsString(
                        "\"sourceCode\",\"sourceName\",\"sourceShortName\",\"targetCode\",\"targetName\","
                                + "\"targetShortName\",\"validFrom\",\"validTo\"\n"
                                + "\"0301\",\"Oslo\",\"\",\"030101\",\"Gamle Oslo\",\"\",\"2015-01-01\",\n"
                                + "\"0301\",\"Oslo\",\"\",\"030102\",\"Grünerløkka\",\"\",\"2015-01-01\",\n"
                                + "\"0301\",\"Oslo\",\"\",\"030103\",\"Sagene\",\"\",\"2015-01-01\",\""+TestDataProvider.TEN_YEARS_LATER_DATE+"\"\n"
                                + "\"0301\",\"Oslo\",\"\",\"030103\",\"Sagene ny\",\"\",\""+TestDataProvider.TEN_YEARS_LATER_DATE+"\","
                ));

    }
// @formatter:on
}
