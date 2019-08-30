package no.ssb.klass.designer.listeners;

import com.vaadin.data.Container.Filterable;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.ui.Table;

public class SimpleTextChangeListener implements TextChangeListener {
    private SimpleStringFilter filter = null;
    private final Table table;
    private final String propertyId;

    public SimpleTextChangeListener(Table table, String propertyId) {
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
        filter = new SimpleStringFilter(propertyId, event.getText(), true, false);
        f.addContainerFilter(filter);
    }
}
