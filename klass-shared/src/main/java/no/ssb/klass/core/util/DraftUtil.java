package no.ssb.klass.core.util;

import java.time.LocalDate;

/**
 * @author Mads Lundemo, SSB.
 */
public final class DraftUtil {

    private DraftUtil() {}

    public static boolean isDraft(DateRange dateRange) {
        return LocalDate.MIN.isEqual(dateRange.getFrom())
                && LocalDate.MAX.isEqual(dateRange.getTo());
    }

    public static boolean isDraft(LocalDate validFrom, LocalDate validTo) {
        return (validFrom == null && validTo == null)
                || (LocalDate.MIN.isEqual(validFrom) && LocalDate.MAX.isEqual(validTo));
    }

    public static DateRange getDraftDateRange() {
        return DateRange.create(LocalDate.MIN, LocalDate.MAX);
    }
}
