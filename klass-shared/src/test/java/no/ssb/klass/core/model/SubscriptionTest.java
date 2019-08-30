package no.ssb.klass.core.model;

import static org.junit.Assert.*;

import java.net.URL;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import no.ssb.klass.core.util.ClientException;
import no.ssb.klass.core.util.TimeUtil;
import no.ssb.klass.testutil.TestUtil;
import no.ssb.klass.testutil.UpdateableClockSource;

public class SubscriptionTest {

    private static final int EXPIRY_TIME_IN_MINS = 60 * 24; // 24 hours
    private static final long ONE_MINUTE_IN_MILLISECS = 60000;

    private Subscription subject;
    private UpdateableClockSource clockSource;

    @Before
    public void init() throws Exception {
        clockSource = new UpdateableClockSource(new Date().getTime());
        TimeUtil.setClockSource(clockSource);
        subject = new Subscription(TestUtil.createClassification("name"), new URL("http://test.url"));
    }

    @After
    public void teardown() {
        TimeUtil.revertClockSource();
    }

    @Test
    public void expired() {
        // when
        clockSource.updateTimeMillis(TimeUtil.now().getTime() + EXPIRY_TIME_IN_MINS * ONE_MINUTE_IN_MILLISECS + 1);
        // then
        assertEquals(true, subject.isExpired());
    }

    @Test
    public void notExpired() {
        // when
        // then
        assertEquals(false, subject.isExpired());
    }

    @Test
    public void verifyValid() {
        // when
        subject.verify();
        // then
        assertEquals(Verification.VALID, subject.getVerification());
    }

    @Test(expected = ClientException.class)
    public void verifyExpired() {
        // when
        clockSource.updateTimeMillis(TimeUtil.now().getTime() + EXPIRY_TIME_IN_MINS * ONE_MINUTE_IN_MILLISECS + 1);
        subject.verify();
        // then exception
    }

}
