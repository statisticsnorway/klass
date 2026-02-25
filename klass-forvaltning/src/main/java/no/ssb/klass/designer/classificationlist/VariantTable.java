package no.ssb.klass.designer.classificationlist;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.google.common.collect.ImmutableMap;
import com.vaadin.data.Item;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

import no.ssb.klass.core.model.BaseEntity;
import no.ssb.klass.core.model.ClassificationEntityOperations;
import no.ssb.klass.core.model.ClassificationVariant;
import no.ssb.klass.core.model.CorrespondenceTable;
import no.ssb.klass.core.service.KlassMessageException;
import no.ssb.klass.designer.components.ClassificationListViewSelection;
import no.ssb.klass.designer.editing.correspondencetable.EditCorrespondenceTableView;
import no.ssb.klass.designer.editing.variant.EditVariantEditorView;
import no.ssb.klass.designer.service.ClassificationFacade;
import no.ssb.klass.designer.user.UserContext;
import no.ssb.klass.designer.util.VaadinUtil;
import no.ssb.klass.designer.windows.NewCorrespondenceTableWindow;
import no.ssb.klass.designer.windows.NewVariantWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
@UIScope
@SpringComponent
public class VariantTable extends AbstractTable {
    private static final Logger log = LoggerFactory.getLogger(VariantTable.class);
    private static final String NEW_CORRESPONDENCE_TABLE_TOOLTIP = "Lag ny korrespondansetabell";
    private static final String NEW_VARIANT_TOOLTIP = "Lag ny variant";

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private ClassificationFacade classificationFacade;

    private UserContext userContext;

    private Table table;
    private TextField filterBox;
    private Button addCorrespondenceButton;
    private Button addVariantButton;

    @Autowired
    public void init(VersionTable versionTable, UserContext userContext) {
        this.userContext = userContext;
        table = createTable(new VariantContainer(userContext, classificationFacade, versionTable));
        table.setColumnExpandRatio(AbstractPropertyContainer.NAME, 1);
        addCorrespondenceButton = createAddElementButton(NEW_CORRESPONDENCE_TABLE_TOOLTIP);
        addCorrespondenceButton.setCaption("K-tabell");
        addCorrespondenceButton.addClickListener(event1 -> newCorrespondenceTable(versionTable));
        addCorrespondenceButton.setEnabled(false);
        addVariantButton = createAddElementButton(NEW_VARIANT_TOOLTIP);
        addVariantButton.setCaption("Variant");
        addVariantButton.addClickListener(event -> newVariant(versionTable));
        addVariantButton.setEnabled(false);
        filterBox = createFilterBox(table, "Filtrer Korrespondansetabell/variant");
        rootLayout.addComponents(createHeader("Korrespondansetabell/variant", addCorrespondenceButton,
                addVariantButton), wrapFilter(filterBox), table);
        rootLayout.setExpandRatio(table, 1);

    }

    public void selectItem(final ClassificationEntityOperations item) {
        selectItem(item, table);
    }

    private void newCorrespondenceTable(VersionTable versionTable) {
        Long versionId = versionTable.getSelectedVersionId();
        if (versionId != null) {
            NewCorrespondenceTableWindow window = applicationContext.getBean(NewCorrespondenceTableWindow.class);
            UI.getCurrent().addWindow(window);
            window.init(versionId);
        }

    }

    private void newVariant(VersionTable versionTable) {
        Long versionId = versionTable.getSelectedVersionId();
        if (versionId != null) {
            NewVariantWindow window = applicationContext.getBean(NewVariantWindow.class);
            UI.getCurrent().addWindow(window);
            window.init(versionId);
        }
    }

    public void addVariants(List<ClassificationEntityOperations> listOfVariantsAndCorrespondenceTables) {
        AbstractPropertyContainer<ClassificationEntityOperations> container = VaadinUtil.getContainerDataSource(table);
        container.addEntriesAndPopulateProperties(listOfVariantsAndCorrespondenceTables);
    }

    private void disableButtons() {
        addCorrespondenceButton.setEnabled(false);
        addVariantButton.setEnabled(false);
    }

    public void enableButtons(boolean enableCorrespondance, boolean enableVariant) {
        addCorrespondenceButton.setEnabled(enableCorrespondance);
        addVariantButton.setEnabled(enableVariant);
    }

    public void reset() {
        table.getContainerDataSource().removeAllItems();
        disableButtons();
    }

    private static class VariantContainer extends AbstractPropertyContainer<ClassificationEntityOperations> {
        private final ClassificationFacade classificationFacade;
        private final UserContext userContext;
        private final VersionTable versionTable;

