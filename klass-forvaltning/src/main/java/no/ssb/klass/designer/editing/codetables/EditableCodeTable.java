package no.ssb.klass.designer.editing.codetables;

import static java.util.stream.Collectors.*;

import java.util.List;
import java.util.Objects;

import com.google.common.eventbus.Subscribe;
import com.vaadin.data.Item;
import com.vaadin.event.ItemClickEvent;

import no.ssb.klass.core.model.ClassificationItem;
import no.ssb.klass.designer.editing.codetables.codeeditors.CodeEditor;
import no.ssb.klass.designer.editing.codetables.events.CancelEditEvent;
import no.ssb.klass.designer.editing.codetables.events.CodeCreatedEvent;
import no.ssb.klass.designer.editing.codetables.events.CodeDeletedEvent;
import no.ssb.klass.designer.editing.codetables.events.CodeUpdatedEvent;
import no.ssb.klass.designer.editing.codetables.events.LevelCreatedEvent;
import no.ssb.klass.designer.editing.codetables.events.LevelDeletedEvent;

/**
 * EditableCodeTable lists classificationItems of a ClassificationVersion (or Variant), it may edit existing
 * classificationItems.
 * <p>
 * It subscribes to numerous events such as CodeCreatedEvent, CodeDeletedEvent, etc. The events will affect which codes
 * are shown in the CodeTable.
 */
public abstract class EditableCodeTable extends BaseCodeTable {

    public EditableCodeTable() {
        addItemClickListener(event -> rowClicked(event));
    }

    /**
     * Method is called when a row is clicked. Create a codeEditor that shall be inserted instead. For example when
     * clicking a row, the row may change so that user can edit values. Subclasses may return null if no new code editor
     * is required when row is clicked.
     */
    protected abstract CodeEditor createRowSelectedCodeEditor(ClassificationItem classificationItem);

    private void rowClicked(ItemClickEvent event) {
        rowSelected(event.getItem());
    }

    protected void rowSelected(Item item) {
        ClassificationItem classificationItem = getClassificationItemColumn(item);
        if (classificationItem != null) {
            CodeEditor codeEditor = createRowSelectedCodeEditor(classificationItem);
            if (codeEditor != null) {
                commitDirtyCodeEditors();
                setPropertyValue(item.getItemProperty(CODE_COLUMN), codeEditor);
            }
        }
    }

    public void commitDirtyCodeEditors() {
        for (CodeEditor codeEditor : getOpenCodeEditors()) {
            codeEditor.closeEditor();
        }
    }

    private List<CodeEditor> getOpenCodeEditors() {
        return getContainer().getItemIds().stream()
                .map(this::getCodeEditorOrNull)
                .filter(Objects::nonNull)
                .collect(toList());
    }

    private CodeEditor getCodeEditorOrNull(Object itemId) {
        Object value = getContainer().getItem(itemId).getItemProperty(CODE_COLUMN).getValue();
        if (value instanceof CodeEditor) {
            return (CodeEditor) value;
        }
        return null;
    }


    @Subscribe
    public void classificationItemCreated(CodeCreatedEvent event) {
        if (isReadOnly()) {
            return;
        }
        markAsModified();
        addClassificationItemToTable(event.getNewItem());
        updateParent(event.getNewItem());
        updateChildrenAllowed(event.getNewItem());
        if (getContainer().containsId(event.getSiblingItemId())) {
            moveBeforeSibling(event.getNewItem().getUuid(), event.getSiblingItemId());
            // Make New itemEditor visible if out of view
            if (!getVisibleItemIds().contains(event.getSiblingItemId())) {
                setCurrentPageFirstItemIndex(getCurrentPageFirstItemIndex() + 1);
            }
        }
    }

    private void moveBeforeSibling(Object newItemId, Object siblingItemId) {
        getContainer().moveAfterSibling(newItemId, siblingItemId);
        getContainer().moveAfterSibling(siblingItemId, newItemId);
    }

    @Subscribe
    public void classificationItemUpdated(CodeUpdatedEvent event) {
        ClassificationItem classificationItem = event.getClassificationItem();
        Item item = getContainer().getItem(classificationItem.getUuid());
        setPropertyValue(item.getItemProperty(CODE_COLUMN), createCodeComponent(classificationItem));
        markAsModified();
    }

    @Subscribe
    public void classificationItemDeleted(CodeDeletedEvent event) {
        getContainer().removeItemRecursively(event.getClassificationItem().getUuid());
        markAsModified();
    }

    @Subscribe
    public void levelDeleted(LevelDeletedEvent event) {
        contentSource.getAllClassificationItems().forEach(this::updateChildrenAllowed);
        if (contentSource.getLevels().isEmpty()) {
            removeAllItems();
        }
        markAsModified();
    }

    @Subscribe
    public void levelCreated(LevelCreatedEvent event) {
        contentSource.getAllClassificationItems().forEach(this::updateChildrenAllowed);
        markAsModified();
    }



    @Subscribe
    public void cancelEdit(CancelEditEvent event) {
        ClassificationItem classificationItem = event.getClassificationItem();
        setPropertyValue(getContainer().getItem(classificationItem.getUuid()).getItemProperty(CODE_COLUMN),
                createCodeComponent(classificationItem));
    }

    public boolean hasChanges() {
        boolean dirty = getOpenCodeEditors().stream().anyMatch(CodeEditor::isDirty);
        return dirty || super.hasChanges();
    }
}