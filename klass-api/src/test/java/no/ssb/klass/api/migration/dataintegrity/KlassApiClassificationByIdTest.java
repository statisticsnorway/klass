package no.ssb.klass.api.migration.dataintegrity;

import io.restassured.response.Response;
import no.ssb.klass.api.migration.MigrationTestConstants;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Test class for classification by ID. Compare data from two sources.
 */
public class KlassApiClassificationByIdTest extends AbstractKlassApiDataIntegrityTest {

    static Map<String, Object> paramsLanguageEn = new HashMap<>();
    static Map<String, Object> paramsLanguageNn = new HashMap<>();
    static Map<String, Object> paramsIncludeFuture = new HashMap<>();

    @BeforeAll
    static void setUpClassification() {
        paramsLanguageEn.put(LANGUAGE, EN);
        paramsLanguageNn.put(LANGUAGE, NN);
        paramsIncludeFuture.put(INCLUDE_FUTURE, TRUE);
    }
    @ParameterizedTest
    @MethodSource("provideClassificationIds")
    void getClassification(Integer classificationId) {
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(CLASSIFICATIONS_PATH + "/" + classificationId, null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(CLASSIFICATIONS_PATH + "/" + classificationId, null);

        if(sourceResponse.getStatusCode() != 200) {
                assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
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

    @ParameterizedTest
    @MethodSource("provideClassificationIds")
    void getClassificationVersions(Integer classificationId){

        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(CLASSIFICATIONS_PATH + "/" + classificationId, null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(CLASSIFICATIONS_PATH + "/" + classificationId, null);

        if(sourceResponse.getStatusCode() != 200) {
                assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        } else {
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
                    } else{
                        assertThat(versionSource.get(pathName)).isEqualTo(versionTarget.get(pathName));
                    }
                }
            }

        }
    }

    @ParameterizedTest
    @MethodSource("provideClassificationIds")
    void getClassificationLinks(Integer classificationId) {

            Response sourceResponse = klassApiMigrationClient.getFromSourceApi(CLASSIFICATIONS_PATH + "/" + classificationId, null);
            Response targetResponse = klassApiMigrationClient.getFromTargetApi(CLASSIFICATIONS_PATH + "/" + classificationId, null);

            if(sourceResponse.getStatusCode() != 200) {
                assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
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

    @ParameterizedTest
    @MethodSource("provideClassificationIds")
    void getClassificationEnglish(Integer classificationId) {

        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(CLASSIFICATIONS_PATH + "/" + classificationId, paramsLanguageEn);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(CLASSIFICATIONS_PATH + "/" + classificationId, paramsLanguageEn);

        if(sourceResponse.getStatusCode() != 200) {
                assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
            }
            else {
                for (String pathName : pathNamesClassification) {
                    Object sourceField = sourceResponse.path(pathName);

                    assertThat(sourceField).isNotNull();

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

    @ParameterizedTest
    @MethodSource("provideClassificationIds")
    void getClassificationNewNorwegian(Integer classificationId) {

        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(CLASSIFICATIONS_PATH + "/" + classificationId, paramsLanguageNn);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(CLASSIFICATIONS_PATH + "/" + classificationId, paramsLanguageNn);


        if(sourceResponse.getStatusCode() != 200) {
                assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
            }
            else {

                for (String pathName : pathNamesClassification) {
                    Object sourceField = sourceResponse.path(pathName);
                    assertThat(sourceField).isNotNull();

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

    @ParameterizedTest
    @MethodSource("provideClassificationIds")
    void getClassificationIncludeFuture(Integer classificationId) {
        for (Integer id : classificationsIdsSourceHost) {

            Response sourceResponse = klassApiMigrationClient.getFromSourceApi(CLASSIFICATIONS_PATH + "/" + classificationId, paramsIncludeFuture);
            Response targetResponse = klassApiMigrationClient.getFromTargetApi(CLASSIFICATIONS_PATH + "/" + classificationId, paramsIncludeFuture);


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

    static Stream<Integer> provideClassificationIds() {
        return classificationsIdsSourceHost.stream();
    }
}
