package no.ssb.klass.designer.editing.classification;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.annotation.PrototypeScope;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.vaadin.data.Property;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

import no.ssb.klass.core.model.ClassificationFamily;
import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.ClassificationType;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.model.StatisticalUnit;
import no.ssb.klass.core.model.User;
import no.ssb.klass.core.service.UserService;
import no.ssb.klass.designer.EditingView;
import no.ssb.klass.designer.components.BreadcumbPanel.Breadcrumb;
import no.ssb.klass.designer.editing.EditingState;
import no.ssb.klass.designer.editing.validators.UniqueTitleValidator;
import no.ssb.klass.designer.service.ClassificationFacade;
import no.ssb.klass.designer.user.UserContext;
import no.ssb.klass.designer.util.ComponentUtil;
import no.ssb.klass.designer.util.ParameterUtil;
import no.ssb.klass.designer.util.PassiveValidationUtil;
import no.ssb.klass.designer.util.VaadinUtil;

/**
 * @author Mads Lundemo, SSB.
 */
@PrototypeScope
@SpringView(name = ClassificationEditorView.NAME)
@SuppressWarnings("serial")
public class ClassificationEditorView extends ClassificationEditorComponentLayer implements EditingView {
    public static final String NAME = "classificationEditor";
    public static final String PARAM_ID = "classificationId";
    public static final String CLASSIFICATION_FAMILY_ID = "classificationFamilyId";

    private boolean ignoreChanges = false;

    @Autowired
    private ClassificationFacade classificationFacade;
    @Autowired
    private UserService userService;
    @Autowired
    private UserContext userContext;

    private boolean isCreatingNewClassification;
    private boolean isLanguageVisible;
    private boolean showThirdLanguage;

    private Long classificationId;
    private ClassificationFamily classificationFamily;
    private ClassificationSeries series;

    public ClassificationEditorView() {
        actionButtons.setConfirmText("Lagre");
        disableComboboxNullSelection();
        addEventHandlers();
    }

    private void disableComboboxNullSelection() {
        classificationTypeCombobox.setNullSelectionAllowed(false);
        classificationTypeCombobox.setTextInputAllowed(false);
        contactPersonCombobox.setNullSelectionAllowed(false);
        familiesCombobox.setNullSelectionAllowed(false);
        primaryLanguageCombobox.setNullSelectionAllowed(false);

        classificationTypeCombobox.setFilteringMode(FilteringMode.CONTAINS);
        contactPersonCombobox.setFilteringMode(FilteringMode.CONTAINS);
        familiesCombobox.setFilteringMode(FilteringMode.CONTAINS);
        primaryLanguageCombobox.setFilteringMode(FilteringMode.CONTAINS);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        restoreViewAsBeforeSave(VaadinUtil.getKlassState().getAndClearEditingState());
        showLanguageTab(isLanguageVisible);
        setLanguageDataForFields();
        determineEditorState(event);
        populateFieldsWithLegalValues();
        updateViewWithClassificationValues();
        addCustomValidators();
        checkUserAccess();
    }

    private void restoreViewAsBeforeSave(EditingState editingState) {
        isLanguageVisible = editingState.isLanguageTabVisible();
        showThirdLanguage = !editingState.isSecondNotThirdLanguageVisible();
    }

