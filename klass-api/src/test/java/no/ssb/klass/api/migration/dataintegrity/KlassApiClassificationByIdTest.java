package no.ssb.klass.api.migration.dataintegrity;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import no.ssb.klass.api.migration.MigrationTestConstants;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public class KlassApiClassificationByIdTest extends AbstractKlassApiDataIntegrityTest {

    @Test
    void getClassification(){
        for(int i=0; i < classificationsIdsSourceHost.size(); i++){
            Response sourceResponse = getClassificationResponse(klassApSourceHostPath, classificationsIdsSourceHost.get(i));
            Response targetResponse = getClassificationResponse(klassApiTargetHostPath, classificationsIdsTargetHost.get(i));

            for (String pathName : pathNamesClassification) {
                Object sourceField = sourceResponse.path(pathName);

                if(pathName.equals(STATISTICAL_UNITS)) {
                    ArrayList<String> sourceList = sourceResponse.path(pathName);
                    ArrayList<String>  targetList = targetResponse.path(pathName);
                    assertThat(sourceList.size()).isEqualTo(targetList.size());
                    assertThat(sourceList.containsAll(targetList)).isTrue();
                    assertThat(targetList.containsAll(sourceList)).isTrue();
                }
                else{
                    assertThat(sourceField).isEqualTo(targetResponse.path(pathName));
                }
            }
        }
    }

    @Test
    void getClassificationVersions(){
        for(int i=0; i < classificationsIdsSourceHost.size(); i++){
            Response sourceResponse = getClassificationResponse(klassApSourceHostPath, classificationsIdsSourceHost.get(i));
            Response targetResponse = getClassificationResponse(klassApiTargetHostPath, classificationsIdsTargetHost.get(i));

            List<Map<String, Object>> versionsSourceHost = sourceResponse.path(VERSIONS);
            List<Map<String, Object>> versionsTargetHost = targetResponse.path(VERSIONS);

            assertThat(versionsSourceHost.size()).isEqualTo(versionsTargetHost.size());

            Map<Object, Map<String, Object>> sourceMap = versionsSourceHost.stream()
                    .collect(Collectors.toMap(v -> v.get("id"), Function.identity()));

            Map<Object, Map<String, Object>> targetMap = versionsTargetHost.stream()
                    .collect(Collectors.toMap(v -> v.get("id"), Function.identity()));

            for (Object versionId : sourceMap.keySet()) {
                Map<String, Object> versionSource = sourceMap.get(versionId);
                Map<String, Object> versionTarget = targetMap.get(versionId);

               for(String pathName: MigrationTestConstants.pathNamesVersion) {
                   Object sourceField = resolvePath(versionSource, pathName);
                   Object targetField = resolvePath(versionTarget, pathName);

                   if (pathName.endsWith(HREF)) {
                       if (sourceField == null) {
                           assertThat(targetField).isNull();
                       }
                       assert sourceField != null;
                       assert targetField != null;

                       assertThat(isPathEqualIgnoreHost(sourceField.toString(), targetField.toString())).isTrue();
                   }
                   else{
                           assertThat(versionSource.get(pathName)).isEqualTo(versionTarget.get(pathName));

                       }

                   }

            }

        }

    }

    @Test
    void getClassificationLinks(){
        for(int i=0; i < classificationsIdsSourceHost.size(); i++){
            Response sourceResponse = getClassificationResponse(klassApSourceHostPath, classificationsIdsSourceHost.get(i));
            Response targetResponse = getClassificationResponse(klassApiTargetHostPath, classificationsIdsTargetHost.get(i));

            for (String pathName : MigrationTestConstants.pathNamesClassificationLinks) {
                Object sourceField = sourceResponse.path(pathName);

                if (pathName.endsWith(HREF)) {
                    String sourceLinkSelf = sourceResponse.path(pathName);
                    assertThat(isPathEqualIgnoreHost(sourceLinkSelf, targetResponse.path(pathName))).isTrue();
                }
                else{
                    assertThat(sourceField).isEqualTo(targetResponse.path(pathName));
                }
            }
        }

    }

    @Test
    void getClassificationParams(){

    }

    private Response getClassificationResponse(String basePath, Integer id) {
        Response response = RestAssured.get(basePath + "/" + id);
        response.then().assertThat().statusCode(200);
        return response;
    }
}
