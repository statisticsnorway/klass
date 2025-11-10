package no.ssb.klass.testutil;

import no.ssb.klass.core.util.ClockSource;

public class UpdateableClockSource implements ClockSource {
    private long timeMillis;

    public UpdateableClockSource(long timeMillis) {
        this.timeMillis = timeMillis;
    }

    @Override
    public long currentTimeMillis() {
        return timeMillis;
    }

    public void updateTimeMillis(long timeMillis) {
        this.timeMillis = timeMillis;
    }
}
