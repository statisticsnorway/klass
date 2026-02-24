package no.ssb.klass.designer.util;

import no.ssb.klass.core.service.enums.SubscriberMode;
import no.ssb.klass.core.service.enums.UseStatisticsMode;
import no.ssb.klass.designer.admin.util.SubscriberModeChoice;
import no.ssb.klass.designer.admin.util.UseStatisticsModeChoice;

public final class ChoiceMapper {

    //    public static ReportMode map(ReportModeChoice choice) {
//        switch (choice) {
//            case TOTAL: return ReportMode.TOTAL;
//            case UNPUBLISHED: return ReportMode.;
//        }
//    }
    public static SubscriberMode map(SubscriberModeChoice choice) {
        switch (choice) {
            case TOTAL:
                return SubscriberMode.TOTAL;
            case EXTERNAL:
                return SubscriberMode.EXTERNAL;
            case INTERNAL:
                return SubscriberMode.INTERNAL;
            default:
                throw new RuntimeException("Unknown Choice");
        }
    }

    public static UseStatisticsMode map(UseStatisticsModeChoice choice) {
        switch (choice) {
            case NUMBEROF_SEARCH_RETURNED_NULL:
                return UseStatisticsMode.NUMBEROF_SEARCH_RETURNED_NULL;
            case TOTAL_CLASSIFIC:
                return UseStatisticsMode.TOTAL_CLASSIFIC;
            case TOTAL_SEARCH_WORDS:
                return UseStatisticsMode.TOTAL_SEARCH_WORDS;
            default:
                throw new RuntimeException("Unknown Choice");
        }
    }
}
