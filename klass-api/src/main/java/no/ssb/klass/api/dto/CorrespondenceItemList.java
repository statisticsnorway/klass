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
                .map(i -> new RangedCorrespondenceItem(i, i.getDateRange().subRange(dateRange)))
                .collect(toList()));
    }

/*    public CorrespondenceItemList compress1() {
        Map<RangedCorrespondenceItem, List<RangedCorrespondenceItem>> grouped = correspondenceItems.stream()
                .collect(groupingBy(i -> i));
        return newList(grouped.entrySet().stream()
                .map(entry -> combine(entry.getKey(), entry.getValue())).collect(toList()));
    }

    private RangedCorrespondenceItem combine(RangedCorrespondenceItem base,
            List<RangedCorrespondenceItem> correspondenceItems) {
        // TODO kmgv need to check dateRanges of correspondenceItems, and group those that are back to back.
        DateRange dateRange = DateRange.create(minValidFrom(correspondenceItems), maxValidTo(correspondenceItems));
        return new RangedCorrespondenceItem(base, dateRange);
    }*/

    public CorrespondenceItemList compress() {
        List<RangedCorrespondenceItem> aremark = correspondenceItems.stream()
                .filter(i -> i.getTargetName().equalsIgnoreCase("aremark"))
                .sorted(Comparator.comparing(RangedCorrespondenceItem::getValidFrom))
                .collect(toList());
        log.error("\nKF-316: before compress aremark list" + aremark);

        Map<RangedCorrespondenceItem, List<RangedCorrespondenceItem>> grouped = correspondenceItems.stream().collect(groupingBy(i -> i));
        log.error("\nKF-316: grouped aremark group");

        CorrespondenceItemList o = newList(grouped.entrySet().stream()
                .map(i -> newList(i.getValue()).mergeContiguous())
                .flatMap(Collection::stream)
                .collect(toList()));

        List<CorrespondenceItem> aremark_o = o.getCorrespondenceItems().stream()
                .filter(i -> i.getTargetName().equalsIgnoreCase("aremark"))
                .collect(toList());
        log.error("\nKF-316: compressed list" + aremark_o);
        return o;
    }

    // NB! suits for equal correspondence items (source and target, but not range)
    public List<RangedCorrespondenceItem> mergeContiguous() {
        log.error("KF-316: mergeContiguous");

        if (correspondenceItems == null || correspondenceItems.isEmpty()) {
            log.error("KF-316: mergeContiguous EMPTY");
            return Collections.emptyList();
        }
        if (correspondenceItems.get(0).getTargetName().equalsIgnoreCase("aremark")) {
            log.error("KF-316: mergeContiguous aremark ", correspondenceItems);
        }
        if (correspondenceItems.size() == 1) {
            log.error("KF-316: mergeContiguous SINGLE");
            return Collections.singletonList(correspondenceItems.get(0));
        }

        // TODO: strait forward logic, try with groupingBy map later
        // The code could be simpler if correspondence item members or data range member are not final
        correspondenceItems.sort(Comparator.comparing(RangedCorrespondenceItem::getValidFrom));
        if (correspondenceItems.get(0).getTargetName().equalsIgnoreCase("aremark")) {
            log.error("KF-316: sorted aremark list " + correspondenceItems);
        }
        List<DateRange> ranges = new ArrayList<>();
        ranges.add(correspondenceItems.get(0).getDateRange(includeFuture));
        correspondenceItems.forEach(i -> {

            DateRange lastRange = ranges.get(ranges.size() - 1);
            DateRange nextItemRange = i.getDateRange();
            if (correspondenceItems.get(0).getTargetName().equalsIgnoreCase("aremark")) {
                log.error("KF-316: merge aremark " + lastRange + " and "+ nextItemRange);
            }
            if (lastRange.contiguous(nextItemRange)) {
                lastRange = new DateRange(lastRange.getFrom(), nextItemRange.getTo());
            } else {
                ranges.add(nextItemRange);
            }
            if (correspondenceItems.get(0).getTargetName().equalsIgnoreCase("aremark")) {
                log.error("KF-316: last aremark range " + ranges.get(ranges.size() - 1));
            }
        });
        return ranges.stream().map(i -> new RangedCorrespondenceItem(correspondenceItems.get(0), i)).collect(toList());
    }

    private LocalDate maxValidTo(List<RangedCorrespondenceItem> correspondenceItems) {
        return TimeUtil.max(correspondenceItems.stream().map(correspondenceItem -> correspondenceItem.getDateRange(includeFuture)
                .getTo()).collect(toList()));
    }

    private LocalDate minValidFrom(List<RangedCorrespondenceItem> correspondenceItems) {
        return TimeUtil.min(correspondenceItems.stream().map(correspondenceItem -> correspondenceItem.getDateRange(includeFuture)
                .getFrom()).collect(toList()));
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
