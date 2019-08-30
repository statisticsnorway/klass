package no.ssb.klass.designer.editing.codetables.events;

import no.ssb.klass.core.model.ClassificationItem;

public class CodeUpdatedEvent {
    private final ClassificationItem classificationItem;

    public CodeUpdatedEvent(ClassificationItem classificationItem) {
        this.classificationItem = classificationItem;
    }

    public ClassificationItem getClassificationItem() {
        return classificationItem;
    }
}