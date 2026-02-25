package no.ssb.klass.designer.classificationlist;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.google.common.collect.ImmutableMap;
import com.vaadin.data.Item;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

import no.ssb.klass.forvaltning.converting.xml.FullVersionExportService;
import no.ssb.klass.core.model.ClassificationEntityOperations;
import no.ssb.klass.core.model.ClassificationVariant;
import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.model.CorrespondenceTable;
import no.ssb.klass.core.service.KlassMessageException;
import no.ssb.klass.designer.components.ClassificationListViewSelection;
import no.ssb.klass.designer.editing.version.VersionMainView;
import no.ssb.klass.designer.service.ClassificationFacade;
import no.ssb.klass.designer.user.UserContext;
import no.ssb.klass.core.util.AlphaNumericalComparator;
import no.ssb.klass.designer.util.VaadinUtil;
import no.ssb.klass.designer.windows.NewVersionWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
@UIScope
@SpringComponent
public class VersionTable extends AbstractTable {

    private static final Logger log = LoggerFactory.getLogger(VersionTable.class);
    private static final String COPY_VERSION_TOOLTIP = "Lag ny versjon av valgt kodeverk";
    private static final String EXPORT_VERSION_TOOLTIP = "Eksporter valgt kodeverk";

    private final StreamResource streamResource = new StreamResource(this::generateExportData, "");
    private final FileDownloader fileDownloader = new FileDownloader(streamResource);

    @Autowired
    private ApplicationContext applicationContext;

    private ClassificationFacade classificationFacade;

    private UserContext userContext;

    @Autowired
    private FullVersionExportService exportService;

    private Table table;
    private TextField filterBox;
    private Button addVersionButton;
    private Button exportVersionButton;

    private ClassificationVersionClickListener classificationVersionClickListener;

    @Autowired
    public void init(ClassificationTable classificationTable, VariantTable variantTable, UserContext userContext, ClassificationFacade classificationFacade) {
        this.userContext = userContext;
        this.classificationFacade = classificationFacade;
        classificationVersionClickListener = new ClassificationVersionClickListener(userContext,
                classificationFacade, variantTable);
        table = createTable(new VersionContainer(userContext, classificationFacade),
                classificationVersionClickListener, this::itemClick);
        table.setColumnExpandRatio(AbstractPropertyContainer.NAME, 1);
        addVersionButton = createAddElementButton(COPY_VERSION_TOOLTIP);
        addVersionButton.addClickListener(e -> copyVersion(classificationTable));
        addVersionButton.setEnabled(false);

        exportVersionButton = createExportButton(EXPORT_VERSION_TOOLTIP);
        exportVersionButton.setEnabled(false);
        fileDownloader.extend(exportVersionButton);

        filterBox = createFilterBox(table, "Filtrer versjoner");
        rootLayout.addComponents(createHeader("Versjoner", exportVersionButton, addVersionButton),
                wrapFilter(filterBox), table);
        rootLayout.setExpandRatio(table, 1);
    }

    private InputStream generateExportData() {
        Long versionId = getSelectedVersionId();
        ClassificationVersion version = classificationFacade.getRequiredClassificationVersion(versionId);
        streamResource.setFilename("komplett_" + exportService.createFileName(version));
        return exportService.toXmlStream(version);
    }

    private void itemClick(ItemClickEvent itemClickEvent) {
        boolean selected = table.isSelected(itemClickEvent.getItemId());
        // TODO find a better solution for whats explained below
        // "!" is used as a workaround for selection as it seems our onClick method is called before
        // the table selects the item, and will trigger before unselection as well
        exportVersionButton.setEnabled(!selected);
    }

    public void selectItem(final ClassificationEntityOperations item) {
        selectItem(item, table);
    }

    @Override
    protected void onSelectItem(Item selectedItem) {
        classificationVersionClickListener.itemClick(new ItemClickEvent(this, selectedItem, null, null, null));
        exportVersionButton.setEnabled(true);
    }

    public void addVersions(List<ClassificationVersion> versions) {
        AbstractPropertyContainer<ClassificationVersion> container = VaadinUtil.getContainerDataSource(table);
        container.addEntriesAndPopulateProperties(versions);
    }

    public void reset() {
        table.getContainerDataSource().removeAllItems();
        addVersionButton.setEnabled(false);
        exportVersionButton.setEnabled(false);
    }

    private void copyVersion(ClassificationTable classificationTable) {
        if (classificationTable.getSelectedClassificationId() != null) {

            NewVersionWindow copyVersionWindow = applicationContext.getBean(NewVersionWindow.class);
            copyVersionWindow.init(classificationTable.getSelectedClassificationId(),
                    classificationTable.getSelectedClassificationName());
            UI.getCurrent().addWindow(copyVersionWindow);
        }
    }

    public void enableButtons(boolean enabled) {
        addVersionButton.setEnabled(enabled);
    }

    private static class ClassificationVersionClickListener implements ItemClickListener {
        private final ClassificationFacade classificationFacade;
        private final UserContext userContext;
        private final VariantTable variantTable;

