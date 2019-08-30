package no.ssb.klass.designer.editing.codetables.events;

public class CloseHierarchyEvent extends HierarchyEvent {

    public CloseHierarchyEvent() {

    }

    public CloseHierarchyEvent(Object target) {
        this.target = target;
    }

}