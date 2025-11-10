package no.ssb.klass.core.model;

import static org.junit.jupiter.api.Assertions.*;

import no.ssb.klass.core.util.TimeUtil;
import no.ssb.klass.testutil.TestUtil;
import no.ssb.klass.testutil.UpdateableClockSource;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.util.Date;

public class SubscriptionTest {

    private static final int EXPIRY_TIME_IN_MINS = 60 * 24; // 24 hours
    private static final long ONE_MINUTE_IN_MILLISECS = 60000;

    private Subscription subject;
    private UpdateableClockSource clockSource;

    @BeforeEach
    public void init() throws Exception {
        clockSource = new UpdateableClockSource(new Date().getTime());
        TimeUtil.setClockSource(clockSource);
        subject =
                new Subscription(TestUtil.createClassification("name"), new URL("http://test.url"));
    }

    @AfterEach
    public void teardown() {
        TimeUtil.revertClockSource();
    }

    @Test
    public void expired() {
        // when
        clockSource.updateTimeMillis(
                TimeUtil.now().getTime() + EXPIRY_TIME_IN_MINS * ONE_MINUTE_IN_MILLISECS + 1);
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

    @Test
    public void verifyExpired() {
        // when
        clockSource.updateTimeMillis(
                TimeUtil.now().getTime() + EXPIRY_TIME_IN_MINS * ONE_MINUTE_IN_MILLISECS + 1);
        // then exception
        Assertions.assertThrows(Exception.class, () -> subject.verify());
    }
}
