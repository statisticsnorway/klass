package no.ssb.klass.api.migration.dataintegrity;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import no.ssb.klass.api.migration.MigrationTestConfig;
import no.ssb.klass.api.util.RestConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.get;
import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static no.ssb.klass.api.migration.MigrationTestConstants.EMBEDDED_CLASSIFICATIONS;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public abstract class AbstractKlassApiDataIntegrityTest {

    private static final String sourceHost = MigrationTestConfig.getSourceHost();

    private static final String targetHost = MigrationTestConfig.getTargetHost();

    Response responseKlassApiSourceHost;
    Response responseKlassApiTargetHost;

    public String klassApSourceHostPath = sourceHost + BASE_PATH + RestConstants.API_VERSION_V1 + CLASSIFICATIONS_PATH;
    public String klassApiTargetHostPath = targetHost + BASE_PATH + RestConstants.API_VERSION_V1 + CLASSIFICATIONS_PATH;

    public List<Integer> classificationsIdsSourceHost = new ArrayList<>();
    public List<Integer> classificationsIdsTargetHost = new ArrayList<>();
    public List<Map<String, Object>> allClassificationsSourceHost = new ArrayList<>();
    public List<Map<String, Object>> allClassificationsTargetHost = new ArrayList<>();

    List<Map<String, Object>> sourceHostClassificationsPage;
    List<Map<String, Object>> targetHostClassificationsPage;

    List<Integer> classificationsIdsSourceHostPart1 = new ArrayList<>();
    List<Integer> classificationsIdsSourceHostPart2 = new ArrayList<>();
    List<Integer> classificationsIdsSourceHostPart3 = new ArrayList<>();
    List<Integer> classificationsIdsSourceHostPart4 = new ArrayList<>();

    void getAllSourceHost() {
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

    void getAllTargetHost() {
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

    public static boolean isPathEqualIgnoreHost(String sourceHref, String targetHref) {
        try {
            URL sourceUrl = new URL(sourceHref);
            URL targetUrl = new URL(targetHref);

            return sourceUrl.getPath().equals(targetUrl.getPath());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Response getResponse(String path) {
        Response response = RestAssured.get(path);
        response.then().assertThat().statusCode(200);
        return response;
    }

    public static Object resolvePath(Map<String, Object> map, String path) {
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

    protected void setClassificationLists(){
        int listLength = classificationsIdsSourceHost.size();
        int shareLength = listLength / 4;
        int remainder = listLength % 4;

        int index = 0;
        for (int i = 0; i < shareLength + (remainder > 0 ? 1 : 0); i++) {
            classificationsIdsSourceHostPart1.add(classificationsIdsSourceHost.get(index++));
        }
        for (int i = 0; i < shareLength + (remainder > 1 ? 1 : 0); i++) {
            classificationsIdsSourceHostPart2.add(classificationsIdsSourceHost.get(index++));
        }
        for (int i = 0; i < shareLength + (remainder > 2 ? 1 : 0); i++) {
            classificationsIdsSourceHostPart3.add(classificationsIdsSourceHost.get(index++));
        }
        while (index < listLength) {
            classificationsIdsSourceHostPart4.add(classificationsIdsSourceHost.get(index++));
        }
    }

    public static boolean compareError(int ID, Response sourceResponse, Response targetResponse) {
        Object sourceBody = sourceResponse.getBody().asString();
        Object targetBody = targetResponse.getBody().asString();

        List<String> sourceErrors = new ArrayList<>();
        List<String> targetErrors = new ArrayList<>();

        sourceErrors.add("Source: ID: " + ID + ", Code: " + sourceResponse.getStatusCode() + ", " + sourceBody);
        targetErrors.add("Target: ID: " + ID + ", Code: " + targetResponse.getStatusCode() + ", " + targetBody);

        if (sourceResponse.getStatusCode() != targetResponse.getStatusCode() || !sourceBody.equals(targetBody)){
            System.out.println(String.join(", ", sourceErrors) + "\n" + String.join(", ", targetErrors));
            return false;
        }
        return true;
    }

    @BeforeEach
    void setUp() {
        responseKlassApiSourceHost = getResponse(klassApSourceHostPath);
        responseKlassApiTargetHost = getResponse(klassApiTargetHostPath);
        sourceHostClassificationsPage = responseKlassApiSourceHost.path(EMBEDDED_CLASSIFICATIONS);
        targetHostClassificationsPage = responseKlassApiTargetHost.path(EMBEDDED_CLASSIFICATIONS);
        getAllSourceHost();
        getAllTargetHost();
        setClassificationLists();
    }

    @AfterEach
    public void cleanUpEach(){
        System.out.println("Cleanup after test");
    }
}
