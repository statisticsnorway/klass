package no.ssb.klass.designer.editing.codetables.events;

import no.ssb.klass.core.model.ClassificationItem;

public class CancelEditEvent {
    private final ClassificationItem classificationItem;

    public CancelEditEvent(ClassificationItem classificationItem) {
        this.classificationItem = classificationItem;
    }

    public ClassificationItem getClassificationItem() {
        return classificationItem;
    }
}