package no.ssb.klass.api.migration.dataintegrity;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.curator.shaded.com.google.common.util.concurrent.RateLimiter;
import org.junit.jupiter.api.Test;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiClassificationCodesTest extends AbstractKlassApiDataIntegrityTest {

    RateLimiter limiter = RateLimiter.create(1.5);

    String dateFromToMax = "2019-01-01";
    String dateFromInRange = "1994-11-23";
    String dateToInRange = "2002-05-20";

    @Test
    void getClassificationCodesFromDatePartOneTest() {
        for (Integer id : classificationsIdsSourceHostPart1) {

            limiter.acquire();

            Response sourceResponse = getCodesResponse(klassApSourceHostPath, id, dateFromToMax, null );
            Response targetResponse = getCodesResponse(klassApiTargetHostPath, id, dateFromToMax, null );

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
    void getClassificationCodesFromDatePartTwoTest() {
        for (Integer id : classificationsIdsSourceHostPart2) {

            limiter.acquire();

            Response sourceResponse = getCodesResponse(klassApSourceHostPath, id, dateFromToMax, null );
            Response targetResponse = getCodesResponse(klassApiTargetHostPath, id, dateFromToMax, null );

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
    void getClassificationCodesFromDatePartThreeTest() {

        for (Integer id : classificationsIdsSourceHostPart3) {

            limiter.acquire();

            Response sourceResponse = getCodesResponse(klassApSourceHostPath, id, dateFromToMax, null );
            Response targetResponse = getCodesResponse(klassApiTargetHostPath, id, dateFromToMax, null );

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
    void getClassificationCodesFromDatePartFourTest() {
        for (Integer id : classificationsIdsSourceHostPart4) {

            limiter.acquire();

            Response sourceResponse = getCodesResponse(klassApSourceHostPath, id, dateFromToMax, null );
            Response targetResponse = getCodesResponse(klassApiTargetHostPath, id, dateFromToMax, null );

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
    void getClassificationCodesInRangeDatePartOneTest() {
        for (Integer id : classificationsIdsSourceHostPart1) {

            limiter.acquire();

            Response sourceResponse = getCodesResponse(klassApSourceHostPath, id,dateFromInRange, dateToInRange);
            Response targetResponse = getCodesResponse(klassApiTargetHostPath, id,dateFromInRange, dateToInRange);

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
    void getClassificationCodesInRangeDatePartTwoTest() {
        for (Integer id : classificationsIdsSourceHostPart2) {

            limiter.acquire();

            Response sourceResponse = getCodesResponse(klassApSourceHostPath, id,dateFromInRange, dateToInRange);
            Response targetResponse = getCodesResponse(klassApiTargetHostPath, id,dateFromInRange, dateToInRange);

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
    void getClassificationCodesInRangeDatePartThreeTest() {
        for (Integer id : classificationsIdsSourceHostPart3) {

            limiter.acquire();

            Response sourceResponse = getCodesResponse(klassApSourceHostPath, id,dateFromInRange, dateToInRange);
            Response targetResponse = getCodesResponse(klassApiTargetHostPath, id,dateFromInRange, dateToInRange);

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
    void getClassificationCodesInRangeDatePartFourTest() {
        for (Integer id : classificationsIdsSourceHostPart4) {

            limiter.acquire();

            Response sourceResponse = getCodesResponse(klassApSourceHostPath, id,dateFromInRange, dateToInRange);
            Response targetResponse = getCodesResponse(klassApiTargetHostPath, id,dateFromInRange, dateToInRange);

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
    void getClassificationCodesInWrongDateOrderPartOneTest() {
        for (Integer id : classificationsIdsSourceHostPart1) {

            limiter.acquire();

            Response sourceResponse = getCodesResponse(klassApSourceHostPath, id,dateFromInRange, dateToInRange);
            Response targetResponse = getCodesResponse(klassApiTargetHostPath, id,dateFromInRange, dateToInRange);

            if(sourceResponse.getStatusCode() != 200) {
                assertThat(compareError(id, sourceResponse, targetResponse)).isTrue();
            }
            else{
                Object sourceField = sourceResponse.path(CODES);
                assertThat(sourceField).isEqualTo(targetResponse.path(CODES));
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
