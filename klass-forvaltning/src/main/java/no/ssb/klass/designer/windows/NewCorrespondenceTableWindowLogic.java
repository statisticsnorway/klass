package no.ssb.klass.designer.windows;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.annotation.PrototypeScope;

import com.google.common.collect.ImmutableMap;
import com.vaadin.data.Property;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Window;

import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.model.Level;
import no.ssb.klass.designer.editing.correspondencetable.CreateCorrespondenceTableView;
import no.ssb.klass.designer.service.ClassificationFacade;
import no.ssb.klass.designer.util.PassiveValidationUtil;
import no.ssb.klass.designer.util.VaadinUtil;

/**
 * @author Mads Lundemo, SSB.
 */

@SpringComponent
@PrototypeScope
@SuppressWarnings("serial")
class NewCorrespondenceTableWindowLogic extends NewCorrespondenceTableWindowDesign {

    private static final String ALL_LEVELS_ITEMID = "0";
    private Window parent;
    private ClassificationVersion version;
    //@formatter:off
    private List<ComboBox> fieldsToValidate = Arrays.asList(
            toLevelCombobox,
            fromLevelCombobox,
            toVersionCombobox,
            toClassificationCombobox
    );
    //@formatter:on

    @Autowired
    private ClassificationFacade classificationFacade;

    NewCorrespondenceTableWindowLogic(Window parent) {
        this.parent = parent;

        for (ComboBox field : fieldsToValidate) {
            field.removeAllItems();
            field.setItemCaptionMode(AbstractSelect.ItemCaptionMode.EXPLICIT);
            field.setNullSelectionAllowed(false);
            field.setFilteringMode(FilteringMode.CONTAINS);
            field.setRequired(true);
            field.addValueChangeListener(event -> PassiveValidationUtil.revalidateField(field));
        }
        actionButtons.setConfirmText("Opprett");
        actionButtons.setConfirmAsPrimaryButton();
        actionButtons.addConfirmClickListener(this::createTable);
        actionButtons.addCancelClickListener(event -> parent.close());

        toVersionCombobox.addValueChangeListener(this::populateToLevels);
        toClassificationCombobox.addValueChangeListener(this::populateVersions);
    }

    public void init(Long versionId) {
        version = classificationFacade.getRequiredClassificationVersion(versionId);
        headerLabel.setValue("Opprett ny korrespondansetabell fra " + version.getNameInPrimaryLanguage());
        fromCorrespondenceLabel.setValue(version.getNameInPrimaryLanguage());
        populateFromLevel();
        populateClassifications();
    }

    private void createTable(Button.ClickEvent event) {
        if (!PassiveValidationUtil.validate(fieldsToValidate)) {
            return;
        }
        if (isCorresponenceTableAlreadyPresent()) {
            Notification.show("Ugyldig kombinasjon",
                    "Det eksisterer allerede korrespondansetabell mellom disse versjonene (og nivåene)",
                    Notification.Type.WARNING_MESSAGE);
            return;
        }
        if (!checkForLegalOverlap()) {
            Notification.show("Ugyldig kombinasjon", "Versjonene må overlappe i tid",
                    Notification.Type.WARNING_MESSAGE);
            return;
        }
        VaadinUtil.navigateTo(CreateCorrespondenceTableView.NAME,
                ImmutableMap.of(
                        CreateCorrespondenceTableView.PARAM_FROM_VERSION, String.valueOf(version.getId()),
                        CreateCorrespondenceTableView.PARAM_FROM_LEVEL, getFromLevel(),
                        CreateCorrespondenceTableView.PARAM_TO_VERSION, getToVersion(),
                        CreateCorrespondenceTableView.PARAM_TO_LEVEL, getToLevel()));

        parent.close();
    }

    private boolean isCorresponenceTableAlreadyPresent() {
        ClassificationVersion sourceVersion = version;
        Level sourceLevel = asLevel(sourceVersion, getFromLevel());
        ClassificationVersion targetVersion = reloadToAvoidLazyInitializationException(
                (ClassificationVersion) toVersionCombobox.getValue());
        Level targetLevel = asLevel(targetVersion, getToLevel());

        return !classificationFacade.findCorrespondenceTablesBetween(sourceVersion, sourceLevel, targetVersion,
                targetLevel).isEmpty();
    }

    private Level asLevel(ClassificationVersion version, String levelAsString) {
        int levelNumber = Integer.parseInt(levelAsString);
        if (levelNumber == 0) {
            return null;
        }
        return version.getLevel(levelNumber);
    }

    private boolean checkForLegalOverlap() {
        ClassificationVersion toVersion = (ClassificationVersion) toVersionCombobox.getValue();
        if (toVersion != null) {
            if (Objects.equals(toVersion.getOwnerClassification(), version.getOwnerClassification())) {
                return true;
            }
            if (version.getDateRange().overlaps(toVersion.getDateRange())) {
                return true;
            }
        }
        return false;
    }

    private void populateToLevels(Property.ValueChangeEvent event) {
        ClassificationVersion version = (ClassificationVersion) event.getProperty().getValue();
        if (version != null) {
            version = reloadToAvoidLazyInitializationException(version);
            populateLevelComboBox(toLevelCombobox, version);
        }
    }

    private void populateFromLevel() {
        populateLevelComboBox(fromLevelCombobox, version);
    }

    private void populateLevelComboBox(ComboBox levelComboBox, ClassificationVersion version) {
        levelComboBox.removeAllItems();
        addAllLevels(levelComboBox);
        for (Level level : version.getLevels()) {
            String levelNumberAsString = String.valueOf(level.getLevelNumber());
            levelComboBox.addItem(levelNumberAsString);
            levelComboBox.setItemCaption(levelNumberAsString, level.getName(version.getPrimaryLanguage()));
        }
        levelComboBox.select(ALL_LEVELS_ITEMID);
    }

    private void populateVersions(Property.ValueChangeEvent event) {
        ClassificationSeries series = (ClassificationSeries) event.getProperty().getValue();
        if (series != null) {
            toVersionCombobox.removeAllItems();
            series = reloadToAvoidLazyInitializationException(series);
            for (ClassificationVersion version : series.getClassificationVersions()) {
                if (this.version.equals(version)) {
                    // preventing that target and source is the same version
                    continue;
                }
                toVersionCombobox.addItem(version);
                toVersionCombobox.setItemCaption(version, version.getNameInPrimaryLanguage());
            }
        }
    }

    private ClassificationSeries reloadToAvoidLazyInitializationException(ClassificationSeries classification) {
        return classificationFacade.getRequiredClassificationSeries(classification.getId());
    }

    private ClassificationVersion reloadToAvoidLazyInitializationException(ClassificationVersion version) {
        return classificationFacade.getRequiredClassificationVersion(version.getId());
    }

    private void addAllLevels(ComboBox combobox) {
        combobox.addItem(ALL_LEVELS_ITEMID);
        combobox.setItemCaption(ALL_LEVELS_ITEMID, "Alle nivåer");
    }

    private void populateClassifications() {
        toClassificationCombobox.removeAllItems();
        List<ClassificationSeries> seriesList = classificationFacade.findAllClassificationSeries();

        for (ClassificationSeries series : seriesList) {
            toClassificationCombobox.addItem(series);
            toClassificationCombobox.setItemCaption(series, series.getNameInPrimaryLanguage());
        }
    }

    public String getFromLevel() {
        return (String) fromLevelCombobox.getValue();
    }

    public String getToLevel() {
        return (String) toLevelCombobox.getValue();
    }

    public String getToVersion() {
        return String.valueOf(((ClassificationVersion) toVersionCombobox.getValue()).getId());
    }
}
