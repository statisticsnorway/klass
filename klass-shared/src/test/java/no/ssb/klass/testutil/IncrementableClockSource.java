package no.ssb.klass.testutil;

import no.ssb.klass.core.util.ClockSource;

public class IncrementableClockSource implements ClockSource {
    private long timeMillis;

    public IncrementableClockSource(long timeMillis) {
        this.timeMillis = timeMillis;
    }

    @Override
    public long currentTimeMillis() {
        return timeMillis;
    }

    public void increment() {
        timeMillis++;
    }
}
