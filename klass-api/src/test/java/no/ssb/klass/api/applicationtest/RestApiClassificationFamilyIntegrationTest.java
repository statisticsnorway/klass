package no.ssb.klass.api.applicationtest;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import io.restassured.http.ContentType;
import no.ssb.klass.core.model.ClassificationType;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

public class RestApiClassificationFamilyIntegrationTest extends AbstractRestApiApplicationTest {
  @Test
  public void restServiceReturnClassificationFamily() {
    String urlParts = REQUEST_CLASSIFICATION_FAMILY + "/" + classificationFamily.getId();

    given()
        .port(port)
        .accept(ContentType.JSON)
        .get(REQUEST_CLASSIFICATION_FAMILY_WITH_ID, classificationFamily.getId())
        //                .prettyPeek()
        .then()
        .assertThat()
        .statusCode(HttpStatus.OK.value())
        .assertThat()
        .contentType(ContentType.JSON)
        // classificationFamily
        .assertThat()
        .body("name", equalTo(classificationFamily.getName()))
        // classifications
        .assertThat()
        .body(
            "classifications.size()",
            equalTo(
                classificationFamily
                    .getClassificationSeriesBySectionAndClassificationType(
                        null, ClassificationType.CLASSIFICATION)
                    .size()))

        // links
        .body(JSON_LINKS + ".self.href", containsString(urlParts));
  }

  @Test
  public void restServiceClassificationFamilyFiltersClassificationType() {
    String urlParts = REQUEST_CLASSIFICATION_FAMILY + "/" + classificationFamily.getId();

    given()
        .port(port)
        .accept(ContentType.JSON)
        .param("includeCodelists", "true")
        .get(REQUEST_CLASSIFICATION_FAMILY_WITH_ID, classificationFamily.getId())
        //                .prettyPeek()
        .then()
        .assertThat()
        .statusCode(HttpStatus.OK.value())
        .assertThat()
        .contentType(ContentType.JSON)
        // classificationFamily
        .assertThat()
        .body("name", equalTo(classificationFamily.getName()))
        // classifications
        .assertThat()
        .body(
            "classifications.size()",
            equalTo(
                classificationFamily
                    .getClassificationSeriesBySectionAndClassificationType(null, null)
                    .size()))

        // links
        .body(JSON_LINKS + ".self.href", containsString(urlParts));
  }

  @Test
  public void restServiceClassificationFamilyFiltersSsbSection() {
    final String ssbSection = "unknown section";
    String urlParts = REQUEST_CLASSIFICATION_FAMILY + "/" + classificationFamily.getId();

    given()
        .port(port)
        .accept(ContentType.JSON)
        .param("ssbSection", ssbSection)
        .get(REQUEST_CLASSIFICATION_FAMILY_WITH_ID, classificationFamily.getId())
        //                .prettyPeek()
        .then()
        .assertThat()
        .statusCode(HttpStatus.OK.value())
        .assertThat()
        .contentType(ContentType.JSON)
        // classificationFamily
        .assertThat()
        .body("name", equalTo(classificationFamily.getName()))
        // classifications
        .assertThat()
        .body("classifications.size()", equalTo(0))

        // links
        .body(JSON_LINKS + ".self.href", containsString(urlParts));
  }

  @Test
  public void restServiceClassificationFamilyFiltersSsbSectionKnownSection() {
    final String ssbSection = "section";
    String urlParts = REQUEST_CLASSIFICATION_FAMILY + "/" + classificationFamily.getId();

    given()
        .port(port)
        .accept(ContentType.JSON)
        .param("ssbSection", ssbSection)
        .get(REQUEST_CLASSIFICATION_FAMILY_WITH_ID, classificationFamily.getId())
        .then()
        .assertThat()
        .statusCode(HttpStatus.OK.value())
        .assertThat()
        .contentType(ContentType.JSON)
        .assertThat()
        .body("name", equalTo(classificationFamily.getName()))
        .assertThat()
        .body("classifications.size()", equalTo(2))
        .body(JSON_LINKS + ".self.href", containsString(urlParts));
  }

  @Test
  public void restServiceClassificationFamilyFiltersSsbSectionKnownSectionFullName() {
    final String ssbSection = "section - section name";
    String urlParts = REQUEST_CLASSIFICATION_FAMILY + "/" + classificationFamily.getId();

    given()
        .port(port)
        .accept(ContentType.JSON)
        .param("ssbSection", ssbSection)
        .get(REQUEST_CLASSIFICATION_FAMILY_WITH_ID, classificationFamily.getId())
        .then()
        .assertThat()
        .statusCode(HttpStatus.OK.value())
        .assertThat()
        .contentType(ContentType.JSON)
        .assertThat()
        .body("name", equalTo(classificationFamily.getName()))
        .assertThat()
        .body("classifications.size()", equalTo(2))
        .body(JSON_LINKS + ".self.href", containsString(urlParts));
  }
}
