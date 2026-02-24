package no.ssb.klass.designer.editing.codetables;

import java.util.List;

import com.google.common.eventbus.EventBus;
import com.vaadin.event.FieldEvents;
import com.vaadin.ui.Table;

import no.ssb.klass.core.model.ClassificationItem;
import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.designer.listeners.CodeEditorHierarchyTextChangeListener;

/**
 * @author Mads Lundemo, SSB.
 */
public class ReadOnlyCodeTableFilterWrapper extends
        CodeTableFilterWrapper<ReadOnlyCodeTable, ClassificationVersion> {

    private Language language;

    @Override
    protected ReadOnlyCodeTable createTable() {
        ReadOnlyCodeTable table = new ReadOnlyCodeTable();
        setNotSelectable(table);
        return table;
    }

    @Override
    protected void initTable(EventBus eventbus, ReadOnlyCodeTable table, ClassificationVersion entity) {
        table.init(eventbus, entity, language);
        eventbus.register(getTable());
    }

    @Override
    public void init(EventBus eventbus, ClassificationVersion entity) {
        language = entity.getPrimaryLanguage();
        super.init(eventbus, entity);
    }

    public void init(EventBus eventbus, ClassificationVersion entity, Language language) {
        this.language = language;
        super.init(eventbus, entity);

    }

    @Override
    protected FieldEvents.TextChangeListener getTextChangeListener(Table classificationTable) {
        return new CodeEditorHierarchyTextChangeListener(language, classificationTable,
                BaseCodeTable.CLASSIFICATION_ITEM_COLUMN);
    }

    private void setNotSelectable(Table table) {
        if (isReadOnly()) {
            return;
        }
        table.setMultiSelect(false);
        table.setSelectable(false);
        table.setDragMode(Table.TableDragMode.ROW);
    }

    public void markAsReferenced(List<ClassificationItem> classificationItems) {
        getTable().markAsReferenced(classificationItems);
    }
}
