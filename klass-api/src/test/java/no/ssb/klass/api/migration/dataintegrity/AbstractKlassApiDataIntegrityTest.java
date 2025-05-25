package no.ssb.klass.api.migration.dataintegrity;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import no.ssb.klass.api.migration.KlassApiMigrationClient;
import no.ssb.klass.api.migration.MigrationTestConfig;
import no.ssb.klass.api.util.RestConstants;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;

import java.net.URL;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static io.restassured.RestAssured.get;
import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static no.ssb.klass.api.migration.MigrationTestUtils.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public abstract class AbstractKlassApiDataIntegrityTest {

    static KlassApiMigrationClient klassApiMigrationClient;

    public static final String sourceHost = MigrationTestConfig.getSourceHost();

    public static final String targetHost = MigrationTestConfig.getTargetHost();

    static Response sourceResponseClassifications;
    static Response targetResponseClassifications;

    static String klassApSourceHostPath = sourceHost + BASE_PATH + RestConstants.API_VERSION_V1 + CLASSIFICATIONS_PATH;
    static String klassApiTargetHostPath = targetHost + BASE_PATH + RestConstants.API_VERSION_V1 + CLASSIFICATIONS_PATH;

    static List<Integer> classificationsIdsSourceHost = new ArrayList<>();
    static List<Integer> classificationsIdsTargetHost = new ArrayList<>();
    static List<Map<String, Object>> allClassificationsSourceHost = new ArrayList<>();
    static List<Map<String, Object>> allClassificationsTargetHost = new ArrayList<>();


    static List<Integer> classificationsIdsSourceHostPart1 = new ArrayList<>();
    static List<Integer> classificationsIdsSourceHostPart2 = new ArrayList<>();
    static List<Integer> classificationsIdsSourceHostPart3 = new ArrayList<>();
    static List<Integer> classificationsIdsSourceHostPart4 = new ArrayList<>();
    static List<Integer> classificationsIdsSourceHostPart5 = new ArrayList<>();
    static List<Integer> classificationsIdsSourceHostPart6 = new ArrayList<>();
    static List<Integer> classificationsIdsSourceHostPart7 = new ArrayList<>();
    static List<Integer> classificationsIdsSourceHostPart8 = new ArrayList<>();
    static List<Integer> classificationsIdsSourceHostPart9 = new ArrayList<>();
    static List<Integer> classificationsIdsSourceHostPart10 = new ArrayList<>();
    static List<Integer> classificationsIdsSourceHostPart11 = new ArrayList<>();
    static List<Integer> classificationsIdsSourceHostPart12 = new ArrayList<>();
    static List<Integer> classificationsIdsSourceHostPart13= new ArrayList<>();
    static List<Integer> classificationsIdsSourceHostPart14 = new ArrayList<>();
    static List<Integer> classificationsIdsSourceHostPart15 = new ArrayList<>();
    static List<Integer> classificationsIdsSourceHostPart16 = new ArrayList<>();


    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    static void getAllSourceHost() {
        String url = klassApSourceHostPath;

        while (url != null) {
            JsonPath json = get(url)
                    .then().statusCode(200)
                    .extract().jsonPath();

            // Extract IDs from this page
            List<Integer> ids = json.getList("_embedded.classifications.id");
            classificationsIdsSourceHost.addAll(ids);

            // Extract ClassificationItems
            List<Map<String, Object>> classifications =
                    json.getList("_embedded.classifications");

            allClassificationsSourceHost.addAll(classifications);

            // Get next page URL if available
            url = json.get("_links.next.href");
        }
    }

    static void getAllTargetHost() {
        String url = klassApiTargetHostPath;

        while (url != null) {
            JsonPath json = get(url)
                    .then().statusCode(200)
                    .extract().jsonPath();

            // Extract IDs from this page
            List<Integer> ids = json.getList("_embedded.classifications.id");
            classificationsIdsTargetHost.addAll(ids);

            // Extract ClassificationItems
            List<Map<String, Object>> classifications =
                    json.getList("_embedded.classifications");

            allClassificationsTargetHost.addAll(classifications);

            // Get next page URL if available
            url = json.get("_links.next.href");
        }
    }

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

    static void setClassificationLists(){
        int listLength = classificationsIdsSourceHost.size();
        int shareLength = listLength / 8;
        int remainder = listLength % 8;

        int index = 0;
        List<List<Integer>> parts = Arrays.asList(
                classificationsIdsSourceHostPart1,
                classificationsIdsSourceHostPart2,
                classificationsIdsSourceHostPart3,
                classificationsIdsSourceHostPart4,
                classificationsIdsSourceHostPart5,
                classificationsIdsSourceHostPart6,
                classificationsIdsSourceHostPart7,
                classificationsIdsSourceHostPart8
        );
        for (int i = 0; i < 8; i++) {
            int currentPartSize = shareLength + (remainder > i ? 1 : 0);
            for (int j = 0; j < currentPartSize; j++) {
                parts.get(i).add(classificationsIdsSourceHost.get(index++));
            }
        }
    }

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

    static LocalDate generateRandomDate() {
        LocalDate startDate = LocalDate.of(1800, 1, 1);
        LocalDate endDate = LocalDate.of(2030, 12, 31);
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        Random random = new Random();
        long randomDay = random.nextLong(daysBetween + 1);
        return startDate.plusDays(randomDay);
    }

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

    static Integer generateRandomId(int to) {
        Random random = new Random();
        return random.nextInt(to);
    }

    @BeforeAll
    static void beforeAll() {
        klassApiMigrationClient = new KlassApiMigrationClient();

        boolean sourceUp = klassApiMigrationClient.isApiAvailable(sourceHost);
        boolean targetUp = klassApiMigrationClient.isApiAvailable(targetHost);

        Assumptions.assumeTrue(sourceUp && targetUp, "One or both APIs are not available, skipping tests.");

        getAllSourceHost();
        getAllTargetHost();

        sourceResponseClassifications = klassApiMigrationClient.getFromSourceApi(CLASSIFICATIONS_PATH, null);
        targetResponseClassifications = klassApiMigrationClient.getFromTargetApi(CLASSIFICATIONS_PATH, null);
    }

    @AfterAll
    public static void cleanUp(){
        System.out.println("Cleanup after tests");
    }

    /**
     *
     * @param sourceResponse Response object from source Api
     * @param targetResponse Response object from target Api
     * @param pathNames List of path names in Response object
     */
    static void validateItem(Response sourceResponse, Response targetResponse, List<String> pathNames) {

        Object sourceField;
        Object targetField;

        for (String pathName : pathNames) {
            sourceField = sourceResponse.path(pathName);
            targetField = targetResponse.path(pathName);

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
     * @param pathListName List of path names in nested list
     */
    static void validateList(Response sourceResponse, Response targetResponse, String pathListName) {

        ArrayList<String> sourceList = sourceResponse.path(pathListName);
        ArrayList<String> targetList = targetResponse.path(pathListName);
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
                assertThat(sourceField)
                        .withFailMessage(FAIL_MESSAGE, pathName, sourceField, targetField)
                        .isEqualTo(targetField);
            }
        }
    }
}
