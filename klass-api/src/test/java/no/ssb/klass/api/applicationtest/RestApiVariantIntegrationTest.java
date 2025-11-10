package no.ssb.klass.api.applicationtest;

import io.restassured.http.ContentType;
import no.ssb.klass.api.util.RestConstants;
import no.ssb.klass.testutil.TestDataProvider;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

/**
 * @author Mads Lundemo, SSB.
 */
public class RestApiVariantIntegrationTest extends AbstractRestApiApplicationTest {
    // @formatter:off
    @Test
    public void restServiceVariantJSON() {
        given().port(port)
                .accept(ContentType.JSON)
                .param("variantName", "Variant - Tilleggsinndeling for familier")
                .param("from", "2014-01-01")
                .get(REQUEST_WITH_ID_AND_VARIANT, familieGrupperingCodelist.getId())
                //                .prettyPeek()
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("codes.size()", equalTo(5))
                // 1
                .body("codes[0].code", equalTo("A"))
                .body("codes[0].level", equalTo("1"))
                .body("codes[0].name", equalTo("Enpersonfamilie"))
                .body("codes[0].shortName", equalTo(""))
                .body("codes[0].presentationName", equalTo(""))
                .body("codes[0].validFromInRequestedRange", equalTo("2014-01-01"))
                .body("codes[0].validToInRequestedRange", equalTo(null))
                // 2
                .body("codes[1].code", equalTo("A_"))
                .body("codes[1].level", equalTo("2"))
                .body("codes[1].name", equalTo("Enpersonfamilie"))
                .body("codes[1].shortName", equalTo(""))
                .body("codes[1].presentationName", equalTo(""))
                .body("codes[1].validFromInRequestedRange", equalTo("2014-01-01"))
                .body("codes[1].validToInRequestedRange", equalTo(null))
                // ...
                // 5
                .body("codes[4].code", equalTo("BB"))
                .body("codes[4].level", equalTo("2"))
                .body("codes[4].name", equalTo("Ektepar uten barn 0-17 år"))
                .body("codes[4].shortName", equalTo(""))
                .body("codes[4].presentationName", equalTo(""))
                .body("codes[4].validFromInRequestedRange", equalTo("2014-01-01"))
                .body("codes[4].validToInRequestedRange", equalTo(null));
    }

    @Test
    public void restServiceVariantIncludeFutureVersionsJSON() {
        given().port(port)
                .accept(ContentType.JSON)
                .param("variantName", "Variant - Tilleggsinndeling for familier")
                .param("from", "2014-01-01")
                .param("includeFuture", true)
                .get(REQUEST_WITH_ID_AND_VARIANT, familieGrupperingCodelist.getId())
                //                .prettyPeek()
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("codes.size()", equalTo(8))
                // 1
                .body("codes[0].code", equalTo("A"))
                .body("codes[0].level", equalTo("1"))
                .body("codes[0].name", equalTo("Enpersonfamilie"))
                .body("codes[0].shortName", equalTo(""))
                .body("codes[0].presentationName", equalTo(""))
                .body("codes[0].validFromInRequestedRange", equalTo("2014-01-01"))
                .body(
                        "codes[0].validToInRequestedRange",
                        equalTo(TestDataProvider.TEN_YEARS_LATER_DATE))
                // 2
                .body("codes[1].code", equalTo("A_"))
                .body("codes[1].level", equalTo("2"))
                .body("codes[1].name", equalTo("Enpersonfamilie"))
                .body("codes[1].shortName", equalTo(""))
                .body("codes[1].presentationName", equalTo(""))
                .body("codes[1].validFromInRequestedRange", equalTo("2014-01-01"))
                .body(
                        "codes[1].validToInRequestedRange",
                        equalTo(TestDataProvider.TEN_YEARS_LATER_DATE))
                // ..
                // 6
                .body("codes[5].code", equalTo("C"))
                .body("codes[5].level", equalTo("1"))
                .body("codes[5].name", equalTo("Enslig person med barn"))
                .body("codes[5].shortName", equalTo(""))
                .body("codes[5].presentationName", equalTo(""))
                .body(
                        "codes[5].validFromInRequestedRange",
                        equalTo(TestDataProvider.TEN_YEARS_LATER_DATE))
                .body("codes[5].validToInRequestedRange", equalTo(null))
                // ...
                // 8
                .body("codes[7].code", equalTo("CB"))
                .body("codes[7].level", equalTo("2"))
                .body("codes[7].name", equalTo("Enslig person med barn 6-17 år"))
                .body("codes[7].shortName", equalTo(""))
                .body("codes[7].presentationName", equalTo(""))
                .body(
                        "codes[7].validFromInRequestedRange",
                        equalTo(TestDataProvider.TEN_YEARS_LATER_DATE))
                .body("codes[7].validToInRequestedRange", equalTo(null));
    }

