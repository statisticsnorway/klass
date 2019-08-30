package no.ssb.klass.api.dto;

import static java.util.stream.Collectors.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.google.common.base.Preconditions;

import no.ssb.klass.core.service.dto.CorrespondenceDto;
import no.ssb.klass.core.util.DateRange;
import no.ssb.klass.core.util.TimeUtil;
import no.ssb.klass.api.dto.CorrespondenceItem.RangedCorrespondenceItem;

@JacksonXmlRootElement(localName = "correspondenceItemList")
public class CorrespondenceItemList {
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
        return correspondenceItems.stream().map(i -> new CorrespondenceItem(i)).collect(toList());
    }

    @JsonIgnore
    public char getCsvSeparator() {
        return csvSeparator;
    }

    public CorrespondenceItemList convert(List<CorrespondenceDto> correspondences) {
        return newCorrespondenceItemList(correspondences.stream().map(RangedCorrespondenceItem::new).collect(toList()));
    }

    private CorrespondenceItemList newCorrespondenceItemList(List<RangedCorrespondenceItem> items) {
        return new CorrespondenceItemList(csvSeparator, displayWithValidRange, items, includeFuture);
    }

    public CorrespondenceItemList removeOutside(DateRange dateRange) {
        Preconditions.checkNotNull(dateRange);
        return newCorrespondenceItemList(correspondenceItems.stream().filter(correspondenceItem -> correspondenceItem
                .getDateRange(includeFuture).overlaps(dateRange)).collect(toList()));
    }

    public CorrespondenceItemList limit(DateRange dateRange) {
        return newCorrespondenceItemList(correspondenceItems.stream().map(
                correspondenceItem -> new RangedCorrespondenceItem(correspondenceItem, correspondenceItem.getDateRange(includeFuture)
                        .subRange(dateRange))).collect(toList()));
    }

    public CorrespondenceItemList compress() {
        Map<RangedCorrespondenceItem, List<RangedCorrespondenceItem>> grouped = correspondenceItems.stream().collect(
                groupingBy(correspondenceItem -> correspondenceItem));
        return newCorrespondenceItemList(grouped.entrySet().stream().map(entry -> combineCorrespondenceItems(entry
                .getKey(), entry.getValue())).collect(toList()));
    }

    private RangedCorrespondenceItem combineCorrespondenceItems(RangedCorrespondenceItem base,
            List<RangedCorrespondenceItem> correspondenceItems) {
        // TODO kmgv need to check dateRanges of correspondenceItems, and group those that are back to back.
        DateRange dateRange = DateRange.create(minValidFrom(correspondenceItems), maxValidTo(correspondenceItems));
        return new RangedCorrespondenceItem(base, dateRange);
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

}
