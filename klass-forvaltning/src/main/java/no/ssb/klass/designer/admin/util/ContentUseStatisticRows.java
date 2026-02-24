package no.ssb.klass.designer.admin.util;

import java.util.LinkedHashMap;
import java.util.Map;


public class ContentUseStatisticRows {
    private final Map<ReportDescription<ReportModeChoice>, Integer> rows;

    public ContentUseStatisticRows(int numberOfClassifications, int publishedClassifications,
            int unpublishedClassifications,
            int publishedVersionsWithMissingLanguages) {
        rows = new LinkedHashMap<>();
        rows.put(ReportModeChoice.TOTAL, numberOfClassifications);
        rows.put(ReportModeChoice.PUBLISHED, publishedClassifications);
        rows.put(ReportModeChoice.UNPUBLISHED, unpublishedClassifications);
        rows.put(ReportModeChoice.MISSING_LANG, publishedVersionsWithMissingLanguages);
    }

    public Map<ReportDescription<ReportModeChoice>, Integer> getRows() {
        return rows;
    }
}
