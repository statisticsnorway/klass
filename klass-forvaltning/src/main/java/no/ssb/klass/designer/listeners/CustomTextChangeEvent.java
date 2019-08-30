package no.ssb.klass.designer.listeners;

import com.vaadin.event.FieldEvents;
import com.vaadin.ui.AbstractTextField;

/**
 * @author Mads Lundemo, SSB.
 */
public class CustomTextChangeEvent extends FieldEvents.TextChangeEvent {
    private String curText;
    private int cursorPosition;

    public CustomTextChangeEvent(final AbstractTextField tf) {
        super(tf);
        curText = tf.getValue();
        cursorPosition = tf.getCursorPosition();
    }

    @Override
    public AbstractTextField getComponent() {
        return (AbstractTextField) super.getComponent();
    }

    @Override
    public String getText() {
        return curText;
    }

    @Override
    public int getCursorPosition() {
        return cursorPosition;
    }

}
