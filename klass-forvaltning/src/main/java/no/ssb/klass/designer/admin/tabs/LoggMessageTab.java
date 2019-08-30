package no.ssb.klass.designer.admin.tabs;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.renderers.ButtonRenderer;

import no.ssb.klass.core.model.Changelog;
import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.model.User;
import no.ssb.klass.core.util.TimeUtil;
import no.ssb.klass.designer.service.ClassificationFacade;
import no.ssb.klass.designer.service.InformSubscribers;
import no.ssb.klass.designer.user.UserContext;
import no.ssb.klass.designer.util.ConfirmationDialog;
import no.ssb.klass.designer.util.PassiveValidationUtil;
import no.ssb.klass.designer.util.VaadinUtil;
/**
 * Created by jro on 23.03.2017.
 */
public class LoggMessageTab extends LoggMessageTabDesign {
    private static final Logger log = LoggerFactory.getLogger(LoggMessageTab.class);
    private ClassificationVersion version;
    //@formatter:off
    private List<ComboBox> fieldsToValidate = Arrays.asList(
            classificationComboBox,
            versionComboBox
    );
    private String selectedClassificationName;
    private String selectedVersionName;
    //@formatter:on
    @Autowired
    private UserContext userContext;
    @Autowired
    private ClassificationFacade classificationFacade;

    public void init() {
       for (ComboBox field : fieldsToValidate) {
            field.removeAllItems();
            field.setItemCaptionMode(AbstractSelect.ItemCaptionMode.EXPLICIT);
            field.setNullSelectionAllowed(false);
            field.setFilteringMode(FilteringMode.CONTAINS);
            field.setRequired(true);
            field.addValueChangeListener(event -> PassiveValidationUtil.revalidateField(field));
        }
        newButton.addClickListener(this::newButtonClick);
        cancelButton.addClickListener(this::cancelButtonClick);
        saveButton.addClickListener(this::saveButtonClick);
        loggMessageGrid.setVisible(false);
        newButton.setVisible(false);
        classificationComboBox.addValueChangeListener(this::populateVersions);
        populateClassifications();
        versionComboBox.addValueChangeListener(this::populateToLevels);
        hlayout.setVisible(false);
        buttonhlayout.setVisible(false);

    }


    private void populateVersions(Property.ValueChangeEvent event) {
        loggMessageGrid.setVisible(false);
        ClassificationSeries series = (ClassificationSeries) event.getProperty().getValue();
        if (series != null) {
            selectedClassificationName = series.getNameInPrimaryLanguage();
            versionComboBox.removeAllItems();
            series = reloadToAvoidLazyInitializationException(series);
            for (ClassificationVersion version : series.getClassificationVersions()) {
                versionComboBox.addItem(version);
                versionComboBox.setItemCaption(version, version.getNameInPrimaryLanguage());
            }
        }
    }

    private void populateToLevels(Property.ValueChangeEvent event) {
         loggMessageGrid.setVisible(false);
        version = (ClassificationVersion) event.getProperty().getValue();
        if (version != null) {
            selectedVersionName = version.getNameInPrimaryLanguage();
            initLoggMessageContainer();
        }
    }
    private void initLoggMessageContainer() {
        Container container = createLoggMessageContainer();
        // Bind it to a component
        loggMessageGrid.setVisible(true);
        loggMessageGrid.setCaption("Loggmeldinger for klassifikasjon: " + selectedClassificationName +  "\t" + " Versjon:  " + selectedVersionName);
        loggMessageGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        loggMessageGrid.setContainerDataSource((Container.Indexed) container);
        loggMessageGrid.getColumn("Slett").setRenderer(new ButtonRenderer(e ->
                slett(loggMessageGrid.getContainerDataSource().getContainerProperty(e.getItemId(), "data"),
                      loggMessageGrid.getContainerDataSource().getContainerProperty(e.getItemId(), "Slett"))));
        loggMessageGrid.getColumn("Dato").setExpandRatio(3);
        loggMessageGrid.getColumn("Kommentar").setExpandRatio(6);
        loggMessageGrid.getColumn("data").setHidden(true);
        loggMessageGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        loggMessageGrid.setCellStyleGenerator(new Grid.CellStyleGenerator() {
            @Override
            public String getStyle(Grid.CellReference cellReference) {
                if ("c1".equals(cellReference.getPropertyId())) {
                    return "green";
                } else if ("c2".equals(cellReference.getPropertyId())) {
                    return "right-and-left-border";
                } else {
                    return null;
                }
            }
        });
        updateTableContent();
    }
    private Container createLoggMessageContainer() {

        IndexedContainer container = new IndexedContainer();
        // Initialize the container as required by the container type
        container.addContainerProperty("Slett", String.class, "Slett");
        container.addContainerProperty("Dato", Date.class, null);
        container.addContainerProperty("Kommentar", String.class, null);
        container.addContainerProperty("data", Changelog.class, null);
        return container;
    }

