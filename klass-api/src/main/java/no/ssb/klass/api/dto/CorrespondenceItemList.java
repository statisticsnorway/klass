package no.ssb.klass.api.dto;

import static java.util.stream.Collectors.*;

import java.time.LocalDate;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.google.common.base.Preconditions;

import no.ssb.klass.core.service.dto.CorrespondenceDto;
import no.ssb.klass.core.util.DateRange;
import no.ssb.klass.core.util.TimeUtil;
import no.ssb.klass.api.dto.CorrespondenceItem.RangedCorrespondenceItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JacksonXmlRootElement(localName = "correspondenceItemList")
public class CorrespondenceItemList {
    private static final Logger log = LoggerFactory.getLogger(CorrespondenceItemList.class);

    private final char csvSeparator;
    private final boolean displayWithValidRange;
    private final List<RangedCorrespondenceItem> correspondenceItems;
    private final Boolean includeFuture;

    public CorrespondenceItemList(String csvSeparator, boolean displayWithValidRange, boolean includeFuture) {
        if (csvSeparator.toCharArray().length != 1) {
            throw new IllegalArgumentException("Separator must be a single character");
        }
        this.csvSeparator = csvSeparator.charAt(0);
        this.displayWithValidRange = displayWithValidRange;
        this.correspondenceItems = new ArrayList<>();
        this.includeFuture = includeFuture;
    }

