package no.ssb.klass.api.applicationtest;

import io.restassured.http.ContentType;
import no.ssb.klass.api.applicationtest.config.ApplicationTestConfig;
import no.ssb.klass.api.applicationtest.config.IndexServiceTestConfig;
import no.ssb.klass.api.services.IndexServiceImpl;
import no.ssb.klass.core.config.ConfigurationProfiles;
import no.ssb.klass.core.repository.ClassificationSeriesRepository;
import no.ssb.klass.testutil.TestDataProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opensearch.client.RestHighLevelClient;
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

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


@Testcontainers
@SpringBootTest(classes = {ApplicationTestConfig.class, IndexServiceTestConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = {ConfigurationProfiles.POSTGRES_EMBEDDED, ConfigurationProfiles.MOCK_MAILSERVER, ConfigurationProfiles.OPEN_SEARCH_LOCAL}, inheritProfiles = false)
public class RestApiSearchIntegrationTest extends AbstractRestApiApplicationTest {
    // @formatter:off

    @Container
    @SuppressWarnings("resource")  // Managed by Testcontainers
    protected static final OpensearchContainer<?> opensearchContainer =
            new OpensearchContainer<>(DockerImageName.parse("opensearchproject/opensearch:2.11.0"))
                    .withEnv("OPENSEARCH_JAVA_OPTS", "-Xms2g -Xmx2g")
                    .withEnv("discovery.type", "single-node")
                    .withReuse(true);

    @DynamicPropertySource
    static void registerOpenSearchProperties(DynamicPropertyRegistry registry) {
        String uri = "http://" + opensearchContainer.getHost() + ":" + opensearchContainer.getMappedPort(9200);
        registry.add("opensearch.url", () -> uri);
    }


    @Autowired
    private IndexServiceImpl indexService;

    @Autowired
    @Qualifier("opensearchRestTemplate")
    private OpenSearchRestTemplate openSearchRestTemplate;

    @Autowired
    private RestHighLevelClient opensearchClient;

    @Autowired
    private ClassificationSeriesRepository classificationSeriesRepository;

    @BeforeEach
    void setupSearchIndex() throws Exception {

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
        openSearchRestTemplate.indexOps(IndexCoordinates.of("klass")).refresh();
    }

    @Test
    public void restServiceSearchClassificationsJSON() {
        // Should rank name with 'kommune' over bydel
        given().port(port).accept(ContentType.JSON).param(QUERY, "kommune")
                .get(REQUEST_SEARCH)
                .prettyPeek()
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body(JSON_SEARCH_RESULTS + ".size()", equalTo(2))
                .body(JSON_SEARCH_RESULT1 + ".name", equalTo(TestDataProvider.KOMMUNEINNDELING_NAVN_NO))
                .body(JSON_SEARCH_RESULT1 + ".snippet", containsString("kommune"))
                .body(JSON_SEARCH_RESULT1 + ".searchScore", greaterThan(0.0f))
                .body(JSON_SEARCH_RESULT1 + "._links.self.href", containsString(REQUEST + "/" + kommuneinndeling.getId()))
                //result 2
                .body(JSON_SEARCH_RESULT2 + ".name", equalTo(TestDataProvider.BYDELSINNDELING_NAVN_NO))
                .body(JSON_SEARCH_RESULT2 + ".snippet", containsString("kommune"))
                .body(JSON_SEARCH_RESULT2 + ".searchScore", greaterThan(0.0f))
                .body(JSON_SEARCH_RESULT2 + "._links.self.href", containsString(REQUEST + "/" + bydelsinndeling.getId()))
                // footer
                .body(JSON_LINKS + ".self.href", containsString(REQUEST_SEARCH))
                .body(JSON_PAGE + ".size", equalTo(PAGE_SIZE))
                .body(JSON_PAGE + ".totalElements", equalTo(2))
                .body(JSON_PAGE + ".totalPages", equalTo(1))
                .body(JSON_PAGE + ".number", equalTo(0));
    }

    @Test
    public void restServiceSearchPartialWord() {
        // This should be able to find the stem? or is it only for ..
        given().port(port).accept(ContentType.JSON).param(QUERY, "kommun")
                .get(REQUEST_SEARCH)
                .prettyPeek()
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body(JSON_PAGE + ".totalElements", equalTo(2));
    }

    @Test
    public void restServiceSearchExactWord() {
        given().port(port).accept(ContentType.JSON).param(QUERY, "bydelsinndeling")
                .get(REQUEST_SEARCH)
                .prettyPeek()
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body(JSON_PAGE + ".totalElements", equalTo(1));
    }

    @Test
    public void restServiceSearchGetClassificationAndNotCodeList() {
        given().port(port).accept(ContentType.JSON).param(QUERY, "badminton")
                .get(REQUEST_SEARCH)
                .prettyPeek()
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body(JSON_PAGE + ".totalElements", equalTo(2))
                .body(JSON_SEARCH_RESULT1 + ".name", equalTo(TestDataProvider.BADMINTON_NAVN_NO))
                .body(JSON_SEARCH_RESULT2 + ".name", equalTo(TestDataProvider.SPORT_NAVN_NO));
    }

    @Test
    public void restServiceSearchGetClassificationAndCodeList() {
        // Should classification rank over codeList?
        given().port(port).accept(ContentType.JSON).param(QUERY, "badminton").param(INCLUDE_CODE_LISTS, "true")
                .get(REQUEST_SEARCH)
                .prettyPeek()
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body(JSON_PAGE + ".totalElements", equalTo(3))
                .body(JSON_SEARCH_RESULT1 + ".name", equalTo(TestDataProvider.BADMINTON_KODELISTE_NAVN))
                .body(JSON_SEARCH_RESULT2 + ".name", equalTo(TestDataProvider.BADMINTON_NAVN_NO))
                .body(JSON_SEARCH_RESULT3 + ".name", equalTo(TestDataProvider.SPORT_NAVN_NO))
                .body(JSON_SEARCH_RESULT2 + ".snippet", containsString("badminton"));
    }

    @Test
    public void restServiceSearchClassificationsXML() {
        // When searching for 'kommuner' bydelsinndeling is not a hit because the classification contains only 'kommune'
        given().port(port).accept(ContentType.XML).param(QUERY, "kommuner")
                .get(REQUEST_SEARCH)
                .prettyPeek()
                .then().assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.XML)
                .body(XML_SEARCH_RESULTS + ".size()", equalTo(1))
                // result 1
                .body(XML_SEARCH_RESULT1 + ".name", equalTo(TestDataProvider.KOMMUNEINNDELING_NAVN_NO))
                .body(XML_SEARCH_RESULT1 + ".snippet", containsString("kommune"))
                .body(XML_SEARCH_RESULT1 + ".searchScore.toFloat()", greaterThan(0.0f))
                .body(XML_SEARCH_RESULT1 + ".link.rel", equalTo("self"))
                .body(XML_SEARCH_RESULT1 + ".link.href", containsString(REQUEST + "/" + kommuneinndeling.getId()))
                // footer
                .body(XML_ROOT + ".link.href", containsString(REQUEST_SEARCH))
                .body(XML_PAGE + ".size.toInteger();", equalTo(PAGE_SIZE))
                .body(XML_PAGE + ".totalElements.toInteger();", equalTo(1))
                .body(XML_PAGE + ".totalPages.toInteger();", equalTo(1))
                .body(XML_PAGE + ".number.toInteger();", equalTo(0));

    }

    @Test
    public void restServiceSearchClassificationsShouldNotReturnCodelistsByDefaultJSON() {
        // no result expected when codelists are not included (default behavior)
        given().port(port).accept(ContentType.JSON).param(QUERY, "familie")
                .get(REQUEST_SEARCH)
//                .prettyPeek()
                .then().assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body(JSON_PAGE + ".totalElements", equalTo(0))
                .body(JSON_PAGE + ".totalPages", equalTo(0))
                .body(JSON_PAGE + ".number", equalTo(0));
    }

    @Test
    public void restServiceSearchClassificationsShouldNotReturnCodeListsByDefaultXML() {
        // no result expected when  codelists are not included (default behavior)
        given().port(port).accept(ContentType.XML).param(QUERY, "familie")
                .get(REQUEST_SEARCH)
//                .prettyPeek()
                .then().assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.XML)
                .body(XML_SEARCH_RESULTS + ".size()", equalTo(0))
                .body(XML_PAGE + ".totalElements.toInteger();", equalTo(0))
                .body(XML_PAGE + ".totalPages.toInteger();", equalTo(0))
                .body(XML_PAGE + ".number.toInteger();", equalTo(0));
    }

    @Test
    public void restServiceSearchClassificationsWithCodelistJSON() {

        // one result expected when  codelists are  included
        // null ved bruk av "familie" og ikke 0
        given().port(port).accept(ContentType.JSON).param(QUERY, "familier").param(INCLUDE_CODE_LISTS, TRUE)
                .get(REQUEST_SEARCH)
                .then().assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body(JSON_SEARCH_RESULTS + ".size()", equalTo(1))
                // result 1
                .body(JSON_SEARCH_RESULT1 + ".name", equalTo(TestDataProvider.FAMILIEGRUPPERING_NAVN_NO))
                .body(JSON_SEARCH_RESULT1 + ".snippet", containsString("familie"))
                .body(JSON_SEARCH_RESULT1 + ".searchScore", greaterThan(0.0f))
                .body(JSON_SEARCH_RESULT1 + "._links.self.href", containsString(REQUEST + "/" + familieGrupperingCodelist.getId()));

    }

    @Test
    public void restServiceSearchClassificationsWithCodelistXML() {

        // one result expected when  codelists are  included
        // result 0
        given().port(port).accept(ContentType.XML).param(QUERY, "familie").param(INCLUDE_CODE_LISTS, TRUE)
                .get(REQUEST_SEARCH)
//                .prettyPeek()
                .then().assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.XML)
                .body(XML_SEARCH_RESULTS + ".size()", equalTo(1))
                // result 1
                .body(XML_SEARCH_RESULT1 + ".name", equalTo(TestDataProvider.FAMILIEGRUPPERING_NAVN_NO))
                .body(XML_SEARCH_RESULT1 + ".snippet", containsString("familie"))
                .body(XML_SEARCH_RESULT1 + ".searchScore.toFloat();", greaterThan(0.0f))
                .body(XML_SEARCH_RESULT1 + ".link.href", containsString(REQUEST + "/" + familieGrupperingCodelist.getId()));

    }
// @formatter:on
}
