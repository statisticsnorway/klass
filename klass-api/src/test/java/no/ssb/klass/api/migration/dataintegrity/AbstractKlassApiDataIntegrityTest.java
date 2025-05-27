package no.ssb.klass.api.migration.dataintegrity;

import io.restassured.response.Response;
import no.ssb.klass.api.migration.KlassApiMigrationClient;
import no.ssb.klass.api.migration.MigrationTestConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static no.ssb.klass.api.migration.MigrationTestUtils.*;
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
     * Validates that the values at specified path names in two API response bodies are equal.
     * <p>
     *  For each given path name, the corresponding values from the source and target responses
     *  are retrieved and printed to stdout. If the value in the source response is {@code null},
     *  the target value is expected to be {@code null} as well. If the values differ, a failure
     *  message is printed to clearly document the discrepancy, making failed tests easier to trace.
     *</p>
     * @param sourceResponse Response object from the source Api
     * @param targetResponse Response object from the target Api
     * @param pathNames a list of path names to extract and compare from the response bodies
     */
    static void validateItemsLegacy(Response sourceResponse, Response targetResponse, List<String> pathNames) {

        for (String pathName : pathNames) {
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

            System.out.println(sourceField + " -> " + targetField);

            assertThat(sourceField)
                    .withFailMessage(FAIL_MESSAGE,
                            pathName, sourceField, targetField)
                    .isEqualTo(targetField);
        }

    }

    /**
     * Validates that the value at the path {@code "_links.self.href"} in two API response bodies are equal.
     * <p>
     *      The corresponding value from the source and target response
     *      are retrieved and printed to stdout. If the value in the source response is {@code null},
     *      the target value is expected to be {@code null} as well. If the value differ, a failure
     *      message is printed to clearly document the discrepancy, making failed tests easier to trace.
     * </p>
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

        assertThat(isPathEqualIgnoreHost(sourceLink, targetLink)).withFailMessage(FAIL_MESSAGE, LINKS_SELF_HREF, sourceLink, targetLink).isTrue();
    }

    /**
     * Validates that the lists at the specified path in both API response bodies contain the same elements, regardless of order.
     *  <P>
     *     For each given path, the corresponding values from the source and target responses
     *      are retrieved and printed to stdout. If the value in the source response is {@code null},
     *      the target value is expected to be {@code null} as well. If the values differ, a failure
     *      message is printed to clearly document the discrepancy, making failed tests easier to trace.
     *  </P>
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
     * Validates that the values retrieved by path names in
     * @param sourceResponse Response object from source Api
     * @param targetResponse Response object from target Api
     * @param pathNamesLinks List of path names
     */
    static void validateItems(Response sourceResponse, Response targetResponse, List<String> pathNamesLinks) {

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
                System.out.println(sourceHref + " -> " + targetHref);
                assertThat(isPathEqualIgnoreHost(sourceHref, targetHref))
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
    static void validatePathListWithLinks(Response sourceResponse, Response targetResponse, String listName, List<String> pathNames, String idField) {
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

        Map<Object, Map<String, Object>> sourceById = mapByField(sourceList, idField);
        Map<Object, Map<String, Object>> targetById = mapByField(targetList, idField);

        for (Object versionId : sourceById.keySet()) {
            Map<String, Object> versionSource = sourceById.get(versionId);
            Map<String, Object> versionTarget = targetById.get(versionId);

            for (String pathName : pathNames) {

                Object sourceField = resolvePath(versionSource, pathName);
                Object targetField = resolvePath(versionTarget, pathName);

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

    private static Map<Object, Map<String, Object>> mapByField(List<Map<String, Object>> list, String field) {
        return list.stream()
                .filter(item -> item.containsKey(field) && item.get(field) != null)
                .collect(Collectors.toMap(
                        item -> item.get(field),
                        Function.identity()
                ));
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
