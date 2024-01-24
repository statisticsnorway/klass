package no.ssb.klass.api.dto.hal;

import static org.junit.jupiter.api.Assertions.*;

import no.ssb.klass.api.dto.CorrespondenceItem;
import no.ssb.klass.api.dto.CorrespondenceItem.RangedCorrespondenceItem;
import no.ssb.klass.api.dto.CorrespondenceItemList;
import no.ssb.klass.core.util.DateRange;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;


public class CorrespondenceItemListTest {

    @Test
    public void mergeTest() {
        // given
        CorrespondenceItem item = new CorrespondenceItem("1", "A", "a", "2", "B", "b");
        List<RangedCorrespondenceItem> items = Arrays.asList(
                new RangedCorrespondenceItem(item, DateRange.create("2010-01-01", "2011-01-01")),

                new RangedCorrespondenceItem(item, DateRange.create("2006-01-01", "2007-01-01")),
                new RangedCorrespondenceItem(item, DateRange.create("2007-01-01", "2008-01-01")),

                new RangedCorrespondenceItem(item, DateRange.create("2012-01-01", "2013-01-01")),
                new RangedCorrespondenceItem(item, DateRange.create("2013-01-01", "2014-01-01")),
                new RangedCorrespondenceItem(item, DateRange.create("2014-01-01", null))
        );
        CorrespondenceItemList list = new CorrespondenceItemList('-', true, items, true);

        // when
        List<RangedCorrespondenceItem> result = list.merge();

        // then
        assertEquals(result.get(0).getDateRange(true), DateRange.create("2006-01-01", "2008-01-01"));
        assertEquals(result.get(1).getDateRange(false), DateRange.create("2010-01-01", "2011-01-01"));
        assertEquals(result.get(2).getDateRange(true), DateRange.create("2012-01-01", null));
    }
}
