package no.ssb.klass.api.migration.dataintegrity.variant;

import static no.ssb.klass.api.migration.MigrationTestConstants.CODES;
import static no.ssb.klass.api.migration.MigrationTestUtils.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import io.restassured.response.Response;
import java.time.Instant;
import org.junit.jupiter.api.Test;

public class KlassApiClassificationVariantAtJsonTest extends AbstractKlassApiVariantTest {

  @Test
  void getOneClassificationVariantAt() {
    Integer classificationId = 84;
    System.out.println("Start test for ID " + classificationId + " at " + Instant.now());

    String path = getVariantAtPath(classificationId);
    Response sourceResponse =
        klassApiMigrationClient.getFromSourceApi(path, paramsVariantDate, null);
    Response targetResponse =
        klassApiMigrationClient.getFromTargetApi(path, paramsVariantDate, null);

    assertApiResponseIsNotNull(sourceResponse);

    assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

    if (sourceResponse.getStatusCode() != 200) {
      assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
    } else {
      validateList(sourceResponse, targetResponse, CODES);
    }
  }
}
