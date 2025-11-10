package no.ssb.klass.api.dto;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.google.common.base.Strings;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import no.ssb.klass.api.dto.CodeItem.RangedCodeItem;
import no.ssb.klass.api.util.PresentationNameBuilder;
import no.ssb.klass.core.service.dto.CodeDto;
import no.ssb.klass.core.util.DateRange;
import no.ssb.klass.core.util.TimeUtil;

@JacksonXmlRootElement(localName = "codeList")
public class CodeList {
  private static final String RANGE_REGEX = "\\s*(?<from>[^,\\s]*)\\s*\\-\\s*(?<to>[^,\\s]*)";
  private final char csvSeparator;
  private final boolean displayWithValidRange;
  private final List<RangedCodeItem> codeItems;
  private final Boolean includeFuture;
  private final DateRange dateRange;
  private List<String> csvFields;

  public CodeList(
      String csvSeparator,
      boolean displayWithValidRange,
      DateRange dateRange,
      Boolean includeFuture) {
    if (csvSeparator.toCharArray().length != 1) {
      throw new IllegalArgumentException("Separator must be a single character");
    }
    this.csvSeparator = csvSeparator.charAt(0);
    this.displayWithValidRange = displayWithValidRange;
    this.codeItems = new ArrayList<>();
    this.includeFuture = includeFuture;
    this.dateRange = dateRange;
  }

  public CodeList(
      char csvSeparator,
      boolean displayWithValidRange,
      DateRange dateRange,
      List<RangedCodeItem> codes,
      Boolean includeFuture) {
    this.csvSeparator = csvSeparator;
    this.displayWithValidRange = displayWithValidRange;
    this.codeItems = codes;
    this.includeFuture = includeFuture;
    this.dateRange = dateRange;
  }

