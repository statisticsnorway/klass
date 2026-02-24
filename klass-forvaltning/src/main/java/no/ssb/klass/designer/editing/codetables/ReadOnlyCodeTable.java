package no.ssb.klass.designer.editing.codetables;

import java.util.List;

import com.google.common.eventbus.Subscribe;
import com.vaadin.data.Container;
import com.vaadin.ui.Component;
import com.vaadin.ui.Table;

import no.ssb.klass.core.model.ClassificationItem;
import no.ssb.klass.designer.editing.codetables.events.ReferenceChangedEvent.ReferenceCreatedEvent;
import no.ssb.klass.designer.editing.codetables.events.ReferenceChangedEvent.ReferenceRemovedEvent;

/**
 * The classificationItems in the table can not be edited. The CodeTable supports using classificationItems in table as
 * source for dragging onto other CodeTables.
 * <p>
 * When a classificationItem is dragged onto other table a <b>ReferenceCreatedEvent</b> is expected, this is used to
 * style the row representing the classificationItem as already used. Similar if the used classificationItem is removed
 * a <b>ReferenceRemovedEvent</b> is expected, so that used styling can be removed.
 */
public class ReadOnlyCodeTable extends BaseCodeTable {
    private static final String REFERENCED_COLUMN = "referenced";

    public ReadOnlyCodeTable() {
        super();
        setMultiSelect(true);
        setSelectable(true);
        setDragMode(TableDragMode.MULTIROW);
        setCellStyleGenerator(createCellStyleGenerator());
    }

    /**
     * Finds the classificationItems referencing a ClassificationItem in this codeTable, and marks these as already
     * referenced.
     */
    public void markAsReferenced(List<ClassificationItem> classificationItems) {
        for (ClassificationItem classificationItem : classificationItems) {
            if (classificationItem.isReference()) {
                markAsReferenced(classificationItem.getUuid(), true);
            }
        }
    }

    @Override
    protected void addColumnsToContainer(Container container) {
        container.addContainerProperty(CODE_COLUMN, Component.class, null);
        container.addContainerProperty(CLASSIFICATION_ITEM_COLUMN, ClassificationItem.class, null);
        container.addContainerProperty(REFERENCED_COLUMN, Boolean.class, false);
    }

    @Override
    protected void adjustTableColumns() {
        setVisibleColumns(CODE_COLUMN);
    }

    @Subscribe
    public void handleReferenceCreated(ReferenceCreatedEvent event) {
        if (getItem(event.getReferencedItemId()) == null) {
            return;
        }
        ClassificationItem classificationItem = getClassificationItemColumn(getItem(event.getReferencedItemId()));
        markAsReferenced(classificationItem.getUuid(), true);
        refreshRowCache();
    }

    private void markAsReferenced(String itemId, boolean referenced) {
        setPropertyValue(getItem(itemId).getItemProperty(REFERENCED_COLUMN), referenced);
        for (Object childItemId : getChildrenNullSafe(itemId)) {
            markAsReferenced((String) childItemId, referenced);
        }
    }

    @Subscribe
    public void handleReferenceRemoved(ReferenceRemovedEvent event) {
        if (getItem(event.getReferencedItemId()) == null) {
            return;
        }
        ClassificationItem classificationItem = getClassificationItemColumn(getItem(event.getReferencedItemId()));
        markAsReferenced(classificationItem.getUuid(), false);
        refreshRowCache();
    }

    private CellStyleGenerator createCellStyleGenerator() {
        return new CellStyleGenerator() {
            @Override
            public String getStyle(Table source, Object itemId, Object propertyId) {
                if (propertyId == null) {
                    // Styling for row
                    Boolean referenced = (Boolean) getItem(itemId).getItemProperty(REFERENCED_COLUMN).getValue();
                    if (referenced) {
                        // Current row (classificationItem) is referenced by the ClassificationVariant. Styling it to
                        // indicate that row has already been included in ClassificationVariant.
                        return "referenced";
                    }
                }
                return null;
            }
        };
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        setSelectable(!readOnly);
        setDragMode(readOnly ? TableDragMode.NONE : TableDragMode.MULTIROW);
    }
}