    @Test
    public void restServiceVariantDateRangeJSON() {
        given().port(port)
                .accept(ContentType.JSON)
                .param("variantName", "Variant - Tilleggsinndeling for familier")
                .param("from", "2014-01-01")
                .param("to", "2040-01-01")
                .get(REQUEST_WITH_ID_AND_VARIANT, familieGrupperingCodelist.getId())
                //                .prettyPeek()
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("codes.size()", equalTo(5))
                // 1
                .body("codes[0].code", equalTo("A"))
                .body("codes[0].level", equalTo("1"))
                .body("codes[0].name", equalTo("Enpersonfamilie"))
                .body("codes[0].shortName", equalTo(""))
                .body("codes[0].presentationName", equalTo(""))
                .body("codes[0].validFromInRequestedRange", equalTo("2014-01-01"))
                .body("codes[0].validToInRequestedRange", equalTo("2040-01-01"))
                // ...
                // 5
                .body("codes[4].code", equalTo("BB"))
                .body("codes[4].level", equalTo("2"))
                .body("codes[4].name", equalTo("Ektepar uten barn 0-17 år"))
                .body("codes[4].shortName", equalTo(""))
                .body("codes[4].presentationName", equalTo(""))
                .body("codes[4].validFromInRequestedRange", equalTo("2014-01-01"))
                .body("codes[4].validToInRequestedRange", equalTo("2040-01-01"));
    }

    @Test
    public void restServiceVariantDateRangeIncludeFutureVersionsJSON() {
        given().port(port)
                .accept(ContentType.JSON)
                .param("variantName", "Variant - Tilleggsinndeling for familier")
                .param("from", "2014-01-01")
                .param("to", TestDataProvider.TWENTY_YEARS_LATER_DATE)
                .param("includeFuture", true)
                .get(REQUEST_WITH_ID_AND_VARIANT, familieGrupperingCodelist.getId())
                //                .prettyPeek()
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("codes.size()", equalTo(8))
                // 1
                .body("codes[0].code", equalTo("A"))
                .body("codes[0].level", equalTo("1"))
                .body("codes[0].name", equalTo("Enpersonfamilie"))
                .body("codes[0].shortName", equalTo(""))
                .body("codes[0].presentationName", equalTo(""))
                .body("codes[0].validFromInRequestedRange", equalTo("2014-01-01"))
                .body(
                        "codes[0].validToInRequestedRange",
                        equalTo(TestDataProvider.TEN_YEARS_LATER_DATE))
                // ...
                // 5
                .body("codes[7].code", equalTo("CB"))
                .body("codes[7].level", equalTo("2"))
                .body("codes[7].name", equalTo("Enslig person med barn 6-17 år"))
                .body("codes[7].shortName", equalTo(""))
                .body("codes[7].presentationName", equalTo(""))
                .body(
                        "codes[7].validFromInRequestedRange",
                        equalTo(TestDataProvider.TEN_YEARS_LATER_DATE))
                .body(
                        "codes[7].validToInRequestedRange",
                        equalTo(TestDataProvider.TWENTY_YEARS_LATER_DATE));
    }

