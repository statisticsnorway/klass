package no.ssb.klass.core.service.dto;

import static com.google.common.base.Preconditions.checkNotNull;

import no.ssb.klass.core.model.ClassificationItem;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.model.Level;
import no.ssb.klass.core.util.DateRange;

public class CodeDto {
    private final String code;
    private final String parentCode;
    private final String officialName;
    private final String shortName;
    private final String level;
    private final DateRange dateRange;
    private final DateRange validity;
    private final String notes;

    public CodeDto(Level level, ClassificationItem item, DateRange dateRange, Language language) {
        checkNotNull(level);
        checkNotNull(item);
        checkNotNull(dateRange);
        checkNotNull(language);
        this.code = item.getCode();
        ClassificationItem parent = item.getParent();
        this.parentCode = parent == null ? null : parent.getCode();
        this.officialName = item.getOfficialName(language);
        this.shortName = item.getShortName(language);
        this.level = Integer.toString(level.getLevelNumber());
        this.dateRange = dateRange;
        this.validity =
                item.getValidFrom() == null
                        ? null
                        : DateRange.create(item.getValidFrom(), item.getValidTo());
        this.notes = item.getNotes(language).replace('\n', ' ');
    }

    public String getCode() {
        return code;
    }

    public String getParentCode() {
        return parentCode;
    }

    public String getOfficialName() {
        return officialName;
    }

    public String getShortName() {
        return shortName;
    }

    public String getLevel() {
        return level;
    }

    public DateRange getDateRange() {
        return dateRange;
    }

    public DateRange getValidity() {
        return validity;
    }

    public String getNotes() {
        return notes;
    }
}
