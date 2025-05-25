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
import java.util.stream.Stream;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static no.ssb.klass.api.migration.MigrationTestUtils.mapById;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Test class for classification by ID. Compare data from two sources.
 */
public class KlassApiClassificationByIdTest extends AbstractKlassApiDataIntegrityTest {

    static Map<String, Object> paramsLanguageEn = new HashMap<>();
    static Map<String, Object> paramsLanguageNn = new HashMap<>();
    static Map<String, Object> paramsIncludeFuture = new HashMap<>();

    Object sourceField;
    Object targetField;
    Response sourceResponse;
    Response targetResponse;

    @BeforeAll
    static void setUpClassification() {
        paramsLanguageEn.put(LANGUAGE, EN);
        paramsLanguageNn.put(LANGUAGE, NN);
        paramsIncludeFuture.put(INCLUDE_FUTURE, TRUE);
    }
    @ParameterizedTest
    @MethodSource("provideClassificationIds")
    void getClassification(Integer classificationId) {
        sourceResponse = klassApiMigrationClient.getFromSourceApi(CLASSIFICATIONS_PATH + "/" + classificationId, null);
        targetResponse = klassApiMigrationClient.getFromTargetApi(CLASSIFICATIONS_PATH + "/" + classificationId, null);

        if(sourceResponse.getStatusCode() != 200) {
                assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else {
            // General fields
            validateClassification(sourceResponse, targetResponse);
            // check links
            validateClassificationLinks(sourceResponse, targetResponse);

            // check statistical units list
            validateStatisticalUnits(sourceResponse, targetResponse);

            // check versions list
            validateClassificationVersions(sourceResponse, targetResponse);
        }
    }


    @ParameterizedTest
    @MethodSource("provideClassificationIds")
    void getClassificationEnglish(Integer classificationId) {

        sourceResponse = klassApiMigrationClient.getFromSourceApi(CLASSIFICATIONS_PATH + "/" + classificationId, paramsLanguageEn);
        targetResponse = klassApiMigrationClient.getFromTargetApi(CLASSIFICATIONS_PATH + "/" + classificationId, paramsLanguageEn);

        if(sourceResponse.getStatusCode() != 200) {
                assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
            }
            else {
                // General fields
                validateClassification(sourceResponse, targetResponse);
                // check links
                validateClassificationLinks(sourceResponse, targetResponse);

                // check statistical units list
                validateStatisticalUnits(sourceResponse, targetResponse);

                // check versions list
                validateClassificationVersions(sourceResponse, targetResponse);
            }
    }

    @ParameterizedTest
    @MethodSource("provideClassificationIds")
    void getClassificationNewNorwegian(Integer classificationId) {

        sourceResponse = klassApiMigrationClient.getFromSourceApi(CLASSIFICATIONS_PATH + "/" + classificationId, paramsLanguageNn);
        targetResponse = klassApiMigrationClient.getFromTargetApi(CLASSIFICATIONS_PATH + "/" + classificationId, paramsLanguageNn);


        if(sourceResponse.getStatusCode() != 200) {
                assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
            }
        else {
            // General fields
            validateClassification(sourceResponse, targetResponse);
            // check links
            validateClassificationLinks(sourceResponse, targetResponse);

            // check statistical units list
            validateStatisticalUnits(sourceResponse, targetResponse);

            // check versions list
            validateClassificationVersions(sourceResponse, targetResponse);
        }
    }

    @ParameterizedTest
    @MethodSource("provideClassificationIds")
    void getClassificationIncludeFuture(Integer classificationId) {
        for (Integer id : classificationsIdsSourceHost) {

            sourceResponse = klassApiMigrationClient.getFromSourceApi(CLASSIFICATIONS_PATH + "/" + classificationId, paramsIncludeFuture);
            targetResponse = klassApiMigrationClient.getFromTargetApi(CLASSIFICATIONS_PATH + "/" + classificationId, paramsIncludeFuture);


            if(sourceResponse.getStatusCode() != 200) {
                assertThat(compareError(id, sourceResponse, targetResponse)).isTrue();
            }
            else {
                // General fields
                validateClassification(sourceResponse, targetResponse);
                // check links
                validateClassificationLinks(sourceResponse, targetResponse);

                // check statistical units list
                validateStatisticalUnits(sourceResponse, targetResponse);

                // check versions list
                validateClassificationVersions(sourceResponse, targetResponse);
            }
        }

    }

    static Stream<Integer> provideClassificationIds() {
        return classificationsIdsSourceHost.stream();
    }

    void validateClassification(Response sourceResponse, Response targetResponse) {

        for (String pathName : pathNamesClassification) {
            sourceField = sourceResponse.path(pathName);
            targetField = targetResponse.path(pathName);

            assertThat(sourceField)
                    .withFailMessage(FAIL_MESSAGE,
                            pathName, sourceField, targetField)
                    .isEqualTo(targetField);
        }

    }

    void validateStatisticalUnits(Response sourceResponse, Response targetResponse) {

        ArrayList<String> sourceList = sourceResponse.path(STATISTICAL_UNITS);
        ArrayList<String> targetList = targetResponse.path(STATISTICAL_UNITS);

        assertThat(sourceList.size())
                .withFailMessage(FAIL_MESSAGE,
                        STATISTICAL_UNITS, sourceList.size(), targetList.size())
                .isEqualTo(targetList.size());

        assertThat(sourceList.containsAll(targetList))
                .withFailMessage(FAIL_MESSAGE,
                        STATISTICAL_UNITS, sourceList, targetList)
                .isTrue();

        assertThat(targetList.containsAll(sourceList))
                .withFailMessage(FAIL_MESSAGE,
                        STATISTICAL_UNITS, sourceList, targetList)
                .isTrue();
    }

    void validateClassificationLinks(Response sourceResponse, Response targetResponse) {
        for (String pathName : MigrationTestConstants.pathNamesClassificationLinks) {
            sourceField = sourceResponse.path(pathName);
            targetField = targetResponse.path(pathName);

            if (pathName.endsWith(HREF)) {
                String sourceHref = sourceField != null ? sourceField.toString() : null;
                String targetHref = targetField != null ? targetField.toString() : null;

                assertThat(isPathEqualIgnoreHost(sourceHref, targetHref))
                        .withFailMessage(FAIL_MESSAGE, pathName, sourceHref, targetHref)
                        .isTrue();
            } else {
                assertThat(sourceField)
                        .withFailMessage(FAIL_MESSAGE, pathName, sourceField, targetField)
                        .isEqualTo(targetField);
            }
        }
    }

    void validateClassificationVersions(Response sourceResponse, Response targetResponse) {
        List<Map<String, Object>> versionsSourceHost = sourceResponse.path(VERSIONS);
        List<Map<String, Object>> versionsTargetHost = targetResponse.path(VERSIONS);

        assertThat(versionsSourceHost.size()).isEqualTo(versionsTargetHost.size());

        Map<Object, Map<String, Object>> sourceById = mapById(versionsSourceHost);
        Map<Object, Map<String, Object>> targetById = mapById(versionsTargetHost);

        for (Object versionId : sourceById.keySet()) {
            Map<String, Object> versionSource = sourceById.get(versionId);
            Map<String, Object> versionTarget = targetById.get(versionId);

            for (String pathName : MigrationTestConstants.pathNamesVersion) {

                sourceField = resolvePath(versionSource, pathName);
                targetField = resolvePath(versionTarget, pathName);

                if (pathName.endsWith(HREF)) {
                    assertThat(sourceField == null && targetField == null ||
                            sourceField != null && targetField != null && isPathEqualIgnoreHost(sourceField.toString(), targetField.toString()))
                            .withFailMessage(FAIL_MESSAGE, pathName, sourceField, targetField)
                            .isTrue();
                } else {
                    assertThat(versionSource.get(pathName))
                            .withFailMessage(FAIL_MESSAGE, pathName, sourceField, targetField)
                            .isEqualTo(versionTarget.get(pathName));
                }
            }
        }
    }
}
