package no.ssb.klass.api.dto;

import java.time.LocalDate;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Preconditions;
import com.google.common.collect.ComparisonChain;

import no.ssb.klass.core.service.dto.CorrespondenceDto;
import no.ssb.klass.core.util.DateRange;
import no.ssb.klass.core.util.TimeUtil;
import no.ssb.klass.api.util.CustomLocalDateSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JsonPropertyOrder(value = { "sourceCode", "sourceName", "sourceShortName", "targetCode", "targetName",
        "targetShortName" })
public class CorrespondenceItem implements Comparable<CorrespondenceItem> {

    private final String sourceCode;
    private final String sourceName;
    private final String sourceShortName;
    private final String targetCode;
    private final String targetName;
    private final String targetShortName;

    public CorrespondenceItem(CorrespondenceItem correspondenceItem) {
        this.sourceCode = correspondenceItem.getSourceCode();
        this.sourceName = correspondenceItem.getSourceName();
        this.sourceShortName = correspondenceItem.getSourceShortName();
        this.targetCode = correspondenceItem.getTargetCode();
        this.targetName = correspondenceItem.getTargetName();
        this.targetShortName = correspondenceItem.getTargetShortName();
    }

    public CorrespondenceItem(CorrespondenceDto correspondence) {
        this.sourceCode = correspondence.getSourceCode();
        this.sourceName = correspondence.getSourceName();
        this.sourceShortName = correspondence.getSourceShortName();
        this.targetCode = correspondence.getTargetCode();
        this.targetName = correspondence.getTargetName();
        this.targetShortName = correspondence.getTargetShortName();
    }

    public String getSourceShortName() {
        return sourceShortName;
    }

    public String getTargetShortName() {
        return targetShortName;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public String getSourceName() {
        return sourceName;
    }

    public String getTargetCode() {
        return targetCode;
    }

    public String getTargetName() {
        return targetName;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceCode, sourceName, targetCode, targetName);
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && getClass() == obj.getClass() &&
                Objects.equals(this.sourceCode, ((CorrespondenceItem) obj).sourceCode) &&
                Objects.equals(this.sourceName, ((CorrespondenceItem) obj).sourceName) &&
                Objects.equals(this.targetCode, ((CorrespondenceItem) obj).targetCode) &&
                Objects.equals(this.targetName, ((CorrespondenceItem) obj).targetName);
    }

    @Override
    public int compareTo(CorrespondenceItem other) {
        return ComparisonChain.start()
                .compare(this.sourceCode, other.sourceCode)
                .compare(this.sourceName, other.sourceName)
                .compare(this.targetCode, other.targetCode)
                .compare(this.targetName, other.targetName).result();
    }

    /**
     * A CorrespondenceItem that has a validity range
     */
    @JsonPropertyOrder(value = { "sourceCode", "sourceName", "sourceShortName", "targetCode", "targetName",
            "targetShortName", "validFrom", "validTo" })
    public static class RangedCorrespondenceItem extends CorrespondenceItem {
        private final DateRange validRange;

        public RangedCorrespondenceItem(CorrespondenceDto correspondence) {
            super(correspondence);
            this.validRange = Preconditions.checkNotNull(correspondence.getValidRange());
        }

        public RangedCorrespondenceItem(CorrespondenceItem correspondenceItem, DateRange dateRange) {
            super(correspondenceItem);
            this.validRange = Preconditions.checkNotNull(dateRange);
        }

        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate getValidFrom() {
            return TimeUtil.isMinDate(validRange.getFrom()) ? null : validRange.getFrom();
        }

        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate getValidTo() {
            return TimeUtil.isMaxDate(validRange.getTo()) ? null : validRange.getTo();
        }

        @JsonIgnore
        public DateRange getDateRange(boolean includeFuture) {
            return includeFuture && validRange.isCurrentVersion()
                    ? validRange
                    : new DateRange(validRange.getFrom(), LocalDate.MAX);
        }

        @JsonIgnore
        public DateRange getDateRange() {
            return validRange;
        }

        @Override
        public String toString() {
            return "\nRangedCorrespondenceItem{" +
                    "sourceCode='" + super.sourceCode + '\'' +
                    ", sourceName='_______" + super.sourceName.toUpperCase() + '\'' +
                    ", targetCode='" + super.targetCode + '\'' +
                    ", targetName='" + super.targetName + '\'' +
                    ", validRange=" + validRange +
                    '}';
        }
    }
}
