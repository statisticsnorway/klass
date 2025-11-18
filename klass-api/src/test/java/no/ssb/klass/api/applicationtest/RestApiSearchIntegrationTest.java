package no.ssb.klass.api.applicationtest;

import static io.restassured.RestAssured.given;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.*;

import io.restassured.http.ContentType;

import no.ssb.klass.api.applicationtest.config.ApplicationTestConfig;
import no.ssb.klass.api.applicationtest.config.IndexServiceTestConfig;
import no.ssb.klass.api.services.IndexServiceImpl;
import no.ssb.klass.core.config.ConfigurationProfiles;
import no.ssb.klass.testutil.TestDataProvider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opensearch.data.client.orhlc.OpenSearchRestTemplate;
import org.opensearch.testcontainers.OpensearchContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@SpringBootTest(
        classes = {ApplicationTestConfig.class, IndexServiceTestConfig.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(
        profiles = {
            ConfigurationProfiles.POSTGRES_EMBEDDED,
            ConfigurationProfiles.MOCK_MAILSERVER,
            ConfigurationProfiles.OPEN_SEARCH_LOCAL
        },
        inheritProfiles = false)
class RestApiSearchIntegrationTest extends AbstractRestApiApplicationTest {

    @Container
    @SuppressWarnings("resource") // Managed by Testcontainers
    protected static final OpensearchContainer<?> opensearchContainer =
            new OpensearchContainer<>(DockerImageName.parse("opensearchproject/opensearch:2.11.0"))
                    .withEnv("OPENSEARCH_JAVA_OPTS", "-Xms2g -Xmx2g")
                    .withEnv("discovery.type", "single-node")
                    .withReuse(true);

    @DynamicPropertySource
    static void registerOpenSearchProperties(DynamicPropertyRegistry registry) {
        String uri =
                "http://"
                        + opensearchContainer.getHost()
                        + ":"
                        + opensearchContainer.getMappedPort(9200);
        registry.add("opensearch.url", () -> uri);
    }

    @Autowired private IndexServiceImpl indexService;

    @Autowired
    @Qualifier("opensearchRestTemplate")
    private OpenSearchRestTemplate openSearchRestTemplate;

    @BeforeEach
    void setupSearchIndex() {

        IndexCoordinates indexCoords = IndexCoordinates.of("klass");
        try {
            openSearchRestTemplate.indexOps(indexCoords).delete();
        } catch (Exception e) {
            // Index might not exist yet, that's fine
        }

        indexService.createIndexWithStemmingAnalyzer();

        indexService.indexSync(kommuneinndeling);
        indexService.indexSync(bydelsinndeling);
        indexService.indexSync(familieGrupperingCodelist);
        indexService.indexSync(badmintonCodelist);
        indexService.indexSync(badminton);
        indexService.indexSync(sport);
        indexService.indexSync(icd);
        openSearchRestTemplate.indexOps(IndexCoordinates.of("klass")).refresh();
    }

    @Test
    void restServiceSearchClassificationsJSON() {
        // Should rank name with 'kommune' over 'kommune' in description
        given().port(port)
                .accept(ContentType.JSON)
                .param(QUERY, "kommune")
                .get(REQUEST_SEARCH)
                .prettyPeek()
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body(JSON_SEARCH_RESULTS + ".size()", equalTo(2))
                .body(
                        JSON_SEARCH_RESULT1 + ".name",
                        equalTo(TestDataProvider.KOMMUNEINNDELING_NAVN_NO))
                .body(JSON_SEARCH_RESULT1 + ".snippet", containsString("kommune"))
                .body(JSON_SEARCH_RESULT1 + ".searchScore", greaterThan(0.0f))
                .body(
                        JSON_SEARCH_RESULT1 + "._links.self.href",
                        containsString(REQUEST + "/" + kommuneinndeling.getId()))
                // result 2
                .body(
                        JSON_SEARCH_RESULT2 + ".name",
                        equalTo(TestDataProvider.BYDELSINNDELING_NAVN_NO))
                .body(JSON_SEARCH_RESULT2 + ".snippet", containsString("kommune"))
                .body(JSON_SEARCH_RESULT2 + ".searchScore", greaterThan(0.0f))
                .body(
                        JSON_SEARCH_RESULT2 + "._links.self.href",
                        containsString(REQUEST + "/" + bydelsinndeling.getId()))
                // footer
                .body(JSON_LINKS + ".self.href", containsString(REQUEST_SEARCH))
                .body(JSON_PAGE + ".size", equalTo(PAGE_SIZE))
                .body(JSON_PAGE + ".totalElements", equalTo(2))
                .body(JSON_PAGE + ".totalPages", equalTo(1))
                .body(JSON_PAGE + ".number", equalTo(0));
    }

    @Test
    void restServiceSearchFuzzyNameJSON() {
        given().port(port)
                .accept(ContentType.JSON)
                .param(QUERY, "kommu")
                .get(REQUEST_SEARCH)
                .prettyPeek()
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body(
                        JSON_SEARCH_RESULTS + ".name",
                        hasItem(TestDataProvider.KOMMUNEINNDELING_NAVN_NO));

        given().port(port)
                .accept(ContentType.JSON)
                .param(QUERY, "kommun")
                .get(REQUEST_SEARCH)
                .prettyPeek()
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body(
                        JSON_SEARCH_RESULTS + ".name",
                        hasItem(TestDataProvider.KOMMUNEINNDELING_NAVN_NO));

        given().port(port)
                .accept(ContentType.JSON)
                .param(QUERY, "komm")
                .get(REQUEST_SEARCH)
                .prettyPeek()
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body(
                        JSON_SEARCH_RESULTS + ".name",
                        hasItem(TestDataProvider.KOMMUNEINNDELING_NAVN_NO));

        given().port(port)
                .accept(ContentType.JSON)
                .param(QUERY, "kommune")
                .get(REQUEST_SEARCH)
                .prettyPeek()
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body(
                        JSON_SEARCH_RESULTS + ".name",
                        hasItem(TestDataProvider.KOMMUNEINNDELING_NAVN_NO));
    }

    @Test
    void restServiceSearchFuzzyDescriptionJSON() {
        // 'Badminton' has the word 'sport' only in description
        given().port(port)
                .accept(ContentType.JSON)
                .param(QUERY, "sport")
                .get(REQUEST_SEARCH)
                .prettyPeek()
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body(JSON_SEARCH_RESULTS + ".name", hasItem(TestDataProvider.BADMINTON_NAVN_NO));

        given().port(port)
                .accept(ContentType.JSON)
                .param(QUERY, "spor")
                .get(REQUEST_SEARCH)
                .prettyPeek()
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body(JSON_SEARCH_RESULTS + ".name", hasItem(TestDataProvider.BADMINTON_NAVN_NO));

        given().port(port)
                .accept(ContentType.JSON)
                .param(QUERY, "spo")
                .get(REQUEST_SEARCH)
                .prettyPeek()
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body(
                        JSON_SEARCH_RESULTS + ".name",
                        not(hasItem(TestDataProvider.BADMINTON_NAVN_NO)));
    }

    @Test
    void restServiceSearchNotDisplayCopyrighted() {
        assertThat(icd.isCopyrighted()).isTrue();
        given().port(port)
                .accept(ContentType.JSON)
                .param(QUERY, "ICD")
                .param(INCLUDE_CODE_LISTS, "true")
                .get(REQUEST_SEARCH)
                .prettyPeek()
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body(JSON_PAGE + ".totalElements", equalTo(0));
    }

    @Test
    void restServiceSearchExactNameJSON() {
        given().port(port)
                .accept(ContentType.JSON)
                .param(QUERY, "kommuneinndeling")
                .get(REQUEST_SEARCH)
                .prettyPeek()
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body(JSON_PAGE + ".totalElements", equalTo(1));
    }

    @Test
    void restServiceSearchCodesJSON() {
        // 'Kommuneinndeling' has code "0101", "Halden"
        given().port(port)
                .accept(ContentType.JSON)
                .param(QUERY, "0101")
                .get(REQUEST_SEARCH)
                .prettyPeek()
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body(JSON_PAGE + ".totalElements", equalTo(1))
                .body(
                        JSON_SEARCH_RESULTS + ".name",
                        hasItem(TestDataProvider.KOMMUNEINNDELING_NAVN_NO));

        given().port(port)
                .accept(ContentType.JSON)
                .param(QUERY, "Halden")
                .get(REQUEST_SEARCH)
                .prettyPeek()
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body(JSON_PAGE + ".totalElements", equalTo(1))
                .body(
                        JSON_SEARCH_RESULTS + ".name",
                        hasItem(TestDataProvider.KOMMUNEINNDELING_NAVN_NO));

        // 'Kongsvinger' is not a code item
        given().port(port)
                .accept(ContentType.JSON)
                .param(QUERY, "Kongsvinger")
                .get(REQUEST_SEARCH)
                .prettyPeek()
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body(JSON_PAGE + ".totalElements", equalTo(0));
    }

    @Test
    void restServiceSearchGetClassificationAndNotCodeListJSON() {
        given().port(port)
                .accept(ContentType.JSON)
                .param(QUERY, "badminton")
                .get(REQUEST_SEARCH)
                .prettyPeek()
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body(JSON_PAGE + ".totalElements", equalTo(2))
                .body(JSON_SEARCH_RESULTS + ".name", hasItem(TestDataProvider.BADMINTON_NAVN_NO))
                .body(
                        JSON_SEARCH_RESULTS + ".name",
                        not(hasItem(TestDataProvider.BADMINTON_KODELISTE_NAVN)));
    }

    @Test
    void restServiceSearchGetClassificationAndCodeList() {
        given().port(port)
                .accept(ContentType.JSON)
                .param(QUERY, "badminton")
                .param(INCLUDE_CODE_LISTS, "true")
                .get(REQUEST_SEARCH)
                .prettyPeek()
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body(JSON_PAGE + ".totalElements", equalTo(3))
                .body(
                        JSON_SEARCH_RESULT1 + ".name",
                        equalTo(TestDataProvider.BADMINTON_KODELISTE_NAVN))
                .body(JSON_SEARCH_RESULT2 + ".name", equalTo(TestDataProvider.BADMINTON_NAVN_NO))
                .body(JSON_SEARCH_RESULT3 + ".name", equalTo(TestDataProvider.SPORT_NAVN_NO))
                .body(JSON_SEARCH_RESULT2 + ".snippet", containsString("badminton"));
    }

    @Test
    void restServiceSearchClassificationsXML() {
        given().port(port)
                .accept(ContentType.XML)
                .param(QUERY, "kommune")
                .get(REQUEST_SEARCH)
                .prettyPeek()
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.XML)
                .body(XML_SEARCH_RESULTS + ".size()", equalTo(2))
                // result 1
                .body(
                        XML_SEARCH_RESULT1 + ".name",
                        equalTo(TestDataProvider.KOMMUNEINNDELING_NAVN_NO))
                .body(XML_SEARCH_RESULT1 + ".snippet", containsString("kommune"))
                .body(XML_SEARCH_RESULT1 + ".searchScore.toFloat()", greaterThan(0.0f))
                .body(XML_SEARCH_RESULT1 + ".link.rel", equalTo("self"))
                .body(
                        XML_SEARCH_RESULT1 + ".link.href",
                        containsString(REQUEST + "/" + kommuneinndeling.getId()))
                // footer
                .body(XML_ROOT + ".link.href", containsString(REQUEST_SEARCH))
                .body(XML_PAGE + ".size.toInteger();", equalTo(PAGE_SIZE))
                .body(XML_PAGE + ".totalElements.toInteger();", equalTo(2))
                .body(XML_PAGE + ".totalPages.toInteger();", equalTo(1))
                .body(XML_PAGE + ".number.toInteger();", equalTo(0));
    }

    @Test
    void restServiceSearchClassificationsXMLKommuner() {
        given().port(port)
                .accept(ContentType.XML)
                .param(QUERY, "kommuner")
                .get(REQUEST_SEARCH)
                .prettyPeek()
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.XML)
                .body(XML_SEARCH_RESULTS + ".size()", equalTo(2))
                // result 1: 'kommune' in title
                .body(XML_SEARCH_RESULT1 + ".name", containsString("kommune"))
                .body(XML_SEARCH_RESULT1 + ".snippet", containsString("kommune"))
                .body(XML_SEARCH_RESULT1 + ".searchScore.toFloat()", greaterThan(0.0f))
                .body(XML_SEARCH_RESULT1 + ".link.rel", equalTo("self"))
                .body(
                        XML_SEARCH_RESULT1 + ".link.href",
                        containsString(REQUEST + "/" + kommuneinndeling.getId()))
                // result 2 - kommune just in description
                .body(XML_SEARCH_RESULT2 + ".name", not(containsString("kommune")))
                .body(XML_SEARCH_RESULT2 + ".snippet", containsString("kommune"))
                .body(XML_SEARCH_RESULT2 + ".searchScore.toFloat()", greaterThan(0.0f))
                // footer
                .body(XML_ROOT + ".link.href", containsString(REQUEST_SEARCH))
                .body(XML_PAGE + ".size.toInteger();", equalTo(PAGE_SIZE))
                .body(XML_PAGE + ".totalElements.toInteger();", equalTo(2))
                .body(XML_PAGE + ".totalPages.toInteger();", equalTo(1))
                .body(XML_PAGE + ".number.toInteger();", equalTo(0));
    }

    @Test
    void restServiceSearchClassificationsShouldNotReturnCodelistsByDefaultJSON() {
        // no result expected when codelists are not included (default behavior)
        given().port(port)
                .accept(ContentType.JSON)
                .param(QUERY, "familie")
                .get(REQUEST_SEARCH)
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body(JSON_PAGE + ".totalElements", equalTo(0))
                .body(JSON_PAGE + ".totalPages", equalTo(0))
                .body(JSON_PAGE + ".number", equalTo(0));
    }

    @Test
    void restServiceSearchClassificationsShouldNotReturnCodeListsByDefaultXML() {
        given().port(port)
                .accept(ContentType.JSON)
                .param(QUERY, "familie")
                .param(INCLUDE_CODE_LISTS, TRUE)
                .get(REQUEST_SEARCH)
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body(JSON_SEARCH_RESULTS + ".size()", equalTo(1))
                .body(
                        JSON_SEARCH_RESULT1 + ".name",
                        equalTo(TestDataProvider.FAMILIEGRUPPERING_NAVN_NO))
                .body(JSON_SEARCH_RESULT1 + ".snippet", containsString("familie"))
                .body(JSON_SEARCH_RESULT1 + ".searchScore", greaterThan(0.0f))
                .body(
                        JSON_SEARCH_RESULT1 + "._links.self.href",
                        containsString(REQUEST + "/" + familieGrupperingCodelist.getId()));

        // no result expected when codelists are not included (default behavior)
        given().port(port)
                .accept(ContentType.XML)
                .param(QUERY, "familie")
                .get(REQUEST_SEARCH)
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.XML)
                .body(XML_SEARCH_RESULTS + ".size()", equalTo(0))
                .body(XML_PAGE + ".totalElements.toInteger();", equalTo(0))
                .body(XML_PAGE + ".totalPages.toInteger();", equalTo(0))
                .body(XML_PAGE + ".number.toInteger();", equalTo(0));
    }

    @Test
    void restServiceSearchClassificationsWithCodeListXML() {
        given().port(port)
                .accept(ContentType.XML)
                .param(QUERY, "familie")
                .param(INCLUDE_CODE_LISTS, TRUE)
                .get(REQUEST_SEARCH)
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.XML)
                .body(XML_SEARCH_RESULTS + ".size()", equalTo(1))
                .body(
                        XML_SEARCH_RESULT1 + ".name",
                        equalTo(TestDataProvider.FAMILIEGRUPPERING_NAVN_NO))
                .body(XML_SEARCH_RESULT1 + ".snippet", containsString("familie"))
                .body(XML_SEARCH_RESULT1 + ".searchScore.toFloat();", greaterThan(0.0f))
                .body(
                        XML_SEARCH_RESULT1 + ".link.href",
                        containsString(REQUEST + "/" + familieGrupperingCodelist.getId()));
    }

    @Test
    void restServiceSearchCodesWithNorwegianStemmer() {
        given().port(port)
                .accept(ContentType.JSON)
                .param(QUERY, "konkurranser")
                .get(REQUEST_SEARCH)
                .prettyPeek()
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body(JSON_PAGE + ".totalElements", equalTo(1))
                .body(JSON_SEARCH_RESULT1 + ".name", equalTo(TestDataProvider.BADMINTON_NAVN_NO));

        // Code is "løper"
        given().port(port)
                .accept(ContentType.JSON)
                .param(QUERY, "løp")
                .get(REQUEST_SEARCH)
                .prettyPeek()
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body(JSON_PAGE + ".totalElements", equalTo(1))
                .body(JSON_SEARCH_RESULT1 + ".name", equalTo(TestDataProvider.BADMINTON_NAVN_NO));
    }
}
