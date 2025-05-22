package no.ssb.klass.api.migration.dataintegrity;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.curator.shaded.com.google.common.util.concurrent.RateLimiter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiClassificationVariantTest extends AbstractKlassApiDataIntegrityTest {


    RateLimiter limiter = RateLimiter.create(1.5);

    static Integer randomId;

    /*
    optional
        to
        selectCodes
        selectLevel
        presentationNamePattern

        csv
            csvSeparator
            csvFields

    random ids
    different queryParams
    valid values
    invalid values
    */

    String variantNameId84 = "Klimagasser";
    String variantDateId84 = "2015-01-01";
    String variantNameId7 = "Gruppering av lønnstakere med høyere utdanning (2 grupper)";
    String variantDateId7 = "2000-04-01";

    // Test 1 mandatory
    // Test 2 all but csv
    // Test 3 all
    // list of queries?

    @BeforeAll
    static void beforeAll() {
        randomId = generateRandomId();
    }

    @Test
    void getClassificationVariantPartOneTest() {

            limiter.acquire();

            Response sourceResponse = getCodesResponse(klassApSourceHostPath, randomId, variantNameId84, variantDateId84);
            Response targetResponse = getCodesResponse(klassApiTargetHostPath, randomId, variantNameId84, variantDateId84);

            if(sourceResponse.getStatusCode() != 200) {
                assertThat(compareError(randomId, sourceResponse, targetResponse)).isTrue();
            }
            else{
                Object sourceField = sourceResponse.path(CODES);
                assertThat(sourceField).isEqualTo(targetResponse.path(CODES));
            }
    }

    @Test
    void getClassificationVariantPartTest() {

        limiter.acquire();

        Response sourceResponse = getCodesResponse(klassApSourceHostPath, 7, variantNameId7, variantDateId7);
        Response targetResponse = getCodesResponse(klassApiTargetHostPath, 7, variantNameId7, variantDateId7);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(7, sourceResponse, targetResponse)).isTrue();
        }
        else{
            Object sourceField = sourceResponse.path(CODES);
            assertThat(sourceField).isEqualTo(targetResponse.path(CODES));
        }
    }

    private Response getCodesResponse(String basePath, Integer id, String name, String date) {

        return RestAssured.given().queryParam(VARIANT_NAME, name).queryParam(RANGE_FROM, date).get(basePath + "/" + id + "/" + "variant");

    }
}
