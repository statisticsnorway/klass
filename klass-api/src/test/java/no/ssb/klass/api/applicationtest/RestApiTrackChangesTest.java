package no.ssb.klass.api.applicationtest;

import static io.restassured.RestAssured.given;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import io.restassured.http.ContentType;

import no.ssb.klass.core.repository.SubscriberRepository;
import no.ssb.klass.core.service.MailService;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

/**
 * Integration tests for the trackChanges (subscribe/unsubscribe) endpoints.
 *
 * <p>Documents and verifies that:
 *
 * <ul>
 *   <li>Subscriptions are created correctly and return STATUS_CREATED
 *   <li>Duplicate subscriptions are rejected with STATUS_EXISTS
 *   <li>Both the unsubscribe URL (stored in DB) and the verify URL (sent in email) are built using
 *       the configured {@code klass.env.api.public.base.url}, not the internal request host —
 *       ensuring emails sent from containers contain correct public-facing links
 * </ul>
 */
class RestApiTrackChangesTest extends AbstractRestApiApplicationTest {

    private static final String TEST_EMAIL = "test-subscriber@example.com";

    @Autowired private SubscriberRepository subscriberRepository;

    @MockitoSpyBean private MailService mailService;

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

        assertThat(subscriberRepository.findOneByEmail(TEST_EMAIL).isPresent(), is(true));
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
     * Verifies that the unsubscribe URL stored in the database uses {@code
     * klass.env.api.public.base.url} instead of the internal request host.
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
                containsString("/classifications/" + kommuneinndeling.getId() + "/removeTracking"));
        assertThat(storedUnsubscribeUrl, containsString("email=" + TEST_EMAIL));
    }

    /**
     * Verifies that the verify URL included in the verification email uses {@code
     * klass.env.api.public.base.url} instead of the internal request host.
     */
    @Test
    void trackChangesBuildsVerifyUrlFromConfiguredPublicBaseUrl() {
        given().port(port)
                .accept(ContentType.JSON)
                .queryParam("email", TEST_EMAIL)
                .post(REQUEST_SUBSCRIBE, kommuneinndeling.getId());

        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);
        verify(mailService).sendMail(eq(TEST_EMAIL), anyString(), bodyCaptor.capture());

        String emailBody = bodyCaptor.getValue();
        assertThat(
                "Verify URL in email body should start with the configured public base URL, not the"
                        + " internal request host",
                emailBody,
                containsString("href=\"" + publicBaseUrl));
        assertThat(emailBody, containsString("/classifications/verifyTracking/"));
    }
}
