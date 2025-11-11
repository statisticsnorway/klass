package no.ssb.klass.api.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import no.ssb.klass.api.util.CustomLocalDateSerializer;
import no.ssb.klass.core.model.ClassificationItem;
import no.ssb.klass.core.model.CorrespondenceMap;
import no.ssb.klass.core.model.Language;

import java.time.LocalDate;
import java.util.Optional;

@JsonPropertyOrder(
        value = {
            "oldCode",
            "oldName",
            "oldShortName",
            "newCode",
            "newName",
            "newShortName",
            "changeOccurred"
        })
public class CodeChangeItem {
    private final String oldCode;
    private final String oldName;
    private final String oldShortName;
    private final String newCode;
    private final String newName;
    private final String newShortName;
    private final LocalDate changeOccurred;

    public CodeChangeItem(
            CorrespondenceMap correspondenceMap,
            boolean isTargetOldest,
            LocalDate changeOccurred,
            Language language) {
        ClassificationItem oldItem = getOldItem(correspondenceMap, isTargetOldest);
        this.oldCode = oldItem == null ? null : oldItem.getCode();
        this.oldName = oldItem == null ? null : oldItem.getOfficialName(language);
        this.oldShortName = oldItem == null ? null : oldItem.getShortName(language);

        ClassificationItem newItem = getNewItem(correspondenceMap, isTargetOldest);
        this.newCode = newItem == null ? null : newItem.getCode();
        this.newName = newItem == null ? null : newItem.getOfficialName(language);
        this.newShortName = newItem == null ? null : newItem.getShortName(language);

        this.changeOccurred = changeOccurred;
    }

    private ClassificationItem getOldItem(
            CorrespondenceMap correspondenceMap, boolean isTargetOldest) {
        Optional<ClassificationItem> classificationItem =
                isTargetOldest ? correspondenceMap.getTarget() : correspondenceMap.getSource();
        return classificationItem.orElse(null);
    }

    private ClassificationItem getNewItem(
            CorrespondenceMap correspondenceMap, boolean isTargetOldest) {
        Optional<ClassificationItem> classificationItem =
                isTargetOldest ? correspondenceMap.getSource() : correspondenceMap.getTarget();
        return classificationItem.orElse(null);
    }

    public String getOldCode() {
        return oldCode;
    }

    public String getOldName() {
        return oldName;
    }

    public String getOldShortName() {
        return oldShortName;
    }

    public String getNewCode() {
        return newCode;
    }

    public String getNewName() {
        return newName;
    }

    public String getNewShortName() {
        return newShortName;
    }

    @JsonSerialize(using = CustomLocalDateSerializer.class)
    public LocalDate getChangeOccurred() {
        return changeOccurred;
    }
}
