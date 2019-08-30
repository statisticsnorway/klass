package no.ssb.klass.designer.editing.codetables.events;

public abstract class ReferenceChangedEvent {
    private final Object referencedItemId;

    public ReferenceChangedEvent(Object referencedItemIt) {
        this.referencedItemId = referencedItemIt;
    }

    public Object getReferencedItemId() {
        return referencedItemId;
    }

    public static class ReferenceCreatedEvent extends ReferenceChangedEvent {
        public ReferenceCreatedEvent(Object referencedItemIt) {
            super(referencedItemIt);
        }
    }

    public static class ReferenceRemovedEvent extends ReferenceChangedEvent {
        public ReferenceRemovedEvent(Object referencedItemIt) {
            super(referencedItemIt);
        }
    }
}