package no.ssb.klass.api.migration.dataintegrity;

import io.restassured.response.Response;
import no.ssb.klass.api.migration.MigrationTestUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;
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

    Response sourceResponse;
    Response targetResponse;

    @BeforeAll
    static void setUpClassification() {
        paramsLanguageEn.put(LANGUAGE, EN);
        paramsLanguageNn.put(LANGUAGE, NN);
        paramsIncludeFuture.put(INCLUDE_FUTURE, TRUE);
    }

    @ParameterizedTest
    @MethodSource("rangeProviderClassificationIds")
    void getClassification(Integer classificationId) {

        String path = getClassificationByIdPath(classificationId);

        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, null);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, null);

        MigrationTestUtils.assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            System.out.println(LOG_MESSAGE_STATUS_CODE + sourceResponse.getStatusCode());
            assertThat(MigrationTestUtils.compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else {
            validateItems(sourceResponse, targetResponse, pathNamesClassification);
            // Link section
            validateLinks(sourceResponse, targetResponse, pathNamesClassificationItemLinks);
            // Statistical units list
            validateList(sourceResponse, targetResponse, STATISTICAL_UNITS);
            // Versions
            validatePathListWithLinks(sourceResponse, targetResponse, VERSIONS, pathNamesVersion);
        }
    }


    @ParameterizedTest
    @MethodSource("rangeProviderClassificationIds")
    void getClassificationEnglish(Integer classificationId) {

        String path = getClassificationByIdPath(classificationId);

        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsLanguageEn);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsLanguageEn);

        MigrationTestUtils.assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            System.out.println(LOG_MESSAGE_STATUS_CODE + sourceResponse.getStatusCode());
            assertThat(MigrationTestUtils.compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        } else {
            validateItems(sourceResponse, targetResponse, pathNamesClassification);
            // Link section
            validateLinks(sourceResponse, targetResponse, pathNamesClassificationItemLinks);
            // Statistical units list
            validateList(sourceResponse, targetResponse, STATISTICAL_UNITS);
            // Versions
            validatePathListWithLinks(sourceResponse, targetResponse, VERSIONS, pathNamesVersion);
        }
    }

    @ParameterizedTest
    @MethodSource("rangeProviderClassificationIds")
    void getClassificationNewNorwegian(Integer classificationId) {

        String path = CLASSIFICATIONS_PATH + "/" + classificationId;

        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsLanguageNn);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsLanguageNn);

        MigrationTestUtils.assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            System.out.println(LOG_MESSAGE_STATUS_CODE + sourceResponse.getStatusCode());
            assertThat(MigrationTestUtils.compareError(classificationId, sourceResponse, targetResponse)).isTrue();
            }
        else {
            validateItems(sourceResponse, targetResponse, pathNamesClassification);
            // Link section
            validateLinks(sourceResponse, targetResponse, pathNamesClassificationItemLinks);
            // Statistical units list
            validateList(sourceResponse, targetResponse, STATISTICAL_UNITS);
            // Versions
            validatePathListWithLinks(sourceResponse, targetResponse, VERSIONS, pathNamesVersion);
        }
    }

    @ParameterizedTest
    @MethodSource("rangeProviderClassificationIds")
    void getClassificationIncludeFuture(Integer classificationId) {

        String path = getClassificationByIdPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsIncludeFuture);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsIncludeFuture);

        MigrationTestUtils.assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            System.out.println(LOG_MESSAGE_STATUS_CODE + sourceResponse.getStatusCode());
            assertThat(MigrationTestUtils.compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }else {
            validateItems(sourceResponse, targetResponse, pathNamesClassification);
            // Link section
            validateLinks(sourceResponse, targetResponse, pathNamesClassificationItemLinks);
            // Statistical units list
            validateList(sourceResponse, targetResponse, STATISTICAL_UNITS);
            // Versions
            validatePathListWithLinks(sourceResponse, targetResponse, VERSIONS, pathNamesVersion);
        }
    }

    static Stream<Integer> rangeProviderClassificationIds() {
        return IntStream.rangeClosed(0, 652).boxed();
    }

    String getClassificationByIdPath(Integer id) {
        return CLASSIFICATIONS_PATH + "/" + id;
    }
}
