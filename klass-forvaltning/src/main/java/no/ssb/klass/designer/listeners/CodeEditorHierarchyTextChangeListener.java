package no.ssb.klass.designer.listeners;

import com.google.common.base.Strings;
import com.vaadin.data.Container;
import com.vaadin.data.Container.Filterable;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.ui.Table;

import no.ssb.klass.core.model.Language;
import no.ssb.klass.designer.editing.codetables.containers.CustomHierarchicalContainer;
import no.ssb.klass.designer.editing.codetables.filters.HierarchyCodeEditorFilter;

public class CodeEditorHierarchyTextChangeListener implements TextChangeListener {
    private Container.Filter filter = null;
    private final Table table;
    private final String propertyId;
    private final Language language;

    public CodeEditorHierarchyTextChangeListener(Language language, Table table, String propertyId) {
        this.table = table;
        this.language = language;
        this.propertyId = propertyId;
    }

    @Override
    public void textChange(TextChangeEvent event) {
        Filterable f = (Filterable) table.getContainerDataSource();

        // Remove old filter
        if (filter != null) {
            f.removeContainerFilter(filter);
        }
        if (!Strings.isNullOrEmpty(event.getText())) {
            filter = new HierarchyCodeEditorFilter(language,
                    (CustomHierarchicalContainer) table.getContainerDataSource(),
                    propertyId, event.getText(), true);
            f.addContainerFilter(filter);
        }
    }
}
