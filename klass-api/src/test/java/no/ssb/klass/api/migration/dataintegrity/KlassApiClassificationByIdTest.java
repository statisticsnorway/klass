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

/**
 * Test class for classification by ID. Compare data from two sources.
 */
public class KlassApiClassificationByIdTest extends AbstractKlassApiDataIntegrityTest {

    @Test
    void getClassification(){
        for (Integer id : classificationsIdsSourceHost) {

            Response sourceResponse = getClassificationResponse(klassApSourceHostPath,id, null, null);
            Response targetResponse = getClassificationResponse(klassApiTargetHostPath, id, null, null);

            if(sourceResponse.getStatusCode() != 200) {
                assertThat(compareError(id, sourceResponse, targetResponse)).isTrue();
            }
            else {
                for (String pathName : pathNamesClassification) {
                    Object sourceField = sourceResponse.path(pathName);

                    if (pathName.equals(STATISTICAL_UNITS)) {
                        ArrayList<String> sourceList = sourceResponse.path(pathName);
                        ArrayList<String> targetList = targetResponse.path(pathName);

                        assertThat(sourceList.size()).isEqualTo(targetList.size());

                        assertThat(sourceList.containsAll(targetList)).isTrue();
                        assertThat(targetList.containsAll(sourceList)).isTrue();
                    } else {
                        assertThat(sourceField).isEqualTo(targetResponse.path(pathName));
                    }
                }
            }
        }
    }

    @Test
    void getClassificationVersions(){
        for (Integer id : classificationsIdsSourceHost) {

            Response sourceResponse = getClassificationResponse(klassApSourceHostPath, id, null, null);
            Response targetResponse = getClassificationResponse(klassApiTargetHostPath, id, null, null);

            if(sourceResponse.getStatusCode() != 200) {
                assertThat(compareError(id, sourceResponse, targetResponse)).isTrue();
            }
            else {
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

    }

    @Test
    void getClassificationLinks(){
        for (Integer id : classificationsIdsSourceHost) {

            Response sourceResponse = getClassificationResponse(klassApSourceHostPath, id, null, null);
            Response targetResponse = getClassificationResponse(klassApiTargetHostPath, id, null, null);

            if(sourceResponse.getStatusCode() != 200) {
                assertThat(compareError(id, sourceResponse, targetResponse)).isTrue();
            }
            else {
                for (String pathName : MigrationTestConstants.pathNamesClassificationLinks) {
                    Object sourceField = sourceResponse.path(pathName);

                    if (pathName.endsWith(HREF)) {
                        String sourceLinkSelf = sourceResponse.path(pathName);
                        assertThat(isPathEqualIgnoreHost(sourceLinkSelf, targetResponse.path(pathName))).isTrue();
                    } else {
                        assertThat(sourceField).isEqualTo(targetResponse.path(pathName));
                    }
                }
            }
        }

    }

    @Test
    void getClassificationEnglish(){
        for (Integer id : classificationsIdsSourceHost) {

            Response sourceResponse = getClassificationResponse(klassApSourceHostPath, id, LANGUAGE_PARAM, EN);
            Response targetResponse = getClassificationResponse(klassApiTargetHostPath, id, LANGUAGE_PARAM, EN);

            if(sourceResponse.getStatusCode() != 200) {
                assertThat(compareError(id, sourceResponse, targetResponse)).isTrue();
            }
            else {
                for (String pathName : pathNamesClassification) {
                    Object sourceField = sourceResponse.path(pathName);

                    if (pathName.equals(STATISTICAL_UNITS)) {
                        ArrayList<String> sourceList = sourceResponse.path(pathName);
                        ArrayList<String> targetList = targetResponse.path(pathName);
                        assertThat(sourceList.size()).isEqualTo(targetList.size());
                        assertThat(sourceList.containsAll(targetList)).isTrue();
                        assertThat(targetList.containsAll(sourceList)).isTrue();
                    } else {
                        assertThat(sourceField).isEqualTo(targetResponse.path(pathName));
                    }
                }
            }
        }

    }

    @Test
    void getClassificationNewNorwegian(){
        for (Integer id : classificationsIdsSourceHost) {

            Response sourceResponse = getClassificationResponse(klassApSourceHostPath, id, LANGUAGE_PARAM, NN);
            Response targetResponse = getClassificationResponse(klassApiTargetHostPath, id, LANGUAGE_PARAM, NN);

            if(sourceResponse.getStatusCode() != 200) {
                assertThat(compareError(id, sourceResponse, targetResponse)).isTrue();
            }
            else {

                for (String pathName : pathNamesClassification) {
                    Object sourceField = sourceResponse.path(pathName);

                    if(pathName.equals(STATISTICAL_UNITS)) {
                        ArrayList<String> sourceList = sourceResponse.path(pathName);
                        ArrayList<String> targetList = targetResponse.path(pathName);

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

    }

    @Test
    void getClassificationIncludeFuture(){
        for (Integer id : classificationsIdsSourceHost) {

            Response sourceResponse = getClassificationResponse(klassApSourceHostPath, id, INCLUDE_FUTURE_TRUE_PARAM, "true");
            Response targetResponse = getClassificationResponse(klassApiTargetHostPath, id, INCLUDE_FUTURE_TRUE_PARAM, "true");

            if(sourceResponse.getStatusCode() != 200) {
                assertThat(compareError(id, sourceResponse, targetResponse)).isTrue();
            }
            else {
                for (String pathName : pathNamesClassification) {
                    Object sourceField = sourceResponse.path(pathName);

                    if(pathName.equals(STATISTICAL_UNITS)) {
                        ArrayList<String> sourceList = sourceResponse.path(pathName);
                        ArrayList<String> targetList = targetResponse.path(pathName);

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

    }

    private Response getClassificationResponse(String basePath, Integer id, String queryParam, String queryValue) {
        if (queryParam == null) {
            return RestAssured.get(basePath + "/" + id);

        }

        else {
            return RestAssured.given().queryParam(queryParam, queryValue).get(basePath + "/" + id);
        }
    }
}
