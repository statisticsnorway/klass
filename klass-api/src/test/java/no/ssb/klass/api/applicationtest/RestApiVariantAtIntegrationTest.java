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
public class RestApiVariantAtIntegrationTest extends AbstractRestApiApplicationTest {
  // @formatter:off
  @Test
  public void restServiceVariantAtJSON() {
    given()
        .port(port)
        .accept(ContentType.JSON)
        .param("variantName", "Variant - Tilleggsinndeling for familier")
        .param("date", "2015-01-01")
        .get(REQUEST_WITH_ID_AND_VARIANT_AT, familieGrupperingCodelist.getId())
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
        // 2
        .body("codes[1].code", equalTo("A_"))
        .body("codes[1].level", equalTo("2"))
        .body("codes[1].name", equalTo("Enpersonfamilie"))
        .body("codes[1].shortName", equalTo(""))
        .body("codes[1].presentationName", equalTo(""))
        // ...
        // 5
        .body("codes[4].code", equalTo("BB"))
        .body("codes[4].level", equalTo("2"))
        .body("codes[4].name", equalTo("Ektepar uten barn 0-17 år"))
        .body("codes[4].shortName", equalTo(""))
        .body("codes[4].presentationName", equalTo(""));
  }

  @Test
  public void restServiceVariantAtIncludedFutureVersionsJSON() {

    given()
        .port(port)
        .accept(ContentType.JSON)
        .param("variantName", "Variant - Tilleggsinndeling for familier")
        .param("date", TestDataProvider.TEN_YEARS_LATER_DATE)
        .param("includeFuture", true)
        .get(REQUEST_WITH_ID_AND_VARIANT_AT, familieGrupperingCodelist.getId())
        //                .prettyPeek()
        .then()
        .statusCode(HttpStatus.OK.value())
        .contentType(ContentType.JSON)
        .body("codes.size()", equalTo(3))
        // 1
        .body("codes[0].code", equalTo("C"))
        .body("codes[0].level", equalTo("1"))
        .body("codes[0].name", equalTo("Enslig person med barn"))
        .body("codes[0].shortName", equalTo(""))
        .body("codes[0].presentationName", equalTo(""))
        // 2
        .body("codes[1].code", equalTo("CA"))
        .body("codes[1].level", equalTo("2"))
        .body("codes[1].name", equalTo("Enslig person med barn 0-5 år"))
        .body("codes[1].shortName", equalTo(""))
        .body("codes[1].presentationName", equalTo(""))
        // 3
        .body("codes[2].code", equalTo("CB"))
        .body("codes[2].level", equalTo("2"))
        .body("codes[2].name", equalTo("Enslig person med barn 6-17 år"))
        .body("codes[2].shortName", equalTo(""))
        .body("codes[2].presentationName", equalTo(""));
  }

  @Test
  public void restServiceVariantAtXML() {
    given()
        .port(port)
        .accept(ContentType.XML)
        .param("variantName", "Variant - Tilleggsinndeling for familier")
        .param("date", "2015-01-01")
        .get(REQUEST_WITH_ID_AND_VARIANT_AT, familieGrupperingCodelist.getId())
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
        // 2
        .body(XML_CODES + "[1].code", equalTo("A_"))
        .body(XML_CODES + "[1].level", equalTo("2"))
        .body(XML_CODES + "[1].name", equalTo("Enpersonfamilie"))
        .body(XML_CODES + "[1].shortName", equalTo(""))
        .body(XML_CODES + "[1].presentationName", equalTo(""))
        // ...
        // 5
        .body(XML_CODES + "[4].code", equalTo("BB"))
        .body(XML_CODES + "[4].level", equalTo("2"))
        .body(XML_CODES + "[4].name", equalTo("Ektepar uten barn 0-17 år"))
        .body(XML_CODES + "[4].shortName", equalTo(""))
        .body(XML_CODES + "[4].presentationName", equalTo(""));
  }

  @Test
  public void restServiceVariantAtIncludedFutureVersionsXML() {
    given()
        .port(port)
        .accept(ContentType.XML)
        .param("variantName", "Variant - Tilleggsinndeling for familier")
        .param("date", TestDataProvider.TEN_YEARS_LATER_DATE)
        .param("includeFuture", true)
        .get(REQUEST_WITH_ID_AND_VARIANT_AT, familieGrupperingCodelist.getId())
        //                .prettyPeek()
        .then()
        .statusCode(HttpStatus.OK.value())
        .contentType(ContentType.XML)
        .body(XML_CODES + ".size()", equalTo(3))
        // 1
        .body(XML_CODES + "[0].code", equalTo("C"))
        .body(XML_CODES + "[0].level", equalTo("1"))
        .body(XML_CODES + "[0].name", equalTo("Enslig person med barn"))
        .body(XML_CODES + "[0].shortName", equalTo(""))
        .body(XML_CODES + "[0].presentationName", equalTo(""))
        // 2
        .body(XML_CODES + "[1].code", equalTo("CA"))
        .body(XML_CODES + "[1].level", equalTo("2"))
        .body(XML_CODES + "[1].name", equalTo("Enslig person med barn 0-5 år"))
        .body(XML_CODES + "[1].shortName", equalTo(""))
        .body(XML_CODES + "[1].presentationName", equalTo(""))
        // 3
        .body(XML_CODES + "[2].code", equalTo("CB"))
        .body(XML_CODES + "[2].level", equalTo("2"))
        .body(XML_CODES + "[2].name", equalTo("Enslig person med barn 6-17 år"))
        .body(XML_CODES + "[2].shortName", equalTo(""))
        .body(XML_CODES + "[2].presentationName", equalTo(""));
  }

