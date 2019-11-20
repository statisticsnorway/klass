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
        return correspondenceItems.stream().map(CorrespondenceItem::new).collect(toList());
    }

    @JsonIgnore
    public char getCsvSeparator() {
        return csvSeparator;
    }

    public CorrespondenceItemList convert(List<CorrespondenceDto> correspondences) {
        return newList(correspondences.stream()
                .map(RangedCorrespondenceItem::new)
                .collect(toList()));
    }

    private CorrespondenceItemList newList(List<RangedCorrespondenceItem> items) {
        return new CorrespondenceItemList(csvSeparator, displayWithValidRange, items, includeFuture);
    }

    public CorrespondenceItemList removeOutside(DateRange dateRange) {
        Preconditions.checkNotNull(dateRange);
        return newList(correspondenceItems.stream()
                .filter(i -> i.getDateRange(includeFuture).overlaps(dateRange))
                .collect(toList()));
    }

    public CorrespondenceItemList limit(DateRange dateRange) {
        List<RangedCorrespondenceItem> aremark = correspondenceItems.stream()
                .filter(i -> i.getTargetName().equalsIgnoreCase("aremark"))
                .sorted(Comparator.comparing(RangedCorrespondenceItem::getValidFrom))
                .collect(toList());
        log.error("\nKF-316: before limit aremark " + aremark);

        return newList(correspondenceItems.stream()
                .map(i -> new RangedCorrespondenceItem(i, i.getDateRange(includeFuture).subRange(dateRange)))
                .collect(toList()));
    }

    public CorrespondenceItemList group1() {
        List<RangedCorrespondenceItem> aremark = correspondenceItems.stream()
                .filter(i -> i.getTargetName().equalsIgnoreCase("aremark"))
                .sorted(Comparator.comparing(RangedCorrespondenceItem::getValidFrom))
                .collect(toList());
        log.error("\nKF-316: before merge aremark list" + aremark);

        Map<RangedCorrespondenceItem, List<RangedCorrespondenceItem>> grouped = correspondenceItems.stream().collect(groupingBy(i -> i));
        log.error("\nKF-316: grouped " + grouped.keySet());

        CorrespondenceItemList o = newList(grouped.values().stream()
                .map(i -> newList(i).merge())
                .flatMap(Collection::stream)
                .collect(toList()));

        List<CorrespondenceItem> aremark_o = o.getCorrespondenceItems().stream()
                .filter(i -> i.getTargetName().equalsIgnoreCase("aremark"))
                .collect(toList());
        log.error("\nKF-316: compressed list" + aremark_o);
        return o;
    }

    public CorrespondenceItemList group() {
        Map<RangedCorrespondenceItem, List<RangedCorrespondenceItem>> grouped = correspondenceItems.stream().collect(groupingBy(i -> i));

        return newList(grouped.values().stream()
                .map(i -> newList(i).merge())
                .flatMap(Collection::stream)
                .collect(toList()));
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

            ranges.add(prev.contiguous(next)
                    ? new DateRange(prev.getFrom(), next.getTo())
                    : next);
        });

        if (correspondenceItems.get(0).getTargetName().equalsIgnoreCase("aremark")) {
            log.error("\nKF-316: ranges " + ranges);
        }

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
