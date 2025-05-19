package no.ssb.klass.api.migration.dataintegrity;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import no.ssb.klass.api.migration.MigrationTestConstants;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public class KlassApiClassificationItemTest extends AbstractKlassApiDataIntegrityTest {
    Response responseKlassApiSourceHostClassification;
    Response responseKlassApiTargetHostClassification;

    @Test
    void getClassificationTest(){
        for (int i = 0; i < Math.min(classificationsIdsSourceHost.size(), classificationsIdsTargetHost.size()); i++) {
            Integer idSourceHost = classificationsIdsSourceHost.get(i);
            Integer idTargetHost = classificationsIdsTargetHost.get(i);

            responseKlassApiSourceHostClassification = RestAssured.get(klassApSourceHostPath + "/" + idSourceHost);
            responseKlassApiTargetHostClassification = RestAssured.get(klassApiTargetHostPath + "/" + idTargetHost);
            responseKlassApiSourceHostClassification.then().assertThat().statusCode(200);
            responseKlassApiTargetHostClassification.then().assertThat().statusCode(200);

            for(String pathName: MigrationTestConstants.pathNames){
                Object sourceField = responseKlassApiSourceHostClassification.path(pathName);
                Object targetField = responseKlassApiTargetHostClassification.path(pathName);
                System.out.println(sourceField);
                System.out.println(targetField);
                assertThat(sourceField).isEqualTo(targetField);
            }
        }
    }

    @Test
    void getClassificationVersionsTest(){

    }

    @Test
    void getClassificationLinksTest(){

    }
}
