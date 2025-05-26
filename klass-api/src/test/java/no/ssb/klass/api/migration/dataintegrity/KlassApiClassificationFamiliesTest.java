package no.ssb.klass.api.migration.dataintegrity;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiClassificationFamiliesTest extends KlassApiClassificationCodesTest{


    @Test
    void getClassificationFamilies() {
        String path = "/" + CLASSIFICATION_FAMILIES;
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, null);

        assertThat(sourceResponse.getStatusCode()).withFailMessage(
                FAIL_MESSAGE, CLASSIFICATION_FAMILIES, sourceResponse.getStatusCode(), targetResponse.getStatusCode()).isEqualTo(targetResponse.getStatusCode());

        if(sourceResponse.getStatusCode() != 200) {
            System.out.println(LOG_MESSAGE_STATUS_CODE + sourceResponse.getStatusCode());
            assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();
        }
        else{
            Object sourceLinks = sourceResponse.path(LINKS_SELF_HREF);
            Object targetLinks = targetResponse.path(LINKS_SELF_HREF);
            assertThat(sourceLinks).withFailMessage(FAIL_MESSAGE, LINKS_SELF_HREF, sourceLinks, targetLinks).isEqualTo(targetLinks);
        }
    }

    String getClassificationFamiliesPath(Integer id) {
        return CLASSIFICATIONS_PATH + "/" + id;
    }
}
