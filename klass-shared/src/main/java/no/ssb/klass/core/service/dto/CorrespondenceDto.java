package no.ssb.klass.core.service.dto;

import static com.google.common.base.Preconditions.*;

import no.ssb.klass.core.model.ClassificationItem;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.util.DateRange;

public class CorrespondenceDto {
    private final String sourceCode;
    private final String sourceName;
    private final String sourceShortName;
    private final String targetCode;
    private final String targetName;
    private final String targetShortName;
    private final DateRange validRange;

    public CorrespondenceDto(ClassificationItem source, ClassificationItem target, DateRange validRange,
                             Language language) {
        checkNotNull(validRange);
        checkNotNull(language);
        checkState(source != null || target != null, "Both source and target is null");
        this.sourceCode = source == null ? null : source.getCode();
        this.sourceName = source == null ? null : source.getOfficialName(language);
        this.sourceShortName = source == null ? null : source.getShortName(language);
        this.targetCode = target == null ? null : target.getCode();
        this.targetName = target == null ? null : target.getOfficialName(language);
        this.targetShortName = target == null ? null : target.getShortName(language);
        this.validRange = validRange;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public String getSourceName() {
        return sourceName;
    }

    public String getSourceShortName() {
        return sourceShortName;
    }

    public String getTargetCode() {
        return targetCode;
    }

    public String getTargetName() {
        return targetName;
    }

    public String getTargetShortName() {
        return targetShortName;
    }

    public DateRange getValidRange() {
        return validRange;
    }

    @Override
    public String toString() {
        return "\nCorrespondenceDto{" +
                "sourceCode='" + sourceCode + '\'' +
                ", sourceName='" + sourceName + '\'' +
                ", targetCode='___________" + targetName.toUpperCase() + '\'' +
                ", validRange=" + validRange +
                '}';
    }
}
