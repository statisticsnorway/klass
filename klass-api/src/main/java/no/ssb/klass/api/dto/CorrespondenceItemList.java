package no.ssb.klass.api.dto;

import static java.util.stream.Collectors.*;
import java.util.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.google.common.base.Preconditions;
import no.ssb.klass.core.service.dto.CorrespondenceDto;
import no.ssb.klass.core.util.DateRange;
import no.ssb.klass.api.dto.CorrespondenceItem.RangedCorrespondenceItem;

@JacksonXmlRootElement(localName = "correspondenceItemList")
public class CorrespondenceItemList {
    private final char csvSeparator;
    private final boolean displayWithValidRange;
    private final List<RangedCorrespondenceItem> correspondenceItems;
    private final Boolean includeFuture;
    private List<String> csvFields;

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

    @JsonIgnore
    public List<String> getCsvFields() {
        return csvFields;
    }

    @JsonIgnore
    public void setCsvFields(List<String> csvFields) {
        this.csvFields = csvFields;
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
        return newList(correspondenceItems.stream()
                .map(i -> new RangedCorrespondenceItem(i, i.getDateRange(includeFuture).subRange(dateRange)))
                .collect(toList()));
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
            DateRange prev = ranges.isEmpty() ? next : ranges.get(ranges.size() - 1);

            if (prev.contiguous(next)) {
                ranges.set(ranges.size() - 1, new DateRange(prev.getFrom(), next.getTo()));
            } else {
                ranges.add(next);
            }
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
