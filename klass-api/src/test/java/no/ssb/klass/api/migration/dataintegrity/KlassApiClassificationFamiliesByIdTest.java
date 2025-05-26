package no.ssb.klass.api.migration.dataintegrity;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;


import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static no.ssb.klass.api.migration.MigrationTestConstants.LINKS_SELF_HREF;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiClassificationFamiliesByIdTest extends KlassApiClassificationCorrespondsAtTest {

    @Test
    void getOneClassificationFamilyBy() {
        int classificationFamilyId = 11;
        String path = getClassificationFamilyByIdPath(classificationFamilyId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path , null);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            System.out.println(LOG_MESSAGE_STATUS_CODE + sourceResponse.getStatusCode());
            assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateItems(sourceResponse, targetResponse, pathNamesClassificationFamilyById);
            validateOneLink(sourceResponse, targetResponse, LINKS_SELF_HREF);
            validatePathListWithLinks(sourceResponse, targetResponse, CLASSIFICATIONS, pathNamesClassificationsPage);
        }
    }

    String getClassificationFamilyByIdPath(Integer id) {
        return "/" + CLASSIFICATION_FAMILIES + "/" + id;
    }
}
