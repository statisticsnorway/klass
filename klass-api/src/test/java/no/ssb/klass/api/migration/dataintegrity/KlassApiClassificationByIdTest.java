package no.ssb.klass.api.migration.dataintegrity;

import io.restassured.response.Response;
import no.ssb.klass.api.migration.MigrationTestConstants;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

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
            System.out.println(LOG_MESSAGE_STATUS_CODE + sourceResponse.getStatusCode());
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else {
            System.out.println(LOG_MESSAGE_STATUS_CODE + sourceResponse.getStatusCode());
            // General fields
            validateItem(sourceResponse, targetResponse, pathNamesClassification);
            // Links
            validateLinks(sourceResponse, targetResponse, pathNamesClassificationLinks);
            // Statistical units list
            validateList(sourceResponse, targetResponse, STATISTICAL_UNITS);
            // Versions list
            validateClassificationVersions(sourceResponse, targetResponse);
        }
    }


    @ParameterizedTest
    @MethodSource("provideClassificationIds")
    void getClassificationEnglish(Integer classificationId) {

        sourceResponse = klassApiMigrationClient.getFromSourceApi(CLASSIFICATIONS_PATH + "/" + classificationId, paramsLanguageEn);
        targetResponse = klassApiMigrationClient.getFromTargetApi(CLASSIFICATIONS_PATH + "/" + classificationId, paramsLanguageEn);

        if(sourceResponse.getStatusCode() != 200) {
            System.out.println(LOG_MESSAGE_STATUS_CODE + sourceResponse.getStatusCode());
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
            }
            else {
                System.out.println(LOG_MESSAGE_STATUS_CODE + sourceResponse.getStatusCode());
                // General fields
                validateItem(sourceResponse, targetResponse, pathNamesClassification);
                // check links
                validateLinks(sourceResponse, targetResponse, pathNamesClassificationLinks);

                // check statistical units list
                validateList(sourceResponse, targetResponse, STATISTICAL_UNITS);

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
            System.out.println(LOG_MESSAGE_STATUS_CODE + sourceResponse.getStatusCode());
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
            }
        else {
            System.out.println(LOG_MESSAGE_STATUS_CODE + sourceResponse.getStatusCode());
            // General fields
            validateItem(sourceResponse, targetResponse, pathNamesClassification);
            // check links
            validateLinks(sourceResponse, targetResponse, pathNamesClassificationLinks);

            // check statistical units list
            validateList(sourceResponse, targetResponse, STATISTICAL_UNITS);

            // check versions list
            validateClassificationVersions(sourceResponse, targetResponse);
        }
    }

    @ParameterizedTest
    @MethodSource("provideClassificationIds")
    void getClassificationIncludeFuture(Integer classificationId) {

            sourceResponse = klassApiMigrationClient.getFromSourceApi(CLASSIFICATIONS_PATH + "/" + classificationId, paramsIncludeFuture);
            targetResponse = klassApiMigrationClient.getFromTargetApi(CLASSIFICATIONS_PATH + "/" + classificationId, paramsIncludeFuture);


            if(sourceResponse.getStatusCode() != 200) {
                assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
            }
            else {
                // General fields
                validateItem(sourceResponse, targetResponse, pathNamesClassification);
                // check links
                validateLinks(sourceResponse, targetResponse, pathNamesClassificationLinks);

                // check statistical units list
                validateList(sourceResponse, targetResponse, STATISTICAL_UNITS);

                // check versions list
                validateClassificationVersions(sourceResponse, targetResponse);
            }

    }

    static Stream<Integer> provideClassificationIds() {
        return classificationsIdsSourceHost.stream();
    }

    /**
     *
     * @param sourceResponse Response object from source Api
     * @param targetResponse Response object from target Api
     */
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

                Object sourceField;
                Object targetField;

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
