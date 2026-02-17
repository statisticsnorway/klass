package no.ssb.klass.designer.classificationlist;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.ImmutableMap;
import com.vaadin.data.Item;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;

import no.ssb.klass.core.model.ClassificationEntityOperations;
import no.ssb.klass.core.model.ClassificationFamily;
import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.service.KlassMessageException;
import no.ssb.klass.designer.editing.classification.ClassificationEditorView;
import no.ssb.klass.designer.service.ClassificationFacade;
import no.ssb.klass.designer.user.UserContext;
import no.ssb.klass.designer.util.FavoriteUtils;
import no.ssb.klass.designer.util.KlassTheme;
import no.ssb.klass.designer.util.VaadinUtil;

@SuppressWarnings("serial")
@UIScope
@SpringComponent
public class ClassificationTable extends AbstractTable {

    private static final Logger log =
            LoggerFactory.getLogger(ClassificationTable.class);
    private static final String NEW_CLASSIFICATION_TOOLTIP = "Lag nytt kodeverk";

    @Autowired
    private ClassificationFacade classificationFacade;
    @Autowired
    private UserContext userContext;

    private TextField filterBox;
    private Table table;
    private ClassificationFamily classificationFamily;

    private ClassificationClickListener classificationClickListener;

    public void init(VersionTable versionTable, VariantTable variantTable) {
        this.classificationClickListener = new ClassificationClickListener(userContext, classificationFacade,
                versionTable, variantTable);
        table = createTable(new ClassificationContainer(userContext), classificationClickListener);
        table.setColumnExpandRatio(AbstractPropertyContainer.NAME, 1);
        filterBox = createFilterBox(table, "Filtrer kodeverk");
        Button addClassificationButton = createAddElementButton(NEW_CLASSIFICATION_TOOLTIP);
        addClassificationButton.addClickListener(e -> newClassification());

        rootLayout.addComponents(createHeader("Kodeverk", addClassificationButton), wrapFilter(filterBox), table);
        rootLayout.setExpandRatio(table, 1);
    }


    public void addClassifications(List<ClassificationSeries> classifications) {
        AbstractPropertyContainer<ClassificationSeries> container = VaadinUtil.getContainerDataSource(table);
        container.addEntriesAndPopulateProperties(classifications);
    }

    public void updateClassificationFamily(ClassificationFamily classificationFamily) {
        this.classificationFamily = classificationFamily;
    }

    public void reset() {
        table.getContainerDataSource().removeAllItems();
        filterBox.setValue("");
        VaadinUtil.getWrappedContainer(table).removeContainerFilters(AbstractPropertyContainer.NAME);
    }

    private void newClassification() {
        VaadinUtil.navigateTo(ClassificationEditorView.NAME, ImmutableMap.of(
                ClassificationEditorView.CLASSIFICATION_FAMILY_ID, classificationFamily.getId().toString()));
    }

    public String getSelectedClassificationName() {
        if (table.getValue() != null) {
            return table.getContainerProperty(table.getValue(), AbstractPropertyContainer.NAME).getValue().toString();
        } else {
            return null;
        }
    }

    public String getSelectedClassificationId() {
        if (table.getValue() != null) {
            return classificationClickListener.getSelectedClassificationSeries().getId().toString();
        } else {
            return null;
        }
    }

    public void selectItem(final ClassificationEntityOperations item) {
        selectItem(item, table);
    }

    @Override
    protected void onSelectItem(Item selectedItem) {
        classificationClickListener.itemClick(new ItemClickEvent(this, selectedItem, null, null, null));
    }

    private static class ClassificationClickListener implements ItemClickListener {
        private final VersionTable versionTable;
        private final VariantTable variantTable;
        private final UserContext userContext;
        private final ClassificationFacade classificationFacade;
        private ClassificationSeries selectedClassification;

        ClassificationClickListener(UserContext userContext, ClassificationFacade classificationFacade,
                VersionTable versionTable, VariantTable variantTable) {
            this.userContext = userContext;
            this.classificationFacade = classificationFacade;
            this.versionTable = versionTable;
            this.variantTable = variantTable;
        }

        @Override
        public void itemClick(ItemClickEvent event) {
            versionTable.reset();
            variantTable.reset();
            ClassificationSeries classification = (ClassificationSeries) event.getItem().getItemProperty(
                    AbstractPropertyContainer.ENTITY).getValue();
            classification = reloadToAvoidLazyInitializationException(classification);
            versionTable.addVersions(classification.getClassificationVersionsSortedByFromDateReversed());
            versionTable.enableButtons(userContext.canUserAlterObject(classification));
            this.selectedClassification = classification;
        }

