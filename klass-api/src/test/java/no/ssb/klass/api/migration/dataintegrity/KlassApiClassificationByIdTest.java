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

        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(CLASSIFICATIONS_PATH + "/" + classificationId, paramsLanguageEn);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(CLASSIFICATIONS_PATH + "/" + classificationId, paramsLanguageEn);

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

        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(CLASSIFICATIONS_PATH + "/" + classificationId, paramsLanguageNn);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(CLASSIFICATIONS_PATH + "/" + classificationId, paramsLanguageNn);


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

            Response sourceResponse = klassApiMigrationClient.getFromSourceApi(CLASSIFICATIONS_PATH + "/" + classificationId, paramsIncludeFuture);
            Response targetResponse = klassApiMigrationClient.getFromTargetApi(CLASSIFICATIONS_PATH + "/" + classificationId, paramsIncludeFuture);


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
            Object sourceField = sourceResponse.path(pathName);
            Object targetField = targetResponse.path(pathName);

            assertThat(sourceField)
                    .withFailMessage("Mismatch at path '%s':\n  Source: %s\n  Target: %s",
                            pathName, sourceField, targetField)
                    .isEqualTo(targetField);
        }

    }

    void validateStatisticalUnits(Response sourceResponse, Response targetResponse) {

        ArrayList<String> sourceList = sourceResponse.path(STATISTICAL_UNITS);
        ArrayList<String> targetList = targetResponse.path(STATISTICAL_UNITS);

        assertThat(sourceList.size())
                .withFailMessage("Expected source and target lists to be the same size, but got %s vs %s",
                        sourceList.size(), targetList.size())
                .isEqualTo(targetList.size());

        assertThat(sourceList.containsAll(targetList))
                .withFailMessage("Source list is missing some elements from the target list.\nSource: %s\nTarget: %s",
                        sourceList, targetList)
                .isTrue();

        assertThat(targetList.containsAll(sourceList))
                .withFailMessage("Target list is missing some elements from the source list.\nSource: %s\nTarget: %s",
                        sourceList, targetList)
                .isTrue();
    }

    void validateClassificationLinks(Response sourceResponse, Response targetResponse) {
        for (String pathName : MigrationTestConstants.pathNamesClassificationLinks) {
            Object sourceField = sourceResponse.path(pathName);
            Object targetField = targetResponse.path(pathName);

            if (pathName.endsWith(HREF)) {
                String sourceHref = sourceField != null ? sourceField.toString() : null;
                String targetHref = targetField != null ? targetField.toString() : null;

                assertThat(isPathEqualIgnoreHost(sourceHref, targetHref))
                        .withFailMessage("Mismatch in href path '%s':\n  Source: %s\n  Target: %s", pathName, sourceHref, targetHref)
                        .isTrue();
            } else {
                assertThat(sourceField)
                        .withFailMessage("Mismatch in field '%s':\n  Source: %s\n  Target: %s", pathName, sourceField, targetField)
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

                Object sourceField = resolvePath(versionSource, pathName);
                Object targetField = resolvePath(versionTarget, pathName);

                if (pathName.endsWith(HREF)) {
                    assertThat(sourceField == null && targetField == null ||
                            sourceField != null && targetField != null && isPathEqualIgnoreHost(sourceField.toString(), targetField.toString()))
                            .withFailMessage("Mismatch on href field %s for version ID %s", pathName, versionId)
                            .isTrue();
                } else {
                    assertThat(versionSource.get(pathName))
                            .withFailMessage("Mismatch on field %s for version ID %s", pathName, versionId)
                            .isEqualTo(versionTarget.get(pathName));
                }
            }
        }
    }
}
