package no.ssb.klass.core.util;

import static org.junit.Assert.*;

import java.time.LocalDate;

import org.junit.Ignore;
import org.junit.Test;

public class DateRangeTest {

    /**
     * <pre>
     *    subject   |------|
     *    other         |------|
     * </pre>
     */
    @Test
    public void overlapsTest() {
        // given
        DateRange subject = DateRange.create("2012-01-01", "2014-01-01");
        DateRange other = DateRange.create("2013-01-01", "2015-01-01");

        // when
        boolean result = subject.overlaps(other);

        // then
        assertTrue(result);
    }

    /**
     * <pre>
     *    subject   |------|
     *    other            |------|
     * </pre>
     */
    @Test
    public void overlapsSubjectFirstTest() {
        // given
        DateRange subject = DateRange.create("2012-01-01", "2014-01-01");
        DateRange other = DateRange.create("2014-01-01", "2016-01-01");

        // when
        boolean result = subject.overlaps(other);

        // then
        assertFalse(result);
    }

    /**
     * <pre>
     *    subject          |------|
     *    other     |------|
     *    result         false
     * </pre>
     */
    @Test
    public void overlapsSubjectLastTest() {
        // given
        DateRange subject = DateRange.create("2014-01-01", "2016-01-01");
        DateRange other = DateRange.create("2012-01-01", "2014-01-01");

        // when
        boolean result = subject.overlaps(other);

        // then
        assertFalse(result);
    }

    /**
     * <pre>
     *    subject       |------|
     *    other     |------|
     *    result        |--|
     * </pre>
     */
    @Test
    public void subRangeTest() {
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
     *    subject   |---------|
     *    other      |-----|
     *    result     |-----|
     * </pre>
     */
    @Test
    public void subRangeInclusiveTest() {
        // given
        DateRange subject = DateRange.create("2011-01-01", "2015-01-01");
        DateRange other = DateRange.create("2013-01-01", "2014-01-01");

        // when
        DateRange result = subject.subRange(other);

        // then
        DateRange expected = DateRange.create("2013-01-01", "2014-01-01");
        assertEquals(expected, result);
    }

    /**
     * <pre>
     *    subject    |-----|
     *    other    |--------|
     *    result     |-----|
     * </pre>
     */
    @Test
    public void subRangeCommutativeTest() {
        // given
        DateRange other = DateRange.create("2011-01-01", "2015-01-01");
        DateRange subject = DateRange.create("2013-01-01", "2014-01-01");

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
     *    result        none
     * </pre>
     */
    @Test(expected = IllegalArgumentException.class)
    public void subRangeNoOverlapTest() {
        // given
        DateRange subject = DateRange.create("2012-01-01", "2014-01-01");
        DateRange other = DateRange.create("2014-01-01", "2016-01-01");

        // when
        subject.subRange(other);

        // then expect exception
    }

    @Test
    public void containsTest() {
        // given
        LocalDate start = TimeUtil.createDate("2014-01-01");
        LocalDate end = start.plusYears(2);
        DateRange subject = DateRange.create(start, end);

        // then
        assertTrue(subject.contains(start));
        assertTrue(subject.contains(start.plusYears(1)));

        assertFalse(subject.contains(start.minusYears(1)));
        assertFalse(subject.contains(end));
    }

    @Test(expected = IllegalArgumentException.class)
    public void fromAfterTo() {
        DateRange.create("2018-01-01", "2016-01-01");
    }

    @Test(expected = IllegalArgumentException.class)
    public void fromEqualTo() {
        DateRange.create("2016-01-01", "2016-01-01");
    }

    /**
     * <pre>
     *    prev     |------|
     *    next            |------|
     *    result         true
     * </pre>
     */
    @Test
    public void contiguousTest() {
        // given
        DateRange prev = DateRange.create("2014-01-01", "2016-01-01");
        DateRange next = DateRange.create("2016-01-01", "2017-01-01");

        // when
        boolean result1 = prev.contiguous(next);
        boolean result2 = next.contiguous(prev);

        // then
        assertTrue(result1);
        assertFalse(result2);
    }

    /**
     * <pre>
     *    prev     |------|
     *    next              |------|
     *    result         false
     * </pre>
     */
    @Test
    public void contiguousGapTest() {
        // given
        DateRange prev = DateRange.create("2014-01-01", "2015-01-01");
        DateRange next = DateRange.create("2016-01-01", "2017-01-01");

        // when
        boolean result = prev.contiguous(next);

        // then
        assertFalse(result);
    }

    /**
     * <pre>
     *    prev     |------|
     *    next          |------|
     *    result         false
     * </pre>
     */
    @Test
    public void contiguousOverlapTest() {
        // given
        DateRange prev = DateRange.create("2014-01-01", "2016-01-01");
        DateRange next = DateRange.create("2015-01-01", "2017-01-01");

        // when
        boolean result = prev.contiguous(next);

        // then
        assertFalse(result);
    }
}
