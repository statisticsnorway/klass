package no.ssb.klass.designer.editing.codetables;

import java.util.List;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.vaadin.data.Item;
import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.event.dd.acceptcriteria.Or;
import com.vaadin.shared.ui.dd.VerticalDropLocation;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;

import no.ssb.klass.core.model.ClassificationItem;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.model.Level;
import no.ssb.klass.core.model.ReferencingClassificationItem;
import no.ssb.klass.core.model.StatisticalClassification;
import no.ssb.klass.designer.editing.codetables.codeeditors.NewCodeEditor;
import no.ssb.klass.designer.editing.codetables.events.CodeCreatedEvent;
import no.ssb.klass.designer.editing.codetables.events.ReferenceChangedEvent.ReferenceCreatedEvent;
import no.ssb.klass.designer.editing.codetables.events.ReferenceChangedEvent.ReferenceRemovedEvent;
import no.ssb.klass.designer.editing.codetables.utils.DragDropUtils;
import no.ssb.klass.designer.service.ClassificationFacade;

/**
 * VariantCodeTable lists classificationItems for a ClassificationVariant. It supports dragging and dropping
 * classificationItems from its owning ClassificationVersion (a separate codeTable).
 * <p>
 * Only supports creating new classificationItems for the first level. These classificationItems are then considered
 * grouping elements, which classificationItems from owning ClassificationVersion can be dropped upon.
 * <p>
 * Generates events for following actions:
 * <ul>
 * <li>CodeDeletedEvent</li>
 * <li>ReferenceCreatedEvent - so that classificationItem in owning ClassificationVersion can be marked as used in
 * variant</li>
 * <li>ReferenceRemovedEvent - so that classificationItem in owning ClassificationVersion can be marked as not used in
 * variant</li>
 * </ul>
 */
public class VariantCodeTable extends PrimaryCodeTable {

    private boolean dragIncludeChildren = false;
    protected StatisticalClassification referenceSource;

    @Override
    public void init(EventBus eventbus, StatisticalClassification contentSource,
            Language language, ClassificationFacade classificationFacade) {
        throw new UnsupportedOperationException("Use overloaded method");
    }

    public void init(EventBus eventbus, StatisticalClassification contentSource,
            StatisticalClassification referenceSource, Language language,
            ClassificationFacade classificationFacade) {
        super.init(eventbus, contentSource, language, classificationFacade);
        this.referenceSource = referenceSource;

    }

    public void setDragIncludeChildren(boolean dragIncludeChildren) {
        this.dragIncludeChildren = dragIncludeChildren;
    }

