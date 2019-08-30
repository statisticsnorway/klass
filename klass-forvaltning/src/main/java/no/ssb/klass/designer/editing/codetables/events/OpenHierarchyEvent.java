package no.ssb.klass.designer.editing.codetables.events;

public class OpenHierarchyEvent extends HierarchyEvent {

    public OpenHierarchyEvent() {

    }

    public OpenHierarchyEvent(Object sender) {
        this.target = sender;
    }
}