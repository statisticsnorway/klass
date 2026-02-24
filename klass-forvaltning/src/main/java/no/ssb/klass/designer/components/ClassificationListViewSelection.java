package no.ssb.klass.designer.components;

import static com.google.common.base.Preconditions.*;

import java.util.Optional;

import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.ClassificationVariant;
import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.model.CorrespondenceTable;

/**
 * Enables ClassificationListView to know which classificationSeries, version, etc that shall be displayed as selected.
 */
public final class ClassificationListViewSelection {
    private final Long classificationId;
    private final Optional<Long> versionId;
    private final Optional<Long> variantId;
    private final Optional<Long> correspondenceTableId;

    private ClassificationListViewSelection(Long classificationId, Long versionId,
            Long variantId, Long correspondenceTableId) {
        this.classificationId = checkNotNull(classificationId);
        this.versionId = Optional.ofNullable(versionId);
        this.variantId = Optional.ofNullable(variantId);
        this.correspondenceTableId = Optional.ofNullable(correspondenceTableId);
    }

    public Long getClassificationId() {
        return classificationId;
    }

    public Optional<Long> getVersionId() {
        return versionId;
    }

    public Optional<Long> getVariantId() {
        return variantId;
    }

    public Optional<Long> getCorrespondenceTableId() {
        return correspondenceTableId;
    }

    public static ClassificationListViewSelection newClassificationListViewSelection(
            ClassificationSeries classification) {
        return new ClassificationListViewSelection(classification.getId(), null, null, null);
    }

    public static ClassificationListViewSelection newClassificationListViewSelection(
            ClassificationVersion version) {
        return new ClassificationListViewSelection(version.getClassification().getId(), version.getId(), null, null);
    }

    public static ClassificationListViewSelection newClassificationListViewSelection(
            ClassificationVariant variant) {
        return new ClassificationListViewSelection(variant.getClassificationVersion().getClassification().getId(),
                variant.getClassificationVersion().getId(), variant.getId(), null);
    }

    public static ClassificationListViewSelection newClassificationListViewSelection(
            CorrespondenceTable correspondenceTable, ClassificationVersion version) {
        return new ClassificationListViewSelection(version.getClassification().getId(), version.getId(), null,
                correspondenceTable.getId());
    }
}
