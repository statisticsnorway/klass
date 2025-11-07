package no.ssb.klass.api.applicationtest;

import static io.restassured.RestAssured.given;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

import io.restassured.http.ContentType;

import no.ssb.klass.api.util.RestConstants;
import no.ssb.klass.testutil.TestDataProvider;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

/**
 * @author Mads Lundemo, SSB.
 */
public class RestApiCodeAtIntegrationTest extends AbstractRestApiApplicationTest {
    // @formatter:off
    @Test
    public void restServiceCodesAt2012JSON() {
        given().port(port).accept(ContentType.JSON).param("date", "2012-01-01")
                .get(REQUEST_WITH_ID_AND_CODES_AT, kommuneinndeling.getId())
//                .prettyPeek()
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("codes.size()", equalTo(5))
                //1
                .body(JSON_CODES + "[0].code", equalTo("0101"))
                .body(JSON_CODES + "[0].level", equalTo("1"))
                .body(JSON_CODES + "[0].name", equalTo("Halden"))
                .body(JSON_CODES + "[0].shortName", equalTo(""))
                .body(JSON_CODES + "[0].presentationName", equalTo(""))
                //2
                .body(JSON_CODES + "[1].code", equalTo("0104"))
                .body(JSON_CODES + "[1].level", equalTo("1"))
                .body(JSON_CODES + "[1].name", equalTo("Moss"))
                .body(JSON_CODES + "[1].shortName", equalTo(""))
                .body(JSON_CODES + "[1].presentationName", equalTo(""))
                //...
                //5
                .body(JSON_CODES + "[4].code", equalTo("1939"))
                .body(JSON_CODES + "[4].level", equalTo("1"))
                .body(JSON_CODES + "[4].name", equalTo("Storfjord"))
                .body(JSON_CODES + "[4].shortName", equalTo(""))
                .body(JSON_CODES + "[4].presentationName", equalTo(""));
    }

    @Test
    public void restServiceCodesAt2015JSON() {
        given().port(port).accept(ContentType.JSON).param("date", "2015-01-01")
                .get(REQUEST_WITH_ID_AND_CODES_AT, kommuneinndeling.getId())
//                .prettyPeek()
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("codes.size()", equalTo(5))
                //1
                .body(JSON_CODES + "[0].code", equalTo("0101"))
                .body(JSON_CODES + "[0].level", equalTo("1"))
                .body(JSON_CODES + "[0].name", equalTo("Halden"))
                .body(JSON_CODES + "[0].shortName", equalTo(""))
                .body(JSON_CODES + "[0].presentationName", equalTo(""))
                //2
                .body(JSON_CODES + "[1].code", equalTo("0104"))
                .body(JSON_CODES + "[1].level", equalTo("1"))
                .body(JSON_CODES + "[1].name", equalTo("Moss"))
                .body(JSON_CODES + "[1].shortName", equalTo(""))
                .body(JSON_CODES + "[1].presentationName", equalTo(""))
                //...
                //5
                .body(JSON_CODES + "[4].code", equalTo("1939"))
                .body(JSON_CODES + "[4].level", equalTo("1"))
                .body(JSON_CODES + "[4].name", equalTo("Omasvuotna Storfjord Omasvuonon"))
                .body(JSON_CODES + "[4].shortName", equalTo(""))
                .body(JSON_CODES + "[4].presentationName", equalTo(""));
    }

    @Test
    public void restServiceCodesAtFutureDateIncludedFutureVersionIsTrueJSON() {
        given().port(port).accept(ContentType.JSON).param("date", TestDataProvider.TEN_YEARS_LATER_DATE).param("includeFuture", true)
                .get(REQUEST_WITH_ID_AND_CODES_AT, kommuneinndeling.getId())
 //               .prettyPeek()
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("codes.size()", equalTo(5))
                //1
                .body(JSON_CODES + "[0].code", equalTo("0101"))
                .body(JSON_CODES + "[0].name", equalTo("Halden"))
                //2
                .body(JSON_CODES + "[1].code", equalTo("0104"))
                .body(JSON_CODES + "[1].name", equalTo("Moss"))
                //...
                //5
                .body(JSON_CODES + "[4].code", equalTo("5043"))
                .body(JSON_CODES + "[4].name", equalTo("Raarvihke Røyrvik"));
    }

