package no.ssb.klass.designer.editing.codetables;

import com.google.common.eventbus.EventBus;
import com.vaadin.event.FieldEvents;
import com.vaadin.ui.Table;

import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.model.StatisticalClassification;
import no.ssb.klass.designer.listeners.CodeEditorHierarchyTextChangeListener;

/**
 * @author Mads Lundemo, SSB.
 */
public class MarkedTranslationCodeTableFilterWrapper extends
        CodeTableFilterWrapper<MarkedTranslationCodeTable, StatisticalClassification> {

    private boolean initialized = false;

    private Language language;

    @Override
    protected MarkedTranslationCodeTable createTable() {
        return new MarkedTranslationCodeTable();
    }

    @Override
    protected void initTable(EventBus eventbus, MarkedTranslationCodeTable table, StatisticalClassification entity) {
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

    public void init(EventBus eventbus, StatisticalClassification contentSource, Language language) {

        this.language = language;
        // TODO try to find better way to solve this
        if (!initialized) {
            super.init(eventbus, contentSource);
            initialized = true;
        }
        filterBox.clear();
        TranslationCodeTable table = getTable();
        eventbus.register(table);
        table.init(eventbus, contentSource, language);
    }

    public void commitDirtyCodeEditors() {
        getTable().commitDirtyCodeEditors();
    }
}