  @JacksonXmlElementWrapper(useWrapping = false)
  @JacksonXmlProperty(localName = "codeItem")
  public List<? extends CodeItem> getCodes() {
    if (displayWithValidRange) {
      return codeItems;
    }
    return codeItems.stream().map(CodeItem::new).collect(toList());
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

  public CodeList merge(CodeList other) {
    List<RangedCodeItem> combined = new ArrayList<>(codeItems);
    combined.addAll(other.codeItems);
    return newCodeList(combined);
  }

  public CodeList compress() {
    Map<RangedCodeItem, List<RangedCodeItem>> grouped =
        codeItems.stream().collect(groupingBy(codeItem -> codeItem));

    List<RangedCodeItem> combinedItems = new LinkedList<>();
    grouped.forEach((key, value) -> combinedItems.addAll(combineCodeItems(key, value)));
    return newCodeList(combinedItems);
  }

  private List<RangedCodeItem> combineCodeItems(
      RangedCodeItem base, List<RangedCodeItem> codeItems) {
    List<RangedCodeItem> orderedByFrom =
        codeItems.stream()
            .sorted(Comparator.comparing(RangedCodeItem::getValidFromInRequestedRange))
            .collect(toList());

    List<RangedCodeItem> combinedItems = new LinkedList<>();
    LocalDate beginning = null;
    LocalDate end = null;

    int size = orderedByFrom.size();
    for (int i = 0; i < size; i++) {
      // if no dates to compare, set current item date
      RangedCodeItem item = orderedByFrom.get(i);

      if (beginning == null) {
        beginning = item.getValidFromInRequestedRange();
        end = item.getValidToInRequestedRange();
      }

      if (item.getRequestPeriodRange().isCurrentVersion() && !includeFuture) {
        end = TimeUtil.isMaxDate(dateRange.getTo()) ? null : dateRange.getTo();
      }
      // if we have another item in our list, peek and look for back to back items
      int nextId = i + 1;
      if (nextId < size) {
        // if next items are back to back set new end date
        RangedCodeItem nextItem = orderedByFrom.get(nextId);
        LocalDate nextValidTo = nextItem.getValidToInRequestedRange();
        LocalDate nextValidFrom = nextItem.getValidFromInRequestedRange();
        // due to sorting only the last item should in theory be null if no requestRange end date is
        // provided
        // however just as a safety measure we do a null check non the less.
        if (end != null && end.isEqual(nextValidFrom)) {
          end = nextValidTo;
        } else {
          // not back to back, create item and update date variables
          DateRange combinedRange = DateRange.create(beginning, end);
          RangedCodeItem combinedItem = new RangedCodeItem(base, combinedRange);
          combinedItems.add(combinedItem);

          beginning = nextItem.getValidFromInRequestedRange();
          end = nextItem.getValidToInRequestedRange();
        }
      } else {
        // this is the last item, create item
        DateRange combinedRange = DateRange.create(beginning, end);
        RangedCodeItem combinedItem = new RangedCodeItem(base, combinedRange);
        combinedItems.add(combinedItem);
      }
    }
    return combinedItems;
  }

  public CodeList limit(DateRange dateRange) {
    return newCodeList(
        codeItems.stream()
            .map(
                codeItem ->
                    new RangedCodeItem(
                        codeItem,
                        codeItem
                            .getRequestPeriodRange()
                            .subRange(dateRange)
                            .subRange(
                                new DateRange(
                                    codeItem.getValidFrom() != null
                                        ? codeItem.getValidFrom()
                                        : LocalDate.MIN,
                                    codeItem.getValidTo() != null
                                        ? codeItem.getValidTo()
                                        : LocalDate.MAX))))
            .collect(toList()));
  }

  public CodeList filterValidity(DateRange dateRange) {
    return newCodeList(
        codeItems.stream()
            .filter(item -> item.getValidity() == null || dateRange.overlaps(item.getValidity()))
            .collect(toList()));
  }

  public CodeList convert(List<CodeDto> codes) {
    List<RangedCodeItem> result = new ArrayList<>();
    for (CodeDto code : codes) {
      result.add(new RangedCodeItem(code));
    }
    return newCodeList(result);
  }

  public CodeList filterOnLevel(String level) {
    if (Strings.isNullOrEmpty(level)) {
      return this;
    }
    return newCodeList(
        codeItems.stream().filter(codeItem -> level.equals(codeItem.getLevel())).collect(toList()));
  }

  public CodeList filterOnCodes(String selectCodes) {
    if (Strings.isNullOrEmpty(selectCodes)) {
      return this;
    }

    Map<String, String> ranges = findRanges(selectCodes);
    String pattern =
        selectCodes
            .replaceAll(RANGE_REGEX, "") // remove ranges (stored in map above(findRanges))
            .replace(" ", "") // remove whitespace
            .replace(',', '|') // remove commas and insert OR operator
            .replace("*", "\\w*"); // replace * with any word character(any amount)
    return newCodeList(
        codeItems.stream()
            .filter(
                codeItem ->
                    Pattern.matches(pattern, codeItem.getCode())
                        || compareRanges(codeItem.getCode(), ranges))
            .collect(toList()));
  }

  private Map<String, String> findRanges(String input) {
    Map<String, String> ranges = new HashMap<>();
    Pattern p = Pattern.compile(RANGE_REGEX);
    Matcher m = p.matcher(input);
    if (m.find()) {
      String from = m.group("from").replaceAll("\\*", "");
      String to = m.group("to").replaceAll("\\*", String.valueOf(Character.MAX_VALUE));
      ranges.put(from, to);
    }
    return ranges;
  }

  private Boolean compareRanges(String value, Map<String, String> ranges) {
    for (Map.Entry<String, String> entry : ranges.entrySet()) {
      if (entry.getKey().compareTo(value) <= 0 && value.compareTo(entry.getValue()) <= 0) {
        return true;
      }
    }
    return false;
  }

  public CodeList sort() {
    Collections.sort(codeItems);
    return this;
  }

  public CodeList presentationNames(String presentationNamePattern) {
    PresentationNameBuilder presentationNameBuilder =
        new PresentationNameBuilder(presentationNamePattern);
    return newCodeList(
        codeItems.stream()
            .map(codeItem -> new RangedCodeItem(codeItem, presentationNameBuilder))
            .collect(toList()));
  }

  private CodeList newCodeList(List<RangedCodeItem> codeItems) {
    return new CodeList(csvSeparator, displayWithValidRange, dateRange, codeItems, includeFuture);
  }

  public Class<?> codeItemsJavaType() {
    return displayWithValidRange ? RangedCodeItem.class : CodeItem.class;
  }
}
