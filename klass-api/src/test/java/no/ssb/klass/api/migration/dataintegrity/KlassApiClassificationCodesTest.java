package no.ssb.klass.api.migration.dataintegrity;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.curator.shaded.com.google.common.util.concurrent.RateLimiter;
import org.junit.jupiter.api.Test;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiClassificationCodesTest extends AbstractKlassApiDataIntegrityTest {

    RateLimiter limiter = RateLimiter.create(1.5);

    @Test
    void getClassificationCodes1Test() {
        for (Integer integer : classificationsIdsSourceHostPart1) {
            limiter.acquire();

            Response sourceResponse = getCodesResponse(klassApSourceHostPath, integer,"2019-01-01", null );
            Response targetResponse = getCodesResponse(klassApiTargetHostPath, integer,"2019-01-01", null );

            if(sourceResponse.getStatusCode() == 404) {
                System.out.println(integer);
                assertThat(targetResponse.getStatusCode()).isEqualTo(404);
            }
            else{
                assertThat(sourceResponse.getStatusCode()).isEqualTo(200);
                assertThat(targetResponse.getStatusCode()).isEqualTo(200);
            }
        }
    }

    @Test
    void getClassificationCodes2Test() {
        for (Integer integer : classificationsIdsSourceHostPart2) {
            limiter.acquire();
            Response sourceResponse = getCodesResponse(klassApSourceHostPath, integer,"2019-01-01", null );
            Response targetResponse = getCodesResponse(klassApiTargetHostPath, integer,"2019-01-01", null );

            if(sourceResponse.getStatusCode() == 404) {
                System.out.println(integer);
                assertThat(targetResponse.getStatusCode()).isEqualTo(404);
            }
            else{
                assertThat(sourceResponse.getStatusCode()).isEqualTo(200);
                assertThat(targetResponse.getStatusCode()).isEqualTo(200);
            }

        }

    }
    @Test
    void getClassificationCodes3Test() {

        for (Integer integer : classificationsIdsSourceHostPart3) {
            limiter.acquire();
            Response sourceResponse = getCodesResponse(klassApSourceHostPath, integer,"2019-01-01", null );
            Response targetResponse = getCodesResponse(klassApiTargetHostPath, integer,"2019-01-01", null );

            if(sourceResponse.getStatusCode() == 404) {
                System.out.println(integer);
                assertThat(targetResponse.getStatusCode()).isEqualTo(404);
            }
            else{
                assertThat(sourceResponse.getStatusCode()).isEqualTo(200);
                assertThat(targetResponse.getStatusCode()).isEqualTo(200);
            }
        }

    }

    @Test
    void getClassificationCodes4Test() {
        for (Integer integer : classificationsIdsSourceHostPart4) {
            limiter.acquire();
            Response sourceResponse = getCodesResponse(klassApSourceHostPath, integer,"2019-01-01", null );
            Response targetResponse = getCodesResponse(klassApiTargetHostPath, integer,"2019-01-01", null );

            if(sourceResponse.getStatusCode() == 404) {
                System.out.println(integer);
                assertThat(targetResponse.getStatusCode()).isEqualTo(404);
            }
            else{
                assertThat(sourceResponse.getStatusCode()).isEqualTo(200);
                assertThat(targetResponse.getStatusCode()).isEqualTo(200);
            }
        }

    }

    private Response getCodesResponse(String basePath, Integer id, String dateFrom, String dateTo) {

        if (dateTo == null) {
            return RestAssured.given().queryParam(RANGE_FROM, dateFrom).get(basePath + "/" + id + "/" + CODES);
        }
        else{
            return RestAssured.given().queryParam(RANGE_FROM, dateFrom).queryParam(RANGE_TO, dateTo).get(basePath + "/" + id + "/" + CODES);
        }
    }
}
