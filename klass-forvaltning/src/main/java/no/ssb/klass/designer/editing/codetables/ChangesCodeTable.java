package no.ssb.klass.designer.editing.codetables;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.common.eventbus.EventBus;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.event.dd.acceptcriteria.Or;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

import no.ssb.klass.core.model.ClassificationItem;
import no.ssb.klass.core.model.CorrespondenceMap;
import no.ssb.klass.core.model.CorrespondenceTable;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.model.StatisticalClassification;
import no.ssb.klass.designer.editing.codetables.utils.CorrespondenceTableHelper;
import no.ssb.klass.designer.util.ConfirmationDialog;

/**
 * Code table that lists changes between two versions of same classification.
 */
public class ChangesCodeTable extends BaseCodeTable {
    private static final String SOURCE_HELP_TEXT = "<Trekk kildekode hit>";
    private static final String TARGET_HELP_TEXT = "<Trekk målkode hit>";
    private static final String SOURCE_COLUMN = "source";
    private static final String TARGET_COLUMN = "target";
    private static final String CORRESPONDENCE_MAP_COLUMN = "map";
    private static final String DELETE_COLUMN = "delete";
    private CorrespondenceTable correspondenceTable;
    /*
     * New correspondenceMaps are not added directly to correspondenceTable. This since when first created it is in a
     * temporary state where either source or target will be null, and this may be rejected by correspondenceTable since
     * it checks if an identical correspondenceMap exists.
     */
    private final List<CorrespondenceMap> newCorrespondenceMaps;

    public ChangesCodeTable() {
        newCorrespondenceMaps = new ArrayList<>();
        setDropHandler(new ChangesDropHandler());
    }

    @Override
    public final void init(EventBus eventbus, StatisticalClassification contentSource, Language language) {
        CorrespondenceTableHelper.throwUnsupportedException();
    }

    public final void init(EventBus eventbus, CorrespondenceTable correspondenceTable) {
        this.correspondenceTable = correspondenceTable;
        this.eventbus = eventbus;
        newCorrespondenceMaps.clear();
        removeAllItems();
        populateContainer();
    }

    public void addNewRow() {
        if (isReadOnly()) {
            return;
        }
        Object itemId = getContainer().addItem();
        Item item = prepareNewItem(itemId);
        Label sourceLabel = new Label(SOURCE_HELP_TEXT);
        setPropertyValue(item.getItemProperty(SOURCE_COLUMN), sourceLabel);
        Label targetLabel = new Label(TARGET_HELP_TEXT);
        setPropertyValue(item.getItemProperty(TARGET_COLUMN), targetLabel);
        setCurrentPageFirstItemId(itemId);
    }

    @Override
    protected void populateContainer() {
        for (CorrespondenceMap map : correspondenceTable.getCorrespondenceMaps()) {
            addCorrespondenceMapToTable(map, "Utgått", "Ny");
        }
    }

    private void addCorrespondenceMapToTable(CorrespondenceMap map, String expiredLabel, String newLabel) {
        Item item = prepareNewItem(getContainer().addItem());
        updateItem(map, item, expiredLabel, newLabel);
        setPropertyValue(item.getItemProperty(CORRESPONDENCE_MAP_COLUMN), map);
    }

    private Item prepareNewItem(Object itemId) {
        Item item = getContainer().getItem(itemId);
        getContainer().setChildrenAllowed(itemId, false);
        if (!isReadOnly()) {
            addDeleteButton(itemId);
        }
        return item;
    }

    private void updateItem(CorrespondenceMap map, Item item, String sourceDefaultLabel, String targetDefaultLabel) {
        Label sourceLabel = createLabel(map.getSource(), sourceDefaultLabel);
        Label targetLabel = createLabel(map.getTarget(), targetDefaultLabel);
        setPropertyValue(item.getItemProperty(SOURCE_COLUMN), sourceLabel);
        setPropertyValue(item.getItemProperty(TARGET_COLUMN), targetLabel);
    }

    private Label createLabel(Optional<ClassificationItem> classificationItem, String optionalText) {
        if (!classificationItem.isPresent()) {
            return new Label(optionalText);
        }
        Label label = new Label(classificationItem.get().getCode());
        label.setDescription(classificationItem.get().getOfficialName(correspondenceTable.getPrimaryLanguage()));
        return label;
    }

