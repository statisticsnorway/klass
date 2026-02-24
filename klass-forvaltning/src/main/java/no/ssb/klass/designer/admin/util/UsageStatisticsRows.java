package no.ssb.klass.designer.admin.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class UsageStatisticsRows {
    private final Map<ReportDescription<UseStatisticsModeChoice>, Integer> rows;

    public UsageStatisticsRows(int numberOfClassifications, int numberOfSearchReturnedNull, int totalSearchWords) {
        rows = new LinkedHashMap<>();
        rows.put(UseStatisticsModeChoice.TOTAL_CLASSIFIC, numberOfClassifications);
        rows.put(UseStatisticsModeChoice.NUMBEROF_SEARCH_RETURNED_NULL, numberOfSearchReturnedNull);
        rows.put(UseStatisticsModeChoice.TOTAL_SEARCH_WORDS, totalSearchWords);
    }

    public Map<ReportDescription<UseStatisticsModeChoice>, Integer> getRows() {
        return rows;
    }
}
