package no.ssb.klass.designer.components.metadata;

import static no.ssb.klass.core.util.TimeUtil.*;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.annotation.PrototypeScope;

import com.google.common.base.Strings;
import com.vaadin.data.Property;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Notification;

import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.model.Publishable;
import no.ssb.klass.core.model.User;
import no.ssb.klass.core.service.UserService;
import no.ssb.klass.core.util.DateRange;
import no.ssb.klass.core.util.DraftUtil;
import no.ssb.klass.core.util.TimeUtil;
import no.ssb.klass.designer.editing.EditingState;
import no.ssb.klass.designer.editing.HasEditingState;
import no.ssb.klass.designer.editing.components.PublicationChoiceEditor;
import no.ssb.klass.designer.user.UserContext;
import no.ssb.klass.designer.util.ComponentUtil;
import no.ssb.klass.designer.util.PassiveValidationUtil;
import no.ssb.klass.designer.util.VaadinUtil;

/**
 * @author Mads Lundemo, SSB.
 */
@SuppressWarnings("serial")
@SpringComponent
@PrototypeScope
public class MetadataEditorComponent extends CustomComponent implements HasEditingState {
    private static final String DATE_INPUT_MASK = "MM.yyyy";

    private MetadataEditorComponentDesign design = new MetadataEditorComponentDesign();
    private List<AbstractField<?>> fieldsToValidate = Arrays.asList(design.primaryNameTextfield,
            design.primaryDescriptionTextArea, design.contactPersonCombobox);

    @Autowired
    private UserService userService;

    @Autowired
    private UserContext userContext;

    private Language primaryLanguage;
    private boolean visible = false;
    private boolean secondaryLanguage = false;
    private PublicationChoiceEditor publicationChoiceEditor;

    public MetadataEditorComponent() {
        setCompositionRoot(design);
        design.draftCheckBox.setVisible(false);
        design.secondLanguageLayout.setVisible(false);
        design.thirdLanguageLayout.setVisible(false);
        design.languageSelect.addEditLanguagesListener(this::showOtherLanguages);
        design.languageSelect.addLanguageChangeListener(this::updateLanguagePanels);
        design.fromDate.setDateFormat(DATE_INPUT_MASK);
        design.toDate.setDateFormat(DATE_INPUT_MASK);
        setDatesVisible(false);
        for (AbstractField<?> field : fieldsToValidate) {
            field.setValidationVisible(false);
            // field.setImmediate(false);
            field.setRequired(true);
            field.addValueChangeListener(event -> PassiveValidationUtil.revalidateField(field));
        }

        design.primaryNameTextfield.setRequiredError("Navn for primærspråk er påkrevd");
        design.primaryDescriptionTextArea.setRequiredError("Beskrivelse for primærspråk er påkrevd");
        design.contactPersonCombobox.setRequiredError("Kontaktperson er påkrevd");

        configureContactPersonCombobox();
    }

    public void setDatesVisible(boolean visible) {
        design.dateLayout.setVisible(visible);
        design.dateFillerLayout.setVisible(visible);
    }

    public void setNameVisible(boolean visible) {
        design.primaryNameTextfield.setRequired(visible);
        design.primaryNameTextfield.setVisible(visible);
        design.secondaryNameTextfield.setVisible(visible);
        design.thirdNameTextfield.setVisible(visible);
    }

    public void setDraftVisible(boolean visible) {
        design.draftCheckBox.setVisible(visible);
        design.draftFillerLayout.setVisible(visible);
    }

    public void setDescriptionRequired(boolean required) {
        design.primaryDescriptionTextArea.setRequired(required);
    }

    public void configureContactPersonCombobox() {
        design.contactPersonCombobox.setNullSelectionAllowed(false);
        design.contactPersonCombobox.setFilteringMode(FilteringMode.CONTAINS);
        design.contactPersonCombobox.setItemCaptionMode(AbstractSelect.ItemCaptionMode.EXPLICIT);
        design.contactPersonCombobox.addValueChangeListener(this::updateSection);
    }

    private void updateSection(Property.ValueChangeEvent event) {
        design.sectionTextfield.setReadOnly(false);
        User user = (User) event.getProperty().getValue();
        design.sectionTextfield.setValue(user != null ? user.getSection() : "");
        design.sectionTextfield.setReadOnly(true);
    }

