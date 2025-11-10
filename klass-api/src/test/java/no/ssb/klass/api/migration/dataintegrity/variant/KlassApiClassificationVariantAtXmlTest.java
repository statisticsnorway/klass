package no.ssb.klass.api.migration.dataintegrity.variant;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static no.ssb.klass.api.migration.MigrationTestUtils.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import io.restassured.response.Response;

import org.junit.jupiter.api.Test;

import java.time.Instant;

public class KlassApiClassificationVariantAtXmlTest extends AbstractKlassApiVariantTest {

    @Test
    void getOneClassificationVariantAt() {
        Integer classificationId = 84;
        System.out.println("Start test for ID " + classificationId + " at " + Instant.now());

        String path = getVariantAtPath(classificationId);
        Response sourceResponse =
                klassApiMigrationClient.getFromSourceApi(path, paramsVariantDate, APPLICATION_XML);
        Response targetResponse =
                klassApiMigrationClient.getFromTargetApi(path, paramsVariantDate, APPLICATION_XML);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(
                sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        } else {
            validateXmlList(path, sourceResponse, targetResponse, CODE_LIST_CODE_ITEM);
        }
    }
}
