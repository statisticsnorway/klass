package no.ssb.klass.designer.editing.codetables;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

import no.ssb.klass.core.model.ClassificationItem;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.model.Level;
import no.ssb.klass.core.model.StatisticalClassification;
import no.ssb.klass.designer.editing.codetables.codeeditors.CodeEditor;
import no.ssb.klass.designer.editing.codetables.codeeditors.NewCodeEditor;
import no.ssb.klass.designer.editing.codetables.codeeditors.UpdateCodeEditor;
import no.ssb.klass.designer.editing.codetables.events.CodeDeletedEvent;
import no.ssb.klass.designer.editing.codetables.events.LevelCreatedEvent;
import no.ssb.klass.designer.editing.codetables.utils.CodeTableUtils;
import no.ssb.klass.designer.service.ClassificationFacade;
import no.ssb.klass.designer.util.ConfirmationDialog;

/**
 * PrimaryCodeTable is used for primaryLanguage. Only PrimaryCodeTable has possibility to delete and add
 * classificationItems.
 * <p>
 * Generates events for following actions:
 * <ul>
 * <li>CodeDeletedEvent</li>
 * </ul>
 */
public class PrimaryCodeTable extends EditableCodeTable {
    private static final String DELETE_COLUMN = "delete";
    private ClassificationFacade classificationFacade;

    @Override
    public void init(EventBus eventbus, StatisticalClassification contentSource, Language language) {
        throw new UnsupportedOperationException("Use overloaded method");
    }

    public void init(EventBus eventbus, StatisticalClassification contentSource, Language language,
            ClassificationFacade classificationFacade) {
        this.classificationFacade = classificationFacade;
        super.init(eventbus, contentSource, language);
        if (isReadOnly()) {
            setVisibleColumns(CODE_COLUMN);
        }
    }

    @Override
    protected void addColumnsToContainer(Container container) {
        container.addContainerProperty(CODE_COLUMN, Component.class, null);
        container.addContainerProperty(DELETE_COLUMN, Button.class, null);
        container.addContainerProperty(CLASSIFICATION_ITEM_COLUMN, ClassificationItem.class, null);
    }

    @Override
    protected void adjustTableColumns() {
        setVisibleColumns(CODE_COLUMN, DELETE_COLUMN);
    }

    @Override
    protected Item addClassificationItemToTable(ClassificationItem classificationItem) {
        Item item = super.addClassificationItemToTable(classificationItem);
        Button deleteButton = new Button(FontAwesome.TRASH_O);
        deleteButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        deleteButton.setHeight("100%");
        deleteButton.addClickListener(event -> deleteItemWithConfirmation(classificationItem));
        setPropertyValue(item.getItemProperty(DELETE_COLUMN), deleteButton);
        return item;
    }

    @Override
    protected CodeEditor createRowSelectedCodeEditor(ClassificationItem classificationItem) {
        if (classificationItem.isReference()) {
            return null;
        }
        if (isReadOnly()) {
            return null;
        }
        return new UpdateCodeEditor(eventbus, contentSource, classificationItem, language);
    }

    @Override
    protected void updateChildrenAllowed(ClassificationItem classificationItem) {
        boolean isLastLevel = contentSource.isLastLevel(classificationItem.getLevel());
        getContainer().setChildrenAllowed(classificationItem.getUuid(), !isLastLevel);

        if (isLastLevel) {
            // Remove any NewCodeEditors dangling if a level has been deleted
            for (Object childId : getChildrenNullSafe(classificationItem.getUuid())) {
                getContainer().removeItemRecursively(childId);
            }
        } else {
            addNewCodeEditor(classificationItem, contentSource.getNextLevel(classificationItem.getLevel()).get());
        }
    }

    @Override
    protected void populateContainer() {
        super.populateContainer();
        for (ClassificationItem classificationItem : getNotLeafClassificationItems()) {
            moveNewCodeEditorLast(classificationItem);
        }
        if (!contentSource.getLevels().isEmpty()) {
            addNewCodeEditor(null, contentSource.getFirstLevel().get());
        }
    }

    private List<ClassificationItem> getNotLeafClassificationItems() {
        List<ClassificationItem> notLeafItems = new ArrayList<>();
        Optional<Level> level = contentSource.getFirstLevel();
        while (level.isPresent() && !contentSource.isLastLevel(level.get())) {
            notLeafItems.addAll(level.get().getClassificationItems());
            level = contentSource.getNextLevel(level.get());
        }
        return notLeafItems;
    }

    protected void moveNewCodeEditorLast(ClassificationItem parent) {
        if (getContainer().getItem(parent) == null) {
            // If readOnly a NewCodeEditor has not been added, and hence can not be moved
            return;
        }
        // ItemId of NewCodeEditor is parent
        getContainer().removeItem(parent);
        addNewCodeEditor(parent, contentSource.getNextLevel(parent.getLevel()).get());
    }

    private void deleteItemWithConfirmation(ClassificationItem classificationItem) {
        if (isReadOnly()) {
            return;
        }
        String body = "Slette kode: <b>" + classificationItem.getCode() + " - " + classificationItem.getOfficialName(
                language) + "</b> og alle elementene som ligger under denne?";
        ConfirmationDialog confWindow = new ConfirmationDialog("Slett", body, (answerYes) -> {
            if (answerYes) {
                deleteItem(classificationItem);
            }
        });
        UI.getCurrent().addWindow(confWindow);
    }

    protected void deleteItem(ClassificationItem classificationItem) {
        if (CodeTableUtils.existsReferencesToClassificationItem(classificationFacade, classificationItem)) {
            return;
        }
        contentSource.deleteClassificationItem(classificationItem);
        eventbus.post(new CodeDeletedEvent(classificationItem));
    }

    protected void addNewCodeEditor(ClassificationItem parent, Level level) {
        if (isReadOnly()) {
            return;
        }
        NewCodeEditor codeEditor = new NewCodeEditor(eventbus, contentSource, parent, level, language);
        Item item = getContainer().addItem(codeEditor.getItemId());
        if (item == null) {
            // already inserted codeEditor for this parent
            return;
        }
        setPropertyValue(item.getItemProperty(CODE_COLUMN), codeEditor);
        if (parent != null) {
            getContainer().setParent(codeEditor.getItemId(), parent.getUuid());
        }
        getContainer().setChildrenAllowed(codeEditor.getItemId(), false);
    }

    @Subscribe
    public void checkFirstLevelCreated(LevelCreatedEvent event) {
        if (contentSource.isFirstLevel(event.getCreatedLevel())) {
            addNewCodeEditor(null, event.getCreatedLevel());
        }
    }
}
