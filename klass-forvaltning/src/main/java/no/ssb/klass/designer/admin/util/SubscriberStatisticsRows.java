package no.ssb.klass.designer.admin.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class SubscriberStatisticsRows {
    private final Map<SubscriberReportDescription<SubscriberModeChoice>, Integer> rows;

    public SubscriberStatisticsRows(int numberOfSubscribers, int numberOfInternalSubscribers,
                                    int numberOfExternalSubscribers) {
        rows = new LinkedHashMap<>();
        rows.put(SubscriberModeChoice.TOTAL, numberOfSubscribers);
        rows.put(SubscriberModeChoice.INTERNAL, numberOfInternalSubscribers);
        rows.put(SubscriberModeChoice.EXTERNAL, numberOfExternalSubscribers);
    }

    public Map<SubscriberReportDescription<SubscriberModeChoice>, Integer> getRows() {
        return rows;
    }
}
