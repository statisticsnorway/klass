package no.ssb.klass.designer.editing.codetables;

import com.google.common.eventbus.EventBus;
import com.vaadin.event.FieldEvents;
import com.vaadin.ui.Table;

import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.model.StatisticalClassification;
import no.ssb.klass.designer.listeners.CodeEditorHierarchyTextChangeListener;
import no.ssb.klass.designer.service.ClassificationFacade;

/**
 * @author Mads Lundemo, SSB.
 */
public class VariantCodeTableFilterWrapper extends
        CodeTableFilterWrapper<VariantCodeTable, StatisticalClassification> {

    private boolean initialized = false;

    private Language language;

    @Override
    protected VariantCodeTable createTable() {
        return new VariantCodeTable();
    }

    @Override
    protected void initTable(EventBus eventbus, VariantCodeTable table, StatisticalClassification entity) {
        // Do nothing
    }

    @Override
    protected FieldEvents.TextChangeListener getTextChangeListener(Table classificationTable) {
        return new CodeEditorHierarchyTextChangeListener(language, classificationTable,
                BaseCodeTable.CLASSIFICATION_ITEM_COLUMN);
    }

    @Override
    public void init(EventBus eventbus, StatisticalClassification entity) {
        throw new UnsupportedOperationException("Use overloaded method");
    }

    public void init(EventBus eventbus, StatisticalClassification contentSource,
            StatisticalClassification referenceSource, Language language,
            ClassificationFacade classificationFacade) {

        this.language = language;
        // TODO try to find better way to solve this
        if (!initialized) {
            super.init(eventbus, contentSource);
            initialized = true;
        }
        filterBox.clear();

        VariantCodeTable table = getTable();
        eventbus.register(table);
        table.init(eventbus, contentSource, referenceSource, language, classificationFacade);
    }

    public void commitDirtyCodeEditors() {
        getTable().commitDirtyCodeEditors();
    }

    public void setDragIncludeChildren(boolean dragIncludeChildren) {
        getTable().setDragIncludeChildren(dragIncludeChildren);
    }
}
