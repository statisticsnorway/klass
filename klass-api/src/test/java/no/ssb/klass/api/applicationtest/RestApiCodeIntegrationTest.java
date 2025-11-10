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
public class RestApiCodeIntegrationTest extends AbstractRestApiApplicationTest {
  @Test
  public void restServiceCodesJSON() {
    given()
        .port(port)
        .accept(ContentType.JSON)
        .param("from", "2014-01-01")
        .param("to", "2015-01-01")
        .get(REQUEST_WITH_ID_AND_CODES, kommuneinndeling.getId())
        //                .prettyPeek()
        .then()
        .statusCode(HttpStatus.OK.value())
        .contentType(ContentType.JSON)
        .body("codes.size()", equalTo(5))
        // 1
        .body("codes[0].code", equalTo("0101"))
        .body("codes[0].level", equalTo("1"))
        .body("codes[0].name", equalTo("Halden"))
        .body("codes[0].shortName", equalTo(""))
        .body("codes[0].notes", equalTo(""))
        .body("codes[0].presentationName", equalTo(""))
        .body("codes[0].validFromInRequestedRange", equalTo("2014-01-01"))
        .body("codes[0].validToInRequestedRange", equalTo("2015-01-01"))
        // 2
        .body("codes[1].code", equalTo("0104"))
        .body("codes[1].level", equalTo("1"))
        .body("codes[1].name", equalTo("Moss"))
        .body("codes[1].shortName", equalTo(""))
        .body("codes[1].notes", equalTo(""))
        .body("codes[1].presentationName", equalTo(""))
        .body("codes[1].validFromInRequestedRange", equalTo("2014-01-01"))
        .body("codes[1].validToInRequestedRange", equalTo("2015-01-01"));
    // ...
  }

  @Test
  public void restServiceCodesDifferentNamesAndNotesJSON() {
    given()
        .port(port)
        .accept(ContentType.JSON)
        .param("from", "2008-01-01")
        .param("to", "2015-01-01")
        .get(REQUEST_WITH_ID_AND_CODES, kommuneinndeling.getId())
        //                .prettyPeek()
        .then()
        .statusCode(HttpStatus.OK.value())
        .contentType(ContentType.JSON)
        .body("codes.size()", equalTo(7))
        // 4
        .body("codes[3].code", equalTo("1739"))
        .body("codes[3].level", equalTo("1"))
        .body("codes[3].name", equalTo("Raarvihke Røyrvik"))
        .body("codes[3].shortName", equalTo(""))
        .body(
            "codes[3].notes",
            equalTo(
                "13. juni 2014 ble det vedtatt at kommunenavnet skulle være tospråklig (samisk -norsk)"))
        .body("codes[3].presentationName", equalTo(""))
        .body("codes[3].validFromInRequestedRange", equalTo("2014-01-01"))
        .body("codes[3].validToInRequestedRange", equalTo("2015-01-01"))
        // 5
        .body("codes[4].code", equalTo("1739"))
        .body("codes[4].level", equalTo("1"))
        .body("codes[4].name", equalTo("Røyrvik"))
        .body("codes[4].shortName", equalTo(""))
        .body(
            "codes[4].notes",
            equalTo(
                "Tospråklig navn Raarvihke - Røyrvik innført fra 1. 1. 2015. Før het kommunen Røyrvik."))
        .body("codes[4].presentationName", equalTo(""))
        .body("codes[4].validFromInRequestedRange", equalTo("2008-01-01"))
        .body("codes[4].validToInRequestedRange", equalTo("2014-01-01"))
        // 6
        .body("codes[5].code", equalTo("1939"))
        .body("codes[5].level", equalTo("1"))
        .body("codes[5].name", equalTo("Omasvuotna Storfjord Omasvuonon"))
        .body("codes[5].shortName", equalTo(""))
        .body("codes[5].notes", equalTo(""))
        .body("codes[5].presentationName", equalTo(""))
        .body("codes[5].validFromInRequestedRange", equalTo("2014-01-01"))
        .body("codes[5].validToInRequestedRange", equalTo("2015-01-01"));
    // ...
  }

