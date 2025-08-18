package no.ssb.klass.api.migration.section;

import io.restassured.response.Response;
import no.ssb.klass.api.migration.dataintegrity.AbstractKlassApiDataIntegrityTest;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static no.ssb.klass.api.migration.MigrationTestUtils.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiSectionMigrationTest extends AbstractKlassApiDataIntegrityTest {

    @Test
    void getSsbSections(){
        String path = getSsbSectionsPath();
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, null, null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, null, null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);
        List<Map<String, Object>> sourceList = sourceResponse.path("_embedded.ssbSections");
        List<Map<String, Object>> targetList = targetResponse.path("_embedded.ssbSections");

        for (int i = 0; i < sourceList.size(); i++){
            String sourceName = sourceList.get(i).get("name").toString();
            String targetName = targetList.get(i).get("name").toString();
            if(sourceList.get(i).get("name") != null){
                assertThat(sourceName).startsWith(targetName);
            }
        }


    }
    String getSsbSectionsPath() {
        return "/" + SSB_SECTIONS;
    }
}
