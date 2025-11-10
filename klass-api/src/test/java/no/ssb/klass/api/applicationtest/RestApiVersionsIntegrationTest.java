package no.ssb.klass.api.applicationtest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

public class RestApiVersionsIntegrationTest extends AbstractRestApiApplicationTest {
  @Test
  public void restServiceVersionsJSON() {
    given()
        .port(port)
        .accept(ContentType.JSON)
        .param("language", "nb")
        .get(REQUEST_VERSIONS_WITH_ID, badmintonCodelist.getNewestVersion().getId())
        .then()
        .contentType(ContentType.JSON)
        .statusCode(HttpStatus.OK.value())
        .body("name", is("Badminton 2006"))
        .body("contactPerson.name", is("Ziggy Stardust"))
        .body("owningSection", is("section"))
        .body("classificationVariants[0].owningSection", is("425"))
        .body("classificationVariants[1].owningSection", is("150"));
  }
}