  @Test
  public void restServiceCodesIncludeFutureVersionJSON() {
    given()
        .port(port)
        .accept(ContentType.JSON)
        .param("from", TestDataProvider.TEN_YEARS_LATER_DATE)
        .param("to", TestDataProvider.FIFTEEN_YEARS_LATER_DATE)
        .param("includeFuture", true)
        .get(REQUEST_WITH_ID_AND_CODES, kommuneinndeling.getId())
        //              .prettyPeek()
        .then()
        .statusCode(HttpStatus.OK.value())
        .contentType(ContentType.JSON)
        .body("codes.size()", equalTo(5))
        // 1
        .body("codes[0].code", equalTo("0101"))
        .body("codes[0].level", equalTo("1"))
        .body("codes[0].name", equalTo("Halden"))
        .body("codes[0].validFromInRequestedRange", equalTo(TestDataProvider.TEN_YEARS_LATER_DATE))
        .body(
            "codes[0].validToInRequestedRange", equalTo(TestDataProvider.FIFTEEN_YEARS_LATER_DATE))
        // 2
        .body("codes[4].code", equalTo("5043"))
        .body("codes[4].level", equalTo("1"))
        .body("codes[4].name", equalTo("Raarvihke Røyrvik"))
        .body("codes[4].validFromInRequestedRange", equalTo(TestDataProvider.TEN_YEARS_LATER_DATE))
        .body(
            "codes[4].validToInRequestedRange", equalTo(TestDataProvider.FIFTEEN_YEARS_LATER_DATE));
    // ...
  }

  @Test
  public void restServiceCodesXML() {
    given()
        .port(port)
        .accept(ContentType.XML)
        .param("from", "2014-01-01")
        .param("to", "2015-01-01")
        .get(REQUEST_WITH_ID_AND_CODES, kommuneinndeling.getId())
        //                .prettyPeek()
        .then()
        .statusCode(HttpStatus.OK.value())
        .contentType(ContentType.XML)
        .body("codeList.codeItem.size()", equalTo(5))
        // 1
        .body("codeList.codeItem[0].code", equalTo("0101"))
        .body("codeList.codeItem[0].level", equalTo("1"))
        .body("codeList.codeItem[0].name", equalTo("Halden"))
        .body("codeList.codeItem[0].shortName", equalTo(""))
        .body("codeList.codeItem[0].notes", equalTo(""))
        .body("codeList.codeItem[0].presentationName", equalTo(""))
        .body("codeList.codeItem[0].validFromInRequestedRange", equalTo("2014-01-01"))
        .body("codeList.codeItem[0].validToInRequestedRange", equalTo("2015-01-01"))
        // 2
        .body("codeList.codeItem[1].code", equalTo("0104"))
        .body("codeList.codeItem[1].level", equalTo("1"))
        .body("codeList.codeItem[1].name", equalTo("Moss"))
        .body("codeList.codeItem[1].shortName", equalTo(""))
        .body("codeList.codeItem[1].notes", equalTo(""))
        .body("codeList.codeItem[1].presentationName", equalTo(""))
        .body("codeList.codeItem[1].validFromInRequestedRange", equalTo("2014-01-01"))
        .body("codeList.codeItem[1].validToInRequestedRange", equalTo("2015-01-01"));
    // ...
  }

  @Test
  public void restServiceCodesIncludeFutureVersionXML() {
    given()
        .port(port)
        .accept(ContentType.XML)
        .param("from", TestDataProvider.TEN_YEARS_LATER_DATE)
        .param("to", TestDataProvider.FIFTEEN_YEARS_LATER_DATE)
        .param("includeFuture", true)
        .get(REQUEST_WITH_ID_AND_CODES, kommuneinndeling.getId())
        //                .prettyPeek()
        .then()
        .statusCode(HttpStatus.OK.value())
        .contentType(ContentType.XML)
        .body("codeList.codeItem.size()", equalTo(5))
        // 1
        .body("codeList.codeItem[0].code", equalTo("0101"))
        .body("codeList.codeItem[0].level", equalTo("1"))
        .body("codeList.codeItem[0].name", equalTo("Halden"))
        .body(
            "codeList.codeItem[0].validFromInRequestedRange",
            equalTo(TestDataProvider.TEN_YEARS_LATER_DATE))
        .body(
            "codeList.codeItem[0].validToInRequestedRange",
            equalTo(TestDataProvider.FIFTEEN_YEARS_LATER_DATE))
        .body("codeList.codeItem[0].notes", equalTo(""))
        // 2
        .body("codeList.codeItem[4].code", equalTo("5043"))
        .body("codeList.codeItem[4].level", equalTo("1"))
        .body("codeList.codeItem[4].name", equalTo("Raarvihke Røyrvik"))
        .body(
            "codeList.codeItem[4].validFromInRequestedRange",
            equalTo(TestDataProvider.TEN_YEARS_LATER_DATE))
        .body(
            "codeList.codeItem[4].validToInRequestedRange",
            equalTo(TestDataProvider.FIFTEEN_YEARS_LATER_DATE))
        .body("codeList.codeItem[4].notes", equalTo(""));
    // ...
  }