    public VariantCodeTable() {
        super();

        setDropHandler(new DropHandler() {
            @Override
            public AcceptCriterion getAcceptCriterion() {
                return new Or(VerticalLocationIs.TOP, VerticalLocationIs.MIDDLE);
            }

            @Override
            public void drop(DragAndDropEvent event) {

                DataBoundTransferable transferable = (DataBoundTransferable) event.getTransferable();
                AbstractSelectTargetDetails target = (AbstractSelectTargetDetails) event.getTargetDetails();
                Object targetItemId = target.getItemIdOver();
                Item droppedOnItem = getItem(targetItemId);
                Object editor = getPropertyValue(droppedOnItem, CODE_COLUMN);
                ClassificationItem targetItem = getClassificationItemColumn(droppedOnItem);
                boolean isTargetNewCodeEditor = editor instanceof NewCodeEditor;
                if (target.getDropLocation() == VerticalDropLocation.MIDDLE && !areChildrenAllowed(targetItemId)
                        && !isTargetNewCodeEditor) {
                    Notification.show("Ikke mulig å legge til under elementer her", Type.WARNING_MESSAGE);
                    return;
                }
                if (isAnyDraggedCodeAlreadyPresent(transferable)) {
                    return;
                }

                Table sourceTable = (Table) transferable.getSourceComponent();
                Level level;
                for (Object sourceItemId : DragDropUtils.getSourceItemIds(transferable)) {
                    ReferencingClassificationItem refClassificationItem = createReferencingClassificationItem(
                            sourceTable,
                            sourceItemId);
                    Object insertBefore;
                    ClassificationItem parent;
                    if (isTargetNewCodeEditor) {
                        // Dropped over NewCodeEditor
                        ClassificationItem parentItem = ((NewCodeEditor) editor).getParentClassificationItem();
                        if (parentItem != null) {
                            level = contentSource.getLevel(parentItem.getLevel().getLevelNumber() + 1);
                            insertBefore = targetItemId;
                            parent = parentItem;
                        } else {
                            level = contentSource.getFirstLevel().get();
                            insertBefore = targetItemId;
                            parent = null;
                        }

                    } else {
                        if (target.getDropLocation() == VerticalDropLocation.TOP) {
                            // Dropped above an existing element, the dropped element shall be a sibling
                            level = targetItem.getLevel();
                            insertBefore = targetItemId;
                            parent = targetItem.getParent();
                        } else {
                            // Dropped onto an existing element, the dropped element shall be a child
                            level = contentSource.getNextLevel(targetItem.getLevel()).get();
                            insertBefore = null;
                            parent = targetItem;
                            setCollapsed(targetItemId, false);
                        }
                    }
                    if (dragIncludeChildren) {
                        // find items children depth and check to make sure we got enough levels
                        int levelsNeeded = getLevelsNeeded(refClassificationItem.getReferenceItem());
                        // Note: the variable level is the level we will be putting our items in so we will subtract 1
                        int levelsAvailable = contentSource.getLastLevelNumber() - (level.getLevelNumber() - 1);
                        if (levelsAvailable < levelsNeeded) {
                            Notification.show("Varianten har ikke tilstrekkelig med nivåer", Type.WARNING_MESSAGE);
                            return;
                        }
                        contentSource.addClassificationItem(refClassificationItem, level.getLevelNumber(), parent);
                        eventbus.post(new CodeCreatedEvent(refClassificationItem, insertBefore));
                        addItemChildren(refClassificationItem, level.getLevelNumber());
                        updateSourceItem(sourceItemId);
                    } else {
                        contentSource.addClassificationItem(refClassificationItem, level.getLevelNumber(), parent);
                        eventbus.post(new CodeCreatedEvent(refClassificationItem, insertBefore));
                        updateSourceItem(sourceItemId);
                    }
                }
            }

            private void addItemChildren(ReferencingClassificationItem parentItem, int levelNumber) {
                List<ClassificationItem> children = referenceSource.getChildrenOfClassificationItem(parentItem
                        .getReferenceItem());
                int childLevel = levelNumber + 1;
                for (ClassificationItem child : children) {
                    ReferencingClassificationItem refClassificationItem = new ReferencingClassificationItem(child);
                    contentSource.addClassificationItem(refClassificationItem, childLevel, parentItem);
                    eventbus.post(new CodeCreatedEvent(refClassificationItem, null));
                    addItemChildren(refClassificationItem, childLevel);
                }
                moveNewCodeEditorLast(parentItem);

            }

            private int getLevelsNeeded(ClassificationItem item) {
                return getChildrenDepth(item, 0);
            }

            private int getChildrenDepth(ClassificationItem item, int depth) {
                List<ClassificationItem> children = referenceSource.getChildrenOfClassificationItem(item);
                int childDepth = ++depth;
                for (ClassificationItem child : children) {
                    childDepth = Math.max(childDepth, getChildrenDepth(child, depth));

                }
                return childDepth;
            }

            private boolean isAnyDraggedCodeAlreadyPresent(DataBoundTransferable transferable) {
                Table sourceTable = (Table) transferable.getSourceComponent();
                for (Object sourceItemId : DragDropUtils.getSourceItemIds(transferable)) {
                    ClassificationItem sourceClassificationItem = getSourceClassificationItem(sourceTable,
                            sourceItemId);
                    if (contentSource.hasClassificationItem(sourceClassificationItem.getCode())) {
                        Notification.show("Kode: " + sourceClassificationItem.getCode() + ", finnes allerede",
                                Type.WARNING_MESSAGE);
                        return true;
                    }
                }
                return false;
            }

            private ReferencingClassificationItem createReferencingClassificationItem(Table sourceTable,
                    Object sourceItemId) {
                ReferencingClassificationItem classificationItem = new ReferencingClassificationItem(
                        getSourceClassificationItem(
                        sourceTable, sourceItemId));
                return classificationItem;
            }

            private ClassificationItem getSourceClassificationItem(Table sourceTable, Object sourceItemId) {
                return getClassificationItemColumn(sourceTable.getItem(sourceItemId));
            }

            private void updateSourceItem(Object sourceItemId) {
                eventbus.post(new ReferenceCreatedEvent(sourceItemId));
            }
        });
    }



    @Override
    protected void deleteItem(ClassificationItem classificationItem) {
        postReferenceRemoveEvent(classificationItem);
        super.deleteItem(classificationItem);
    }

    private void postReferenceRemoveEvent(ClassificationItem classificationItem) {
        if (classificationItem.isReference()) {
            eventbus.post(new ReferenceRemovedEvent(classificationItem.getUuid()));
        }
        for (ClassificationItem child : contentSource.getChildrenOfClassificationItem(classificationItem)) {
            postReferenceRemoveEvent(child);
        }
    }

    @Subscribe
    public void handleReferenceCreated(ReferenceCreatedEvent event) {
        if (getItem(event.getReferencedItemId()) == null) {
            return;
        }
        ClassificationItem classificationItem = getClassificationItemColumn(getItem(event.getReferencedItemId()));
        moveNewCodeEditorLast(classificationItem.getParent());
    }

    @Subscribe
    public void handleReferenceRemoved(ReferenceRemovedEvent event) {
        if (getItem(event.getReferencedItemId()) == null) {
            return;
        }
        ClassificationItem classificationItem = getClassificationItemColumn(getItem(event.getReferencedItemId()));
        moveNewCodeEditorLast(classificationItem.getParent());
    }

    @Override
    protected void addNewCodeEditor(ClassificationItem parent, Level level) {
        if (isReadOnly()) {
            return;
        }
        super.addNewCodeEditor(parent, level);
    }

}
