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

        String path = getVersionByIdPath(randomId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, null);


        assertThat(sourceResponse.getStatusCode()).withFailMessage(
                FAIL_MESSAGE, path, sourceResponse.getStatusCode(), targetResponse.getStatusCode()).isEqualTo(targetResponse.getStatusCode());

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

        String path = getVersionByIdPath(randomId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi( path, params);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi( path, params);

        assertThat(sourceResponse.getStatusCode()).withFailMessage(
                FAIL_MESSAGE, path, sourceResponse.getStatusCode(), targetResponse.getStatusCode()).isEqualTo(targetResponse.getStatusCode());

        if(sourceResponse.getStatusCode() != 200) {
            System.out.println(LOG_MESSAGE_STATUS_CODE + sourceResponse.getStatusCode());
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
    void getVersionByIdNewNorwegianLanguage() {

        Map<String, Object> params = new HashMap<>();
        params.put(LANGUAGE, NN);

        String path = getVersionByIdPath(randomId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, params);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, params);

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

        String path = getVersionByIdPath(randomId);

        Response sourceResponse = klassApiMigrationClient.getFromSourceApi( path, params);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi( path, params);

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

    String getVersionByIdPath(Integer id) {
        return "/" + VERSIONS + "/" + id;
    }
}