        private ClassificationSeries reloadToAvoidLazyInitializationException(ClassificationSeries classification) {
            return classificationFacade.getRequiredClassificationSeries(classification.getId());
        }

        public ClassificationSeries getSelectedClassificationSeries() {
            return this.selectedClassification;
        }
    }

    private class ClassificationContainer extends AbstractPropertyContainer<ClassificationSeries> {
        static final String FAVORITE_COLUMN = "Favorite";

        ClassificationContainer(UserContext userContext) {
            super(userContext);
        }

        @Override
        protected void defineProperties() {
            addProperty(FAVORITE_COLUMN, Button.class, null);
            addProperty(NAME, String.class, null);
            addProperty(OPEN, Button.class, null);
            addProperty(DELETE, Button.class, null);
            addProperty(ENTITY, ClassificationSeries.class, null);
            hideProperty(ENTITY);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void populateProperties(Item item, ClassificationSeries classification) {
            item.getItemProperty(FAVORITE_COLUMN).setValue(createFavoriteButton(classification));
            item.getItemProperty(NAME).setValue(classification.getNameInPrimaryLanguage());
            item.getItemProperty(OPEN).setValue(createViewButton(classification));
            item.getItemProperty(DELETE).setValue(createDeleteButton(classification));
            item.getItemProperty(ENTITY).setValue(classification);
        }

        private Button createDeleteButton(ClassificationSeries classification) {
            Button deleteButton = createButton(classification, FontAwesome.TRASH_O, "Slett kodeverk");
            if (userContext.canUserAlterObject(classification)) {
                deleteButton.addClickListener(event -> deleteClassificationEntityWithConfirmation(
                        reloadToAvoidLazyInitializationException(getClassificationEntity(event))));
            } else {
                deleteButton.setEnabled(false);
                deleteButton.setDescription("Du har ikke de nødvendige rettighetene til å slette dette kodeverket");
            }
            return deleteButton;
        }

        private ClassificationSeries reloadToAvoidLazyInitializationException(ClassificationSeries classification) {
            return classificationFacade.getClassificationSeriesFullyInitialized(classification.getId());
        }

        private Button createViewButton(ClassificationSeries classification) {
            Button openButton = createButton(classification, FontAwesome.EDIT, "Åpne kodeverk");
            openButton.addClickListener(this::openClassification);
            return openButton;
        }

        private Button createFavoriteButton(ClassificationSeries classification) {

            log.debug("Creating favorite button for classification: {}", classification);
            if (userContext == null){
                log.info("User context is null");
                return null;
            }

            if (classification.getContactPerson() == null) {
                log.debug("No contact person for classification: {}", classification);
                return null;
            }

            if (classification.getId() == null) {
                log.debug("No id: {}", classification);
                return null;
            }
            boolean isFavorite = userContext.isFavorite(classification);
            Resource favoriteSymbol = FavoriteUtils.getFavoriteIcon(isFavorite);
            Button favoriteButton = createButton(classification, favoriteSymbol, createFavoriteTooltip(isFavorite));
            favoriteButton.addClickListener(this::favoriteClassification);
            favoriteButton.addStyleName(KlassTheme.FAVORITE_BUTTON);
            return favoriteButton;
        }

        private String createFavoriteTooltip(boolean isFavorite) {
            return isFavorite ? "Fjern kodeverk som favoritt" : "Velg kodeverk som favoritt";
        }

        private void favoriteClassification(ClickEvent event) {
            // Switch the symbol of the favorite button and store the favorite for the actual user
            ClassificationSeries classificationSeries = (ClassificationSeries) event.getButton().getData();
            boolean isFavorite = userContext.toggleFavorite(classificationSeries);
            event.getButton().setIcon(FavoriteUtils.getFavoriteIcon(isFavorite));
            event.getButton().setDescription(createFavoriteTooltip(isFavorite));
        }

        private void openClassification(ClickEvent event) {
            ClassificationSeries series = (ClassificationSeries) event.getButton().getData();
            VaadinUtil.navigateTo(ClassificationEditorView.NAME, ImmutableMap.of(ClassificationEditorView.PARAM_ID,
                    String.valueOf(series.getId())));
        }

        @Override
        protected void deleteClassificationEntity(ClassificationSeries classification) throws KlassMessageException {
            classificationFacade.deleteAndIndexClassification(userContext.getDetachedUserObject(), classification);
        }
    }
}