    private ClassificationSeries reloadToAvoidLazyInitializationException(ClassificationSeries classification) {
        return classificationFacade.getRequiredClassificationSeries(classification.getId());
    }

    private ClassificationVersion reloadToAvoidLazyInitializationException(ClassificationVersion version) {
        return classificationFacade.getRequiredClassificationVersion(version.getId());
    }


    private void populateClassifications() {
        classificationComboBox.removeAllItems();
        List<ClassificationSeries> seriesList = classificationFacade.findAllClassificationSeries();

        for (ClassificationSeries series : seriesList) {
            classificationComboBox.addItem(series);
            classificationComboBox.setItemCaption(series, series.getNameInPrimaryLanguage());
        }
    }

    private void updateTableContent() {
        List<Changelog> loggMeldinger = classificationFacade.getVersionChangelogs(version.getId());
        if (!loggMeldinger.isEmpty()) {
            loggMessageGrid.getContainerDataSource().removeAllItems();
            String slett = "Slett";
            for (Changelog c : loggMeldinger) {
                loggMessageGrid.addRow(
                        slett,
                        c.getChangeOccured(),
                        c.getDescription(),
                        c);
            }
            newButton.setVisible(true);
        } else {
            loggMessageGrid.setVisible(false);
            newButton.setVisible(true);
            Notification.show("Har ingen loggmeldinger", Notification.Type.WARNING_MESSAGE);
            hlayout.setVisible(false);
            buttonhlayout.setVisible(false);

        }
    }

    private void slett(Property id, Property slett) {
        Changelog changelog = (Changelog) id.getValue();
        StringBuilder confirmationText = new StringBuilder("Er du sikker på at du ønsker å slette? ");
        ConfirmationDialog confWindow = new ConfirmationDialog("Slett kommentar", "Ja", "Nei", (
                    answerYes) -> {
                if (answerYes) {
                    version = reloadToAvoidLazyInitializationException(version);
                    version.deleteChangelog(changelog);
                    classificationFacade.saveAndIndexVersion(version, InformSubscribers.createWhenWasUnpublished(version));
                    classificationFacade.deleteChangelog(changelog);
                    VaadinUtil.showSavedMessage();
                    updateTableContent();
                }
            }, confirmationText.toString());
            UI.getCurrent().addWindow(confWindow);
    }
    private void cancelButtonClick(Button.ClickEvent clickEvent) {
        loggMessageGrid.setVisible(true);
        hlayout.setVisible(false);
        buttonhlayout.setVisible(false);
        newButton.setVisible(true);
    }
    private void newButtonClick(Button.ClickEvent clickEvent) {
        loggMessageGrid.setVisible(false);
        newButton.setVisible(false);
        hlayout.setVisible(true);
        hlayout.setCaption("Nye kommentarer for klassifikasjon: " + selectedClassificationName + "\t" + " Versjon:  " + selectedVersionName);
        buttonhlayout.setVisible(true);
        dato.setValue(TimeUtil.now());
        dato.setEnabled(false);
        kommentarer.setEnabled(true);
        kommentarer.setValue("Ny kommentar");
    }
    private void saveButtonClick(Button.ClickEvent clickEvent) {
        User user = userContext.getDetachedUserObject();
        Changelog changelog = new Changelog(user.getUsername(), kommentarer.getValue());
        StringBuilder confirmationText = new StringBuilder("Er du sikker på at du ønsker å lagre? ");
        ConfirmationDialog confWindow = new ConfirmationDialog("Lagre kommentar", "Ja", "Nei", (
                answerYes) -> {
            if (answerYes) {
                version = reloadToAvoidLazyInitializationException(version);
                version.addChangelog(changelog);
                classificationFacade.saveAndIndexVersion(version, InformSubscribers.createWhenWasUnpublished(version));
                VaadinUtil.showSavedMessage();
                hlayout.setVisible(false);
                buttonhlayout.setVisible(false);
                loggMessageGrid.setVisible(true);
                updateTableContent();
            }
        }, confirmationText.toString());
        UI.getCurrent().addWindow(confWindow);
    }
}


