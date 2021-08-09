package no.ssb.klass.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import no.ssb.klass.api.util.CustomLocalDateSerializer;
import no.ssb.klass.api.util.PresentationNameBuilder;
import no.ssb.klass.core.service.dto.CodeDto;
import no.ssb.klass.core.util.DateRange;
import no.ssb.klass.core.util.TimeUtil;

import java.time.LocalDate;
import java.util.Objects;

@JsonPropertyOrder(value = { "code", "parentCode", "level", "name", "shortName", "presentationName", "validFrom",
        "validTo" })
public class CodeItem implements Comparable<CodeItem> {
    private final String code;
    private final String parentCode;
    private final String name;
    private final String shortName;
    private final String presentationName;
    private final String level;
    private final DateRange validity;

    public CodeItem(CodeItem codeItem, PresentationNameBuilder builder) {
        this.code = codeItem.getCode();
        this.parentCode = codeItem.getParentCode();
        this.name = codeItem.getName();
        this.shortName = codeItem.getShortName();
        this.level = codeItem.getLevel();
        this.validity = codeItem.getValidity();
        this.presentationName = builder.presentationName(codeItem.getCode(), codeItem.getName(), codeItem
                .getShortName());
    }

    public CodeItem(CodeItem codeItem) {
        this.code = codeItem.getCode();
        this.parentCode = codeItem.getParentCode();
        this.name = codeItem.getName();
        this.shortName = codeItem.getShortName();
        this.level = codeItem.getLevel();
        this.validity = codeItem.getValidity();
        this.presentationName = codeItem.getPresentationName();
    }

    public CodeItem(CodeDto code) {
        this.code = code.getCode();
        this.parentCode = code.getParentCode();
        this.name = code.getOfficialName();
        this.shortName = code.getShortName();
        this.level = code.getLevel();
        this.validity = code.getValidity();
        this.presentationName = "";
    }

    public String getCode() {
        return code;
    }

    public String getParentCode() {
        return parentCode;
    }

    public String getShortName() {
        return shortName;
    }

    public String getPresentationName() {
        return presentationName;
    }

    public String getLevel() {
        return level;
    }

    public String getName() {
        return name;
    }

//    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    public LocalDate getValidFrom() {
        return validity == null ? null : validity.getFrom();
    }

//    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    public LocalDate getValidTo() {
        if (validity == null || TimeUtil.isMaxDate(validity.getTo())) {
            return null;
        }
        return validity.getTo();
    }

    @JsonIgnore
    DateRange getValidity() {
        return validity;
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, name, level);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        CodeItem other = (CodeItem) obj;
        return Objects.equals(this.code, other.code) && Objects.equals(this.name, other.name) && Objects.equals(
                this.level, other.level);
    }

    @Override
    public int compareTo(CodeItem other) {
        return code.compareTo(other.code);
    }

    /**
     * A CodeItem with date range information (version validity mapped to request range)
     */
    @JsonPropertyOrder(value = { "code", "parentCode", "level", "name", "shortName", "presentationName",
            "validFrom",
            "validTo",
            "validFromInRequestedRange",
            "validToInRequestedRange" })
    public static class RangedCodeItem extends CodeItem {
        private final DateRange RequestPeriodRange;

        public RangedCodeItem(RangedCodeItem codeItem, DateRange newDateRange) {
            super(codeItem);
            this.RequestPeriodRange = newDateRange;
        }

        public RangedCodeItem(RangedCodeItem codeItem, PresentationNameBuilder builder) {
            super(codeItem, builder);
            this.RequestPeriodRange = codeItem.getRequestPeriodRange();
        }

        public RangedCodeItem(CodeDto code) {
            super(code);
            this.RequestPeriodRange = code.getDateRange();
        }

        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate getValidFromInRequestedRange() {
            if (TimeUtil.isMinDate(RequestPeriodRange.getFrom())) {
                return null;
            }
            return RequestPeriodRange.getFrom();
        }

        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate getValidToInRequestedRange() {
            if (TimeUtil.isMaxDate(RequestPeriodRange.getTo())) {
                return null;
            }
            return RequestPeriodRange.getTo();
        }

        @JsonIgnore
        public DateRange getRequestPeriodRange() {
            return RequestPeriodRange;
        }
    }
}