        VariantContainer(UserContext userContext, ClassificationFacade classificationFacade,
                VersionTable versionTable) {
            super(userContext);
            this.classificationFacade = classificationFacade;
            this.userContext = userContext;
            this.versionTable = versionTable;
        }

        @Override
        protected void defineProperties() {
            addProperty(NAME, String.class, null);
            addProperty(OPEN, Button.class, null);
            addProperty(DELETE, Button.class, null);
            addProperty(ENTITY, BaseEntity.class, null);
            hideProperty(ENTITY);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void populateProperties(Item item, ClassificationEntityOperations entity) {
            item.getItemProperty(NAME).setValue(entity.getNameInPrimaryLanguage());
            item.getItemProperty(OPEN).setValue(createViewButton(entity));
            item.getItemProperty(DELETE).setValue(createDeleteButton(entity));
            item.getItemProperty(ENTITY).setValue(entity);
        }

        private Button createDeleteButton(ClassificationEntityOperations entity) {
            Button deleteButton = createButton(entity, FontAwesome.TRASH_O, isVariant(entity) ? "Slett variant"
                    : "Slett korrespondansetabell");

            if (userContext.canUserAlterObject(entity)) {
                deleteButton.addClickListener(event -> deleteClassificationEntityWithConfirmation(
                        getClassificationEntity(event)));
            } else {
                deleteButton.setEnabled(false);
                deleteButton.setDescription("Du har ikke de nødvendige rettighetene til å slette denne "
                        + (isVariant(entity) ? "varianten" : "korrespondansetabellen"));
            }
            return deleteButton;
        }

        private Button createViewButton(ClassificationEntityOperations entity) {
            Button openButton = createButton(entity, FontAwesome.EDIT, isVariant(entity) ? "Åpne variant"
                    : "Åpne korrespondansetabell");
            openButton.addClickListener(event -> openVariant(event));
            return openButton;
        }

        private void openVariant(ClickEvent event) {
            ClassificationEntityOperations entity = (ClassificationEntityOperations) event.getButton().getData();
            if (isVariant(entity)) {
                ClassificationVariant variant = (ClassificationVariant) entity;
                VaadinUtil.navigateTo(EditVariantEditorView.NAME, ImmutableMap.of(EditVariantEditorView.PARAM_ID, String
                        .valueOf(variant.getId())));
            } else {
                CorrespondenceTable correspondenceTable = (CorrespondenceTable) entity;
                VaadinUtil.getKlassState().setListingSourceVersionOfCorrespondenceTable(isSourceVersionSelected(
                        correspondenceTable));
                VaadinUtil.navigateTo(EditCorrespondenceTableView.NAME, ImmutableMap.of(
                        EditCorrespondenceTableView.PARAM_ID, String.valueOf(correspondenceTable.getId())));
            }
        }

        private boolean isVariant(ClassificationEntityOperations entity) {
            if (entity instanceof ClassificationVariant) {
                return true;
            }
            return false;
        }

        @Override
        protected void deleteClassificationEntity(ClassificationEntityOperations entity) throws KlassMessageException {
            if (isVariant(entity)) {
                classificationFacade.deleteAndIndexVariant(userContext.getDetachedUserObject(),
                        (ClassificationVariant) entity);
            } else {
                classificationFacade.deleteAndIndexCorrespondenceTable(userContext
                        .getDetachedUserObject(), (CorrespondenceTable) entity);
            }
        }

        @Override
        protected ClassificationListViewSelection createSelectionForDeletedEntity(
                ClassificationEntityOperations deletedEntity) {
            // Finds selected classificationVersion for deleted variant or correspondenceTable
            if (isVariant(deletedEntity)) {
                return ClassificationListViewSelection.newClassificationListViewSelection(
                        ((ClassificationVariant) deletedEntity).getClassificationVersion());
            } else {
                CorrespondenceTable correspondenceTable = (CorrespondenceTable) deletedEntity;
                if (isSourceVersionSelected(correspondenceTable)) {
                    return ClassificationListViewSelection.newClassificationListViewSelection(correspondenceTable
                            .getSource());
                } else {
                    return ClassificationListViewSelection.newClassificationListViewSelection(correspondenceTable
                            .getTarget());
                }
            }
        }

        private boolean isSourceVersionSelected(CorrespondenceTable correspondenceTable) {
            return correspondenceTable.getSource().getId().equals(versionTable.getSelectedVersionId());
        }
    }
}
