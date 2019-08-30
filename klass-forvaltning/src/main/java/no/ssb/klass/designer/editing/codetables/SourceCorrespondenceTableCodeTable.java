package no.ssb.klass.designer.editing.codetables;

import java.util.List;

import com.google.common.eventbus.EventBus;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.event.dd.acceptcriteria.Or;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

import no.ssb.klass.core.model.ClassificationItem;
import no.ssb.klass.core.model.CorrespondenceMap;
import no.ssb.klass.core.model.CorrespondenceTable;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.model.StatisticalClassification;
import no.ssb.klass.designer.editing.codetables.events.ReferenceChangedEvent.ReferenceCreatedEvent;
import no.ssb.klass.designer.editing.codetables.events.ReferenceChangedEvent.ReferenceRemovedEvent;
import no.ssb.klass.designer.editing.codetables.utils.CorrespondenceTableHelper;
import no.ssb.klass.designer.editing.codetables.utils.DragDropUtils;
import no.ssb.klass.designer.util.ConfirmationDialog;

/**
 * Lists classificationItems for source version of CorrespondenceTable. ClassificationItem from a target version may be
 * dragged and dropped onto this table, creating mapping between source and target classificationItems.
 * <p>
 * Generates events for following actions:
 * <ul>
 * <li>ReferenceCreatedEvent - so that classificationItem in target ClassificationVersion can be marked as mapped</li>
 * <li>ReferenceRemovedEvent - so that classificationItem in target ClassificationVersion can be marked as not mapped
 * </li>
 * </ul>
 */
public class SourceCorrespondenceTableCodeTable extends BaseCodeTable {
    private static final String DELETE_COLUMN = "delete";
    private static final String CORRESPONDENCE_MAP_COLUMN = "map";
    private CorrespondenceTable correspondenceTable;

    public SourceCorrespondenceTableCodeTable() {
        setDropHandler(new DropHandler() {
            @Override
            public AcceptCriterion getAcceptCriterion() {
                return new Or(VerticalLocationIs.TOP, VerticalLocationIs.MIDDLE, VerticalLocationIs.BOTTOM);
            }

            @Override
            public void drop(DragAndDropEvent event) {
                DataBoundTransferable transferable = (DataBoundTransferable) event.getTransferable();
                Object targetItemId = getTargetItemId(event);
                ClassificationItem targetItem = getClassificationItemColumn(getItem(targetItemId));
                Table sourceTable = (Table) transferable.getSourceComponent();
                for (Object sourceItemId : DragDropUtils.getSourceItemIds(transferable)) {
                    CorrespondenceMap map = new CorrespondenceMap(targetItem, getSourceClassificationItem(sourceTable,
                            sourceItemId));
                    if (CorrespondenceTableHelper.alreadyContainsMapping(correspondenceTable, map)) {
                        return;
                    }
                    correspondenceTable.addCorrespondenceMap(map);
                    addCorrespondanceMapToTable(map);
                }
                markAsModified();
            }

            private Object getTargetItemId(DragAndDropEvent event) {
                Object targetItemId = ((AbstractSelectTargetDetails) event.getTargetDetails()).getItemIdOver();
                CorrespondenceMap correspondenceMap = getPropertyValue(getContainer().getItem(targetItemId),
                        CORRESPONDENCE_MAP_COLUMN);
                if (correspondenceMap != null) {
                    return correspondenceMap.getSource().get().getUuid();
                }

                return targetItemId;
            }

            private ClassificationItem getSourceClassificationItem(Table sourceTable, Object sourceItemId) {
                return getClassificationItemColumn(sourceTable.getItem(sourceItemId));
            }
        });
    }

    @Override
    public final void init(EventBus eventbus, StatisticalClassification contentSource, Language language) {
        CorrespondenceTableHelper.throwUnsupportedException();
    }

