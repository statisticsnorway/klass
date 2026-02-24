package no.ssb.klass.designer.editing.codetables;

import com.google.common.eventbus.Subscribe;
import com.vaadin.data.Container;
import com.vaadin.ui.Component;

import no.ssb.klass.core.model.ClassificationItem;
import no.ssb.klass.designer.editing.codetables.codeeditors.CodeEditor;
import no.ssb.klass.designer.editing.codetables.codeeditors.TranslationCodeEditor;
import no.ssb.klass.designer.editing.codetables.events.TranslationUpdatedEvent;

/**
 * TranslationCodeTable is used for secondaryLanguage and thirdLanguage. TranslationCodeTable does not have possibility
 * to delete and add codes. Only translations of title, shortTitle etc may be changed.
 */
public class TranslationCodeTable extends EditableCodeTable {
    @Override
    protected void addColumnsToContainer(Container container) {
        container.addContainerProperty(CODE_COLUMN, Component.class, null);
        container.addContainerProperty(CLASSIFICATION_ITEM_COLUMN, ClassificationItem.class, null);
    }

    @Override
    protected void adjustTableColumns() {
        setVisibleColumns(CODE_COLUMN);
    }

    @Override
    protected CodeEditor createRowSelectedCodeEditor(ClassificationItem classificationItem) {
        if (classificationItem.isReference()) {
            return null;
        }
        if (isReadOnly()) {
            return null;
        }
        return new TranslationCodeEditor(eventbus, contentSource, classificationItem, language);
    }

    @Subscribe
    public void handleTranslationUpdate(TranslationUpdatedEvent event) {
        ClassificationItem classificationItem = event.getClassificationItem();
        setPropertyValue(getContainer().getItem(classificationItem.getUuid()).getItemProperty(CODE_COLUMN),
                createCodeComponent(classificationItem));

        Object nextItemId = getContainer().nextItemId(classificationItem.getUuid());
        if (nextItemId != null) {
            rowSelected(getItem(nextItemId));
            setCollapsed(getParent(nextItemId), false);
        }
        markAsModified();
    }
}
