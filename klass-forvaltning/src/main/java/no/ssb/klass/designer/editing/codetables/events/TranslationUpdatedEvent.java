package no.ssb.klass.designer.editing.codetables.events;

import no.ssb.klass.core.model.ClassificationItem;

public class TranslationUpdatedEvent {
    private final ClassificationItem classificationItem;

    public TranslationUpdatedEvent(ClassificationItem classificationItem) {
        this.classificationItem = classificationItem;
    }

    public ClassificationItem getClassificationItem() {
        return classificationItem;
    }
}