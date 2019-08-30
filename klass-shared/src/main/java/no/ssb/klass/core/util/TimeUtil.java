package no.ssb.klass.core.util;

import static com.google.common.base.Preconditions.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

/**
 * Supports mocking of clock source. Hence possible to control time returned for tests.
 * <p>
 * Use TimeUtil.now() instead of new Date() in source code in order to be able to mock the time.
 * 
 */
public final class TimeUtil {
    private static final ClockSource DEFAULT_CLOCKSOURCE = () -> System.currentTimeMillis();
    private static ClockSource clockSource = DEFAULT_CLOCKSOURCE;

    private TimeUtil() {
        // Utility class
    }

    /**
     * Gets the time now. Same as new Date(), but with this time can be mocked in tests.
     */
    public static Date now() {
        return new Date(clockSource.currentTimeMillis());
    }

    public static long millisecondsSince(Date start) {
        return now().getTime() - start.getTime();
    }

    public static LocalDate createDate(int year, int month, int day) {
        return LocalDate.of(year, month, day);
    }

    public static LocalDate createDate(String dateString) {
        if (dateString == null) {
            return null;
        }
        return LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE);
    }

    /**
     * Set clock source. Use in test when control of time is required
     */
    public static void setClockSource(ClockSource clockSource) {
        TimeUtil.clockSource = clockSource;
    }

    /**
     * Reverts clock source to original value. Call this when test is done
     */
    public static void revertClockSource() {
        clockSource = DEFAULT_CLOCKSOURCE;
    }

    public static LocalDate min(List<LocalDate> dates) {
        checkArgument(!dates.isEmpty());
        LocalDate least = LocalDate.MAX;
        for (LocalDate date : dates) {
            if (date.isBefore(least)) {
                least = date;
            }
        }
        return least;
    }

    public static LocalDate max(List<LocalDate> dates) {
        checkArgument(!dates.isEmpty());
        LocalDate max = LocalDate.MIN;
        for (LocalDate date : dates) {
            if (date.isAfter(max)) {
                max = date;
            }
        }
        return max;
    }

    public static LocalDate createMaxDate() {
        return LocalDate.MAX;
    }

    public static LocalDate createMinDate() {
        return LocalDate.MIN;
    }

    public static boolean isMaxDate(LocalDate date) {
        return LocalDate.MAX.equals(date);
    }

    public static boolean isMinDate(LocalDate date) {
        return LocalDate.MIN.equals(date);
    }

    public static Date toDate(LocalDate localDate) {
        if (localDate == null || isMinDate(localDate) || isMaxDate(localDate)) {
            return null;
        }
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static LocalDate toLocalDate(Date date) {
        if (date != null) {
            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        } else {
            return null;
        }
    }

    // fix date issues from stabas imported data
    public static LocalDate fistDayOfMonth(LocalDate date) {
        return date.equals(LocalDate.MAX) ? date : date.withDayOfMonth(1);
    }

}
