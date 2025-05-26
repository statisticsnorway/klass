package no.ssb.klass.api.migration.dataintegrity;

import io.restassured.response.Response;
import no.ssb.klass.api.migration.MigrationTestUtils;
import org.junit.jupiter.api.Test;


import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiClassificationFamiliesByIdTest extends KlassApiClassificationCorrespondsAtTest {

    @Test
    void getOneClassificationFamilyBy() {
        int classificationFamilyId = 11;
        String path = getClassificationFamilyByIdPath(classificationFamilyId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path , null);

        MigrationTestUtils.assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            System.out.println(LOG_MESSAGE_STATUS_CODE + sourceResponse.getStatusCode());
            assertThat(MigrationTestUtils.compareError(null, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateItems(sourceResponse, targetResponse, pathNamesClassificationFamilyById);
            validateSelfLink(sourceResponse, targetResponse);
            validatePathListWithLinks(sourceResponse, targetResponse, CLASSIFICATIONS, pathNamesClassificationsPage);
        }
    }

    String getClassificationFamilyByIdPath(Integer id) {
        return "/" + CLASSIFICATION_FAMILIES + "/" + id;
    }
}
