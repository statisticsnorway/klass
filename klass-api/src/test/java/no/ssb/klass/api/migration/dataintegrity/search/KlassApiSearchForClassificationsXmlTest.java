package no.ssb.klass.api.migration.dataintegrity.search;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static no.ssb.klass.api.migration.MigrationTestUtils.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

public class KlassApiSearchForClassificationsXmlTest extends AbstractKlassApiSearchTest {

  @Test
  void search() {

    String path = getSearchPath();
    Response sourceResponse =
        klassApiMigrationClient.getFromSourceApi(path, paramsQuery, APPLICATION_XML);
    Response targetResponse =
        klassApiMigrationClient.getFromTargetApi(path, paramsQuery, APPLICATION_XML);

    assertApiResponseIsNotNull(sourceResponse);

    assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

    if (sourceResponse.getStatusCode() != 200) {
      assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();
    } else {
      validateXmlList(path, sourceResponse, targetResponse, PAGED_ENTITIES_CONTENTS_CONTENT);
      validateXmlList(path, sourceResponse, targetResponse, PAGED_ENTITIES_PAGE);
    }
  }

  @Test
  void searchIncludeCodeLists() {

    String path = getSearchPath();
    Response sourceResponse =
        klassApiMigrationClient.getFromSourceApi(
            path, paramsQueryIncludeCodeLists, APPLICATION_XML);
    Response targetResponse =
        klassApiMigrationClient.getFromTargetApi(
            path, paramsQueryIncludeCodeLists, APPLICATION_XML);

    assertApiResponseIsNotNull(sourceResponse);

    assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

    if (sourceResponse.getStatusCode() != 200) {
      assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();
    } else {
      validateXmlList(path, sourceResponse, targetResponse, PAGED_ENTITIES_CONTENTS_CONTENT);
      validateXmlList(path, sourceResponse, targetResponse, PAGED_ENTITIES_PAGE);
    }
  }

  @Test
  void searchFilterSsbSection() {

    String path = getSearchPath();
    Response sourceResponse =
        klassApiMigrationClient.getFromSourceApi(path, paramsQuerySsbSection, APPLICATION_XML);
    Response targetResponse =
        klassApiMigrationClient.getFromTargetApi(path, paramsQuerySsbSection, APPLICATION_XML);

    assertApiResponseIsNotNull(sourceResponse);

    assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

    if (sourceResponse.getStatusCode() != 200) {
      assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();
    } else {
      validateXmlList(path, sourceResponse, targetResponse, PAGED_ENTITIES_CONTENTS_CONTENT);
      validateXmlList(path, sourceResponse, targetResponse, PAGED_ENTITIES_PAGE);
    }
  }
}
