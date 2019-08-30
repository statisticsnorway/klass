package no.ssb.klass.core.util;

import static org.junit.Assert.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

import org.junit.Test;

import com.google.common.collect.Lists;

import no.ssb.klass.testutil.ConstantClockSource;

public class TimeUtilTest {

    @Test
    public void milliSecondsSince() throws Exception {
        // given
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date start = formatter.parse("2015-01-01");
        Date now = formatter.parse("2015-01-02");
        try {
            TimeUtil.setClockSource(new ConstantClockSource(now));

            // when
            long result = TimeUtil.millisecondsSince(start);

            // then
            assertEquals(now.getTime() - start.getTime(), result);
        } finally {
            TimeUtil.revertClockSource();
        }
    }

    @Test
    public void createDate() {
        // given
        final LocalDate dateFromString = TimeUtil.createDate("2015-01-01");
        final LocalDate dateFromInts = TimeUtil.createDate(2015, 1, 1);

        // then
        assertEquals(dateFromString, dateFromInts);
    }

    @Test
    public void min() {
        // given
        final LocalDate first = TimeUtil.createDate("2010-01-01");
        final LocalDate second = TimeUtil.createDate("2014-01-01");

        // then
        assertEquals(TimeUtil.min(Lists.newArrayList(first, second)), first);
        assertEquals(TimeUtil.min(Lists.newArrayList(second, first)), first);
        assertEquals(TimeUtil.min(Lists.newArrayList(first)), first);
    }

    @Test
    public void max() {
        // given
        final LocalDate first = TimeUtil.createDate("2010-01-01");
        final LocalDate second = TimeUtil.createDate("2014-01-01");

        // then
        assertEquals(TimeUtil.max(Lists.newArrayList(first, second)), second);
        assertEquals(TimeUtil.max(Lists.newArrayList(second, first)), second);
        assertEquals(TimeUtil.max(Lists.newArrayList(second)), second);
    }

    @Test
    public void createMaxDate() {
        assertEquals(LocalDate.MAX, TimeUtil.createMaxDate());
    }

    @Test
    public void createMinDate() {
        assertEquals(LocalDate.MIN, TimeUtil.createMinDate());
    }

    @Test
    public void isMaxDate() {
        final LocalDate notMaxDate = TimeUtil.createDate("2015-01-01");
        final LocalDate maxDate = LocalDate.MAX;
        assertEquals(false, TimeUtil.isMaxDate(notMaxDate));
        assertEquals(true, TimeUtil.isMaxDate(maxDate));
    }

    @Test
    public void isMinDate() {
        final LocalDate notMinDate = TimeUtil.createDate("2015-01-01");
        final LocalDate minDate = LocalDate.MIN;
        assertEquals(false, TimeUtil.isMinDate(notMinDate));
        assertEquals(true, TimeUtil.isMinDate(minDate));
    }
}