  @Test
  public void restServiceVariantAtCSV() {
    given()
        .port(port)
        .accept(RestConstants.CONTENT_TYPE_CSV)
        .param("variantName", "Variant - Tilleggsinndeling for familier")
        .param("date", "2015-01-01")
        .get(REQUEST_WITH_ID_AND_VARIANT_AT, familieGrupperingCodelist.getId())
        //                .prettyPeek()
        .then()
        .statusCode(HttpStatus.OK.value())
        .contentType(RestConstants.CONTENT_TYPE_CSV)
        .body(
            containsString(
                "\"code\",\"parentCode\",\"level\",\"name\",\"shortName\",\"presentationName\",\"validFrom\",\"validTo\",\"notes\"\n"
                    + "\"A\",,\"1\",\"Enpersonfamilie\",\"\",\"\",,,\"\"\n"
                    + "\"A_\",\"A\",\"2\",\"Enpersonfamilie\",\"\",\"\",,,\"\"\n"
                    + "\"B\",,\"1\",\"Ektepar\",\"\",\"\",,,\"\"\n"
                    + "\"BA\",\"B\",\"2\",\"Ektepar med barn (yngste barn 0-17 år)\",\"\",\"\",,,\"\"\n"
                    + "\"BB\",\"B\",\"2\",\"Ektepar uten barn 0-17 år\",\"\",\"\",,,\"\""));
  }

  @Test
  public void restServiceVariantAtIncludedFutureVersionsCSV() {
    given()
        .port(port)
        .accept(RestConstants.CONTENT_TYPE_CSV)
        .param("variantName", "Variant - Tilleggsinndeling for familier")
        .param("date", TestDataProvider.TEN_YEARS_LATER_DATE)
        .param("includeFuture", true)
        .get(REQUEST_WITH_ID_AND_VARIANT_AT, familieGrupperingCodelist.getId())
        //                .prettyPeek()
        .then()
        .statusCode(HttpStatus.OK.value())
        .contentType(RestConstants.CONTENT_TYPE_CSV)
        .body(
            containsString(
                "\"code\",\"parentCode\",\"level\",\"name\",\"shortName\",\"presentationName\",\"validFrom\",\"validTo\",\"notes\"\n"
                    + "\"C\",,\"1\",\"Enslig person med barn\",\"\",\"\",,,\"\"\n"
                    + "\"CA\",\"C\",\"2\",\"Enslig person med barn 0-5 år\",\"\",\"\",,,\"\"\n"
                    + "\"CB\",\"C\",\"2\",\"Enslig person med barn 6-17 år\",\"\",\"\",,,\"\""));
  }

  @Test
  public void restServiceVariantAtCSVFullVariantName() {
    String name =
        "Variant - Tilleggsinndeling for familier 2006"
            + "  - variant av Standard for gruppering av familier 2006";
    given()
        .port(port)
        .accept(RestConstants.CONTENT_TYPE_CSV)
        .param("variantName", name)
        .param("date", "2015-01-01")
        .get(REQUEST_WITH_ID_AND_VARIANT_AT, familieGrupperingCodelist.getId())
        .then()
        .statusCode(HttpStatus.OK.value())
        .contentType(RestConstants.CONTENT_TYPE_CSV)
        .body(
            containsString(
                "\"code\",\"parentCode\",\"level\",\"name\",\"shortName\",\"presentationName\",\"validFrom\",\"validTo\",\"notes\"\n"
                    + "\"A\",,\"1\",\"Enpersonfamilie\",\"\",\"\",,,\"\"\n"
                    + "\"A_\",\"A\",\"2\",\"Enpersonfamilie\",\"\",\"\",,,\"\"\n"
                    + "\"B\",,\"1\",\"Ektepar\",\"\",\"\",,,\"\"\n"
                    + "\"BA\",\"B\",\"2\",\"Ektepar med barn (yngste barn 0-17 år)\",\"\",\"\",,,\"\"\n"
                    + "\"BB\",\"B\",\"2\",\"Ektepar uten barn 0-17 år\",\"\",\"\",,,\"\""));
  }

  @Test
  public void restServiceVariantAtCSVFullVariantNameFutureVersion() {
    String name = "Variant - Tilleggsinndeling for familier";
    given()
        .port(port)
        .accept(RestConstants.CONTENT_TYPE_CSV)
        .param("variantName", name)
        .param("date", TestDataProvider.TEN_YEARS_LATER_DATE)
        .param("includeFuture", true)
        .get(REQUEST_WITH_ID_AND_VARIANT_AT, familieGrupperingCodelist.getId())
        //                .prettyPeek()
        .then()
        .statusCode(HttpStatus.OK.value())
        .contentType(RestConstants.CONTENT_TYPE_CSV)
        .body(
            containsString(
                "\"code\",\"parentCode\",\"level\",\"name\",\"shortName\",\"presentationName\",\"validFrom\",\"validTo\",\"notes\"\n"
                    + "\"C\",,\"1\",\"Enslig person med barn\",\"\",\"\",,,\"\"\n"
                    + "\"CA\",\"C\",\"2\",\"Enslig person med barn 0-5 år\",\"\",\"\",,,\"\"\n"
                    + "\"CB\",\"C\",\"2\",\"Enslig person med barn 6-17 år\",\"\",\"\",,,\"\""));
  }
  // @formatter:on
}
