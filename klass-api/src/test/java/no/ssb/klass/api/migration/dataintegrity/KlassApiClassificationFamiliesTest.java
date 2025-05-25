package no.ssb.klass.api.migration.dataintegrity;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiClassificationFamiliesTest extends KlassApiClassificationCodesTest{


    @Test
    void getClassificationFamilies() {
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi("/" + CLASSIFICATION_FAMILIES, null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi("/" + CLASSIFICATION_FAMILIES, null);

        if(sourceResponse.getStatusCode() != 200 || targetResponse.getStatusCode() != 200) {
            System.out.println(LOG_MESSAGE_STATUS_CODE + sourceResponse.getStatusCode());
            assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();
        }
        else{
            Object sourceLinks = sourceResponse.path(LINKS_SELF_HREF);
            Object targetLinks = targetResponse.path(LINKS_SELF_HREF);
            assertThat(sourceLinks).withFailMessage("h").isEqualTo(targetLinks);
        }
    }
}
