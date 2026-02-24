package no.ssb.klass.designer.editing.codetables.events;

import no.ssb.klass.core.model.ClassificationItem;

public class CodeDeletedEvent {
    private final ClassificationItem classificationItem;

    public CodeDeletedEvent(ClassificationItem classificationItem) {
        this.classificationItem = classificationItem;
    }

    public ClassificationItem getClassificationItem() {
        return classificationItem;
    }
}