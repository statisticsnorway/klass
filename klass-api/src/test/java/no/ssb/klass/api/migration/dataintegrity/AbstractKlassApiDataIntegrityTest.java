package no.ssb.klass.api.migration.dataintegrity;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import no.ssb.klass.api.migration.MigrationTestConfig;
import no.ssb.klass.api.util.RestConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.get;
import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static no.ssb.klass.api.migration.MigrationTestConstants.EMBEDDED_CLASSIFICATIONS;

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

    @BeforeEach
    void setUp() {
        responseKlassApiSourceHost = get(klassApSourceHostPath);
        responseKlassApiTargetHost = get(klassApiTargetHostPath);
        sourceHostClassificationsPage = responseKlassApiSourceHost.path(EMBEDDED_CLASSIFICATIONS);
        targetHostClassificationsPage = responseKlassApiTargetHost.path(EMBEDDED_CLASSIFICATIONS);
        getAllSourceHost();
        getAllTargetHost();
    }

    @AfterEach
    public void cleanUpEach(){
        System.out.println("Cleanup after test");
    }
}
