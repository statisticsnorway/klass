package no.ssb.klass.api.applicationtest;

import io.restassured.http.ContentType;
import no.ssb.klass.api.applicationtest.config.ApplicationTestConfig;
import no.ssb.klass.api.applicationtest.config.IndexServiceTestConfig;
import no.ssb.klass.api.services.IndexServiceImpl;
import no.ssb.klass.core.config.ConfigurationProfiles;
import no.ssb.klass.core.repository.ClassificationSeriesRepository;
import no.ssb.klass.testutil.TestDataProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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

        String indexDefinition = """
            {
              "settings": {
                "number_of_shards": 1,
                "number_of_replicas": 0,
                "analysis": {
                  "analyzer": {
                    "norwegian": {
                      "type": "norwegian"
                    }
                  }
                }
              },
              "mappings": {
                "properties": {
                  "title": { "type": "text", "analyzer": "norwegian" },
                  "description": { "type": "text", "analyzer": "norwegian" },
                  "codes": { "type": "text", "analyzer": "norwegian" },
                  "type": { "type": "keyword" },
                  "published": { "type": "boolean" },
                  "copyrighted": { "type": "boolean" },
                  "itemid": { "type": "long" },
                  "language": { "type": "keyword" },
                  "section": { "type": "keyword" },
                  "family": { "type": "text" },
                  "uuid": { "type": "keyword" }
                }
              }
            }
            """;

        org.opensearch.client.indices.CreateIndexRequest createRequest =
            new org.opensearch.client.indices.CreateIndexRequest("klass")
                .source(indexDefinition, org.opensearch.common.xcontent.XContentType.JSON);

        opensearchClient.indices().create(createRequest, org.opensearch.client.RequestOptions.DEFAULT);

        indexService.indexSync(kommuneinndeling);
        indexService.indexSync(bydelsinndeling);
        indexService.indexSync(familieGrupperingCodelist);
        indexService.indexSync(badmintonCodelist);
        openSearchRestTemplate.indexOps(IndexCoordinates.of("klass")).refresh();
    }

    @Test
    public void restServiceSearchClassificationsJSON() {
        given().port(port).accept(ContentType.JSON).params("query", "kommune")
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
    public void restServiceSearchClassificationsXML() {
        given().port(port).accept(ContentType.XML).params("query", "kommuner")
                .get(REQUEST_SEARCH)
                .prettyPeek()
                .then().assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.XML)
                .body(XML_SEARCH_RESULTS + ".size()", equalTo(2))
                // result 1
                .body(XML_SEARCH_RESULT1 + ".name", equalTo(TestDataProvider.KOMMUNEINNDELING_NAVN_NO))
                .body(XML_SEARCH_RESULT1 + ".snippet", containsString("kommune"))
                .body(XML_SEARCH_RESULT1 + ".searchScore.toFloat()", greaterThan(0.0f))
                .body(XML_SEARCH_RESULT1 + ".link.rel", equalTo("self"))
                .body(XML_SEARCH_RESULT1 + ".link.href", containsString(REQUEST + "/" + kommuneinndeling.getId()))
                //result 2
                .body(XML_SEARCH_RESULT2 + ".name", equalTo(TestDataProvider.BYDELSINNDELING_NAVN_NO))
                .body(XML_SEARCH_RESULT2 + ".snippet", containsString("kommune"))
                .body(XML_SEARCH_RESULT2 + ".searchScore.toFloat();", greaterThan(0.0f))
                .body(XML_SEARCH_RESULT2 + ".link.rel", equalTo("self"))
                .body(XML_SEARCH_RESULT2 + ".link.href", containsString(REQUEST + "/" + bydelsinndeling.getId()))
                // footer
                .body(XML_ROOT + ".link.href", containsString(REQUEST_SEARCH))
                .body(XML_PAGE + ".size.toInteger();", equalTo(PAGE_SIZE))
                .body(XML_PAGE + ".totalElements.toInteger();", equalTo(2))
                .body(XML_PAGE + ".totalPages.toInteger();", equalTo(1))
                .body(XML_PAGE + ".number.toInteger();", equalTo(0));

    }

    @Test
    public void restServiceSearchClassificationsShouldNotReturnCodelistsByDefaultJSON() {
        // no result expected when codelists are not included (default behavior)
        given().port(port).accept(ContentType.JSON).params("query", "familie")
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
    public void restServiceSearchClassificationsShouldNotReturnCodelistsByDefaultXML() {
        // no result expected when  codelists are not included (default behavior)
        given().port(port).accept(ContentType.XML).params("query", "familie")
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
        given().port(port).accept(ContentType.JSON).param("query", "familie").param("includeCodelists", "true")
                .get(REQUEST_SEARCH)
//                .prettyPeek()
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
        given().port(port).accept(ContentType.XML).param("query", "familie").param("includeCodelists", "true")
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