    @Override
    public List<Breadcrumb> getBreadcrumbs() {
        if (isCreatingNewClassification) {
            return Breadcrumb.newBreadcrumbs(classificationFamily);
        }
        return Breadcrumb.newBreadcrumbs(series);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean hasChanges() {
        if (ignoreChanges) {
            return false;
        }
        EqualsBuilder equalsBuilder = new EqualsBuilder();
        equalsBuilder.append(series.getClassificationType(), classificationTypeCombobox.getValue());
        supportedLanguages.forEach(language -> {
            equalsBuilder.append(series.getNameWithoutPrefix(language), getTitle(language));
            equalsBuilder.append(series.getDescription(language), getDescriptionTextArea(language).getValue());
        });
        if (series.getClassificationFamily() != null) {
            equalsBuilder.append(series.getClassificationFamily(), familiesCombobox.getValue());
        }
        if (series.getContactPerson() != null) {
            equalsBuilder.append(series.getContactPerson(), contactPersonCombobox.getValue());
        }
        if (series.getPrimaryLanguage() != null) {
            equalsBuilder.append(series.getPrimaryLanguage(), primaryLanguageCombobox.getValue());
        }
        return !equalsBuilder
                .append(series.getStatisticalUnits()
                        .containsAll((Collection<StatisticalUnit>) statisticalUnitSelector.getValue()), true)
                .append(Boolean.valueOf(series.isIncludeShortName()), includeShortnameCheckbox.getValue())
                .append(Boolean.valueOf(series.isIncludeNotes()), includeNotesCheckbox.getValue())
                .append(Boolean.valueOf(series.isCopyrighted()), copyrightCheckbox.getValue())
                .isEquals();

    }

    @Override
    public void ignoreChanges() {
        ignoreChanges = true;
    }

    private void checkUserAccess() {
        if (series != null && series.getContactPerson() != null) {
            if (!userContext.canUserAlterObject(series)) {
                actionButtons.disableConfirmButton(true, "Du har ikke lov til å endre denne klassifikasjonen");
                ComponentUtil.setReadOnlyRecursively(this, true);
            } else {
                actionButtons.disableConfirmButton(false, "");
            }
        }
    }

    private void setLanguageDataForFields() {
        setLanguageForPrimaryFields(Language.NB);
        setLanguageForSecondaryFields(Language.NN);
        setLanguageForThirdFields(Language.EN);
    }

    private void showLanguageTab(boolean visible) {
        isLanguageVisible = visible;
        languageLayout.setVisible(isLanguageVisible);

        if (!isLanguageVisible) {
            secondaryLanguageLayout.setVisible(false);
            thirdLanguageLayout.setVisible(false);
        } else {
            showLanguage(showThirdLanguage);
        }
    }

    private void showLanguage(boolean thirdLanguage) {
        showThirdLanguage = thirdLanguage;
        secondaryLanguageLayout.setVisible(!showThirdLanguage);
        secondaryLanguageButton.setStyleName(!showThirdLanguage ? "v-label-bold" : "");

        thirdLanguageLayout.setVisible(showThirdLanguage);
        thirdLanguageButton.setStyleName(showThirdLanguage ? "v-label-bold" : "");
    }

    private void determineEditorState(ViewChangeListener.ViewChangeEvent event) {
        String parameter = event.getParameters();
        isCreatingNewClassification = !ParameterUtil.hasParameter(PARAM_ID, parameter);
        if (!isCreatingNewClassification) {
            classificationId = ParameterUtil.getRequiredLongParameter(PARAM_ID, event.getParameters());
        } else {
            classificationFamily = classificationFacade.getRequiredClassificationFamily(ParameterUtil
                    .getRequiredLongParameter(CLASSIFICATION_FAMILY_ID, event.getParameters()));
        }
    }

    private void addCustomValidators() {

        for (Language language : supportedLanguages) {
            TextField titleTextField = getTitleTextField(language);
            titleTextField.setInvalidAllowed(true);
            titleTextField.setValidationVisible(false);
            titleTextField.addValidator(new UniqueTitleValidator(classificationFacade, language,
                    getTitlePrefixTextField(language), isCreatingNewClassification ? "" : series.getName(language)));
        }
    }

    private void populateFieldsWithLegalValues() {
        populateTypeDropDown();
        populatefamilies();
        populateContactPerson();
        populateStatisticalUnits();
        populatePrimaryLanguageDropDown();
    }

    private void updateViewWithClassificationValues() {
        if (isCreatingNewClassification) {
            creatingNewClassification();
        } else {
            editingExistingClassification(classificationId);
        }
    }

    private void creatingNewClassification() {
        series = new ClassificationSeries();
        headerLabel.setValue("");
        contactPersonCombobox.setValue(userContext.getDetachedUserObject());
        primaryLanguageCombobox.setValue(Language.getDefault());
        familiesCombobox.setValue(classificationFamily);
    }

    private void editingExistingClassification(Long classificationId) {
        series = classificationFacade.getRequiredClassificationSeries(classificationId);

        updateFieldsForPrimaryLanguage(series.getPrimaryLanguage());
        String name = series.getNameInPrimaryLanguage();
        String type = series.getClassificationType().getDisplayName(Language.getDefault());
        String header = type + " - " + name;
        headerLabel.setValue(header);
        setClassificationValues();
    }

    private void updateFieldsForPrimaryLanguage(Language primaryLanguage) {
        Language[] languageOrder = Language.getPrioritizedOrder(primaryLanguage);
        setLanguageForPrimaryFields(languageOrder[0]);
        setLanguageForSecondaryFields(languageOrder[1]);
        setLanguageForThirdFields(languageOrder[2]);
        // update validation
        updateLanguageFieldRequirements(primaryLanguage);
        classificationTypeChanged(); // trigger title prefix update

    }

    private void setLanguageForPrimaryFields(Language language) {
        primaryLabel.setValue(language.getDisplayName());
        primaryTitleTextfield.setData(language);
        primaryTitlePrefixTextfield.setData(language);
        primaryDescriptionTextArea.setData(language);
    }

    private void setLanguageForSecondaryFields(Language language) {
        secondaryLanguageButton.setCaption(language.getDisplayName());
        secondaryTitleTextfield.setData(language);
        secondaryTitlePrefixTextfield.setData(language);
        secondaryDescriptionTextArea.setData(language);
    }

    private void setLanguageForThirdFields(Language language) {
        thirdLanguageButton.setCaption(language.getDisplayName());
        thirdTitleTextfield.setData(language);
        thirdTitlePrefixTextfield.setData(language);
        thirdDescriptionTextArea.setData(language);
    }

    private void setClassificationValues() {
        ClassificationType classificationType = series.getClassificationType();
        classificationTypeCombobox.setValue(classificationType);

        supportedLanguages.forEach(language -> {
            getTitleTextField(language).setValue(series.getNameWithoutPrefix(language));
            getDescriptionTextArea(language).setValue(series.getDescription(language));
        });

        familiesCombobox.setValue(series.getClassificationFamily());
        contactPersonCombobox.setValue(series.getContactPerson());
        contactPersonCombobox.select(series.getContactPerson()); // will trigger section update
        statisticalUnitSelector.setValue(new HashSet<>(series.getStatisticalUnits()));
        primaryLanguageCombobox.setValue(series.getPrimaryLanguage());
        includeShortnameCheckbox.setValue(series.isIncludeShortName());
        includeNotesCheckbox.setValue(series.isIncludeNotes());
        copyrightCheckbox.setValue(series.isCopyrighted());
        includeValidCheckbox.setValue(series.isIncludeValidity());
    }

    private void addEventHandlers() {
        classificationTypeCombobox.addValueChangeListener(event -> classificationTypeChanged());
        contactPersonCombobox.addValueChangeListener(event -> contactPersonValueChanged(event));
        actionButtons.addConfirmClickListener(event -> validateAndSave());
        actionButtons.addCancelClickListener(event -> abort());
        languageToggleButton.addClickListener(event -> showLanguageTab(!isLanguageVisible));
        secondaryLanguageButton.addClickListener(event -> showLanguage(false));
        thirdLanguageButton.addClickListener(event -> showLanguage(true));
        primaryLanguageCombobox.addValueChangeListener(evnet -> primaryLanguageChanged());

        for (AbstractField<?> field : fields) {
            field.addValueChangeListener(event -> PassiveValidationUtil.revalidateField(field));
        }

    }

    private void primaryLanguageChanged() {
        Language primaryLanguage = (Language) primaryLanguageCombobox.getValue();
        if (primaryLanguage == null) {
            return;
        }
        updateFieldsForPrimaryLanguage(primaryLanguage);

    }

    private void classificationTypeChanged() {
        ClassificationType type = (ClassificationType) classificationTypeCombobox.getValue();
        if (type != null) {
            titlePrefixFields.forEach(field -> updateReadOnlyTextField(field, ClassificationSeries.getNamePrefix(
                    (Language) field.getData(), type)));
        } else {
            titlePrefixFields.forEach(field -> updateReadOnlyTextField(field, ""));
        }
        titleFields.forEach(PassiveValidationUtil::revalidateField);
        if (isCreatingNewClassification) {
            // if new classification; reset error after classificationTypeChanged revalidation
            getTitleTextField((Language) primaryLanguageCombobox.getValue()).setComponentError(null);
        }

    }

    private void updateLanguageFieldRequirements(Language primaryLanguage) {

        for (TextField field : titleFields) {
            field.setRequired(false);
        }

        for (TextArea area : descriptionFields) {
            area.setRequired(false);
        }
        getTitleTextField(primaryLanguage).setRequired(true);
        getDescriptionTextArea(primaryLanguage).setRequired(true);

    }

    private void validateAndSave() {
        if (validate()) {
            save();
        }
    }

    @SuppressWarnings("unchecked")
    private void save() {
        ClassificationType classificationType = (ClassificationType) classificationTypeCombobox.getValue();

        series.setClassificationType(classificationType);

        supportedLanguages.forEach(language -> {
            series.setNameAndAddPrefix(getTitle(language), language);
            series.setDescription(language, getDescriptionTextArea(language).getValue());
        });

        series.setClassificationFamily((ClassificationFamily) familiesCombobox.getValue());
        series.setContactPerson((User) contactPersonCombobox.getValue());
        series.getStatisticalUnits().clear();
        series.getStatisticalUnits().addAll((Collection<StatisticalUnit>) statisticalUnitSelector.getValue());
        series.setPrimaryLanguage((Language) primaryLanguageCombobox.getValue());
        series.setIncludeShortName(includeShortnameCheckbox.getValue());
        series.setIncludeNotes(includeNotesCheckbox.getValue());
        series.setIncludeValidity(includeValidCheckbox.getValue());
        series.setCopyrighted(copyrightCheckbox.getValue());

        series = classificationFacade.saveAndIndexClassification(series);

        VaadinUtil.showSavedMessage();
        VaadinUtil.getKlassState().setEditingState(EditingState.newMetadataEditingState(isLanguageVisible,
                !showThirdLanguage));
        VaadinUtil.navigateTo(ClassificationEditorView.NAME, ImmutableMap.of(ClassificationEditorView.PARAM_ID, String
                .valueOf(series.getId())));
    }

    private boolean validate() {
        Map<AbstractField<?>, String> fieldMap = new HashMap<>();
        for (AbstractField<?> field : fields) {
            String fieldName = field.getCaption();
            // workaround for title fields as they dont have captions
            if (fieldName == null) {
                if (field == getTitleTextField(Language.NB)
                        || field == getTitleTextField(Language.NN)
                        || field == getTitleTextField(Language.EN)) {
                    fieldName = "kodeverkstittel";
                }
            }
            fieldMap.put(field, fieldName);
        }
        return PassiveValidationUtil.validateAndShowWarnings(fieldMap, "Kan ikke lagre kodeverk\n");
    }

    private void abort() {
        VaadinUtil.navigateTo(Iterables.getLast(getBreadcrumbs()));
    }

    private void contactPersonValueChanged(Property.ValueChangeEvent event) {
        User person = (User) event.getProperty().getValue();
        if (person != null) {
            updateReadOnlyTextField(sectionTextfield, person.getSection());
        } else {
            updateReadOnlyTextField(sectionTextfield, "");
        }
    }

    private void populateTypeDropDown() {
        classificationTypeCombobox.setItemCaptionMode(AbstractSelect.ItemCaptionMode.EXPLICIT);
        for (ClassificationType type : ClassificationType.values()) {
            classificationTypeCombobox.addItem(type);
            classificationTypeCombobox.setItemCaption(type, type.getDisplayName(Language.getDefault()));

        }
    }

    private void populatePrimaryLanguageDropDown() {
        primaryLanguageCombobox.setItemCaptionMode(AbstractSelect.ItemCaptionMode.EXPLICIT);
        for (Language type : Language.values()) {
            primaryLanguageCombobox.addItem(type);
            primaryLanguageCombobox.setItemCaption(type, type.getDisplayName());
        }
    }

    private void populateContactPerson() {
        List<User> userList = userService.getAllUsers();
        contactPersonCombobox.clear();
        for (User user : userList) {
            contactPersonCombobox.addItem(user);
            contactPersonCombobox.setItemCaption(user, user.getFullname());
        }
    }

    private void populatefamilies() {
        List<ClassificationFamily> familyList = classificationFacade.findAllClassificationFamilies();
        familiesCombobox.clear();
        for (ClassificationFamily family : familyList) {
            familiesCombobox.addItem(family);
            familiesCombobox.setItemCaption(family, family.getName());
        }

    }

    private void populateStatisticalUnits() {
        List<StatisticalUnit> unitList = classificationFacade.findAllStatisticalUnits().stream()
                .sorted(Comparator.comparing(o -> o.getName(Language.NB))).collect(Collectors.toList());

        statisticalUnitSelector.setLeftColumnCaption("Tilgjengelige enheter");
        statisticalUnitSelector.setRightColumnCaption("Valgte enheter");
        statisticalUnitSelector.setItemCaptionMode(AbstractSelect.ItemCaptionMode.EXPLICIT);

        statisticalUnitSelector.clear();
        for (StatisticalUnit unit : unitList) {
            statisticalUnitSelector.addItem(unit);
            statisticalUnitSelector.setItemCaption(unit, unit.getName(Language.NB));
        }
    }

    private String getTitle(Language language) {
        return StringUtils.trim(getTitleTextField(language).getValue());
    }

}