    public final void init(EventBus eventbus, CorrespondenceTable correspondenceTable) {
        this.correspondenceTable = correspondenceTable;
        super.init(eventbus, correspondenceTable.getSource(), correspondenceTable.getSource().getPrimaryLanguage());
    }

    @Override
    protected final void populateContainer() {
        List<ClassificationItem> classificationItems = CorrespondenceTableHelper.findClassificationItems(
                correspondenceTable.getSource(), correspondenceTable.getSourceLevel());
        for (ClassificationItem classificationItem : classificationItems) {
            addClassificationItemToTable(classificationItem);
            getContainer().setChildrenAllowed(classificationItem.getUuid(), false);
        }
        for (CorrespondenceMap map : correspondenceTable.getCorrespondenceMaps()) {
            addCorrespondanceMapToTable(map);
        }
    }

    private void addCorrespondanceMapToTable(CorrespondenceMap map) {
        ClassificationItem targetCopy = map.getTarget().get().copy();
        Item item = addClassificationItemToTable(targetCopy);
        if (!isReadOnly()) {
            addDeleteButton(item, targetCopy.getUuid(), map);
        }
        setPropertyValue(item.getItemProperty(CORRESPONDENCE_MAP_COLUMN), map);
        updateSourceItemWithChildrenAllowed(map.getSource().get().getUuid());
        getContainer().setParent(targetCopy.getUuid(), map.getSource().get().getUuid());
        getContainer().setChildrenAllowed(targetCopy.getUuid(), false);
        eventbus.post(new ReferenceCreatedEvent(map.getTarget().get().getUuid()));
    }

    private void addDeleteButton(Item item, String uuid, CorrespondenceMap map) {
        Button deleteButton = new Button(FontAwesome.TRASH_O);
        deleteButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        deleteButton.setHeight("100%");
        deleteButton.addClickListener(event -> deleteItemWithConfirmation(uuid, map));
        setPropertyValue(item.getItemProperty(DELETE_COLUMN), deleteButton);
    }

    private void deleteItemWithConfirmation(String itemId, CorrespondenceMap map) {
        if (isReadOnly()) {
            return;
        }
        String body = "Slette knytningen mellom: " + map.getSource().get().getCode() + " og " + map.getTarget().get()
                .getCode();
        UI.getCurrent().addWindow(new ConfirmationDialog("Slett", body, (answerYes) -> {
            if (answerYes) {
                deleteCorrespondenceMap(itemId, map);
            }
        }));
    }

    private void deleteCorrespondenceMap(String itemId, CorrespondenceMap map) {
        getContainer().removeItem(itemId);
        if (!getContainer().hasChildren(map.getSource().get().getUuid())) {
            getContainer().setChildrenAllowed(map.getSource().get().getUuid(), false);
        }
        correspondenceTable.removeCorrespondenceMap(map);
        eventbus.post(new ReferenceRemovedEvent(map.getTarget().get().getUuid()));
        markAsModified();
    }

    private void updateSourceItemWithChildrenAllowed(Object targetItemId) {
        getContainer().setChildrenAllowed(targetItemId, true);
        setCollapsed(targetItemId, false);
    }

    @Override
    protected void updateChildrenAllowed(ClassificationItem classificationItem) {
        boolean isFirstLevel = contentSource.isFirstLevel(classificationItem.getLevel());
        getContainer().setChildrenAllowed(classificationItem.getUuid(), isFirstLevel);
    }

    @Override
    protected void addColumnsToContainer(Container container) {
        container.addContainerProperty(CODE_COLUMN, Component.class, null);
        container.addContainerProperty(CLASSIFICATION_ITEM_COLUMN, ClassificationItem.class, null);
        container.addContainerProperty(CORRESPONDENCE_MAP_COLUMN, CorrespondenceMap.class, null);
        container.addContainerProperty(DELETE_COLUMN, Button.class, null);
    }

    @Override
    protected void adjustTableColumns() {
        setVisibleColumns(CODE_COLUMN, DELETE_COLUMN);
    }
}