        private ClassificationVersion selectedClassificationVersion;

        ClassificationVersionClickListener(UserContext userContext, ClassificationFacade classificationFacade,
                VariantTable variantTable) {
            this.classificationFacade = classificationFacade;
            this.userContext = userContext;
            this.variantTable = variantTable;
        }

        @Override
        public void itemClick(ItemClickEvent event) {
            ClassificationVersion version = (ClassificationVersion) event.getItem().getItemProperty(
                    AbstractPropertyContainer.ENTITY).getValue();

            variantTable.reset();
            variantTable.addVariants(getListOfVariantsAndCorrespondenceTables(version));
            variantTable.enableButtons(userContext.canUserAlterObject(version), true);
            selectedClassificationVersion = version;
        }

        private List<ClassificationEntityOperations> getListOfVariantsAndCorrespondenceTables(
                ClassificationVersion version) {
            List<ClassificationEntityOperations> entities = new LinkedList<>();
            log.info("Classification facade version table {}", classificationFacade);
            List<CorrespondenceTable> sourceList = classificationFacade.findCorrespondenceTablesWithSource(version)
                    .stream()
                    .sorted(AlphaNumericalComparator.comparing(ClassificationEntityOperations::getNameInPrimaryLanguage,
                            true))
                    .collect(Collectors.toList());

            List<CorrespondenceTable> targetList = classificationFacade.findCorrespondenceTablesWithTarget(version)
                    .stream().sorted(AlphaNumericalComparator.comparing(
                            ClassificationEntityOperations::getNameInPrimaryLanguage, true))
                    .collect(Collectors.toList());

            List<ClassificationVariant> variantList = reloadToAvoidLazyInitializationException(version)
                    .getClassificationVariants();

            entities.addAll(sourceList);
            entities.addAll(targetList);
            entities.addAll(variantList);
            return entities;

        }

        private ClassificationVersion reloadToAvoidLazyInitializationException(ClassificationVersion version) {
            return classificationFacade.getRequiredClassificationVersion(version.getId());
        }

        public ClassificationVersion getSelectedClassificationVersion() {
            return this.selectedClassificationVersion;
        }
    }

    public Long getSelectedVersionId() {
        if (table.getValue() != null) {
            return classificationVersionClickListener.getSelectedClassificationVersion().getId();
        } else {
            return null;
        }
    }

    private static class VersionContainer extends AbstractPropertyContainer<ClassificationVersion> {

        private final ClassificationFacade classificationFacade;
        private final UserContext userContext;

        VersionContainer(UserContext userContext, ClassificationFacade classificationFacade) {
            super(userContext);
            this.classificationFacade = classificationFacade;
            this.userContext = userContext;
        }

        @Override
        protected void defineProperties() {
            addProperty(NAME, String.class, null);
            addProperty(OPEN, Button.class, null);
            addProperty(DELETE, Button.class, null);
            addProperty(ENTITY, ClassificationVersion.class, null);
            hideProperty(ENTITY);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void populateProperties(Item item, ClassificationVersion version) {
            item.getItemProperty(NAME).setValue(version.getNameInPrimaryLanguage());
            item.getItemProperty(OPEN).setValue(createViewButton(version));
            item.getItemProperty(DELETE).setValue(createDeleteButton(version));
            item.getItemProperty(ENTITY).setValue(version);
        }

        private Button createViewButton(ClassificationVersion version) {
            Button openButton = createButton(version, FontAwesome.EDIT, "Åpne versjon");
            openButton.addClickListener(this::openVersion);
            return openButton;
        }

        private Button createDeleteButton(ClassificationVersion version) {
            Button deleteButton = createButton(version, FontAwesome.TRASH_O, "Slett versjon");

            if (userContext.canUserAlterObject(version)) {
                deleteButton.addClickListener(event -> deleteClassificationEntityWithConfirmation(
                        reloadToAvoidLazyInitializationException(getClassificationEntity(event))));
            } else {
                deleteButton.setEnabled(false);
                deleteButton.setDescription("Du har ikke de nødvendige rettighetene til å slette denne versjonen");
            }
            return deleteButton;
        }

        private ClassificationVersion reloadToAvoidLazyInitializationException(ClassificationVersion version) {
            return classificationFacade.getRequiredClassificationVersion(version.getId());
        }

        private void openVersion(ClickEvent event) {
            ClassificationVersion version = (ClassificationVersion) event.getButton().getData();
            VaadinUtil.navigateTo(VersionMainView.NAME, ImmutableMap.of(VersionMainView.PARAM_VERSION_ID, String
                    .valueOf(version.getId())));
        }

        @Override
        protected void deleteClassificationEntity(ClassificationVersion version) throws KlassMessageException {
            classificationFacade.deleteAndIndexVersion(userContext.getDetachedUserObject(), version);
        }

        @Override
        protected ClassificationListViewSelection createSelectionForDeletedEntity(
                ClassificationVersion deletedVersion) {
            return ClassificationListViewSelection.newClassificationListViewSelection(deletedVersion
                    .getClassification());
        }
    }
}
