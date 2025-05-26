package no.ssb.klass.api.migration.dataintegrity;

import io.restassured.response.Response;
import no.ssb.klass.api.migration.KlassApiMigrationClient;
import no.ssb.klass.api.migration.MigrationTestConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;

import java.net.URL;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

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

    /**
     * Compare two href and ignore host
     *
     * @param sourceHref String path
     * @param targetHref String path
     * @return True if the two paths are the same, False otherwise
     */
    static boolean isPathEqualIgnoreHost(String sourceHref, String targetHref) {
        try {
            URL sourceUrl = new URL(sourceHref);
            URL targetUrl = new URL(targetHref);

            if(!sourceUrl.getPath().equals(targetUrl.getPath())){
                System.out.println(
                        "Url path comparison issue: \nsource url path: " +
                                sourceUrl.getPath() + "\ntarget url path: " +
                                targetUrl.getPath());
            }
            return sourceUrl.getPath().equals(targetUrl.getPath());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     *
     * @param map
     * @param path
     * @return
     */
    static Object resolvePath(Map<String, Object> map, String path) {
        String[] parts = path.split("\\.");
        Object current = map;

        for (String part : parts) {
            if (!(current instanceof Map)) {
                return null;
            }
            current = ((Map<?, ?>) current).get(part);
        }

        return current;
    }

    /**
     *
     * @param ID
     * @param sourceResponse Response object from source Api
     * @param targetResponse Response object from target Api
     * @return
     */
    static boolean compareErrorJsonResponse(Integer ID, Response sourceResponse, Response targetResponse) {
        Object sourceBody = sourceResponse.getBody().jsonPath().get("error");
        Object targetBody = targetResponse.getBody().jsonPath().get("error");

        if (sourceResponse.getStatusCode() != targetResponse.getStatusCode() || !sourceBody.equals(targetBody)){
           String sourceError = (ID != null)? ("Source: ID: " + ID + ", Code: " + sourceResponse.getStatusCode() + ", " + sourceBody) : ("Source: " +  "Code: " + sourceResponse.getStatusCode() + ", " + sourceBody);
            String targetError = (ID != null)? ("Target: ID: " + ID + ", Code: " + targetResponse.getStatusCode() + ", " + targetBody) : ("Target: " + "Code: " + targetResponse.getStatusCode() + ", " + targetBody);

            System.out.println(String.join(", ", sourceError) + "\n" + String.join(", ", targetError));
            return false;
        }
        return true;
    }

    /**
     *
     * @param ID
     * @param sourceResponse Response object from source Api
     * @param targetResponse Response object from target Api
     * @return
     */
    static boolean compareError(Integer ID, Response sourceResponse, Response targetResponse) {
        Object sourceBody = sourceResponse.getBody().asString();
        Object targetBody = targetResponse.getBody().asString();

        if (sourceResponse.getStatusCode() != targetResponse.getStatusCode() || !sourceBody.equals(targetBody)){
           String sourceError = (ID != null)? ("Source: ID: " + ID + ", Code: " + sourceResponse.getStatusCode() + ", " + sourceBody) : ("Source: " +  "Code: " + sourceResponse.getStatusCode() + ", " + sourceBody);
            String targetError = (ID != null)? ("Target: ID: " + ID + ", Code: " + targetResponse.getStatusCode() + ", " + targetBody) : ("Target: " + "Code: " + targetResponse.getStatusCode() + ", " + targetBody);

            System.out.println(String.join(", ", sourceError) + "\n" + String.join(", ", targetError));
            return false;
        }
       return true;
    }

    /**
     *
     * @param startDate
     * @param endDate
     * @return
     */
    static LocalDate generateRandomDate(LocalDate startDate, LocalDate endDate) {
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        Random random = new Random();
        long randomDay = random.nextLong(daysBetween + 1);
        return startDate.plusDays(randomDay);
    }

    /**
     *
     * @return
     */
    static String generateRandomDateTime() {
        LocalDate startDate = LocalDate.of(1800, 1, 1);
        LocalDate endDate = LocalDate.of(2030, 12, 31);

        long totalDays = ChronoUnit.DAYS.between(startDate, endDate);
        LocalDate randomDate = startDate.plusDays(ThreadLocalRandom.current().nextLong(totalDays + 1));

        LocalTime randomTime = LocalTime.of(
                ThreadLocalRandom.current().nextInt(0, 24),
                ThreadLocalRandom.current().nextInt(0, 60),
                ThreadLocalRandom.current().nextInt(0, 60),
                ThreadLocalRandom.current().nextInt(0, 1_000_000_000)
        );

        LocalDateTime localDateTime = LocalDateTime.of(randomDate, randomTime);

        int offsetHours = ThreadLocalRandom.current().nextInt(-12, 15);
        ZoneOffset offset = ZoneOffset.ofHours(offsetHours);
        OffsetDateTime offsetDateTime = localDateTime.atOffset(offset);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        return offsetDateTime.format(formatter);
    }

    /**
     *
     * @param to
     * @return
     */
    static Integer generateRandomId(int to) {
        Random random = new Random();
        return random.nextInt(to);
    }

    /**
     * Assert that source and target returns the same status code
     *
     * @param sourceStatusCode Status code value of request to sourceHost
     * @param targetStatusCode Status code value of request to targetHost
     * @param path Path to the request
     */
    static void assertStatusCodesEqual(int sourceStatusCode, int targetStatusCode, String path){

        assertThat(sourceStatusCode)
                .withFailMessage(FAIL_MESSAGE, path, sourceStatusCode, targetStatusCode)
                .isEqualTo(targetStatusCode);
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
     * @param pathName Path name to url
     */
    static void validateOneLink(Response sourceResponse, Response targetResponse, String pathName) {
        String sourceLink;
        String targetLink;

        sourceLink = sourceResponse.path(pathName);
        targetLink = targetResponse.path(pathName);

        System.out.println(sourceLink + " -> " + targetLink);

        assertThat(isPathEqualIgnoreHost(sourceLink, targetLink)).withFailMessage(FAIL_MESSAGE, LINKS_SELF_HREF, sourceLink, targetLink).isTrue();
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

        Object sourceField;
        Object targetField;

        for (String pathName : pathNamesLinks) {
            sourceField = sourceResponse.path(pathName);
            targetField = targetResponse.path(pathName);

            if (pathName.endsWith(HREF)) {
                String sourceHref = sourceField != null ? sourceField.toString() : null;
                String targetHref = targetField != null ? targetField.toString() : null;

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
