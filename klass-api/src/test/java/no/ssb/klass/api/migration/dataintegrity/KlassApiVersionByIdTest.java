package no.ssb.klass.api.migration.dataintegrity;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiVersionByIdTest extends AbstractKlassApiDataIntegrityTest{

    static Integer randomId;

    @BeforeAll
    static void beforeAll() {
        randomId = generateRandomId();
    }

    @Test
    void getVersionById() {
        Response sourceResponse = getVersionByIdResponse(sourceBasePath, randomId);
        Response targetResponse = getVersionByIdResponse(targetBasePath, randomId);

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
        Response sourceResponse = getVersionByIdLanguageResponse(sourceBasePath, randomId, EN);
        Response targetResponse = getVersionByIdLanguageResponse(targetBasePath, randomId, EN);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(randomId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            System.out.println("tested");
        }
    }

    @Test
    void getVersionByIdNewNorwegianLanguage() {
        Response sourceResponse = getVersionByIdLanguageResponse(sourceBasePath, randomId, NN);
        Response targetResponse = getVersionByIdLanguageResponse(targetBasePath, randomId, NN);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(randomId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            System.out.println("tested");
        }
    }

    @Test
    void getVersionByIdIncludingFuture() {
        Response sourceResponse = getVersionByIdIncludeFutureResponse(sourceBasePath, randomId);
        Response targetResponse = getVersionByIdIncludeFutureResponse(targetBasePath, randomId);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(randomId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            System.out.println("tested");
        }
    }


    private Response getVersionByIdResponse(String basePath, Integer id) {
        return RestAssured.get(basePath + "/" + id);
    }

    private Response getVersionByIdLanguageResponse(String basePath, Integer id, String language) {
        return RestAssured.given().queryParam("language", language).get(basePath + "/" + id);
    }

    private Response getVersionByIdIncludeFutureResponse(String basePath, Integer id) {
        return RestAssured.given().queryParam(INCLUDE_FUTURE, TRUE).get(basePath + "/" + id);
    }

}
