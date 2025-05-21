package no.ssb.klass.api.migration.dataintegrity;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.curator.shaded.com.google.common.util.concurrent.RateLimiter;
import org.junit.jupiter.api.Test;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiClassificationCodesAtTest extends AbstractKlassApiDataIntegrityTest{

    RateLimiter limiter = RateLimiter.create(1.5);

    @Test
    void getClassificationCodesAtPartOne() {

        for (Integer integer : classificationsIdsSourceHostPart1) {
            limiter.acquire();

            Response sourceResponse = getCodesAtResponse(klassApSourceHostPath, integer,"2024-01-01");
            Response targetResponse = getCodesAtResponse(klassApiTargetHostPath, integer,"2024-01-01");

            if(sourceResponse.getStatusCode() == 404) {
                System.out.println(integer);
                assertThat(targetResponse.getStatusCode()).isEqualTo(404);
            }
            else{
                assertThat(sourceResponse.getStatusCode()).isEqualTo(200);
                assertThat(targetResponse.getStatusCode()).isEqualTo(200);

                Object sourceField = sourceResponse.path(CODES);
                assertThat(sourceField).isEqualTo(targetResponse.path(CODES));
            }
        }

    }

    @Test
    void getClassificationCodesAtPartTwo() {
        for (Integer integer : classificationsIdsSourceHostPart2) {
            limiter.acquire();

            Response sourceResponse = getCodesAtResponse(klassApSourceHostPath, integer,"2024-01-01");
            Response targetResponse = getCodesAtResponse(klassApiTargetHostPath, integer,"2024-01-01");

            if(sourceResponse.getStatusCode() == 404) {
                System.out.println(integer);
                assertThat(targetResponse.getStatusCode()).isEqualTo(404);
            }
            else{
                assertThat(sourceResponse.getStatusCode()).isEqualTo(200);
                assertThat(targetResponse.getStatusCode()).isEqualTo(200);

                Object sourceField = sourceResponse.path(CODES);
                assertThat(sourceField).isEqualTo(targetResponse.path(CODES));
            }
        }

    }

    @Test
    void getClassificationCodesAtPartThree() {
        for (Integer integer : classificationsIdsSourceHostPart3) {
            limiter.acquire();

            Response sourceResponse = getCodesAtResponse(klassApSourceHostPath, integer,"2024-01-01");
            Response targetResponse = getCodesAtResponse(klassApiTargetHostPath, integer,"2024-01-01");

            if(sourceResponse.getStatusCode() == 404) {
                System.out.println(integer);
                assertThat(targetResponse.getStatusCode()).isEqualTo(404);
            }
            else{
                assertThat(sourceResponse.getStatusCode()).isEqualTo(200);
                assertThat(targetResponse.getStatusCode()).isEqualTo(200);

                Object sourceField = sourceResponse.path(CODES);
                assertThat(sourceField).isEqualTo(targetResponse.path(CODES));
            }
        }

    }

    @Test
    void getClassificationCodesAtPartFour() {
        for (Integer integer : classificationsIdsSourceHostPart4) {
            limiter.acquire();

            Response sourceResponse = getCodesAtResponse(klassApSourceHostPath, integer,"2024-01-01");
            Response targetResponse = getCodesAtResponse(klassApiTargetHostPath, integer,"2024-01-01");

            if(sourceResponse.getStatusCode() == 404) {
                System.out.println(integer);
                assertThat(targetResponse.getStatusCode()).isEqualTo(404);
            }
            else{
                assertThat(sourceResponse.getStatusCode()).isEqualTo(200);
                assertThat(targetResponse.getStatusCode()).isEqualTo(200);

                Object sourceField = sourceResponse.path(CODES);
                assertThat(sourceField).isEqualTo(targetResponse.path(CODES));
            }
        }

    }

    @Test
    void getClassificationCodesAtDateInFuturePartOne() {

        for (Integer integer : classificationsIdsSourceHostPart1) {
            limiter.acquire();

            Response sourceResponse = getCodesAtResponse(klassApSourceHostPath, integer,"2040-09-15");
            Response targetResponse = getCodesAtResponse(klassApiTargetHostPath, integer,"2040-09-15");

            if(sourceResponse.getStatusCode() == 404) {
                System.out.println(integer);
                assertThat(targetResponse.getStatusCode()).isEqualTo(404);
            }
            else{
                assertThat(sourceResponse.getStatusCode()).isEqualTo(200);
                assertThat(targetResponse.getStatusCode()).isEqualTo(200);

                Object sourceField = sourceResponse.path(CODES);
                assertThat(sourceField).isEqualTo(targetResponse.path(CODES));
            }
        }

    }

    private Response getCodesAtResponse(String basePath, Integer id, String date) {

        return RestAssured.given().queryParam(DATE, date).get(basePath + "/" + id + "/" + CODES_AT);

    }
}
