package no.ssb.klass.designer.editing.codetables.events;

public abstract class HierarchyEvent {

    protected Object target;

    public HierarchyEvent() {
        target = null;
    }

    public HierarchyEvent(Object target) {
        this.target = target;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }
}