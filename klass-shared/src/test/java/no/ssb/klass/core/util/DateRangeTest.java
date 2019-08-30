package no.ssb.klass.core.util;

import static org.junit.Assert.*;

import java.time.LocalDate;

import org.junit.Test;

public class DateRangeTest {

    /**
     * <pre>
     *    subject   |------|
     *    other         |------|
     * </pre>
     */
    @Test
    public void overlaps() {
        // given
        DateRange subject = DateRange.create("2012-01-01", "2014-01-01");
        DateRange other = DateRange.create("2013-01-01", "2015-01-01");

        // when
        boolean result = subject.overlaps(other);

        // then
        assertEquals(true, result);
    }

    /**
     * <pre>
     *    subject   |------|
     *    other            |------|
     * </pre>
     */
    @Test
    public void overlapsSubjectFirst() {
        // given
        DateRange subject = DateRange.create("2012-01-01", "2014-01-01");
        DateRange other = DateRange.create("2014-01-01", "2016-01-01");

        // when
        boolean result = subject.overlaps(other);

        // then
        assertEquals(false, result);
    }

    /**
     * <pre>
     *    subject          |------|
     *    other     |------|
     * </pre>
     */
    @Test
    public void overlapsSubjectLast() {
        // given
        DateRange subject = DateRange.create("2014-01-01", "2016-01-01");
        DateRange other = DateRange.create("2012-01-01", "2014-01-01");

        // when
        boolean result = subject.overlaps(other);

        // then
        assertEquals(false, result);
    }

    /**
     * <pre>
     *    subject       |------|
     *    other     |------|
     * </pre>
     */
    @Test
    public void subRange() {
        // given
        DateRange subject = DateRange.create("2012-01-01", "2014-01-01");
        DateRange other = DateRange.create("2013-01-01", "2015-01-01");

        // when
        DateRange result = subject.subRange(other);

        // then
        DateRange expected = DateRange.create("2013-01-01", "2014-01-01");
        assertEquals(expected, result);
    }

    /**
     * <pre>
     *    subject   |------|
     *    other            |------|
     * </pre>
     */
    @Test(expected = IllegalArgumentException.class)
    public void subRangeNotOverlaping() {
        // given
        DateRange subject = DateRange.create("2012-01-01", "2014-01-01");
        DateRange other = DateRange.create("2014-01-01", "2016-01-01");

        // when
        subject.subRange(other);

        // then expect exception
    }

    @Test
    public void contains() {
        // given
        LocalDate start = TimeUtil.createDate("2014-01-01");
        LocalDate end = start.plusYears(2);
        DateRange subject = DateRange.create(start, end);

        // then
        assertEquals(false, subject.contains(start.minusYears(1)));
        assertEquals(true, subject.contains(start));
        assertEquals(true, subject.contains(start.plusYears(1)));
        assertEquals(false, subject.contains(end));
    }

    @Test(expected = IllegalArgumentException.class)
    public void fromAfterTo() {
        DateRange.create("2018-01-01", "2016-01-01");
    }

    @Test(expected = IllegalArgumentException.class)
    public void fromEqualTo() {
        DateRange.create("2016-01-01", "2016-01-01");
    }
}