    private void addDeleteButton(Object itemId) {
        Button deleteButton = new Button(FontAwesome.TRASH_O);
        deleteButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        deleteButton.setHeight("100%");
        deleteButton.addClickListener(event -> deleteItemWithConfirmation(itemId));
        setPropertyValue(getContainer().getItem(itemId).getItemProperty(DELETE_COLUMN), deleteButton);
    }

    private void deleteItemWithConfirmation(Object itemId) {
        if (isReadOnly()) {
            return;
        }
        CorrespondenceMap map = getCorrespondenceMapFromItem(itemId);
        if (map == null) {
            deleteCorrespondenceMap(map, itemId);
            return;
        }
        String sourceCode = map.getSource().isPresent() ? map.getSource().get().getCode() : "ingen";
        String targetCode = map.getTarget().isPresent() ? map.getTarget().get().getCode() : "ingen";

        String body = "Slette korrespondansen mellom: " + sourceCode + " og " + targetCode;
        UI.getCurrent().addWindow(new ConfirmationDialog("Slett", body, (answerYes) -> {
            if (answerYes) {
                deleteCorrespondenceMap(map, itemId);
            }
        }));
    }

    private CorrespondenceMap getCorrespondenceMapFromItem(Object itemId) {
        return (CorrespondenceMap) getContainer().getItem(itemId).getItemProperty(CORRESPONDENCE_MAP_COLUMN).getValue();
    }

    private void deleteCorrespondenceMap(CorrespondenceMap map, Object itemId) {
        getContainer().removeItem(itemId);
        if (map != null) {
            correspondenceTable.removeCorrespondenceMap(map);
            newCorrespondenceMaps.remove(map);
            markAsModified();
        }
    }

    @Override
    protected void addColumnsToContainer(Container container) {
        container.addContainerProperty(SOURCE_COLUMN, Label.class, null);
        container.addContainerProperty(TARGET_COLUMN, Label.class, null);
        container.addContainerProperty(CORRESPONDENCE_MAP_COLUMN, CorrespondenceMap.class, null);
        container.addContainerProperty(DELETE_COLUMN, Button.class, null);
    }

    @Override
    protected void adjustTableColumns() {
        setVisibleColumns(SOURCE_COLUMN, TARGET_COLUMN, DELETE_COLUMN);
        setColumnExpandRatio(SOURCE_COLUMN, 1.1f);
        setColumnExpandRatio(TARGET_COLUMN, 1);
        setColumnHeaders(new String[] { "Fra", "Til", "" });
        setColumnAlignment(SOURCE_COLUMN, Align.CENTER);
        setColumnAlignment(TARGET_COLUMN, Align.CENTER);
    }

    private class ChangesDropHandler implements DropHandler {
        @Override
        public AcceptCriterion getAcceptCriterion() {
            return new Or(VerticalLocationIs.TOP, VerticalLocationIs.MIDDLE, VerticalLocationIs.BOTTOM);
        }

        @Override
        public void drop(DragAndDropEvent event) {
            DataBoundTransferable transferable = (DataBoundTransferable) event.getTransferable();
            ClassificationItem classificationItem = getDraggedClassificationItem(transferable);
            CorrespondenceMap correspondenceMap = getCorrespondenceMap(event);
            if (correspondenceMap == null) {
                // Creating new map
                CorrespondenceMap map = createCorrespondenceMap(classificationItem, isDraggedFromSourceTable(
                        classificationItem));
                /*
                 * Not adding directly to correspondenceTable since it is in a temporary state where either source or
                 * target will be null, and this may be rejected by correspondenceTable since it checks if an identical
                 * correspondenceMap exists.
                 */
                newCorrespondenceMaps.add(map);
                setCorrespondenceMapOnRow(event, map);
                updateItem(map, getDroppedOntoItem(event), SOURCE_HELP_TEXT, TARGET_HELP_TEXT);
                markAsModified();
            } else {
                // Updating existing map
                String existingMapping = getExistingMappingOrNull(event);
                if (existingMapping != null) {
                    ConfirmationDialog confWindow = new ConfirmationDialog("Ok",
                            "Vil du endre korrespondansen mellom " + existingMapping + "?", (answerYes) -> {
                                if (answerYes) {
                                    updateClassificationMap(correspondenceMap, classificationItem, event);
                                }
                            });
                    UI.getCurrent().addWindow(confWindow);
                } else {
                    updateClassificationMap(correspondenceMap, classificationItem, event);
                }
            }
        }

