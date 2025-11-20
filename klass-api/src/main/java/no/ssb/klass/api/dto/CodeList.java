package no.ssb.klass.api.dto;

import static java.util.stream.Collectors.groupingBy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.google.common.base.Strings;

import no.ssb.klass.api.dto.CodeItem.RangedCodeItem;
import no.ssb.klass.api.util.PresentationNameBuilder;
import no.ssb.klass.core.service.dto.CodeDto;
import no.ssb.klass.core.util.DateRange;
import no.ssb.klass.core.util.TimeUtil;

import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;

@JacksonXmlRootElement(localName = "codeList")
public class CodeList {
    private static final int MAX_SELECTCODES_LENGTH = 200;
    private static final int MAX_PARAMETERS = 20;
    private static final Pattern LEGAL_SELECTCODES_CHARS = Pattern.compile("^[0-9,\\-*\\s]+$");
    private static final Pattern DIGITS = Pattern.compile("^\\d+$");
    private static final Pattern DIGITS_OR_STARS = Pattern.compile("^[0-9*]+$");
    private final char csvSeparator;
    private final boolean displayWithValidRange;
    private final List<RangedCodeItem> codeItems;
    private final boolean includeFuture;
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
        this.includeFuture = defaultTrue(includeFuture);
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
        this.codeItems = new ArrayList<>(codes);
        this.includeFuture = defaultTrue(includeFuture);
        this.dateRange = dateRange;
    }

    private static boolean defaultTrue(Boolean value) {
        return value == null || value;
    }

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "codeItem")
    @SuppressWarnings("squid:S1452") // wildcard required: returns RangedCodeItem or CodeItem
    public List<? extends CodeItem> getCodes() {
        if (displayWithValidRange) {
            return codeItems;
        }
        return codeItems.stream().map(CodeItem::new).toList();
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

    public CodeList compress() {
        Map<RangedCodeItem, List<RangedCodeItem>> grouped =
                codeItems.stream().collect(groupingBy(codeItem -> codeItem));
        List<RangedCodeItem> combinedItems = new LinkedList<>();
        grouped.forEach((key, value) -> combinedItems.addAll(combineCodeItems(key, value)));
        return newCodeList(combinedItems);
    }

    List<RangedCodeItem> combineCodeItems(RangedCodeItem base, List<RangedCodeItem> codeItems) {
        if (codeItems.isEmpty()) {
            return List.of();
        }
        List<RangedCodeItem> orderedByFrom =
                codeItems.stream()
                        .sorted(Comparator.comparing(RangedCodeItem::getValidFromInRequestedRange))
                        .toList();
        List<RangedCodeItem> combinedItems = new ArrayList<>();
        LocalDate beginning = null;
        LocalDate end = null;
        for (RangedCodeItem item : orderedByFrom) {
            LocalDate itemFrom = item.getValidFromInRequestedRange();
            LocalDate itemTo = item.getValidToInRequestedRange();
            if (!includeFuture && item.getRequestPeriodRange().isCurrentVersion()) {
                LocalDate to = dateRange.getTo();
                itemTo = TimeUtil.isMaxDate(to) ? null : to;
            }
            if (beginning == null) {
                beginning = itemFrom;
            } else if (end == null || !end.isEqual(itemFrom)) {
                DateRange combinedRange = DateRange.create(beginning, end);
                combinedItems.add(new RangedCodeItem(base, combinedRange));
                beginning = itemFrom;
            }
            end = itemTo;
        }
        if (beginning != null) {
            DateRange combinedRange = DateRange.create(beginning, end);
            combinedItems.add(new RangedCodeItem(base, combinedRange));
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
                                                codeItem.getRequestPeriodRange()
                                                        .subRange(dateRange)
                                                        .subRange(
                                                                new DateRange(
                                                                        codeItem.getValidFrom()
                                                                                        != null
                                                                                ? codeItem
                                                                                        .getValidFrom()
                                                                                : LocalDate.MIN,
                                                                        codeItem.getValidTo()
                                                                                        != null
                                                                                ? codeItem
                                                                                        .getValidTo()
                                                                                : LocalDate.MAX))))
                        .toList());
    }

    public CodeList filterValidity(DateRange dateRange) {
        return newCodeList(
                codeItems.stream()
                        .filter(
                                item ->
                                        item.getValidity() == null
                                                || dateRange.overlaps(item.getValidity()))
                        .toList());
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
                codeItems.stream().filter(codeItem -> level.equals(codeItem.getLevel())).toList());
    }

    public CodeList filterOnCodes(String selectCodes) {
        if (isNoFilter(selectCodes)) {
            return this;
        }
        validateSelectCodes(selectCodes);
        ParsedSelectCodes parsed = parseSelectCodes(selectCodes);
        if (isParsedEmpty(parsed)) {
            return this;
        }
        Set<String> exactSet = new HashSet<>(parsed.exact);
        List<RangedCodeItem> filtered = new ArrayList<>(codeItems.size());
        for (RangedCodeItem item : codeItems) {
            String code = item.getCode();
            if (code != null && matchesAny(code, exactSet, parsed.prefixes, parsed.ranges)) {
                filtered.add(item);
            }
        }
        return newCodeList(filtered);
    }

    private boolean isNoFilter(String selectCodes) {
        return Strings.isNullOrEmpty(selectCodes)
                || selectCodes.trim().isEmpty(); // Short-circuit eval
    }

    private void validateSelectCodes(String selectCodes) {
        if (selectCodes.length() > MAX_SELECTCODES_LENGTH) {
            throw new IllegalArgumentException("selectCodes too long");
        }
        if (!LEGAL_SELECTCODES_CHARS.matcher(selectCodes).matches()) {
            throw new IllegalArgumentException("selectCodes contains illegal characters");
        }
    }

    private boolean isParsedEmpty(ParsedSelectCodes parsed) {
        return parsed.exact.isEmpty() && parsed.prefixes.isEmpty() && parsed.ranges.isEmpty();
    }

    private boolean matchesAny(
            String code, Set<String> exactSet, List<String> prefixes, List<Range> ranges) {
        if (exactSet.contains(code)) return true;
        if (matchesAnyPrefix(code, prefixes)) return true;
        return matchesAnyRange(code, ranges);
    }

    private boolean matchesAnyPrefix(String code, List<String> prefixes) {
        for (String p : prefixes) {
            if (code.startsWith(p)) return true;
        }
        return false;
    }

    private boolean matchesAnyRange(String code, List<Range> ranges) {
        for (Range r : ranges) {
            if (r.contains(code)) return true;
        }
        return false;
    }

    private static final class ParsedSelectCodes {
        final List<String> exact = new ArrayList<>();
        final List<String> prefixes = new ArrayList<>();
        final List<Range> ranges = new ArrayList<>();
    }

    /**
     * Represents a lexicographical range from {@code from} to {@code toExclusive}.
     *
     * @param from inclusive lower bound (lexicographical)
     * @param toExclusive exclusive upper bound (lexicographical)
     */
    private record Range(String from, String toExclusive) {
        boolean contains(String value) {
            return (from.compareTo(value) <= 0) && (value.compareTo(toExclusive) < 0);
        }
    }

    private ParsedSelectCodes parseSelectCodes(String input) {
        ParsedSelectCodes out = new ParsedSelectCodes();
        String[] parameters = input.split(",");
        if (parameters.length > MAX_PARAMETERS) {
            throw new IllegalArgumentException("Too many selectCodes parameters");
        }
        for (String raw : parameters) {
            String parameter = safeTrim(raw);
            if (!parameter.isEmpty()) {
                if (isRange(parameter)) {
                    addRangeIfValid(parameter, out);
                } else if (isPrefix(parameter)) {
                    addPrefixIfValid(parameter, out);
                } else {
                    addExactIfValid(parameter, out);
                }
            }
        }
        postProcess(out);
        return out;
    }

    private String safeTrim(String raw) {
        return raw == null ? "" : raw.trim();
    }

    private boolean isRange(String parameter) {
        return parameter.indexOf('-') >= 0;
    }

    private boolean isPrefix(String parameter) {
        return parameter.endsWith("*");
    }

    private void addRangeIfValid(String parameter, ParsedSelectCodes out) {
        int dashPos = parameter.indexOf('-');
        String left = parameter.substring(0, dashPos).trim();
        String right = parameter.substring(dashPos + 1).trim();
        if (left.isEmpty() || right.isEmpty()) return;
        if (!DIGITS_OR_STARS.matcher(left).matches()) return;
        if (!DIGITS_OR_STARS.matcher(right).matches()) return;
        String from = left.replace("*", "");
        if (from.isEmpty()) return;
        String toExclusive = computeExclusiveUpperBound(right);
        if (toExclusive == null) return;
        if (from.compareTo(toExclusive) >= 0) return;
        out.ranges.add(new Range(from, toExclusive));
    }

    private void addPrefixIfValid(String parameter, ParsedSelectCodes out) {
        String prefix = parameter.substring(0, parameter.length() - 1);
        if (prefix.isEmpty()) return;
        if (!DIGITS.matcher(prefix).matches()) return;
        out.prefixes.add(prefix);
    }

    private void addExactIfValid(String parameter, ParsedSelectCodes out) {
        if (!DIGITS.matcher(parameter).matches()) return;
        out.exact.add(parameter);
    }

    private void postProcess(ParsedSelectCodes out) {
        out.exact.sort(String::compareTo);
        out.prefixes.sort(String::compareTo);
        out.ranges.sort(
                Comparator.<Range, String>comparing(r -> r.from).thenComparing(r -> r.toExclusive));
    }

    private static String computeExclusiveUpperBound(String right) {
        if (right.endsWith("*")) {
            String prefix = right.substring(0, right.length() - 1);
            if (!DIGITS.matcher(prefix).matches()) return null;
            return incrementNumericString(prefix);
        } else {
            if (!DIGITS.matcher(right).matches()) return null;
            return right + "\uFFFF";
        }
    }

    /**
     * Returns the shortest prefix representing the next lexicographic increment of the given
     * numeric string. The method scans from right to left, increments the first non-'9' digit, and
     * truncates everything after it. If all digits are '9', it returns "1" followed by zeros (e.g.,
     * "999" -> "1000").
     *
     * <p>Examples: "34" -> "35" (increment last digit) "399" -> "4" (increment first non-'9' digit
     * and truncate) "9" -> "10" (all digits are '9', so add leading '1')
     */
    private static String incrementNumericString(String digits) {
        char[] arr = digits.toCharArray();
        for (int i = arr.length - 1; i >= 0; i--) {
            if (arr[i] != '9') {
                arr[i]++;
                return new String(arr, 0, i + 1);
            }
            arr[i] = '0';
        }
        return "1" + "0".repeat(digits.length());
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
                        .toList());
    }

    private CodeList newCodeList(List<RangedCodeItem> codeItems) {
        return new CodeList(
                csvSeparator, displayWithValidRange, dateRange, codeItems, includeFuture);
    }

    public Class<?> codeItemsJavaType() {
        return displayWithValidRange ? RangedCodeItem.class : CodeItem.class;
    }
}
