package no.ssb.klass.designer.util;

import java.util.Iterator;

import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HasComponents;

/**
 * @author Mads Lundemo, SSB.
 */
public final class ComponentUtil {

    private ComponentUtil() {
    }

    public static void setReadOnlyRecursively(Component component, Boolean state) {
        if (component instanceof HasComponents) {
            Iterator<Component> componentIterator = ((HasComponents) component).iterator();
            while (componentIterator.hasNext()) {
                Component subComponent = componentIterator.next();
                if (subComponent instanceof AbstractField) {
                    subComponent.setReadOnly(state);
                } else if (subComponent instanceof CustomComponent) {
                    subComponent.setReadOnly(state);
                } else if (subComponent instanceof HasComponents) {
                    setReadOnlyRecursively(subComponent, state);
                }
            }
        } else {
            component.setReadOnly(state);
        }

    }
}