        private String getExistingMappingOrNull(DragAndDropEvent event) {
            Item item = getDroppedOntoItem(event);
            String sourceLabel = ((Label) getPropertyValue(item, SOURCE_COLUMN)).getValue();
            String targetLabel = ((Label) getPropertyValue(item, TARGET_COLUMN)).getValue();
            if (SOURCE_HELP_TEXT.equals(sourceLabel) || TARGET_HELP_TEXT.equals(targetLabel)) {
                return null;
            }
            return sourceLabel + " og " + targetLabel;
        }

        private void updateClassificationMap(CorrespondenceMap correspondenceMap, ClassificationItem classificationItem,
                DragAndDropEvent event) {
            if (isDraggedFromSourceTable(classificationItem)) {
                if (alreadyContainsMapping(new CorrespondenceMap(classificationItem, correspondenceMap.getTarget()
                        .orElse(null)))) {
                    return;
                }
                correspondenceTable.updateCorrespondenceMapSource(correspondenceMap, classificationItem);
                moveToCorrespondenceTable(correspondenceMap);
            } else {
                if (alreadyContainsMapping(new CorrespondenceMap(correspondenceMap.getSource()
                        .orElse(null), classificationItem))) {
                    return;
                }
                correspondenceTable.updateCorrespondenceMapTarget(correspondenceMap, classificationItem);
                moveToCorrespondenceTable(correspondenceMap);
            }
            Item item = getDroppedOntoItem(event);
            updateItem(correspondenceMap, item, getLabelValue(item.getItemProperty(SOURCE_COLUMN)),
                    getLabelValue(item.getItemProperty(TARGET_COLUMN)));
            markAsModified();
        }

        private String getLabelValue(Property<?> labelProperty) {
            return ((Label) labelProperty.getValue()).getValue();
        }

        private void moveToCorrespondenceTable(CorrespondenceMap correspondenceMap) {
            if (newCorrespondenceMaps.contains(correspondenceMap)) {
                newCorrespondenceMaps.remove(correspondenceMap);
                correspondenceTable.addCorrespondenceMap(correspondenceMap);
            }
        }

        private boolean alreadyContainsMapping(CorrespondenceMap map) {
            return CorrespondenceTableHelper.alreadyContainsMapping(correspondenceTable, map);
        }

        private boolean isDraggedFromSourceTable(ClassificationItem classificationItem) {
            return correspondenceTable.getSource().getAllClassificationItems().stream().filter(item -> item
                    .getUuid().equals(classificationItem.getUuid())).findFirst().isPresent();
        }

        private CorrespondenceMap createCorrespondenceMap(ClassificationItem item, boolean source) {
            if (source) {
                return new CorrespondenceMap(item, null);
            } else {
                return new CorrespondenceMap(null, item);
            }
        }

        private CorrespondenceMap getCorrespondenceMap(DragAndDropEvent event) {
            return getPropertyValue(getDroppedOntoItem(event), CORRESPONDENCE_MAP_COLUMN);
        }

        private void setCorrespondenceMapOnRow(DragAndDropEvent event, CorrespondenceMap map) {
            setPropertyValue(getDroppedOntoItem(event).getItemProperty(CORRESPONDENCE_MAP_COLUMN), map);
        }

        private Item getDroppedOntoItem(DragAndDropEvent event) {
            return getContainer().getItem(getItemIdOver(event));
        }

        private Object getItemIdOver(DragAndDropEvent event) {
            return ((AbstractSelectTargetDetails) event.getTargetDetails()).getItemIdOver();
        }

        private ClassificationItem getDraggedClassificationItem(DataBoundTransferable transferable) {
            Table draggedFromTable = (Table) transferable.getSourceComponent();
            Object draggedItemId = transferable.getItemId();
            return getClassificationItemColumn(draggedFromTable.getItem(draggedItemId));
        }

        private ClassificationItem getClassificationItemColumn(Item item) {
            return getPropertyValue(item, CLASSIFICATION_ITEM_COLUMN);
        }

    }

    /**
     * Adds created correspondenceMaps to correspondenceTable. Must be called before correspondenceTable is saved
     */
    public void commitNewCorrespondenceMaps() {
        // Wrapped in ArrayList to avoid ConcurrentModificationException
        for (CorrespondenceMap map : new ArrayList<>(newCorrespondenceMaps)) {
            if (correspondenceTable.alreadyContainsIdenticalMap(map)) {
                // Silently removed, however only correspondenceMaps that has either source or target as null
                newCorrespondenceMaps.remove(map);
                continue;
            }
            correspondenceTable.addCorrespondenceMap(map);
            newCorrespondenceMaps.remove(map);
        }
    }
}
