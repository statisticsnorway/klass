package no.ssb.klass.core.model;

import static org.junit.Assert.*;

import java.net.URL;

import org.junit.Before;
import org.junit.Test;

import no.ssb.klass.core.util.ClientException;
import no.ssb.klass.testutil.TestUtil;

public class SubscriberTest {

    static final String EMAIL = "email@host.com";
    static final String TOKEN = "token";
    static final String NAME = "name";
    static final String END_SUBSCRIPTION_URL = "http://test.url";
    private Subscriber subject;

    private ClassificationSeries classification;

    @Before
    public void init() throws Exception {
        subject = new Subscriber(EMAIL);
        classification = TestUtil.createClassificationWithId(1, NAME);
        subject.addSubscription(classification, new URL(END_SUBSCRIPTION_URL));
    }

    @Test
    public void addSubscription() throws Exception {
        // when
        ClassificationSeries classification = TestUtil.createClassification(NAME);
        String result = subject.addSubscription(classification, new URL("http://test.url"));
        Verification verification = subject.verify(result);
        // then
        assertEquals(Verification.VALID, verification);
    }

    @Test(expected = ClientException.class)
    public void addSubscriptionExist() throws Exception {
        // when

        ClassificationSeries classification = TestUtil.createClassificationWithId(1, NAME);
        subject.addSubscription(classification, new URL("http://test.url"));
        // then exception
    }

    @Test(expected = NullPointerException.class)
    public void addSubscriptionNull() throws Exception {
        // when
        subject.addSubscription(null, new URL("http://test.url"));
        // then exception
    }

    @Test(expected = NullPointerException.class)
    public void addEndSubscriptionNull() throws Exception {
        // when
        subject.addSubscription(classification, null);
        // then exception
    }

    @Test
    public void removeSubscription() {
        // when
        boolean result = subject.removeSubscription(classification);
        // then
        assertEquals(true, result);
    }

    @Test
    public void getEndSubscriptionUrl() {
        // when
        String result = subject.getEndSubscriptionUrl(classification);
        // then
        assertEquals(END_SUBSCRIPTION_URL, result);
    }

    @Test(expected = ClientException.class)
    public void removeSubscriptionNotFound() {
        // when
        subject.removeSubscription(TestUtil.createClassification(NAME));
        // then exception
    }

    @Test(expected = NullPointerException.class)
    public void verifyNull() {
        // when
        subject.verify(null);
        // then exception
    }

    @Test(expected = ClientException.class)
    public void verifyNotFound() {
        // when
        subject.verify(TOKEN);
        // then exception
    }
}
