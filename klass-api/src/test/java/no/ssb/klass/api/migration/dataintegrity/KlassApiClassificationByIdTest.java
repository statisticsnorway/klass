package no.ssb.klass.api.migration.dataintegrity;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import no.ssb.klass.api.migration.MigrationTestConstants;
import org.junit.jupiter.api.Test;

import java.util.List;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public class KlassApiClassificationByIdTest extends AbstractKlassApiDataIntegrityTest {

    @Test
    void getClassification(){
        for (int i = 0; i < Math.min(classificationsIdsSourceHost.size(), classificationsIdsTargetHost.size()); i++) {
            Response sourceResponse = getResponse(klassApSourceHostPath, classificationsIdsSourceHost.get(i));
            Response targetResponse = getResponse(klassApiTargetHostPath, classificationsIdsTargetHost.get(i));

            for (String pathName : MigrationTestConstants.pathNamesClassification) {
                Object sourceField = sourceResponse.path(pathName);
                if(!pathName.equals(STATISTICAL_UNITS)) {
                    assertThat(sourceField).isEqualTo(targetResponse.path(pathName));
                }

            }
        }
    }

    @Test
    void getClassificationVersions(){

        for (int i = 0; i < Math.min(classificationsIdsSourceHost.size(), classificationsIdsTargetHost.size()); i++) {
            Response sourceResponse = getResponse(klassApSourceHostPath, classificationsIdsSourceHost.get(i));
            Response targetResponse = getResponse(klassApiTargetHostPath, classificationsIdsTargetHost.get(i));
            List<?> versionsSourceHost = sourceResponse.path(VERSIONS);
            List<?> versionsTargetHost = targetResponse.path(VERSIONS);

            assertThat(versionsSourceHost.size()).isEqualTo(versionsTargetHost.size());

            for(String pathName: MigrationTestConstants.pathNamesVersions){
                Object sourceField = sourceResponse.path(pathName);
                if(pathName.equals(LINKS)) {
                    String sourceLinks = sourceResponse.path(LINKS_SELF_HREF);
                    assertThat( isPathEqualIgnoreHost(sourceLinks, targetResponse.path(LINKS_SELF_HREF))).isTrue();
                }
                else{
                    assertThat(sourceField).isEqualTo(targetResponse.path(pathName));
                }
            }
        }

    }

    @Test
    void getClassificationLinks(){

    }

    @Test
    void getClassificationParams(){

    }

    private Response getResponse(String basePath, Integer id) {
        Response response = RestAssured.get(basePath + "/" + id);
        response.then().assertThat().statusCode(200);
        return response;
    }
}
