package no.ssb.klass.designer.listeners;

import com.vaadin.data.Container;
import com.vaadin.data.Container.Filterable;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.ui.Table;

import no.ssb.klass.designer.editing.codetables.containers.CustomHierarchicalContainer;
import no.ssb.klass.designer.editing.codetables.filters.HierarchyLabelFilter;

public class HierarchyTextChangeListener implements TextChangeListener {
    private Container.Filter filter = null;
    private final Table table;
    private final String propertyId;

    public HierarchyTextChangeListener(Table table, String propertyId) {
        this.table = table;
        this.propertyId = propertyId;
    }

    @Override
    public void textChange(TextChangeEvent event) {
        Filterable f = (Filterable) table.getContainerDataSource();

        // Remove old filter
        if (filter != null) {
            f.removeContainerFilter(filter);
        }
        filter = new HierarchyLabelFilter((CustomHierarchicalContainer) table.getContainerDataSource(),
                propertyId, event.getText(), true, false);
        f.addContainerFilter(filter);
    }
}
