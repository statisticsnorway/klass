package no.ssb.klass.api.migration.dataintegrity;

import io.restassured.response.Response;
import no.ssb.klass.api.migration.KlassApiMigrationClient;
import no.ssb.klass.api.migration.MigrationTestConfig;
import no.ssb.klass.api.migration.MigrationTestUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;

import java.time.format.DateTimeFormatter;
import java.util.*;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static no.ssb.klass.api.migration.MigrationTestUtils.mapById;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public abstract class AbstractKlassApiDataIntegrityTest {

    static KlassApiMigrationClient klassApiMigrationClient;

    static Response sourceResponseClassifications;
    static Response targetResponseClassifications;

    static int numClassifications;

    public static final String sourceHost = MigrationTestConfig.getSourceHost();

    public static final String targetHost = MigrationTestConfig.getTargetHost();

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    static void validateObject(Response sourceResponse, Response targetResponse, String pathName) {
        Object sourceField = sourceResponse.path(pathName);
        Object targetField = targetResponse.path(pathName);

        if (sourceField == null) {
            assertThat(targetField)
                    .withFailMessage(FAIL_MESSAGE, pathName, null, targetField)
                    .isNull();
            return;
        }
        assertThat(targetField)
                .withFailMessage(FAIL_MESSAGE, pathName, sourceField, targetField)
                .isNotNull();

        assertThat(sourceField).withFailMessage(FAIL_MESSAGE,pathName, sourceField, targetField).isEqualTo(targetField);

    }

    /**
     *
     * @param sourceResponse Response object from source Api
     * @param targetResponse Response object from target Api
     * @param pathNames List of path names in Response object
     */
    static void validateItems(Response sourceResponse, Response targetResponse, List<String> pathNames) {

        Object sourceField;
        Object targetField;

        for (String pathName : pathNames) {
            sourceField = sourceResponse.path(pathName);
            targetField = targetResponse.path(pathName);

            if (sourceField == null) {
                assertThat(targetField)
                        .withFailMessage(FAIL_MESSAGE, pathName, null, targetField)
                        .isNull();
                return;
            }

            assertThat(targetField)
                    .withFailMessage(FAIL_MESSAGE, pathName, sourceField, targetField)
                    .isNotNull();

            System.out.println(sourceField + " -> " + targetField);

            assertThat(sourceField)
                    .withFailMessage(FAIL_MESSAGE,
                            pathName, sourceField, targetField)
                    .isEqualTo(targetField);
        }

    }

    /**
     *
     * @param sourceResponse Response object from source Api
     * @param targetResponse Response object from target Api
     */
    static void validateSelfLink(Response sourceResponse, Response targetResponse) {
        String sourceLink = sourceResponse.path(LINKS_SELF_HREF);
        String targetLink = targetResponse.path(LINKS_SELF_HREF);

        if (sourceLink == null) {
            assertThat(targetLink)
                    .withFailMessage(FAIL_MESSAGE, LINKS_SELF_HREF, null, targetLink)
                    .isNull();
            return;
        }

        assertThat(targetLink)
                .withFailMessage(FAIL_MESSAGE, LINKS_SELF_HREF, sourceLink, targetLink)
                .isNotNull();

        System.out.println(sourceLink + " -> " + targetLink);

        assertThat(MigrationTestUtils.isPathEqualIgnoreHost(sourceLink, targetLink)).withFailMessage(FAIL_MESSAGE, LINKS_SELF_HREF, sourceLink, targetLink).isTrue();
    }

    /**
     *
     * @param sourceResponse Response object from source Api
     * @param targetResponse Response object from target Api
     * @param pathListName List of path names in nested list
     */
    static void validateList(Response sourceResponse, Response targetResponse, String pathListName) {

        ArrayList<String> sourceList = sourceResponse.path(pathListName);
        ArrayList<String> targetList = targetResponse.path(pathListName);

        if (sourceList == null) {
            assertThat(targetList)
                    .withFailMessage(FAIL_MESSAGE, pathListName, null, targetList)
                    .isNull();
            return;
        }

        assertThat(targetList)
                .withFailMessage(FAIL_MESSAGE, pathListName, sourceList, targetList)
                .isNotNull();

        System.out.println("List sizes: " + sourceList.size() + " -> " + targetList.size());
        assertThat(sourceList.size())
                .withFailMessage(FAIL_MESSAGE,
                        pathListName, sourceList.size(), targetList.size())
                .isEqualTo(targetList.size());

        assertThat(sourceList.containsAll(targetList))
                .withFailMessage(FAIL_MESSAGE,
                        pathListName, sourceList, targetList)
                .isTrue();

        assertThat(targetList.containsAll(sourceList))
                .withFailMessage(FAIL_MESSAGE,
                        pathListName, sourceList, targetList)
                .isTrue();
    }

    /**
     *
     * @param sourceResponse Response object from source Api
     * @param targetResponse Response object from target Api
     * @param pathNamesLinks List of path names in _links object
     */
    static void validateLinks(Response sourceResponse, Response targetResponse, List<String> pathNamesLinks) {

        for (String pathName : pathNamesLinks) {
            Object sourceField = sourceResponse.path(pathName);
            Object targetField = targetResponse.path(pathName);

            if (sourceField == null) {
                assertThat(targetField)
                        .withFailMessage(FAIL_MESSAGE, pathName, null, targetField)
                        .isNull();
                return;
            }

            assertThat(targetField)
                    .withFailMessage(FAIL_MESSAGE, pathName, sourceField, targetField)
                    .isNotNull();

            if (pathName.endsWith(HREF)) {
                String sourceHref = sourceField.toString();
                String targetHref = targetField.toString();

                assertThat(MigrationTestUtils.isPathEqualIgnoreHost(sourceHref, targetHref))
                        .withFailMessage(FAIL_MESSAGE, pathName, sourceHref, targetHref)
                        .isTrue();
            } else {
                System.out.println(sourceField + " -> " + targetField);
                assertThat(sourceField)
                        .withFailMessage(FAIL_MESSAGE, pathName, sourceField, targetField)
                        .isEqualTo(targetField);
            }
        }
    }

    /**
     *
     * @param sourceResponse Response object from source Api
     * @param targetResponse Response object from target Api
     * @param listName Name of list element in path
     * @param pathNames List of fields in listName list
     */
    static void validatePathListWithLinks(Response sourceResponse, Response targetResponse, String listName, List<String> pathNames) {
        List<Map<String, Object>> sourceList = sourceResponse.path(listName);
        List<Map<String, Object>> targetList = targetResponse.path(listName);
        if (sourceList == null) {
            assertThat(targetList)
                    .withFailMessage(FAIL_MESSAGE, listName, null, targetList)
                    .isNull();
            return;
        }

        assertThat(targetList)
                .withFailMessage(FAIL_MESSAGE, listName, sourceList, targetList)
                .isNotNull();

        assertThat(sourceList.size()).isEqualTo(targetList.size());
        System.out.println("List sizes: " + sourceList.size() + " -> " + targetList.size());

        Map<Object, Map<String, Object>> sourceById = mapById(sourceList);
        Map<Object, Map<String, Object>> targetById = mapById(targetList);

        for (Object versionId : sourceById.keySet()) {
            Map<String, Object> versionSource = sourceById.get(versionId);
            Map<String, Object> versionTarget = targetById.get(versionId);

            for (String pathName : pathNames) {

                Object sourceField;
                Object targetField;

                sourceField = MigrationTestUtils.resolvePath(versionSource, pathName);
                targetField = MigrationTestUtils.resolvePath(versionTarget, pathName);

                if (pathName.endsWith(HREF)) {
                    assertThat(sourceField == null && targetField == null ||
                            sourceField != null && targetField != null && MigrationTestUtils.isPathEqualIgnoreHost(sourceField.toString(), targetField.toString()))
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

    @BeforeAll
    static void beforeAll() {
        klassApiMigrationClient = new KlassApiMigrationClient();

        boolean sourceUp = klassApiMigrationClient.isApiAvailable(sourceHost);
        boolean targetUp = klassApiMigrationClient.isApiAvailable(targetHost);

        Assumptions.assumeTrue(sourceUp && targetUp, "One or both APIs are not available, skipping tests.");
        sourceResponseClassifications = klassApiMigrationClient.getFromSourceApi(CLASSIFICATIONS_PATH, null);
        targetResponseClassifications = klassApiMigrationClient.getFromTargetApi(CLASSIFICATIONS_PATH, null);

        numClassifications = sourceResponseClassifications.path(PAGE_TOTAL_ELEMENTS);
    }

    @AfterAll
    public static void cleanUp(){
        System.out.println("Cleanup after tests");
    }
}
