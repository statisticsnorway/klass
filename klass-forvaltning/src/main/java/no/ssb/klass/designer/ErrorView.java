package no.ssb.klass.designer;

import com.vaadin.navigator.View;

/**
 * Implementors of this interface handle errors in Klass. When navigating to an ErrorView a previous View will not be
 * asked to checked for changes, etc. Since this could lead to other new faults.
 */
public interface ErrorView extends View {
}
