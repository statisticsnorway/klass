package no.ssb.klass.api.migration.dataintegrity;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.curator.shaded.com.google.common.util.concurrent.RateLimiter;
import org.junit.jupiter.api.Test;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiClassificationCodesAtTest extends AbstractKlassApiDataIntegrityTest{

    RateLimiter limiter = RateLimiter.create(1.5);

    String date = "2024-01-01";

    @Test
    void getClassificationCodesAtPartOne() {

        for (Integer id : classificationsIdsSourceHostPart1) {

            limiter.acquire();

            Response sourceResponse = getCodesAtResponse(klassApSourceHostPath, id,date);
            Response targetResponse = getCodesAtResponse(klassApiTargetHostPath, id,date);

            if(sourceResponse.getStatusCode() != 200) {
                assertThat(compareError(id, sourceResponse, targetResponse)).isTrue();
            }
            else{
                Object sourceField = sourceResponse.path(CODES);
                assertThat(sourceField).isEqualTo(targetResponse.path(CODES));
            }
        }

    }

    @Test
    void getClassificationCodesAtPartTwo() {
        for (Integer id : classificationsIdsSourceHostPart2) {
            limiter.acquire();

            Response sourceResponse = getCodesAtResponse(klassApSourceHostPath, id,date);
            Response targetResponse = getCodesAtResponse(klassApiTargetHostPath, id,date);

            if(sourceResponse.getStatusCode() != 200) {
                assertThat(compareError(id, sourceResponse, targetResponse)).isTrue();
            }
            else{
                Object sourceField = sourceResponse.path(CODES);
                assertThat(sourceField).isEqualTo(targetResponse.path(CODES));
            }
        }

    }

    @Test
    void getClassificationCodesAtPartThree() {
        for (Integer id : classificationsIdsSourceHostPart3) {

            limiter.acquire();

            Response sourceResponse = getCodesAtResponse(klassApSourceHostPath, id,date);
            Response targetResponse = getCodesAtResponse(klassApiTargetHostPath, id,date);

            if(sourceResponse.getStatusCode() != 200) {
                assertThat(compareError(id, sourceResponse, targetResponse)).isTrue();
            }
            else{
                Object sourceField = sourceResponse.path(CODES);
                assertThat(sourceField).isEqualTo(targetResponse.path(CODES));
            }
        }

    }

    @Test
    void getClassificationCodesAtPartFour() {
        for (Integer id : classificationsIdsSourceHostPart4) {
            limiter.acquire();

            Response sourceResponse = getCodesAtResponse(klassApSourceHostPath, id,date);
            Response targetResponse = getCodesAtResponse(klassApiTargetHostPath, id,date);

            if(sourceResponse.getStatusCode() != 200) {
                assertThat(compareError(id, sourceResponse, targetResponse)).isTrue();
            }
            else{
                Object sourceField = sourceResponse.path(CODES);
                assertThat(sourceField).isEqualTo(targetResponse.path(CODES));
            }
        }

    }

    private Response getCodesAtResponse(String basePath, Integer id, String date) {

        return RestAssured.given().queryParam(DATE, date).get(basePath + "/" + id + "/" + CODES_AT);

    }
}
