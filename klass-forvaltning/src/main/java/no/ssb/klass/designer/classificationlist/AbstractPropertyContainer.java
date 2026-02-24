package no.ssb.klass.designer.classificationlist;

import java.util.List;

import com.vaadin.data.Item;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.Resource;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

import no.ssb.klass.core.model.ClassificationEntityOperations;
import no.ssb.klass.core.service.KlassMessageException;
import no.ssb.klass.designer.components.ClassificationListViewSelection;
import no.ssb.klass.designer.user.UserContext;
import no.ssb.klass.designer.util.ConfirmationDialog;
import no.ssb.klass.designer.util.KlassTheme;
import no.ssb.klass.designer.util.VaadinUtil;

/**
 * @author Mads Lundemo, SSB.
 */
@SuppressWarnings("serial")
@SpringComponent
abstract class AbstractPropertyContainer<T extends ClassificationEntityOperations> extends GeneratedPropertyContainer {
    public static final String ENTITY = "Entity";
    public static final String NAME = "Name";
    public static final String DELETE = "Delete";
    public static final String OPEN = "Open";
    private final UserContext userContext;

    private AbstractPropertyContainer(Indexed container, UserContext userContext) {
        super(container);
        this.container = container;
        this.userContext = userContext;
        defineProperties();
    }

    AbstractPropertyContainer(UserContext userContext) {
        this(new IndexedContainer(), userContext);
    }

    /**
     * Override this method to define properties for container items
     */
    protected abstract void defineProperties();

    /**
     * Override this method to populate each container item with its corresponding properties.
     *
     * @param item
     *            item to populate with properties
     * @param entry
     *            DB entry containing data for use with the Item.
     */
    protected abstract void populateProperties(Item item, T entry);

    protected final Indexed container;

    /**
     * Add entries to container and populate container items with data.
     *
     * @param entries
     *            list of entries
     */
    public void addEntriesAndPopulateProperties(List<T> entries) {
        for (T entry : entries) {
            Item item = addItem(entry.getUuid());
            populateProperties(item, entry);
        }
    }

    /**
     * adds property definition to container
     *
     * @param propertyId
     *            unique propertyId (property name)
     * @param type
     *            type of data that this Property will contain
     * @param defaultValue
     *            default value if no value is set during population
     * @return true if the operation succeeded, false if not
     */
    public boolean addProperty(Object propertyId, Class<?> type, Object defaultValue) {
        return container.addContainerProperty(propertyId, type, defaultValue);
    }

    /**
     * this method will allow you to hide a given property so it wont be shown in Vaadins GUI components.
     *
     * @param propertyId
     *            unique propertyId (property name) of the property you want to hide.
     * @return true if the operation succeeded, false if not
     */
    public boolean hideProperty(Object propertyId) {
        return removeContainerProperty(propertyId);
    }

    protected Button createButton(ClassificationEntityOperations entity, Resource icon, String tooltip) {
        Button button = new Button();
        button.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        button.addStyleName(KlassTheme.ROW_BUTTON);
        button.setIcon(icon);
        button.setDescription(tooltip);
        button.setData(entity);
        return button;
    }

    protected void deleteClassificationEntityWithConfirmation(T baseEntity) {
        StringBuilder confirmationText = new StringBuilder();
        if (baseEntity.isPublishedInAnyLanguage()) {
            if (userContext.isAdministrator()) {
                confirmationText.append("<b>").append(baseEntity.getNameInPrimaryLanguage()).append(
                        "</b> er publisert!<br>");
                confirmationText.append("Er du sikker på at du ønsker å slette den?");
            } else {
                Notification.show(baseEntity.getNameInPrimaryLanguage()
                        + " er publisert.\nBare administrator kan slette den.", Type.WARNING_MESSAGE);
                return;
            }
        } else {
            confirmationText.append("Er du sikker på at du ønsker å slette:<br>");
            confirmationText.append("<b>").append(baseEntity.getNameInPrimaryLanguage()).append("</b>");
        }
        ConfirmationDialog confWindow = new ConfirmationDialog("Slett", confirmationText.toString(), (answerYes) -> {
            try {
                if (answerYes) {
                    deleteClassificationEntity(baseEntity);
                    VaadinUtil.getKlassState().setClassificationListViewSelection(createSelectionForDeletedEntity(
                            baseEntity));
                    VaadinUtil.navigateToCurrentPage();
                }
            } catch (KlassMessageException exception) {
                Notification.show(exception.getMessage(), Type.WARNING_MESSAGE);
            }
        });
        UI.getCurrent().addWindow(confWindow);
    }

    @SuppressWarnings("unchecked")
    protected T getClassificationEntity(ClickEvent event) {
        return (T) event.getButton().getData();
    }

    protected abstract void deleteClassificationEntity(T entity) throws KlassMessageException;

    /**
     * After deleting an entity the application navigates to current page (ClassificationListView). To restore selection
     * to parent of deleted entity subclasses must provide a ClassificationListViewSelection with path to parent of
     * deleted entity
     * 
     * @param deletedEntity
     * @return ClassificationListViewSelection with path to parent of deleted entity
     */
    protected ClassificationListViewSelection createSelectionForDeletedEntity(T deletedEntity) {
        return null;
    }
}
