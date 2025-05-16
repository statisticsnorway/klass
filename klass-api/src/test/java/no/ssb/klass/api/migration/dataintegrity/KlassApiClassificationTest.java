package no.ssb.klass.api.migration.dataintegrity;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import no.ssb.klass.api.migration.MigrationTestConfig;
import no.ssb.klass.api.util.RestConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public class KlassApiClassificationTest {

    private final String sourceHost = MigrationTestConfig.getSourceHost();

    private final String targetHost = MigrationTestConfig.getTargetHost();

    private Response responseKlassApiMariaDB;
    private Response responseKlassApiPostgresDB;

    public String klassApiMariaDBPath = sourceHost + BASE_PATH + RestConstants.API_VERSION_V1 + CLASSIFICATIONS_PATH;
    public String klassApiPostgresDBPath = targetHost + BASE_PATH + RestConstants.API_VERSION_V1 + CLASSIFICATIONS_PATH;

    @BeforeEach
    void setUp() {
        responseKlassApiMariaDB = RestAssured.get(klassApiMariaDBPath + "/1");
        responseKlassApiPostgresDB = RestAssured.get(klassApiPostgresDBPath + "/1");
        responseKlassApiMariaDB.then().assertThat().statusCode(200);
        responseKlassApiPostgresDB.then().assertThat().statusCode(200);
    }

    @Test
    void getClassificationTest(){
        String nameMariaDB = responseKlassApiMariaDB.then().extract().path(NAME);
        String namePostgresDB = responseKlassApiPostgresDB.then().extract().path(NAME);
        assertThat(nameMariaDB).isEqualTo(namePostgresDB);
    }
}
