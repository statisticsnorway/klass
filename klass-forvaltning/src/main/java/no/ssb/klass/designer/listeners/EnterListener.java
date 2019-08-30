package no.ssb.klass.designer.listeners;

import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.AbstractComponent;

public abstract class EnterListener extends ShortcutListener {

    private AbstractComponent component;

    /**
     * This constructor will create an global EnterListener
     */
    public EnterListener() {
        super("Enter", ShortcutAction.KeyCode.ENTER, null);
    }

    /**
     * This constructor will create an EnterListener that will only trigger if the shortcut target is provided component
     * 
     * @param component
     *            target component
     */
    public EnterListener(AbstractComponent component) {
        super("Enter", ShortcutAction.KeyCode.ENTER, null);
        this.component = component;
    }

    @Override
    public final void handleAction(Object sender, Object target) {
        if (component == null || component == target) {
            enterPressed();
        }
    }

    protected abstract void enterPressed();
}
