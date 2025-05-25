package no.ssb.klass.api.migration.dataintegrity;

import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiVersionByIdTest extends AbstractKlassApiDataIntegrityTest{

    static Integer randomId;

    @BeforeAll
    static void beforeAllVersions() {
        randomId = generateRandomId(2000);
    }

    @Test
    void getVersionById() {
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi( "/versions/" + randomId, null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi( "/versions/" + randomId, null);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(randomId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            for (String pathName : pathNamesVersionsById) {
                Object sourceField = sourceResponse.path(pathName);

                assertThat(sourceField).isEqualTo(targetResponse.path(pathName));
            }
        }
    }

    @Test
    void getVersionByIdEnglishLanguage() {

        Map<String, Object> params = new HashMap<>();
        params.put(LANGUAGE, EN);

        Response sourceResponse = klassApiMigrationClient.getFromSourceApi( VERSIONS + "/" + randomId, params);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi( VERSIONS + "/" + randomId, params);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareErrorJsonResponse(randomId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            for (String pathName : pathNamesVersionsById) {
                Object sourceField = sourceResponse.path(pathName);

                assertThat(sourceField).isEqualTo(targetResponse.path(pathName));
            }
        }
    }

    @Test
    void getVersionByIdNewNorwegianLanguage() {

        Map<String, Object> params = new HashMap<>();
        params.put(LANGUAGE, NN);

        Response sourceResponse = klassApiMigrationClient.getFromSourceApi( VERSIONS + "/" + randomId, params);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi( VERSIONS + "/" + randomId, params);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareErrorJsonResponse(randomId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            for (String pathName : pathNamesVersionsById) {
                Object sourceField = sourceResponse.path(pathName);
                assertThat(sourceField).isEqualTo(targetResponse.path(pathName));
            }
        }
    }

    @Test
    void getVersionByIdIncludingFuture() {
        Map<String, Object> params = new HashMap<>();
        params.put(INCLUDE_FUTURE, TRUE);

        Response sourceResponse = klassApiMigrationClient.getFromSourceApi( VERSIONS + "/" + randomId, params);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi( VERSIONS + "/" + randomId, params);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareErrorJsonResponse(randomId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            for (String pathName : pathNamesVersionsById) {
                Object sourceField = sourceResponse.path(pathName);
                assertThat(sourceField).isEqualTo(targetResponse.path(pathName));
            }
        }
    }
}
