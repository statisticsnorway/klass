package no.ssb.klass.api.dataintegrity;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import no.ssb.klass.api.util.RestConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public class KlassApiClassificationTest {

    final String basePath = "/api/klass";
    private Response responseKlassApiMariaDB;
    private Response responseKlassApiPostgresDB;
    private final String klassApiMariaDBHost = "http://localhost:8081";
    private final String klassApiPostgresDBHost = "http://localhost:8080";
    public String klassApiMariaDBPath = klassApiMariaDBHost + basePath + RestConstants.API_VERSION_V1 + "/classifications";
    public String klassApiPostgresDBPath = klassApiPostgresDBHost + basePath + RestConstants.API_VERSION_V1 +  "/classifications";

    @BeforeEach
    void setUp() {
        responseKlassApiMariaDB = RestAssured.get(klassApiMariaDBPath + "/1");
        responseKlassApiPostgresDB = RestAssured.get(klassApiPostgresDBPath + "/1");
        responseKlassApiMariaDB.then().assertThat().statusCode(200);
        responseKlassApiPostgresDB.then().assertThat().statusCode(200);
    }

    @Test
    void getClassificationTest(){
        String nameMariaDB = responseKlassApiMariaDB.then().extract().path("name");
        String namePostgresDB = responseKlassApiPostgresDB.then().extract().path("name");
        assertThat(nameMariaDB).isEqualTo(namePostgresDB);
    }
}
