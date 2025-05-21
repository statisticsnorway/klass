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
    void getClassificationCodesFromDatePartOneTest() {
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

                Object sourceField = sourceResponse.path(CODES);
                assertThat(sourceField).isEqualTo(targetResponse.path(CODES));
            }
        }
    }

    @Test
    void getClassificationCodesFromDatePartTwoTest() {
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

                Object sourceField = sourceResponse.path(CODES);
                assertThat(sourceField).isEqualTo(targetResponse.path(CODES));
            }

        }

    }
    @Test
    void getClassificationCodesFromDatePartThreeTest() {

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

                Object sourceField = sourceResponse.path(CODES);
                assertThat(sourceField).isEqualTo(targetResponse.path(CODES));
            }
        }

    }

    @Test
    void getClassificationCodesFromDatePartFourTest() {
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

                Object sourceField = sourceResponse.path(CODES);
                assertThat(sourceField).isEqualTo(targetResponse.path(CODES));
            }
        }

    }

    @Test
    void getClassificationCodesInRangeDatePartOneTest() {
        for (Integer integer : classificationsIdsSourceHostPart1) {
            limiter.acquire();

            Response sourceResponse = getCodesResponse(klassApSourceHostPath, integer,"1994-11-23", "2002-05-20");
            Response targetResponse = getCodesResponse(klassApiTargetHostPath, integer,"1994-11-23", "2002-05-20");

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
    void getClassificationCodesInRangeDatePartTwoTest() {
        for (Integer integer : classificationsIdsSourceHostPart2) {
            limiter.acquire();
            Response sourceResponse = getCodesResponse(klassApSourceHostPath, integer,"1994-11-23", "2002-05-20");
            Response targetResponse = getCodesResponse(klassApiTargetHostPath, integer,"1994-11-23", "2002-05-20");

            if(sourceResponse.getStatusCode() == 404) {
                System.out.println(integer);
                assertThat(targetResponse.getStatusCode()).isEqualTo(404);
            }
            if(sourceResponse.getStatusCode() == 400) {
                System.out.println(integer);
                assertThat(targetResponse.getStatusCode()).isEqualTo(400);
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
    void getClassificationCodesInRangeDatePartThreeTest() {

        for (Integer integer : classificationsIdsSourceHostPart3) {
            limiter.acquire();
            Response sourceResponse = getCodesResponse(klassApSourceHostPath, integer,"1994-11-23", "2002-05-20");
            Response targetResponse = getCodesResponse(klassApiTargetHostPath, integer,"1994-11-23", "2002-05-20");

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
    void getClassificationCodesInRangeDatePartFourTest() {
        for (Integer integer : classificationsIdsSourceHostPart4) {
            limiter.acquire();
            Response sourceResponse = getCodesResponse(klassApSourceHostPath, integer,"1994-11-23", "2002-05-20");
            Response targetResponse = getCodesResponse(klassApiTargetHostPath, integer,"1994-11-23", "2002-05-20");

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
    void getClassificationCodesInWrongDateOrderPartOneTest() {
        for (Integer integer : classificationsIdsSourceHostPart1) {
            limiter.acquire();

            Response sourceResponse = getCodesResponse(klassApSourceHostPath, integer,"2025-10-23", "1970-04-02");
            Response targetResponse = getCodesResponse(klassApiTargetHostPath, integer,"2025-10-23", "1970-04-02");

            assertThat(sourceResponse.getStatusCode()).isEqualTo(400);
            assertThat(targetResponse.getStatusCode()).isEqualTo(400);
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
