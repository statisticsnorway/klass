package no.ssb.klass.api.dataintegrity;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import no.ssb.klass.api.util.RestConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class KlassApiClassificationsTest {
    final String basePath = "/api/klass";
    private Response responseKlassApiMariaDB;
    private Response responseKlassApiPostgresDB;
    private final String klassApiMariaDBHost = "http://localhost:8081";
    private final String klassApiPostgresDBHost = "http://localhost:8080";
    public String klassApiMariaDBPath = klassApiMariaDBHost + basePath + RestConstants.API_VERSION_V1 + "/classifications";
    public String klassApiPostgresDBPath = klassApiPostgresDBHost + basePath + RestConstants.API_VERSION_V1 +  "/classifications";
    List<Map<String, Object>> mariaDBClassificationsPage;
    List<Map<String, Object>> postgresDBClassificationsPage;

    @BeforeEach
    void setUp() {
        responseKlassApiMariaDB = RestAssured.get(klassApiMariaDBPath);
        responseKlassApiPostgresDB = RestAssured.get(klassApiPostgresDBPath);
        mariaDBClassificationsPage = responseKlassApiMariaDB.path("_embedded.classifications");
        postgresDBClassificationsPage = responseKlassApiPostgresDB.path("_embedded.classifications");
    }

    @Test
    void getClassificationsResultIsEqualSize() {
        assumeTrue(responseKlassApiMariaDB.getStatusCode() == 200, "MariaDB API not available");
        assumeTrue(responseKlassApiPostgresDB.getStatusCode() == 200, "Postgres API not available");

        int klassApiMariaDBTotalElements = responseKlassApiMariaDB.path("page.totalElements");
        int klassApiPostgresDBTotalElements = responseKlassApiPostgresDB.path("page.totalElements");

        assertThat(klassApiMariaDBTotalElements).isEqualTo(klassApiPostgresDBTotalElements);
    }

    @Test
    void getClassificationsPageSizeIsEqual(){
        assumeTrue(responseKlassApiMariaDB.getStatusCode() == 200, "MariaDB API not available");
        assumeTrue(responseKlassApiPostgresDB.getStatusCode() == 200, "Postgres API not available");

        assertThat(mariaDBClassificationsPage.size()).isEqualTo(postgresDBClassificationsPage.size());
        String mariaDBClassificationsPage = responseKlassApiMariaDB.path("_embedded.page");
        String postgresDBClassificationsPage = responseKlassApiPostgresDB.path("_embedded.page");
        assertThat(mariaDBClassificationsPage).isEqualTo(postgresDBClassificationsPage);
    }

    @Test
    void getClassificationsItems(){
        assumeTrue(responseKlassApiMariaDB.getStatusCode() == 200, "MariaDB API not available");
        assumeTrue(responseKlassApiPostgresDB.getStatusCode() == 200, "Postgres API not available");

        for (int i = 0; i < mariaDBClassificationsPage.size(); i++) {
            assertThat(
                    mariaDBClassificationsPage.get(i).get("name")).isEqualTo(
                            postgresDBClassificationsPage.get(i).get("name"));
            assertThat(
                    mariaDBClassificationsPage.get(i).get("id")).isEqualTo(
                            postgresDBClassificationsPage.get(i).get("id"));
            assertThat(
                    mariaDBClassificationsPage.get(i).get("classificationType")).isEqualTo(
                            postgresDBClassificationsPage.get(i).get("classificationType"));

            Map<String, Object> classification = mariaDBClassificationsPage.get(i);
            assertThat(classification.get("_links")).isNotEqualTo(
                    postgresDBClassificationsPage.get(i).get("_links"));
        }

    }

}
