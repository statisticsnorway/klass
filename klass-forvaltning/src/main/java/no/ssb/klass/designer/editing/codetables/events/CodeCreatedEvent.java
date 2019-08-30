package no.ssb.klass.designer.editing.codetables.events;

import no.ssb.klass.core.model.ClassificationItem;

public class CodeCreatedEvent {
    private final ClassificationItem newItem;
    private final Object siblingItemId;

    public CodeCreatedEvent(ClassificationItem newItem, Object siblingItemId) {
        this.newItem = newItem;
        this.siblingItemId = siblingItemId;
    }

    public ClassificationItem getNewItem() {
        return newItem;
    }

    public Object getSiblingItemId() {
        return siblingItemId;
    }
}