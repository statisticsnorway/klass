package no.ssb.klass.api.migration.dataintegrity;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import no.ssb.klass.api.migration.MigrationTestConfig;
import no.ssb.klass.api.util.RestConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class KlassApiClassificationsTest {

    private final String sourceHost = MigrationTestConfig.getSourceHost();

    private final String targetHost = MigrationTestConfig.getTargetHost();

    private Response responseKlassApiMariaDB;
    private Response responseKlassApiPostgresDB;

    public String klassApiMariaDBPath = sourceHost + BASE_PATH + RestConstants.API_VERSION_V1 + CLASSIFICATIONS_PATH;
    public String klassApiPostgresDBPath = targetHost + BASE_PATH + RestConstants.API_VERSION_V1 + CLASSIFICATIONS_PATH;

    List<Map<String, Object>> mariaDBClassificationsPage;
    List<Map<String, Object>> postgresDBClassificationsPage;

    @BeforeEach
    void setUp() {
        responseKlassApiMariaDB = RestAssured.get(klassApiMariaDBPath);
        responseKlassApiPostgresDB = RestAssured.get(klassApiPostgresDBPath);
        mariaDBClassificationsPage = responseKlassApiMariaDB.path(EMBEDDED_CLASSIFICATIONS);
        postgresDBClassificationsPage = responseKlassApiPostgresDB.path(EMBEDDED_CLASSIFICATIONS);
        assertThat(sourceHost).isNotNull();
    }

    @Test
    void getClassificationsResultIsEqualSize() {
        assumeTrue(responseKlassApiMariaDB.getStatusCode() == 200, SOURCE_API_CHECK);
        assumeTrue(responseKlassApiPostgresDB.getStatusCode() == 200, TARGET_API_CHECK);

        int klassApiMariaDBTotalElements = responseKlassApiMariaDB.path(TOTAL_ELEMENTS);
        int klassApiPostgresDBTotalElements = responseKlassApiPostgresDB.path(TOTAL_ELEMENTS);

        assertThat(klassApiMariaDBTotalElements).isEqualTo(klassApiPostgresDBTotalElements);
    }

    @Test
    void getClassificationsPageSizeIsEqual(){
        assumeTrue(responseKlassApiMariaDB.getStatusCode() == 200, SOURCE_API_CHECK);
        assumeTrue(responseKlassApiPostgresDB.getStatusCode() == 200, TARGET_API_CHECK);

        assertThat(mariaDBClassificationsPage.size()).isEqualTo(postgresDBClassificationsPage.size());
        String mariaDBClassificationsPage = responseKlassApiMariaDB.path(EMBEDDED_PAGE);
        String postgresDBClassificationsPage = responseKlassApiPostgresDB.path(EMBEDDED_PAGE);
        assertThat(mariaDBClassificationsPage).isEqualTo(postgresDBClassificationsPage);
    }

    @Test
    void getClassificationsItems(){
        assumeTrue(responseKlassApiMariaDB.getStatusCode() == 200, SOURCE_API_CHECK);
        assumeTrue(responseKlassApiPostgresDB.getStatusCode() == 200, TARGET_API_CHECK);

        for (int i = 0; i < mariaDBClassificationsPage.size(); i++) {
            assertThat(
                    mariaDBClassificationsPage.get(i).get(NAME)).isEqualTo(
                            postgresDBClassificationsPage.get(i).get(NAME));
            assertThat(
                    mariaDBClassificationsPage.get(i).get(ID)).isEqualTo(
                            postgresDBClassificationsPage.get(i).get(ID));
            assertThat(
                    mariaDBClassificationsPage.get(i).get(CLASSIFICATION_TYPE)).isEqualTo(
                            postgresDBClassificationsPage.get(i).get(CLASSIFICATION_TYPE));

            Map<String, Object> classification = mariaDBClassificationsPage.get(i);
            assertThat(classification.get(_LINKS)).isNotEqualTo(
                    postgresDBClassificationsPage.get(i).get(_LINKS));
        }

    }

}
