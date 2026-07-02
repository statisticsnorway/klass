package no.ssb.klass.api.applicationtest;

import static io.restassured.RestAssured.given;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import io.restassured.http.ContentType;

import no.ssb.klass.core.repository.SubscriberRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * Integration tests for the trackChanges (subscribe/unsubscribe) endpoints.
 *
 * <p>Documents and verifies that:
 * <ul>
 *   <li>Subscriptions are created correctly and return STATUS_CREATED</li>
 *   <li>Duplicate subscriptions are rejected with STATUS_EXISTS</li>
 *   <li>The unsubscribe URL stored in the database is built using the configured
 *       {@code klass.env.api.public.base.url}, not the internal request host — ensuring
 *       emails sent from containers contain correct public-facing links</li>
 * </ul>
 */
class RestApiTrackChangesTest extends AbstractRestApiApplicationTest {

    private static final String TEST_EMAIL = "test-subscriber@example.com";

    @Autowired private SubscriberRepository subscriberRepository;

    @Value("${klass.env.api.public.base.url}")
    private String publicBaseUrl;

    @Test
    void trackChangesReturnsCreatedAndStoresSubscription() {
        given().port(port)
                .accept(ContentType.JSON)
                .queryParam("email", TEST_EMAIL)
                .when()
                .post(REQUEST_SUBSCRIBE, kommuneinndeling.getId())
                .then()
                .statusCode(200)
                .body("code", equalTo("STATUS_CREATED"));

        assertThat(
                subscriberRepository.findOneByEmail(TEST_EMAIL).isPresent(),
                is(true));
    }

    @Test
    void trackChangesReturnsBadRequestWhenAlreadySubscribed() {
        given().port(port)
                .accept(ContentType.JSON)
                .queryParam("email", TEST_EMAIL)
                .post(REQUEST_SUBSCRIBE, kommuneinndeling.getId());

        given().port(port)
                .accept(ContentType.JSON)
                .queryParam("email", TEST_EMAIL)
                .when()
                .post(REQUEST_SUBSCRIBE, kommuneinndeling.getId())
                .then()
                .statusCode(400)
                .body("code", equalTo("STATUS_EXISTS"));
    }

    /**
     * Verifies that the unsubscribe URL stored when a subscription is created uses
     * {@code klass.env.api.public.base.url} instead of the internal request host.
     *
     * <p>This ensures that users running in containers receive correct public-facing URLs
     * in their verification and unsubscribe emails, even when the internal request host
     * is something like {@code localhost:8080}.
     */
    @Test
    void trackChangesBuildsUnsubscribeUrlFromConfiguredPublicBaseUrl() {
        given().port(port)
                .accept(ContentType.JSON)
                .queryParam("email", TEST_EMAIL)
                .post(REQUEST_SUBSCRIBE, kommuneinndeling.getId());

        String storedUnsubscribeUrl =
                template.execute(
                        status ->
                                subscriberRepository
                                        .findOneByEmail(TEST_EMAIL)
                                        .orElseThrow()
                                        .getEndSubscriptionUrl(kommuneinndeling));

        assertThat(
                "Unsubscribe URL should start with the configured public base URL, not the internal"
                        + " request host",
                storedUnsubscribeUrl,
                startsWith(publicBaseUrl));
        assertThat(
                storedUnsubscribeUrl,
                containsString(
                        "/classifications/" + kommuneinndeling.getId() + "/removeTracking"));
        assertThat(
                storedUnsubscribeUrl,
                containsString("email=" + TEST_EMAIL));
    }
}


