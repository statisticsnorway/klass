package no.ssb.klass.api.applicationtest;

import static com.jayway.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import no.ssb.klass.testutil.TestDataProvider;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import com.jayway.restassured.http.ContentType;

import no.ssb.klass.core.model.ClassificationFamily;
import no.ssb.klass.core.model.ClassificationVariant;
import no.ssb.klass.core.model.User;
import no.ssb.klass.core.util.DraftUtil;
import no.ssb.klass.api.util.RestConstants;
import no.ssb.klass.testutil.TestUtil;

/**
 * @author Mads Lundemo, SSB.
 */
public class RestApiDraftIntegrationTest extends AbstractRestApiApplicationTest {

    private ClassificationVariant variant;

    @Before
    public void addDrafts() {
        applicationTestUtil.clearDatabase();
        applicationTestUtil.clearSearch();

        User user = userRepository.save(TestUtil.createUser());
        ClassificationFamily classificationFamily = TestUtil.createClassificationFamily("Befolkning");
        classificationFamily = classificationFamilyRepository.save(classificationFamily);

        kommuneinndeling = TestDataProvider.createClassificationKommuneinndeling();
        kommuneinndeling.setContactPerson(user);
        kommuneinndeling.getClassificationVersions().forEach(version -> version.setDateRange(DraftUtil
                .getDraftDateRange()));

        bydelsinndeling = TestDataProvider.createClassificationBydelsinndeling();
        bydelsinndeling.setContactPerson(user);
        bydelsinndeling.getClassificationVersions().get(0).setDateRange(DraftUtil.getDraftDateRange());

        familieGrupperingCodelist = TestDataProvider.createFamiliegrupperingCodelist(user);
        familieGrupperingCodelist.setContactPerson(user);
        variant = familieGrupperingCodelist.getClassificationVersions().get(0).getClassificationVariants().get(0);
        variant.setDateRange(DraftUtil.getDraftDateRange());

        classificationFamily.addClassificationSeries(kommuneinndeling);
        classificationFamily.addClassificationSeries(bydelsinndeling);
        classificationFamily.addClassificationSeries(familieGrupperingCodelist);

        kommuneinndeling = classificationService.saveAndIndexClassification(kommuneinndeling);
        bydelsinndeling = classificationService.saveAndIndexClassification(bydelsinndeling);
        classificationService.saveAndIndexClassification(familieGrupperingCodelist);

        correspondenceTableRepository.save(TestDataProvider.createAndAddCorrespondenceTable(kommuneinndeling,
                bydelsinndeling));
        correspondenceTableRepository.save(TestDataProvider.createAndAddChangeCorrespondenceTable(kommuneinndeling));
    }

    // @formatter:off
    @Test
    public void testThatDraftVersionIsNotExposed() {
        given().port(port).accept(ContentType.JSON).get(REQUEST + "/" + kommuneinndeling.getId())
//                .prettyPeek()
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                // links
                .body("versions", hasSize(0));
    }
    @Test
    public void testThatDraftVersionIncludeFutureVersionIsNotExposed() {
        given().port(port).accept(ContentType.JSON).param("includeFuture", true).get(REQUEST + "/" + kommuneinndeling.getId())
                .prettyPeek()
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                // links
                .body("versions", hasSize(0));

    }
    @Test
    public void testThatDraftVariantIsNotExposed() {
        given().port(port).accept(ContentType.JSON)
                .get(RestConstants.API_VERSION_V1 + "/versions/" + familieGrupperingCodelist.getClassificationVersions().get(0).getId())
//                .prettyPeek()
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                // links
                .body("classificationVariants", hasSize(0));

    }
    @Test
    public void testThatDraftVariantIncludeFutureVersionIsNotExposed() {
        given().port(port).accept(ContentType.JSON).param("includeFuture", true)
                .get(RestConstants.API_VERSION_V1 + "/versions/" + familieGrupperingCodelist.getClassificationVersions().get(0).getId())
//                .prettyPeek()
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                // links
                .body("classificationVariants", hasSize(0));

    }

    @Test
    public void testThatDraftCorrespondenceIsNotExposed() {
        given().port(port).accept(ContentType.JSON)
                .get(RestConstants.API_VERSION_V1 + "/versions/" + kommuneinndeling.getClassificationVersions().get(0).getId())
//                .prettyPeek()
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                // links
                .body("correspondenceTables", hasSize(0));
    }

    @Test
    public void testThatDraftCorrespondenceIncludeFutureVersionIsNotExposed() {
        given().port(port).accept(ContentType.JSON).param("includeFuture", true)
                .get(RestConstants.API_VERSION_V1 + "/versions/" + kommuneinndeling.getClassificationVersions().get(0).getId())
//                .prettyPeek()
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                // links
                .body("correspondenceTables", hasSize(0));
    }
// @formatter:on
}
