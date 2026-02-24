package no.ssb.klass.designer;

import java.util.List;

import com.vaadin.navigator.View;

import no.ssb.klass.designer.components.BreadcumbPanel.Breadcrumb;

/**
 * Implementors of this interface will have the breadcrumbPanel visible.
 */
public interface EditingView extends View {
    List<Breadcrumb> getBreadcrumbs();

    boolean hasChanges();

    void ignoreChanges();
}