  @Test
  public void restServiceCodesCSV() {
    given()
        .port(port)
        .accept(RestConstants.CONTENT_TYPE_CSV)
        .param("from", "2014-01-01")
        .param("to", "2015-01-01")
        .get(REQUEST_WITH_ID_AND_CODES, kommuneinndeling.getId())
        //                .prettyPeek()
        .then()
        .statusCode(HttpStatus.OK.value())
        .contentType(RestConstants.CONTENT_TYPE_CSV)
        .body(
            containsString(
                "\"code\",\"parentCode\",\"level\",\"name\",\"shortName\",\"presentationName\","
                    + "\"validFrom\",\"validTo\",\"validFromInRequestedRange\",\"validToInRequestedRange\",\"notes\"\n"
                    + "\"0101\",,\"1\",\"Halden\",\"\",\"\",,,\"2014-01-01\",\"2015-01-01\",\"\"\n"
                    + "\"0104\",,\"1\",\"Moss\",\"\",\"\",,,\"2014-01-01\",\"2015-01-01\",\"\"\n"
                    + "\"0301\",,\"1\",\"Oslo\",\"\",\"\",,,\"2014-01-01\",\"2015-01-01\",\"\"\n"
                    + "\"1739\",,\"1\",\"Raarvihke Røyrvik\",\"\",\"\",,,\"2014-01-01\",\"2015-01-01\",\"13. juni 2014 ble det vedtatt at kommunenavnet skulle være tospråklig (samisk -norsk)\"\n"
                    + "\"1939\",,\"1\",\"Omasvuotna Storfjord Omasvuonon\",\"\",\"\",,,\"2014-01-01\",\"2015-01-01\",\"\""));
  }

  @Test
  public void restServiceCodesIncludeFutureVersionCSV() {
    given()
        .port(port)
        .accept(RestConstants.CONTENT_TYPE_CSV)
        .param("from", TestDataProvider.TEN_YEARS_LATER_DATE)
        .param("to", TestDataProvider.FIFTEEN_YEARS_LATER_DATE)
        .param("includeFuture", true)
        .get(REQUEST_WITH_ID_AND_CODES, kommuneinndeling.getId())
        //                .prettyPeek()
        .then()
        .statusCode(HttpStatus.OK.value())
        .contentType(RestConstants.CONTENT_TYPE_CSV)
        .body(
            containsString(
                "\"code\",\"parentCode\",\"level\",\"name\",\"shortName\",\"presentationName\","
                    + "\"validFrom\",\"validTo\",\"validFromInRequestedRange\",\"validToInRequestedRange\",\"notes\"\n"
                    + "\"0101\",,\"1\",\"Halden\",\"\",\"\",,,\""
                    + TestDataProvider.TEN_YEARS_LATER_DATE
                    + "\",\""
                    + TestDataProvider.FIFTEEN_YEARS_LATER_DATE
                    + "\",\"\"\n"
                    + "\"0104\",,\"1\",\"Moss\",\"\",\"\",,,\""
                    + TestDataProvider.TEN_YEARS_LATER_DATE
                    + "\",\""
                    + TestDataProvider.FIFTEEN_YEARS_LATER_DATE
                    + "\",\"\"\n"
                    + "\"0301\",,\"1\",\"Oslo\",\"\",\"\",,,\""
                    + TestDataProvider.TEN_YEARS_LATER_DATE
                    + "\",\""
                    + TestDataProvider.FIFTEEN_YEARS_LATER_DATE
                    + "\",\"\"\n"
                    + "\"1939\",,\"1\",\"Omasvuotna Storfjord Omasvuonon\",\"\",\"\",,,\""
                    + TestDataProvider.TEN_YEARS_LATER_DATE
                    + "\",\""
                    + TestDataProvider.FIFTEEN_YEARS_LATER_DATE
                    + "\",\"\"\n"
                    + "\"5043\",,\"1\",\"Raarvihke Røyrvik\",\"\",\"\",,,\""
                    + TestDataProvider.TEN_YEARS_LATER_DATE
                    + "\",\""
                    + TestDataProvider.FIFTEEN_YEARS_LATER_DATE
                    + "\",\"\""));
  }

  @Test
  public void restServiceCodesRangeNoWildCardsJSON() {
    given()
        .port(port)
        .accept(ContentType.JSON)
        .param("from", "2014-01-01")
        .param("selectCodes", "0100-0400")
        .get(REQUEST_WITH_ID_AND_CODES, kommuneinndeling.getId())
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
    given()
        .port(port)
        .accept(ContentType.JSON)
        .param("from", "2014-01-01")
        .param("selectCodes", "010*-0400, 193*")
        .get(REQUEST_WITH_ID_AND_CODES, kommuneinndeling.getId())
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
    given()
        .port(port)
        .accept(ContentType.JSON)
        .param("from", "2014-01-01")
        .param("selectCodes", "210*-2400")
        .get(REQUEST_WITH_ID_AND_CODES, kommuneinndeling.getId())
        //                .prettyPeek()
        .then()
        .statusCode(HttpStatus.OK.value())
        .contentType(ContentType.JSON)
        .body("codes.size()", equalTo(0));
  }
}