    public void init(Language primaryLanguage) {
        this.primaryLanguage = primaryLanguage;
        design.primaryLanguageLabel.setValue(primaryLanguage.getDisplayName());
        design.languageSelect.init(primaryLanguage);
        updateLanguagePanels(primaryLanguage);
        populateContactPersons();
        resetValidations();
    }

    @Override
    public void restorePreviousEditingState(EditingState editingState) {
        if (editingState.isLanguageTabVisible()) {
            design.languageSelect.showLanguagesAndNotify(true, editingState.isSecondNotThirdLanguageVisible());
        }
    }

    @Override
    public EditingState currentEditingState() {
        return EditingState.newMetadataEditingState(visible, secondaryLanguage);
    }

    private void resetValidations() {
        for (AbstractField<?> field : fieldsToValidate) {
            field.setValidationVisible(false);
            field.setComponentError(null);
        }
    }

    public void populateContactPersons() {
        ComboBox personCombobox = design.contactPersonCombobox;
        boolean isReadOnly = personCombobox.isReadOnly();
        personCombobox.setReadOnly(false);
        personCombobox.removeAllItems();
        List<User> userList = userService.getAllUsers();
        personCombobox.clear();
        for (User user : userList) {
            personCombobox.addItem(user);
            personCombobox.setItemCaption(user, user.getFullname());
        }
        personCombobox.setReadOnly(isReadOnly);
    }

    private void showOtherLanguages(boolean isEditMode) {
        visible = isEditMode;
        design.secondLanguageLayout.setVisible(visible && secondaryLanguage);
        design.thirdLanguageLayout.setVisible(visible && !secondaryLanguage);
    }

    private void updateLanguagePanels(Language newLanguage) {
        secondaryLanguage = newLanguage == Language.getSecondLanguage(primaryLanguage);
        showOtherLanguages(visible);
    }

    public String getName(Language language) {
        Language[] prioritizedOrder = Language.getPrioritizedOrder(primaryLanguage);
        if (language == prioritizedOrder[0]) {
            return design.primaryNameTextfield.getValue();
        } else if (language == prioritizedOrder[1]) {
            return design.secondaryNameTextfield.getValue();
        } else if (language == prioritizedOrder[2]) {
            return design.thirdNameTextfield.getValue();
        } else {
            throw new RuntimeException("Unable to determine value for Language " + language);
        }
    }

    public String getDescription(Language language) {
        Language[] prioritizedOrder = Language.getPrioritizedOrder(primaryLanguage);
        if (language == prioritizedOrder[0]) {
            return design.primaryDescriptionTextArea.getValue();
        } else if (language == prioritizedOrder[1]) {
            return design.secondaryDescriptionTextArea.getValue();
        } else if (language == prioritizedOrder[2]) {
            return design.thirdDescriptionTextArea.getValue();
        } else {
            throw new RuntimeException("Unable to determine value for Language " + language);
        }
    }

    public boolean checkAndSetPublished(Publishable classificationEntityOperations) {
        return publicationChoiceEditor.checkAndSetPublished(userContext.isAdministrator(),
                classificationEntityOperations);
    }

    public User getContactPerson() {
        return (User) design.contactPersonCombobox.getValue();
    }

    public void setName(Language language, String name) {
        Language[] prioritizedOrder = Language.getPrioritizedOrder(primaryLanguage);
        if (language == prioritizedOrder[0]) {
            design.primaryNameTextfield.setValue(name);
            design.primaryNameTextfield.setData(name);
        } else if (language == prioritizedOrder[1]) {
            design.secondaryNameTextfield.setValue(name);
            design.secondaryNameTextfield.setData(name);
        } else if (language == prioritizedOrder[2]) {
            design.thirdNameTextfield.setValue(name);
            design.thirdNameTextfield.setData(name);
        } else {
            throw new RuntimeException("Unable to apply value for Language " + language);
        }
    }

    public void setDescription(Language language, String description) {
        Language[] prioritizedOrder = Language.getPrioritizedOrder(primaryLanguage);
        if (language == prioritizedOrder[0]) {
            design.primaryDescriptionTextArea.setValue(description);
            design.primaryDescriptionTextArea.setData(description);
        } else if (language == prioritizedOrder[1]) {
            design.secondaryDescriptionTextArea.setValue(description);
            design.secondaryDescriptionTextArea.setData(description);
        } else if (language == prioritizedOrder[2]) {
            design.thirdDescriptionTextArea.setValue(description);
            design.thirdDescriptionTextArea.setData(description);
        } else {
            throw new RuntimeException("Unable to apply value for Language " + language);
        }
    }

