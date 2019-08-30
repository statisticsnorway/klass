package no.ssb.klass.api.applicationtest;

import static com.jayway.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import no.ssb.klass.testutil.TestDataProvider;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import com.jayway.restassured.http.ContentType;

/**
 * @author Mads Lundemo, SSB.
 */
public class RestApiCorrespondsAtIntegrationTest extends AbstractRestApiApplicationTest {
    // @formatter:off
    @Test
    public void restServiceCorrespondsAtJSON() {
        given().port(port).accept(ContentType.JSON)
                .param("targetClassificationId", bydelsinndeling.getId())
                .param("date", "2015-01-01")
                .get(REQUEST_WITH_ID_AND_CORRESPONDS_AT, kommuneinndeling.getId())
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
                //...
                //3
                .body(JSON_CORRESPONDENCES + "[2].sourceCode", equalTo("0301"))
                .body(JSON_CORRESPONDENCES + "[2].sourceName", equalTo("Oslo"))
                .body(JSON_CORRESPONDENCES + "[2].sourceShortName", equalTo(""))
                .body(JSON_CORRESPONDENCES + "[2].targetCode", equalTo("030103"))
                .body(JSON_CORRESPONDENCES + "[2].targetName", equalTo("Sagene"))
                .body(JSON_CORRESPONDENCES + "[2].targetShortName", equalTo(""));

    }

    @Test
    public void restServiceCorrespondsAtIncludeFutureVersionJSON() {
        given().port(port).accept(ContentType.JSON).param("includeFuture", true)
                .param("targetClassificationId", bydelsinndeling.getId())
                .param("date", TestDataProvider.TEN_YEARS_LATER_DATE)
                .get(REQUEST_WITH_ID_AND_CORRESPONDS_AT, kommuneinndeling.getId())
  //              .prettyPeek()
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
                //...
                //3
                .body(JSON_CORRESPONDENCES + "[2].sourceCode", equalTo("0301"))
                .body(JSON_CORRESPONDENCES + "[2].sourceName", equalTo("Oslo"))
                .body(JSON_CORRESPONDENCES + "[2].sourceShortName", equalTo(""))
                .body(JSON_CORRESPONDENCES + "[2].targetCode", equalTo("030103"))
                .body(JSON_CORRESPONDENCES + "[2].targetName", equalTo("Sagene ny"))
                .body(JSON_CORRESPONDENCES + "[2].targetShortName", equalTo(""));

    }

    @Test
    public void restServiceCorrespondsXML() {
        given().port(port).accept(ContentType.XML)
                .param("targetClassificationId", bydelsinndeling.getId())
                .param("date", "2015-01-01")
                .get(REQUEST_WITH_ID_AND_CORRESPONDS_AT, kommuneinndeling.getId())
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
                //...
                //3
                .body(XML_CORRESPONDENCES + "[2].sourceCode", equalTo("0301"))
                .body(XML_CORRESPONDENCES + "[2].sourceName", equalTo("Oslo"))
                .body(XML_CORRESPONDENCES + "[2].sourceShortName", equalTo(""))
                .body(XML_CORRESPONDENCES + "[2].targetCode", equalTo("030103"))
                .body(XML_CORRESPONDENCES + "[2].targetName", equalTo("Sagene"))
                .body(XML_CORRESPONDENCES + "[2].targetShortName", equalTo(""));

    }

    @Test
    public void restServiceCorrespondsIncludeFutureVersionXML() {
        given().port(port).accept(ContentType.XML).param("includeFuture", true)
                .param("targetClassificationId", bydelsinndeling.getId())
                .param("date", TestDataProvider.TEN_YEARS_LATER_DATE)
                .get(REQUEST_WITH_ID_AND_CORRESPONDS_AT, kommuneinndeling.getId())
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
                //...
                //3
                .body(XML_CORRESPONDENCES + "[2].sourceCode", equalTo("0301"))
                .body(XML_CORRESPONDENCES + "[2].sourceName", equalTo("Oslo"))
                .body(XML_CORRESPONDENCES + "[2].sourceShortName", equalTo(""))
                .body(XML_CORRESPONDENCES + "[2].targetCode", equalTo("030103"))
                .body(XML_CORRESPONDENCES + "[2].targetName", equalTo("Sagene ny"))
                .body(XML_CORRESPONDENCES + "[2].targetShortName", equalTo(""));

    }

    @Test
    public void restServiceCorrespondsCSV() {
        given().port(port).accept(CONTENT_TYPE_CSV)
                .param("targetClassificationId", bydelsinndeling.getId())
                .param("date", "2015-01-01")
                .get(REQUEST_WITH_ID_AND_CORRESPONDS_AT, kommuneinndeling.getId())
//                .prettyPeek()
                .then()
                .statusCode(HttpStatus.OK.value())
                .content(containsString(
                        "\"sourceCode\",\"sourceName\",\"sourceShortName\",\"targetCode\",\"targetName\",\"targetShortName\"\n"
                                + "\"0301\",\"Oslo\",\"\",\"030101\",\"Gamle Oslo\",\"\"\n"
                                + "\"0301\",\"Oslo\",\"\",\"030102\",\"Grünerløkka\",\"\"\n"
                                + "\"0301\",\"Oslo\",\"\",\"030103\",\"Sagene\",\"\""
                ));

    }

    @Test
    public void restServiceCorrespondsIncludeFutureVersionCSV() {
        given().port(port).accept(CONTENT_TYPE_CSV).param("includeFuture", true)
                .param("targetClassificationId", bydelsinndeling.getId())
                .param("date", TestDataProvider.TEN_YEARS_LATER_DATE)
                .get(REQUEST_WITH_ID_AND_CORRESPONDS_AT, kommuneinndeling.getId())
//                .prettyPeek()
                .then()
                .statusCode(HttpStatus.OK.value())
                .content(containsString(
                        "\"sourceCode\",\"sourceName\",\"sourceShortName\",\"targetCode\",\"targetName\",\"targetShortName\"\n"
                                + "\"0301\",\"Oslo\",\"\",\"030101\",\"Gamle Oslo\",\"\"\n"
                                + "\"0301\",\"Oslo\",\"\",\"030102\",\"Grünerløkka\",\"\"\n"
                                + "\"0301\",\"Oslo\",\"\",\"030103\",\"Sagene ny\",\"\""
                ));

    }
// @formatter:on
}
