package no.ssb.klass.testutil;

import no.ssb.klass.core.util.ClockSource;

import java.util.Date;

/**
 * Clock source to be used for test that always returns the same time.
 *
 * @author kvileid
 */
public class ConstantClockSource implements ClockSource {
    private final Date constantDate;

    public ConstantClockSource(Date date) {
        this.constantDate = date;
    }

    @Override
    public long currentTimeMillis() {
        return constantDate.getTime();
    }
}
