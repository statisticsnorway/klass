package no.ssb.klass.api.dto.hal;

import static java.util.stream.Collectors.*;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import no.ssb.klass.core.model.ClassificationItem;
import no.ssb.klass.core.model.ClassificationVariant;
import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.model.ConcreteClassificationItem;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.model.Level;
import no.ssb.klass.core.util.AlphaNumericCompareUtil;
import no.ssb.klass.api.util.CustomLocalDateSerializer;

@JsonPropertyOrder(value = { "code",  "parentCode", "level", "name", "shortName", "notes", "validFrom", "validTo" })
public class ClassificationItemResource implements Comparable<ClassificationItemResource> {
    private final String code;
    private final String level;
    private final String name;
    private final String shortName;
    private final String notes;
    private final String parentCode;
    private final LocalDate validFrom;
    private final LocalDate validTo;

    public ClassificationItemResource(ClassificationItem classificationItem, Level level, Language language,
            boolean includeShortName, boolean includeNotes, boolean includeValidity) {
        this.code = classificationItem.getCode();
        this.level = Integer.toString(level.getLevelNumber());
        this.name = classificationItem.getOfficialName(language);
        this.shortName = includeShortName ? classificationItem.getShortName(language) : null;
        this.notes = includeNotes ? classificationItem.getNotes(language) : null;
        this.parentCode = classificationItem.getParent() == null ? "" : classificationItem.getParent().getCode();
        if (includeValidity && classificationItem instanceof ConcreteClassificationItem) {
            ConcreteClassificationItem concreteItem = (ConcreteClassificationItem) classificationItem;
            this.validFrom = concreteItem.getValidFrom();
            this.validTo = concreteItem.getValidTo();
        } else {
            this.validFrom = null;
            this.validTo = null;
        }
    }

    public ClassificationItemResource(String code, String level, String name, String shortName, String notes,
            String parentCode, LocalDate validFrom, LocalDate validTo) {
        this.code = code;
        this.level = level;
        this.name = name;
        this.shortName = shortName;
        this.notes = notes;
        this.parentCode = parentCode;
        this.validFrom = validFrom;
        this.validTo = validTo;
    }

    public String getCode() {
        return code;
    }

    public String getLevel() {
        return level;
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    public String getNotes() {
        return notes;
    }

    public String getParentCode() {
        return parentCode;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    public LocalDate getValidFrom() {
        return validFrom;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    public LocalDate getValidTo() {
        return validTo;
    }

    public int compareTo(ClassificationItemResource other) {
        return AlphaNumericCompareUtil.compare(getCode(), other.getCode());
    }

    public static List<ClassificationItemResource> convert(ClassificationVariant variant, Language language) {
        return variant.getLevels().stream().flatMap(level -> convert(level, language).stream()).sorted().collect(
                toList());
    }

    public static List<ClassificationItemResource> convert(ClassificationVersion version, Language language) {
        boolean includeShortName = version.isIncludeShortName();
        boolean includeValidity = version.isIncludeValidity();
        boolean includeNotes = version.isIncludeNotes();
        return version.getLevels().stream()
                .flatMap(level -> convert(level, language, includeShortName, includeNotes, includeValidity).stream())
                .sorted().collect(toList());
    }

    private static List<ClassificationItemResource> convert(Level level, Language language) {
        return convert(level, language, false, false, false);
    }

    private static List<ClassificationItemResource> convert(Level level, Language language,
            boolean includeShortName, boolean includeNotes, boolean includeValidity) {
        return level.getClassificationItems().stream()
                .map(item -> new ClassificationItemResource(item, level, language,
                        includeShortName, includeNotes, includeValidity))
                .collect(toList());
    }
}
