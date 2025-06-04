package no.ssb.klass.api.applicationtest;

import static io.restassured.RestAssured.*;
import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY;
import static org.hamcrest.Matchers.*;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import no.ssb.klass.core.config.ConfigurationProfiles;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.http.ContentType;

import no.ssb.klass.core.model.ClassificationType;
import org.springframework.test.context.ActiveProfiles;

/**
 * Testsuite that test the list (all) classificationFamilies
 */

@ActiveProfiles({ ConfigurationProfiles.POSTGRES_EMBEDDED, ConfigurationProfiles.MOCK_SEARCH })
@AutoConfigureEmbeddedDatabase(provider = ZONKY, type= AutoConfigureEmbeddedDatabase.DatabaseType.POSTGRES)
public class RestApiListClassificationFamilyIntegrationTest extends AbstractRestApiApplicationTest {
    // @formatter:off
    @Test
    public void restServiceListClassificationFamilies() {
        given().port(port).accept(ContentType.JSON).get(REQUEST_CLASSIFICATION_FAMILY)
//                .prettyPeek()
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("_embedded.classificationFamilies.size()", equalTo(1))
                .body("_embedded.classificationFamilies[0].name", equalTo(classificationFamily.getName()))
                .body("_embedded.classificationFamilies[0].numberOfClassifications", equalTo(classificationFamily
                        .getClassificationSeriesBySectionAndClassificationType(null, ClassificationType.CLASSIFICATION).size()))
                // links
                .body(JSON_LINKS + ".self.href", containsString(REQUEST_CLASSIFICATION_FAMILY));
    }

    @Test
    public void restServiceListClassificationFamiliesFiltersClassificationType() {
        given().port(port).accept(ContentType.JSON)
                .param("includeCodelists", "true")
                .get(REQUEST_CLASSIFICATION_FAMILY)
//                .prettyPeek()
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("_embedded.classificationFamilies.size()", equalTo(1))
                .body("_embedded.classificationFamilies[0].name", equalTo(classificationFamily.getName()))
                .body("_embedded.classificationFamilies[0].numberOfClassifications", equalTo(classificationFamily
                        .getClassificationSeriesBySectionAndClassificationType(null, null).size()))
                // links
                .body(JSON_LINKS + ".self.href", containsString(REQUEST_CLASSIFICATION_FAMILY));
    }

    @Test
    public void restServiceListClassificationFamiliesFiltersSsbSection() {
        final String ssbSection = "section";

        given().port(port).accept(ContentType.JSON)
                .param("ssbSection", ssbSection)
                .get(REQUEST_CLASSIFICATION_FAMILY)
//                .prettyPeek()
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("_embedded.classificationFamilies.size()", equalTo(1))
                .body("_embedded.classificationFamilies[0].name", equalTo(classificationFamily.getName()))
                .body("_embedded.classificationFamilies[0].numberOfClassifications", equalTo(classificationFamily
                        .getClassificationSeriesBySectionAndClassificationType(ssbSection, ClassificationType.CLASSIFICATION).size()))
                // links
                .body(JSON_LINKS + ".self.href", containsString(REQUEST_CLASSIFICATION_FAMILY));
    }
// @formatter:on
}
