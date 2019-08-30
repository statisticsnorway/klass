package no.ssb.klass.designer.listeners;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.event.FieldEvents;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.TextField;

/**
 * @author Mads Lundemo, SSB.
 */
public class SharedEscapeShortcutListener extends ShortcutListener {

    Map<TextField, FieldEvents.TextChangeListener> textFields = new HashMap<>();

    public SharedEscapeShortcutListener() {
        super("clear field", ShortcutAction.KeyCode.ESCAPE, null);
    }

    public void addListener(TextField textField, FieldEvents.TextChangeListener listener) {
        textFields.put(textField, listener);
        textField.addShortcutListener(this);
    }

    @Override
    public void handleAction(Object sender, Object target) {
        if (textFields.keySet().contains(target)) {
            TextField textField = (TextField) target;
            FieldEvents.TextChangeListener listener = textFields.get(target);
            textField.clear();
            if (listener != null) {
                listener.textChange(new CustomTextChangeEvent(textField));
            }
        }
    }
}
