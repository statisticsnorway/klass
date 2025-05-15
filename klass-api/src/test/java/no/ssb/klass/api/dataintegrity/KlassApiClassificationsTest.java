package no.ssb.klass.api.dataintegrity;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import no.ssb.klass.api.util.RestConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiClassificationsTest {
    final String basePath = "/api/klass";
    private Response responseKlassApiMariaDB;
    private Response responseKlassApiPostgresDB;
    private final String klassApiMariaDBHost = "http://localhost:8081";
    private final String klassApiPostgresDBHost = "http://localhost:8080";
    public String klassApiMariaDBPath = klassApiMariaDBHost + basePath + RestConstants.API_VERSION_V1 + "/classifications";
    public String klassApiPostgresDBPath = klassApiPostgresDBHost + basePath + RestConstants.API_VERSION_V1 +  "/classifications";

    @BeforeEach
    void setUp() {
        responseKlassApiMariaDB = RestAssured.get(klassApiMariaDBPath);
        responseKlassApiPostgresDB = RestAssured.get(klassApiPostgresDBPath);
    }

    @Test
    void getClassificationsResultIsEqualSize() {
        assertThat(responseKlassApiMariaDB.getStatusCode()).isEqualTo(200);
        assertThat(responseKlassApiPostgresDB.getStatusCode()).isEqualTo(200);
        int klassApiMariaDBTotalElements = responseKlassApiMariaDB.path("page.totalElements");
        int klassApiPostgresDBTotalElements = responseKlassApiPostgresDB.path("page.totalElements");
        assertThat(klassApiMariaDBTotalElements).isEqualTo(klassApiPostgresDBTotalElements);
    }

}