    public CorrespondenceItemList(char csvSeparator, boolean displayWithValidRange,
            List<RangedCorrespondenceItem> correspondenceItems, boolean includeFuture) {
        this.csvSeparator = csvSeparator;
        this.displayWithValidRange = displayWithValidRange;
        this.correspondenceItems = correspondenceItems;
        this.includeFuture = includeFuture;
    }

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "correspondenceItem")
    public List<? extends CorrespondenceItem> getCorrespondenceItems() {
        if (displayWithValidRange) {
            return correspondenceItems;
        }
        List<CorrespondenceItem> list = new ArrayList<>();
        for (RangedCorrespondenceItem correspondenceItem : correspondenceItems) {
            CorrespondenceItem item = new CorrespondenceItem(correspondenceItem);
            list.add(item);
        }
        return list;
    }

    @JsonIgnore
    public char getCsvSeparator() {
        return csvSeparator;
    }

    public CorrespondenceItemList convert(List<CorrespondenceDto> correspondences) {
        List<RangedCorrespondenceItem> list = new ArrayList<>();
        for (CorrespondenceDto correspondence : correspondences) {
            RangedCorrespondenceItem rangedCorrespondenceItem = new RangedCorrespondenceItem(correspondence);
            list.add(rangedCorrespondenceItem);
        }
        return newList(list);
    }

    private CorrespondenceItemList newList(List<RangedCorrespondenceItem> items) {
        return new CorrespondenceItemList(csvSeparator, displayWithValidRange, items, includeFuture);
    }

    public CorrespondenceItemList removeOutside(DateRange dateRange) {
        Preconditions.checkNotNull(dateRange);
        List<RangedCorrespondenceItem> list = new ArrayList<>();
        for (RangedCorrespondenceItem i : correspondenceItems) {
            if (i.getDateRange(includeFuture).overlaps(dateRange)) {
                list.add(i);
            }
        }
        return newList(list);
    }

    public CorrespondenceItemList limit(DateRange dateRange) {
        List<RangedCorrespondenceItem> aremark = new ArrayList<>();
        for (RangedCorrespondenceItem correspondenceItem : correspondenceItems) {
            if (correspondenceItem.getTargetName().equalsIgnoreCase("aremark")) {
                aremark.add(correspondenceItem);
            }
        }
        aremark.sort(Comparator.comparing(RangedCorrespondenceItem::getValidFrom));
        log.error("\nKF-316: before limit aremark " + aremark);

        List<RangedCorrespondenceItem> list = new ArrayList<>();
        for (RangedCorrespondenceItem i : correspondenceItems) {
            RangedCorrespondenceItem rangedCorrespondenceItem = new RangedCorrespondenceItem(i, i.getDateRange(includeFuture).subRange(dateRange));
            list.add(rangedCorrespondenceItem);
        }
        return newList(list);
    }

    public CorrespondenceItemList group1() {
        List<RangedCorrespondenceItem> aremark = new ArrayList<>();
        for (RangedCorrespondenceItem item : correspondenceItems) {
            if (item.getTargetName().equalsIgnoreCase("aremark")) {
                aremark.add(item);
            }
        }
        aremark.sort(Comparator.comparing(RangedCorrespondenceItem::getValidFrom));
        log.error("\nKF-316: before merge aremark list" + aremark);

        Map<RangedCorrespondenceItem, List<RangedCorrespondenceItem>> grouped = new HashMap<>();
        for (RangedCorrespondenceItem correspondenceItem : correspondenceItems) {
            grouped.computeIfAbsent(correspondenceItem, k -> new ArrayList<>()).add(correspondenceItem);
        }
        log.error("\nKF-316: grouped " + grouped.keySet());

        List<RangedCorrespondenceItem> list = new ArrayList<>();
        for (List<RangedCorrespondenceItem> rangedCorrespondenceItems : grouped.values()) {
            List<RangedCorrespondenceItem> merge = newList(rangedCorrespondenceItems).merge();
            for (RangedCorrespondenceItem rangedCorrespondenceItem : merge) {
                list.add(rangedCorrespondenceItem);
            }
        }
        CorrespondenceItemList o = newList(list);

        List<CorrespondenceItem> aremark_o = new ArrayList<>();
        for (CorrespondenceItem i : o.getCorrespondenceItems()) {
            if (i.getTargetName().equalsIgnoreCase("aremark")) {
                aremark_o.add(i);
            }
        }
        log.error("\nKF-316: compressed list" + aremark_o);
        return o;
    }

    public CorrespondenceItemList group() {
        List<RangedCorrespondenceItem> aremark = new ArrayList<>();
        for (RangedCorrespondenceItem item : correspondenceItems) {
            if (item.getTargetName().equalsIgnoreCase("aremark")) {
                aremark.add(item);
            }
        }
        aremark.sort(Comparator.comparing(RangedCorrespondenceItem::getValidFrom));
        log.error("\nKF-316: before merge aremark list" + aremark);

        Map<RangedCorrespondenceItem, List<RangedCorrespondenceItem>> grouped = new HashMap<>();
        for (RangedCorrespondenceItem correspondenceItem : correspondenceItems) {
            grouped.computeIfAbsent(correspondenceItem, k -> new ArrayList<>()).add(correspondenceItem);
        }

        List<RangedCorrespondenceItem> list = new ArrayList<>();
        for (List<RangedCorrespondenceItem> i : grouped.values()) {
            List<RangedCorrespondenceItem> merge = newList(i).merge();
            for (RangedCorrespondenceItem rangedCorrespondenceItem : merge) {
                list.add(rangedCorrespondenceItem);
            }
        }
        return newList(list);
    }

    // NB! suits for equal correspondence items (source and target, but not range)
    public List<RangedCorrespondenceItem> merge() {
        if (correspondenceItems == null || correspondenceItems.isEmpty()) {
            return Collections.emptyList();
        }

        correspondenceItems.sort(Comparator.comparing(RangedCorrespondenceItem::getValidFrom));

        List<DateRange> ranges = new ArrayList<>();
        correspondenceItems.forEach(i -> {
                DateRange next = i.getDateRange(includeFuture);
                DateRange prev = ranges.isEmpty() ? next : ranges.remove(ranges.size() - 1);

                ranges.add(next.contiguous(prev)
                        ? new DateRange(prev.getFrom(), next.getTo())
                        : next);
            });
        return ranges.stream().map(i -> new RangedCorrespondenceItem(correspondenceItems.get(0), i)).collect(toList());
    }

    public CorrespondenceItemList sort() {
        Collections.sort(correspondenceItems);
        return this;
    }

    public Class<?> classificationItemsJavaType() {
        return displayWithValidRange ? RangedCorrespondenceItem.class : CorrespondenceItem.class;
    }

    @Override
    public String toString() {
        return "CorrespondenceItemList{" +
                "csvSeparator=" + csvSeparator +
                ", displayWithValidRange=" + displayWithValidRange +
                ", correspondenceItems=" + correspondenceItems +
                ", includeFuture=" + includeFuture +
                '}';
    }
}