    @Test
    public void restServiceCodesAtFutureDateIncludedFutureVersionIsFalseJSON() {
        given().port(port).accept(ContentType.JSON).param("date", TestDataProvider.TEN_YEARS_LATER_DATE).param("includeFuture", false)
                .get(REQUEST_WITH_ID_AND_CODES_AT, kommuneinndeling.getId())
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("codes.size()", equalTo(0));
    }

    @Test
    public void restServiceCodesAt2012XML() {
        given().port(port).accept(ContentType.XML).param("date", "2012-01-01")
                .get(REQUEST_WITH_ID_AND_CODES_AT, kommuneinndeling.getId())
//                .prettyPeek()
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.XML)
                .body(XML_CODES + ".size()", equalTo(5))
                //1
                .body(XML_CODES + "[0].code", equalTo("0101"))
                .body(XML_CODES + "[0].level", equalTo("1"))
                .body(XML_CODES + "[0].name", equalTo("Halden"))
                .body(XML_CODES + "[0].shortName", equalTo(""))
                .body(XML_CODES + "[0].presentationName", equalTo(""))
                //2
                .body(XML_CODES + "[1].code", equalTo("0104"))
                .body(XML_CODES + "[1].level", equalTo("1"))
                .body(XML_CODES + "[1].name", equalTo("Moss"))
                .body(XML_CODES + "[1].shortName", equalTo(""))
                .body(XML_CODES + "[1].presentationName", equalTo(""))
                //...
                //5
                .body(XML_CODES + "[4].code", equalTo("1939"))
                .body(XML_CODES + "[4].level", equalTo("1"))
                .body(XML_CODES + "[4].name", equalTo("Storfjord"))
                .body(XML_CODES + "[4].shortName", equalTo(""))
                .body(XML_CODES + "[4].presentationName", equalTo(""));
    }

    @Test
    public void restServiceCodesAt2015XML() {
        given().port(port).accept(ContentType.XML).param("date", "2015-01-01")
                .get(REQUEST_WITH_ID_AND_CODES_AT, kommuneinndeling.getId())
//                .prettyPeek()
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.XML)
                .body(XML_CODES + ".size()", equalTo(5))
                //1
                .body(XML_CODES + "[0].code", equalTo("0101"))
                .body(XML_CODES + "[0].level", equalTo("1"))
                .body(XML_CODES + "[0].name", equalTo("Halden"))
                .body(XML_CODES + "[0].shortName", equalTo(""))
                .body(XML_CODES + "[0].presentationName", equalTo(""))
                .body(XML_CODES + "[0].notes", equalTo(""))
                //2
                .body(XML_CODES + "[1].code", equalTo("0104"))
                .body(XML_CODES + "[1].level", equalTo("1"))
                .body(XML_CODES + "[1].name", equalTo("Moss"))
                .body(XML_CODES + "[1].shortName", equalTo(""))
                .body(XML_CODES + "[1].presentationName", equalTo(""))
                .body(XML_CODES + "[1].notes", equalTo(""))
                //...
                //4
                .body(XML_CODES + "[3].code", equalTo("1739"))
                .body(XML_CODES + "[3].level", equalTo("1"))
                .body(XML_CODES + "[3].name", equalTo("Raarvihke Røyrvik"))
                .body(XML_CODES + "[3].shortName", equalTo(""))
                .body(XML_CODES + "[3].presentationName", equalTo(""))
                .body(XML_CODES + "[3].notes", equalTo("13. juni 2014 ble det vedtatt at kommunenavnet skulle være tospråklig (samisk -norsk)"))
                //5
                .body(XML_CODES + "[4].code", equalTo("1939"))
                .body(XML_CODES + "[4].level", equalTo("1"))
                .body(XML_CODES + "[4].name", equalTo("Omasvuotna Storfjord Omasvuonon"))
                .body(XML_CODES + "[4].shortName", equalTo(""))
                .body(XML_CODES + "[4].presentationName", equalTo(""))
                .body(XML_CODES + "[4].notes", equalTo(""));
    }

    @Test
    public void restServiceCodesAtFutureDateIncludedFutureVersionXML() {
        given().port(port).accept(ContentType.XML).param("date", TestDataProvider.TEN_YEARS_LATER_DATE).param("includeFuture", true)
                .get(REQUEST_WITH_ID_AND_CODES_AT, kommuneinndeling.getId())
                //.prettyPeek()
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.XML)
                .body(XML_CODES + ".size()", equalTo(5))
                //1
                .body(XML_CODES + "[0].code", equalTo("0101"))
                .body(XML_CODES + "[0].name", equalTo("Halden"))
                //2
                .body(XML_CODES + "[1].code", equalTo("0104"))
                .body(XML_CODES + "[1].name", equalTo("Moss"))
                //...
                //5
                .body(XML_CODES + "[4].code", equalTo("5043"))
                .body(XML_CODES + "[4].name", equalTo("Raarvihke Røyrvik"));
    }

    @Test
    public void restServiceCodesAt2015CSV() {
        given().port(port).accept(RestConstants.CONTENT_TYPE_CSV).param("date", "2015-01-01")
                .get(REQUEST_WITH_ID_AND_CODES_AT, kommuneinndeling.getId())
//                .prettyPeek()
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(RestConstants.CONTENT_TYPE_CSV)

                .body(containsString(
                        "\"code\",\"parentCode\",\"level\",\"name\",\"shortName\",\"presentationName\",\"validFrom\",\"validTo\",\"notes\"\n"
                                + "\"0101\",,\"1\",\"Halden\",\"\",\"\",,,\"\"\n"
                                + "\"0104\",,\"1\",\"Moss\",\"\",\"\",,,\"\"\n"
                                + "\"0301\",,\"1\",\"Oslo\",\"\",\"\",,,\"\"\n"
                                + "\"1739\",,\"1\",\"Raarvihke Røyrvik\",\"\",\"\",,,\"13. juni 2014 ble det vedtatt at kommunenavnet skulle være tospråklig (samisk -norsk)\"\n"
                                + "\"1939\",,\"1\",\"Omasvuotna Storfjord Omasvuonon\",\"\",\"\",,,\"\""
                ));

    }

    @Test
    public void restServiceCodesAtFutureDateIncludedFutureVersionCSV() {
        given().port(port).accept(RestConstants.CONTENT_TYPE_CSV).param("date", TestDataProvider.TEN_YEARS_LATER_DATE).param("includeFuture", true)
                .get(REQUEST_WITH_ID_AND_CODES_AT, kommuneinndeling.getId())
                //.prettyPeek()
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(RestConstants.CONTENT_TYPE_CSV)

                .body(containsString(
                        "\"code\",\"parentCode\",\"level\",\"name\",\"shortName\",\"presentationName\",\"validFrom\",\"validTo\",\"notes\"\n"
                                + "\"0101\",,\"1\",\"Halden\",\"\",\"\",,,\"\"\n"
                                + "\"0104\",,\"1\",\"Moss\",\"\",\"\",,,\"\"\n"
                                + "\"0301\",,\"1\",\"Oslo\",\"\",\"\",,,\"\"\n"
                                + "\"1939\",,\"1\",\"Omasvuotna Storfjord Omasvuonon\",\"\",\"\",,,\"\"\n"
                                + "\"5043\",,\"1\",\"Raarvihke Røyrvik\",\"\",\"\",,,\"\""
                ));

    }

    /**
     * NOTE: different behavior between "csvSeparator" and non "csvSeparator" requests,
     * csvSeparator requests wraps values with quotes while the normal one does not.
     * May want make a standard for this.
     */
    @Test
    public void restServiceCodesAt2012CSV() {
        given().port(port).accept(RestConstants.CONTENT_TYPE_CSV).param("date", "2012-01-01")
                .get(REQUEST_WITH_ID_AND_CODES_AT, kommuneinndeling.getId())
//                .prettyPeek()
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(RestConstants.CONTENT_TYPE_CSV)

                .body(containsString(
                        "\"code\",\"parentCode\",\"level\",\"name\",\"shortName\",\"presentationName\",\"validFrom\",\"validTo\",\"notes\"\n"                                + "\"0101\",,\"1\",\"Halden\",\"\",\"\",,,\"\"\n"
                                + "\"0104\",,\"1\",\"Moss\",\"\",\"\",,,\"\"\n"
                                + "\"0301\",,\"1\",\"Oslo\",\"\",\"\",,,\"\"\n"
                                + "\"1739\",,\"1\",\"Røyrvik\",\"\",\"\",,,\"Tospråklig navn Raarvihke - Røyrvik innført fra 1. 1. 2015. Før het kommunen Røyrvik.\"\n"
                                + "\"1939\",,\"1\",\"Storfjord\",\"\",\"\",,,\"\""
                ));

    }

    /**
     * NOTE: different behavior between "csvSeparator" and non "csvSeparator" requests,
     * csvSeparator requests wraps values with quotes while the normal one does not.
     * May want make a standard for this.
     */
    @Test
    public void restServiceCodesAtAlternativeCSVSeparator() {
        given().port(port).accept(RestConstants.CONTENT_TYPE_CSV).param("date", "2012-01-01").param("csvSeparator", ";")
                .get(REQUEST_WITH_ID_AND_CODES_AT, kommuneinndeling.getId())
//                .prettyPeek()
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(RestConstants.CONTENT_TYPE_CSV)

                .body(containsString(
                        "\"code\";\"parentCode\";\"level\";\"name\";\"shortName\";\"presentationName\";\"validFrom\";\"validTo\";\"notes\"\n"
                                + "\"0101\";;\"1\";\"Halden\";\"\";\"\";;;\"\"\n"
                                + "\"0104\";;\"1\";\"Moss\";\"\";\"\";;;\"\"\n"
                                + "\"0301\";;\"1\";\"Oslo\";\"\";\"\";;;\"\"\n"
                                + "\"1739\";;\"1\";\"Røyrvik\";\"\";\"\";;;\"Tospråklig navn Raarvihke - Røyrvik innført fra 1. 1. 2015. Før het kommunen Røyrvik.\"\n"
                                + "\"1939\";;\"1\";\"Storfjord\";\"\";\"\";;;\"\""
                ));

    }


    @Test
    public void restServiceCodesRangeNoWildCardsJSON() {
        given().port(port).accept(ContentType.JSON).param("date", "2015-01-01")
                .param("selectCodes", "0100-0400")
                .get(REQUEST_WITH_ID_AND_CODES_AT, kommuneinndeling.getId())
//                .prettyPeek()
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("codes.size()", equalTo(3))
                .body("codes[0].code", equalTo("0101"))
                .body("codes[1].code", equalTo("0104"))
                .body("codes[2].code", equalTo("0301"));

    }
    @Test
    public void restServiceCodesRangeWithWildCardsJSON() {
        given().port(port).accept(ContentType.JSON).param("date", "2015-01-01")
                .param("selectCodes", "010*-0400, 193*")
                .get(REQUEST_WITH_ID_AND_CODES_AT, kommuneinndeling.getId())
//                .prettyPeek()
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("codes.size()", equalTo(4))
                .body("codes[0].code", equalTo("0101"))
                .body("codes[1].code", equalTo("0104"))
                .body("codes[2].code", equalTo("0301"))
                .body("codes[3].code", equalTo("1939"));

    }
    @Test
    public void restServiceCodesOutOfRangeJSON() {
        given().port(port).accept(ContentType.JSON).param("date", "2015-01-01")
                .param("selectCodes", "210*-2400")
                .get(REQUEST_WITH_ID_AND_CODES_AT, kommuneinndeling.getId())
//                .prettyPeek()
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("codes.size()", equalTo(0));
    }

// @formatter:on
}