    @Test
    public void restServiceVariantXML() {
        given().port(port)
                .accept(ContentType.XML)
                .param("variantName", "Variant - Tilleggsinndeling for familier")
                .param("from", "2014-01-01")
                .get(REQUEST_WITH_ID_AND_VARIANT, familieGrupperingCodelist.getId())
                //                .prettyPeek()
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.XML)
                .body(XML_CODES + ".size()", equalTo(5))
                // 1
                .body(XML_CODES + "[0].code", equalTo("A"))
                .body(XML_CODES + "[0].level", equalTo("1"))
                .body(XML_CODES + "[0].name", equalTo("Enpersonfamilie"))
                .body(XML_CODES + "[0].shortName", equalTo(""))
                .body(XML_CODES + "[0].presentationName", equalTo(""))
                .body(XML_CODES + "[0].validFromInRequestedRange", equalTo("2014-01-01"))
                .body(XML_CODES + "[0].validToInRequestedRange", equalTo(""))
                // 2
                .body(XML_CODES + "[1].code", equalTo("A_"))
                .body(XML_CODES + "[1].level", equalTo("2"))
                .body(XML_CODES + "[1].name", equalTo("Enpersonfamilie"))
                .body(XML_CODES + "[1].shortName", equalTo(""))
                .body(XML_CODES + "[1].presentationName", equalTo(""))
                .body(XML_CODES + "[1].validFromInRequestedRange", equalTo("2014-01-01"))
                .body(XML_CODES + "[1].validToInRequestedRange", equalTo(""))
                // ...
                // 5
                .body(XML_CODES + "[4].code", equalTo("BB"))
                .body(XML_CODES + "[4].level", equalTo("2"))
                .body(XML_CODES + "[4].name", equalTo("Ektepar uten barn 0-17 år"))
                .body(XML_CODES + "[4].shortName", equalTo(""))
                .body(XML_CODES + "[4].presentationName", equalTo(""))
                .body(XML_CODES + "[4].validFromInRequestedRange", equalTo("2014-01-01"))
                .body(XML_CODES + "[4].validToInRequestedRange", equalTo(""));
    }

    @Test
    public void restServiceVariantIncludeFutureVersionsXML() {
        given().port(port)
                .accept(ContentType.XML)
                .param("variantName", "Variant - Tilleggsinndeling for familier")
                .param("from", "2014-01-01")
                .param("includeFuture", true)
                .get(REQUEST_WITH_ID_AND_VARIANT, familieGrupperingCodelist.getId())
                //                .prettyPeek()
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.XML)
                .body(XML_CODES + ".size()", equalTo(8))
                // 1
                .body(XML_CODES + "[0].code", equalTo("A"))
                .body(XML_CODES + "[0].level", equalTo("1"))
                .body(XML_CODES + "[0].name", equalTo("Enpersonfamilie"))
                .body(XML_CODES + "[0].shortName", equalTo(""))
                .body(XML_CODES + "[0].presentationName", equalTo(""))
                .body(XML_CODES + "[0].validFromInRequestedRange", equalTo("2014-01-01"))
                .body(
                        XML_CODES + "[0].validToInRequestedRange",
                        equalTo(TestDataProvider.TEN_YEARS_LATER_DATE))
                // ...
                // 8
                .body(XML_CODES + "[7].code", equalTo("CB"))
                .body(XML_CODES + "[7].level", equalTo("2"))
                .body(XML_CODES + "[7].name", equalTo("Enslig person med barn 6-17 år"))
                .body(XML_CODES + "[7].shortName", equalTo(""))
                .body(XML_CODES + "[7].presentationName", equalTo(""))
                .body(
                        XML_CODES + "[7].validFromInRequestedRange",
                        equalTo(TestDataProvider.TEN_YEARS_LATER_DATE))
                .body(XML_CODES + "[7].validToInRequestedRange", equalTo(""));
    }

    @Test
    public void restServiceVariantCSV() {
        given().port(port)
                .accept(RestConstants.CONTENT_TYPE_CSV)
                .param("variantName", "Variant - Tilleggsinndeling for familier")
                .param("from", "2014-01-01")
                .get(REQUEST_WITH_ID_AND_VARIANT, familieGrupperingCodelist.getId())
                //                .prettyPeek()
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(RestConstants.CONTENT_TYPE_CSV)
                .body(
                        containsString(
                                "\"code\",\"parentCode\",\"level\",\"name\",\"shortName\",\"presentationName\","
                                        + "\"validFrom\",\"validTo\",\"validFromInRequestedRange\",\"validToInRequestedRange\",\"notes\"\n"
                                        + "\"A\",,\"1\",\"Enpersonfamilie\",\"\",\"\",,,\"2014-01-01\",,\"\"\n"
                                        + "\"A_\",\"A\",\"2\",\"Enpersonfamilie\",\"\",\"\",,,\"2014-01-01\",,\"\"\n"
                                        + "\"B\",,\"1\",\"Ektepar\",\"\",\"\",,,\"2014-01-01\",,\"\"\n"
                                        + "\"BA\",\"B\",\"2\",\"Ektepar med barn (yngste barn 0-17 år)\",\"\",\"\",,,\"2014-01-01\",,\"\"\n"
                                        + "\"BB\",\"B\",\"2\",\"Ektepar uten barn 0-17 år\",\"\",\"\",,,\"2014-01-01\",,\"\""));
    }

    @Test
    public void restServiceVariantIncludeFutureVersionsCSV() {
        given().port(port)
                .accept(RestConstants.CONTENT_TYPE_CSV)
                .param("variantName", "Variant - Tilleggsinndeling for familier")
                .param("from", "2014-01-01")
                .param("includeFuture", true)
                .get(REQUEST_WITH_ID_AND_VARIANT, familieGrupperingCodelist.getId())
                //                .prettyPeek()
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(RestConstants.CONTENT_TYPE_CSV)
                .body(
                        containsString(
                                "\"code\",\"parentCode\",\"level\",\"name\",\"shortName\",\"presentationName\","
                                        + "\"validFrom\",\"validTo\",\"validFromInRequestedRange\",\"validToInRequestedRange\",\"notes\"\n"
                                        + "\"A\",,\"1\",\"Enpersonfamilie\",\"\",\"\",,,\"2014-01-01\",\""
                                        + TestDataProvider.TEN_YEARS_LATER_DATE
                                        + "\",\"\"\n"
                                        + "\"A_\",\"A\",\"2\",\"Enpersonfamilie\",\"\",\"\",,,\"2014-01-01\",\""
                                        + TestDataProvider.TEN_YEARS_LATER_DATE
                                        + "\",\"\"\n"
                                        + "\"B\",,\"1\",\"Ektepar\",\"\",\"\",,,\"2014-01-01\",\""
                                        + TestDataProvider.TEN_YEARS_LATER_DATE
                                        + "\",\"\"\n"
                                        + "\"BA\",\"B\",\"2\",\"Ektepar med barn (yngste barn 0-17 år)\",\"\",\"\",,,\"2014-01-01\",\""
                                        + TestDataProvider.TEN_YEARS_LATER_DATE
                                        + "\",\"\"\n"
                                        + "\"BB\",\"B\",\"2\",\"Ektepar uten barn 0-17 år\",\"\",\"\",,,\"2014-01-01\",\""
                                        + TestDataProvider.TEN_YEARS_LATER_DATE
                                        + "\",\"\"\n"
                                        + "\"C\",,\"1\",\"Enslig person med barn\",\"\",\"\",,,\""
                                        + TestDataProvider.TEN_YEARS_LATER_DATE
                                        + "\",,\"\"\n"
                                        + "\"CA\",\"C\",\"2\",\"Enslig person med barn 0-5 år\",\"\",\"\",,,\""
                                        + TestDataProvider.TEN_YEARS_LATER_DATE
                                        + "\",,\"\"\n"
                                        + "\"CB\",\"C\",\"2\",\"Enslig person med barn 6-17 år\",\"\",\"\",,,\""
                                        + TestDataProvider.TEN_YEARS_LATER_DATE
                                        + "\",,\"\""));
    }
    // @formatter:on
}
