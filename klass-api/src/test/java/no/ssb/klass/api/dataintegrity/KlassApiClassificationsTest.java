package no.ssb.klass.api.dataintegrity;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import no.ssb.klass.api.util.RestConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiClassificationsTest {
    final String basePath = "/api/klass";
    private Response responseKlassApiMariaDB;
    private Response responseKlassApiPostgresDB;
    private final String klassApiMariaDBHost = "http://localhost:8081";
    private final String klassApiPostgresDBHost = "http://localhost:8080";
    public String klassApiMariaDBPath = klassApiMariaDBHost + basePath + RestConstants.API_VERSION_V1 + "/classifications";
    public String klassApiPostgresDBPath = klassApiPostgresDBHost + basePath + RestConstants.API_VERSION_V1 +  "/classifications";
    List<Map<String, Object>> mariaDBClassificationsPage0;
    List<Map<String, Object>> postgresDBClassificationsPage0;

    @BeforeEach
    void setUp() {
        responseKlassApiMariaDB = RestAssured.get(klassApiMariaDBPath);
        responseKlassApiPostgresDB = RestAssured.get(klassApiPostgresDBPath);
        mariaDBClassificationsPage0 = responseKlassApiMariaDB.path("_embedded.classifications");
        postgresDBClassificationsPage0 = responseKlassApiPostgresDB.path("_embedded.classifications");
    }

    @Test
    void getClassificationsResultIsEqualSize() {
        assertThat(responseKlassApiMariaDB.getStatusCode()).isEqualTo(200);
        assertThat(responseKlassApiPostgresDB.getStatusCode()).isEqualTo(200);
        int klassApiMariaDBTotalElements = responseKlassApiMariaDB.path("page.totalElements");
        int klassApiPostgresDBTotalElements = responseKlassApiPostgresDB.path("page.totalElements");
        assertThat(klassApiMariaDBTotalElements).isEqualTo(klassApiPostgresDBTotalElements);
    }

    @Test
    void getClassificationsPageSizeIsEqual(){
        assertThat(mariaDBClassificationsPage0.size()).isEqualTo(postgresDBClassificationsPage0.size());
    }

    @Test
    void getClassificationsNamesAreEqual(){
        for (int i = 0; i < mariaDBClassificationsPage0.size(); i++) {
            assertThat(mariaDBClassificationsPage0.get(i).get("name")).isEqualTo(postgresDBClassificationsPage0.get(i).get("name"));
        }

    }

    @Test
    void getClassificationsIdsAreEqual(){
        for (int i = 0; i < mariaDBClassificationsPage0.size(); i++) {
            assertThat(mariaDBClassificationsPage0.get(i).get("id")).isEqualTo(postgresDBClassificationsPage0.get(i).get("id"));
        }

    }

    @Test
    void getClassificationsClassificationTypesAreEqual(){
        for (int i = 0; i < mariaDBClassificationsPage0.size(); i++) {
            assertThat(mariaDBClassificationsPage0.get(i).get("classificationType")).isEqualTo(postgresDBClassificationsPage0.get(i).get("classificationType"));
        }

    }

    @Test
    void getClassificationsLastModifiedAreEqual(){
        for (int i = 0; i < mariaDBClassificationsPage0.size(); i++) {
            assertThat(mariaDBClassificationsPage0.get(i).get("lastModified")).isEqualTo(postgresDBClassificationsPage0.get(i).get("lastModified"));
        }

    }

    @Test
    void getClassificationsLinksAreNotEqual(){
        for (int i = 0; i < mariaDBClassificationsPage0.size(); i++) {
            Map<String, Object> classification = mariaDBClassificationsPage0.get(i);
            assertThat(classification.get("_links")).isNotEqualTo(postgresDBClassificationsPage0.get(i).get("_links"));
        }
    }

    @Test
    void getClassificationsChangedSinceAreEqual(){
    }

}