    public void setContactPerson(User contactPerson) {
        design.contactPersonCombobox.setValue(contactPerson);
        design.contactPersonCombobox.setData(contactPerson);
    }

    public void setContactPersonReadOnly(User contactPerson) {
        design.contactPersonCombobox.setReadOnly(false);
        design.contactPersonCombobox.setValue(contactPerson);
        design.contactPersonCombobox.setData(contactPerson);
        design.contactPersonCombobox.setReadOnly(true);
    }

    public void setPublication(Publishable classificationEntityOperations) {
        publicationChoiceEditor.init(userContext.isAdministrator(), classificationEntityOperations);
    }

    public void addPublicationComponent() {
        publicationChoiceEditor = new PublicationChoiceEditor();
        design.primaryLanguageLayout.addComponent(publicationChoiceEditor);
    }

    public boolean validate() {
        Map<AbstractField<?>, String> fieldMap = new HashMap<>();
        for (AbstractField<?> field : fieldsToValidate) {
            fieldMap.put(field, field.getCaption());
        }
        if ((getDraftCheckBoxValue() || areDatesDraft()) && areAnyPublishBoxesChecked()) {
            Notification.show("Ugyldig kombinasjon (utkast og publisert)");
            return false;
        }
        return PassiveValidationUtil.validateAndShowWarnings(fieldMap);
    }

    private boolean areAnyPublishBoxesChecked() {
        if (publicationChoiceEditor == null) {
            return false;
        }
        return publicationChoiceEditor.isAnyPublishBoxChecked();
    }

    private boolean areDatesDraft() {
        if (design.dateLayout.isVisible()) {
            return DraftUtil.isDraft(getDateRange());
        }
        return false;
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        ComponentUtil.setReadOnlyRecursively(this, readOnly);
    }

    @Override
    public boolean hasChanges() {
        EmptyStringEqualsBuilder builder = new EmptyStringEqualsBuilder();
        if (design.contactPersonCombobox.getData() != null) {
            builder.append(design.contactPersonCombobox.getValue(), design.contactPersonCombobox.getData());
        }

        builder.append(design.primaryDescriptionTextArea.getValue(), (String) design.primaryDescriptionTextArea
                .getData())
                .append(design.secondaryDescriptionTextArea.getValue(), (String) design.secondaryDescriptionTextArea
                        .getData())
                .append(design.thirdDescriptionTextArea.getValue(), (String) design.thirdDescriptionTextArea.getData())
                //
                .append(design.primaryNameTextfield.getValue(), (String) design.primaryNameTextfield.getData())
                .append(design.secondaryNameTextfield.getValue(), (String) design.secondaryNameTextfield.getData())
                .append(design.thirdNameTextfield.getValue(), (String) design.thirdNameTextfield.getData());

        return !builder.isEquals() || (publicationChoiceEditor != null && publicationChoiceEditor.hasChanges());
    }

    public DateRange getDateRange() {
        return DateRange.create(TimeUtil.toLocalDate(design.fromDate.getValue()),
                VaadinUtil.convertToExclusive(TimeUtil.toLocalDate(design.toDate.getValue())));
    }

    public void setDateRange(DateRange dateRange) {
        Date from = TimeUtil.toDate(fistDayOfMonth(dateRange.getFrom()));
        Date to = TimeUtil.toDate(VaadinUtil.convertToInclusive(fistDayOfMonth(dateRange.getTo())));
        design.fromDate.setValue(from);
        design.toDate.setValue(to);
    }

    public boolean getDraftCheckBoxValue() {
        return design.draftCheckBox.getValue();
    }

    public void setDraft(boolean value) {
        design.draftCheckBox.setValue(value);
    }

    private static class EmptyStringEqualsBuilder extends EqualsBuilder {
        public EmptyStringEqualsBuilder append(String lhs, String rhs) {
            lhs = Strings.nullToEmpty(lhs);
            rhs = Strings.nullToEmpty(rhs);
            return (EmptyStringEqualsBuilder) super.append(lhs, rhs);
        }
    }
}
