package no.ssb.klass.designer.editing.codetables;

import com.google.common.eventbus.EventBus;
import com.vaadin.event.FieldEvents;
import com.vaadin.ui.Table;

import no.ssb.klass.core.model.CorrespondenceTable;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.designer.listeners.CodeEditorHierarchyTextChangeListener;

/**
 * @author Mads Lundemo, SSB.
 */
public class SourceCorrespondenceTableFilterWrapper extends
        CodeTableFilterWrapper<SourceCorrespondenceTableCodeTable, CorrespondenceTable> {

    private Language language;

    @Override
    protected SourceCorrespondenceTableCodeTable createTable() {
        return new SourceCorrespondenceTableCodeTable();
    }

    @Override
    protected FieldEvents.TextChangeListener getTextChangeListener(Table classificationTable) {
        return new CodeEditorHierarchyTextChangeListener(language, classificationTable,
                BaseCodeTable.CLASSIFICATION_ITEM_COLUMN);
    }

    @Override
    public void init(EventBus eventbus, CorrespondenceTable entity) {
        language = entity.getPrimaryLanguage();
        super.init(eventbus, entity);
    }

    @Override
    protected void initTable(EventBus eventbus, SourceCorrespondenceTableCodeTable table, CorrespondenceTable entity) {
        table.init(eventbus, entity);
        eventbus.register(table);
        hierarchyState = true; // source table has hierarchy default open
    }
}
