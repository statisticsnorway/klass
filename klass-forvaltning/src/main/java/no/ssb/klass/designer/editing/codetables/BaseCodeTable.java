package no.ssb.klass.designer.editing.codetables;

import java.util.Collection;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.TreeTable;

import no.ssb.klass.core.model.ClassificationItem;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.model.StatisticalClassification;
import no.ssb.klass.designer.editing.codetables.containers.CustomHierarchicalContainer;
import no.ssb.klass.designer.editing.codetables.events.CloseHierarchyEvent;
import no.ssb.klass.designer.editing.codetables.events.OpenHierarchyEvent;

/**
 * A BaseCodeTable lists classificationItems of a ClassificationVersion (or Variant)
 */
public abstract class BaseCodeTable extends TreeTable {

    public static final String CLASSIFICATION_ITEM_COLUMN = "classificationItem";
    public static final String CODE_COLUMN = "code";

    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected EventBus eventbus;
    protected StatisticalClassification contentSource;
    protected Language language;
    private CustomHierarchicalContainer container;

    private boolean hasChangesFlag = false;

    public BaseCodeTable() {
        // custom CSS to force scrollbars as vaadin does a poor job with dynamic size items
        this.addStyleName("force-scroll");
        container = new CustomHierarchicalContainer();
        addColumnsToContainer(container);
        setContainerDataSource(container);
        setColumnExpandRatio(CODE_COLUMN, 1);
        adjustTableColumns();
    }

    protected abstract void addColumnsToContainer(Container container);

    protected abstract void adjustTableColumns();

    public void markAsModified() {
        hasChangesFlag = true;
    }

    public void init(EventBus eventbus, StatisticalClassification contentSource, Language language) {
        this.eventbus = eventbus;
        this.contentSource = contentSource;
        this.language = language;
        removeAllItems();
        populateContainer();
    }

    public void refresh() {
        removeAllItems();
        populateContainer();
    }

    /**
     * Called when table is initialised, and populates table with rows for each ClassificationItem
     */
    protected void populateContainer() {
        // Performance improvement.
        // Populating a container that is attached to a table is slow when many rows are added
        // (this is due to that each added row triggers updates on the table). It is much faster to create a new
        // container, populating this container and then attaching it to table, as done below.
        container = new CustomHierarchicalContainer();
        addColumnsToContainer(container);
        for (ClassificationItem classificationItem : contentSource.getAllClassificationItemsLevelForLevel()) {
            addClassificationItemToTable(classificationItem);
            updateParent(classificationItem);
            updateChildrenAllowed(classificationItem);
        }
        setContainerDataSource(container);
        adjustTableColumns();
    }

    protected void updateChildrenAllowed(ClassificationItem classificationItem) {
        getContainer().setChildrenAllowed(classificationItem.getUuid(), !contentSource.isLastLevel(classificationItem
                .getLevel()));
    }

    protected Item addClassificationItemToTable(ClassificationItem classificationItem) {
        Item i = getContainer().addItem(classificationItem.getUuid());
        if (i != null) {
            setPropertyValue(i.getItemProperty(CODE_COLUMN), createCodeComponent(classificationItem));
            setPropertyValue(i.getItemProperty(CLASSIFICATION_ITEM_COLUMN), classificationItem);
        }
        return i;
    }

    protected final Component createCodeComponent(ClassificationItem classificationItem) {
        return new Label(classificationItem.getCode() + "  -  " + classificationItem.getOfficialName(language));
    }

    protected final CustomHierarchicalContainer getContainer() {
        return (CustomHierarchicalContainer) getContainerDataSource();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected final void setPropertyValue(Property property, Object value) {
        property.setValue(value);
    }

    @SuppressWarnings("unchecked")
    protected final <T> T getPropertyValue(Item item, String columnName) {
        return (T) item.getItemProperty(columnName).getValue();
    }

    protected final void updateParent(ClassificationItem classificationItem) {
        if (classificationItem.getParent() != null) {
            if (!getContainer().setParent(classificationItem.getUuid(), classificationItem.getParent().getUuid())) {
                throw new RuntimeException("Failed to set parent, is parent added to table. Child uuid: "
                        + classificationItem.getUuid() + ", parent uuid: " + classificationItem.getParent().getUuid());
            }
        }
    }

    protected final Collection<?> getChildrenNullSafe(Object itemId) {
        Collection<?> children = super.getChildren(itemId);
        return children == null ? Collections.EMPTY_LIST : children;
    }

    protected ClassificationItem getClassificationItemColumn(Item item) {
        return getPropertyValue(item, CLASSIFICATION_ITEM_COLUMN);
    }

    @Override
    public boolean addContainerProperty(Object propertyId, Class<?> type, Object defaultValue) {
        throw new UnsupportedOperationException("Add container property to container, not table");
    }

    @Override
    public Hierarchical getContainerDataSource() {
        return container;
    }

    public boolean hasChanges() {
        return hasChangesFlag;
    }

    @Subscribe
    public void closeHierarchy(CloseHierarchyEvent event) {
        if (event.getTarget() == null || event.getTarget() == this) {
            for (ClassificationItem classificationItem : contentSource.getAllClassificationItems()) {
                setCollapsed(classificationItem.getUuid(), true);
            }
        }
    }

    @Subscribe
    public void openHierarchy(OpenHierarchyEvent event) {
        if (event.getTarget() == null || event.getTarget() == this) {
            for (ClassificationItem classificationItem : contentSource.getAllClassificationItems()) {
                setCollapsed(classificationItem.getUuid(), false);
            }
        }
    }
}
