package no.ssb.klass.designer.editing.codetables;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;

import no.ssb.klass.core.model.ClassificationItem;
import no.ssb.klass.core.model.ConcreteClassificationItem;

/**
 * MarkedTranslationCodeTable is similar to TranslationCodeTable but will show a small mark behind elements that are
 * translatable, this is mainly intended for variants where we have a mix of elements that can be translated and
 * elements that can not be translated.
 */
public class MarkedTranslationCodeTable extends TranslationCodeTable {
    public static final String TRANSLATABLE_MARK_COLUMN = "translatable";

    @Override
    protected void addColumnsToContainer(Container container) {
        container.addContainerProperty(CODE_COLUMN, Component.class, null);
        container.addContainerProperty(CLASSIFICATION_ITEM_COLUMN, ClassificationItem.class, null);
        container.addContainerProperty(TRANSLATABLE_MARK_COLUMN, Label.class, null);
    }

    @Override
    protected void adjustTableColumns() {
        setVisibleColumns(CODE_COLUMN, TRANSLATABLE_MARK_COLUMN);
    }

    @Override
    protected Item addClassificationItemToTable(ClassificationItem classificationItem) {
        Item item = super.addClassificationItemToTable(classificationItem);
        if (classificationItem instanceof ConcreteClassificationItem) {
            Label mark = new Label("*");
            setPropertyValue(item.getItemProperty(TRANSLATABLE_MARK_COLUMN), mark);
        }
        return item;
    }

}
