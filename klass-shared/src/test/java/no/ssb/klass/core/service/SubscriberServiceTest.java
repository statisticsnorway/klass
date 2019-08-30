package no.ssb.klass.core.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.net.URL;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.Subscriber;
import no.ssb.klass.core.model.Verification;
import no.ssb.klass.core.repository.SubscriberRepository;
import no.ssb.klass.core.util.ClientException;
import no.ssb.klass.testutil.TestUtil;

public class SubscriberServiceTest {

    static final String EMAIL = "email@server.com";
    static final String TOKEN = "dummy token";
    static final String NAME = "name";

    private SubscriberService subject;
    private SubscriberRepository subscriberRepositoryMock;
    private MailService mailServiceMock;

    @Before
    public void setup() {
        subscriberRepositoryMock = mock(SubscriberRepository.class);
        mailServiceMock = mock(MailService.class);
        subject = new SubscriberServiceImpl(subscriberRepositoryMock, mailServiceMock);
    }

    @Test
    public void trackChanges() throws Exception {
        // when
        Subscriber subscriber = new Subscriber(EMAIL);
        Optional<Subscriber> opt = Optional.of(subscriber);
        ClassificationSeries classification = TestUtil.createClassificationWithId(1, NAME);
        when(subscriberRepositoryMock.findOneByEmail(EMAIL)).thenReturn(opt);
        String token = subject.trackChanges(EMAIL, classification, new URL("http://test.url"));
        // then
        assertNotNull(token);
    }

    @Test
    public void trackChangesSubscriberNotExist() throws Exception {
        // when
        Optional<Subscriber> opt = Optional.empty();
        ClassificationSeries classification = TestUtil.createClassificationWithId(1, NAME);
        when(subscriberRepositoryMock.findOneByEmail(EMAIL)).thenReturn(opt);
        String token = subject.trackChanges(EMAIL, classification, new URL("http://test.url"));
        // then
        assertNotNull(token);
    }

    @Test
    public void removeTracking() throws Exception {
        // when
        Subscriber subscriber = new Subscriber(EMAIL);
        ClassificationSeries classification = TestUtil.createClassificationWithId(1, NAME);
        subscriber.addSubscription(classification, new URL("http://test.url"));
        Optional<Subscriber> opt = Optional.of(subscriber);
        when(subscriberRepositoryMock.findOneByEmail(EMAIL)).thenReturn(opt);
        boolean result = subject.removeTracking(EMAIL, classification);
        // then
        assertEquals(true, result);
    }

    @Test(expected = ClientException.class)
    public void removeTrackingClassificationNotExist() {
        // when
        Subscriber subscriber = new Subscriber(EMAIL);
        ClassificationSeries classification = TestUtil.createClassificationWithId(1, NAME);
        Optional<Subscriber> opt = Optional.of(subscriber);
        when(subscriberRepositoryMock.findOneByEmail(EMAIL)).thenReturn(opt);
        subject.removeTracking(EMAIL, classification);
    }

    @Test
    public void verifyTracking() throws Exception {
        // when
        Subscriber subscriber = new Subscriber(EMAIL);

        ClassificationSeries classification = TestUtil.createClassificationWithId(1, NAME);
        String token = subscriber.addSubscription(classification, new URL("http://test.url"));
        Optional<Subscriber> opt = Optional.of(subscriber);
        when(subscriberRepositoryMock.findOneByEmail(EMAIL)).thenReturn(opt);
        Verification verification = subject.verifyTracking(EMAIL, token);
        // then
        assertEquals(Verification.VALID, verification);
    }

    @Test(expected = RuntimeException.class)
    public void verifyTrackingNotExist() {
        // when
        Optional<Subscriber> opt = Optional.empty();
        when(subscriberRepositoryMock.findOneByEmail(EMAIL)).thenReturn(opt);
        subject.verifyTracking(EMAIL, TOKEN);
        // then exception
    }

}
