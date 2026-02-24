package no.ssb.klass.designer.editing.version;

import static com.google.common.base.Preconditions.*;
import static no.ssb.klass.core.util.TimeUtil.*;
import static no.ssb.klass.designer.editing.utils.ValidationHelpers.*;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.vaadin.spring.annotation.PrototypeScope;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Notification;

import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.ClassificationType;
import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.util.DateRange;
import no.ssb.klass.core.util.DraftUtil;
import no.ssb.klass.core.util.TimeUtil;
import no.ssb.klass.designer.editing.EditingState;
import no.ssb.klass.designer.editing.HasEditingState;
import no.ssb.klass.designer.service.ClassificationFacade;
import no.ssb.klass.designer.util.ComponentUtil;
import no.ssb.klass.designer.util.KlassTheme;
import no.ssb.klass.designer.util.VaadinUtil;

// VersionMetadataEditor
@SpringComponent
@PrototypeScope
@SuppressWarnings("serial")
public class VersionEditorView extends VersionEditorDesign implements HasEditingState {
    private static final String DATE_INPUT_MASK = "MM.yyyy";
    private Language primaryLanguage;
    private Language secondaryLanguage;
    private Language thirdLanguage;
    private boolean copyFromPrimary = true;
    private boolean isCodeList = false;

    private ClassificationVersion classificationVersion;

    public VersionEditorView() {
        fromDate.setDateFormat(DATE_INPUT_MASK);
        toDate.setDateFormat(DATE_INPUT_MASK);
        secondaryLanguageButton.addClickListener(event -> enableSecondaryLanguage());
        thirdLanguageButton.addClickListener(event -> enableThirdLanguage());
        languageToggleButton.addClickListener(event -> toggleLanguages());
        enableAlias(false);
    }

    private void toggleLanguages() {
        if (!languageLayout.isVisible()) {
            enableLanguages();
        } else {
            disableLanguages();
        }
    }

    private void enableLanguages() {
        languageLayout.setVisible(true);
        enableSecondaryLanguage();
        languageToggleButton.setCaption("Skjul språk");
    }

    private void disableLanguages() {
        languageLayout.setVisible(false);
        languageToggleButton.setCaption("Rediger språk");
        secondaryLanguageLayout.setVisible(false);
        thirdLanguageLayout.setVisible(false);
    }

    public void enableAlias(boolean enabled) {
        alias.setVisible(enabled);
        secondaryAlias.setVisible(enabled);
        thirdAlias.setVisible(enabled);
    }

    private void enableSecondaryLanguage() {
        secondaryLanguageButton.addStyleName("v-label-bold");
        thirdLanguageButton.removeStyleName("v-label-bold");
        secondaryLanguageLayout.setVisible(true);
        thirdLanguageLayout.setVisible(false);
        disableSecondaryCodelistFields(isCodeList);
        copyValuesFromPrimary();
    }

    private void copyValuesFromPrimary() {
        if (copyFromPrimary && !isReadOnly()) {
            secondaryIntroduction.setValue(introduction.getValue());
            secondaryLegalBase.setValue(legalBase.getValue());
            secondaryPublications.setValue(publications.getValue());
            secondaryDerivedFrom.setValue(derivedFrom.getValue());
            copyFromPrimary = false;
        }
    }

    private boolean checkSecondaryValues() {
        return secondaryIntroduction.getValue().isEmpty() && secondaryLegalBase.getValue().isEmpty()
                && secondaryPublications.getValue().isEmpty() && secondaryDerivedFrom.getValue().isEmpty();
    }

    private void enableThirdLanguage() {
        thirdLanguageButton.addStyleName("v-label-bold");
        secondaryLanguageButton.removeStyleName("v-label-bold");
        thirdLanguageLayout.setVisible(true);
        secondaryLanguageLayout.setVisible(false);
        if (isCodeList) {
            disableThirdCodelistFields(isCodeList);
        }
    }


    public void init(ClassificationVersion classificationVersion) {
        this.classificationVersion = checkNotNull(classificationVersion);
        resetForm();

        DateRange validRange = classificationVersion.getDateRange();
        Date from = TimeUtil.toDate(fistDayOfMonth(validRange.getFrom()));
        Date to = TimeUtil.toDate(VaadinUtil.convertToInclusive(fistDayOfMonth(validRange.getTo())));

        fromDate.setValue(from);
        toDate.setValue(to);

        primaryLanguage = classificationVersion.getOwnerClassification().getPrimaryLanguage();
        secondaryLanguage = Language.getSecondLanguage(primaryLanguage);
        thirdLanguage = Language.getThirdLanguage(primaryLanguage);
        primaryLabel.setValue(primaryLanguage.getDisplayName());
        secondaryLanguageButton.setCaption(secondaryLanguage.getDisplayName());
        thirdLanguageButton.setCaption(thirdLanguage.getDisplayName());
        headerLabel.setValue(classificationVersion.getNameInPrimaryLanguage());
        introduction.setValue(classificationVersion.getIntroduction(primaryLanguage));
        secondaryIntroduction.setValue(classificationVersion.getIntroduction(secondaryLanguage));
        thirdIntroduction.setValue(classificationVersion.getIntroduction(thirdLanguage));
        alias.setValue(classificationVersion.getAlias(primaryLanguage));
        secondaryAlias.setValue(classificationVersion.getAlias(secondaryLanguage));
        thirdAlias.setValue(classificationVersion.getAlias(thirdLanguage));
        copyFromPrimary = classificationVersion.getPrimaryLanguage() == Language.NB && checkSecondaryValues();
        if (classificationVersion.getOwnerClassification().getClassificationType() == ClassificationType.CODELIST) {
            isCodeList = true;
            disableCodelistFields(isCodeList);
        } else {
            isCodeList = false;
            legalBase.setValue(classificationVersion.getLegalBase(primaryLanguage));
            publications.setValue(classificationVersion.getPublications(primaryLanguage));
            derivedFrom.setValue(classificationVersion.getDerivedFrom(primaryLanguage));
            secondaryLegalBase.setValue(classificationVersion.getLegalBase(secondaryLanguage));
            secondaryPublications.setValue(classificationVersion.getPublications(secondaryLanguage));
            secondaryDerivedFrom.setValue(classificationVersion.getDerivedFrom(secondaryLanguage));
            thirdLegalBase.setValue(classificationVersion.getLegalBase(thirdLanguage));
            thirdPublications.setValue(classificationVersion.getPublications(thirdLanguage));
            thirdDerivedFrom.setValue(classificationVersion.getDerivedFrom(thirdLanguage));
        }

        contactPersonCombobox.addItem(classificationVersion.getContactPerson().getFullname());
        contactPersonCombobox.setValue(classificationVersion.getContactPerson().getFullname());
        contactPersonCombobox.setReadOnly(true);
        sectionTextfield.setValue(classificationVersion.getContactPerson().getSection());
        sectionTextfield.setReadOnly(true);
    }

    @Override
    public void restorePreviousEditingState(EditingState editingState) {
        if (editingState.isLanguageTabVisible()) {
            enableLanguages();
            if (!editingState.isSecondNotThirdLanguageVisible()) {
                enableThirdLanguage();
            }
        }
    }

    @Override
    public EditingState currentEditingState() {
        return EditingState.newMetadataEditingState(languageLayout.isVisible(), secondaryLanguageLayout.isVisible());
    }

    public boolean updateVersion(ClassificationVersion classificationVersion,
            ClassificationFacade classificationFacade) {
        if (!checkForm()) {
            return false;
        }
        Date from = fromDate.getValue();
        Date to = toDate.getValue();
        DateRange newRange = null;
        if (from != null) {
            newRange = DateRange.create(TimeUtil.toLocalDate(from),
                    VaadinUtil.convertToExclusive(TimeUtil.toLocalDate(to)));
        } else {
            newRange = DraftUtil.getDraftDateRange();
        }
        boolean overlapConflict = hasIntervalOverlapConflict(classificationVersion, newRange, classificationFacade);
        if (overlapConflict) {
            return false;
        }
        classificationVersion.setDateRange(newRange);
        getIntroduction(classificationVersion);
        getAlias(classificationVersion);
        if (classificationVersion.getOwnerClassification()
                .getClassificationType() == ClassificationType.CLASSIFICATION) {
            getLegalBase(classificationVersion);
            getPublications(classificationVersion);
            getDerivedFrom(classificationVersion);
        }
        return true;
    }

    private boolean checkForm() {
        removeErrorMarkings();

        return (classificationVersion.isDraft() || verifyDateInput(fromDate, toDate, true))
                && verifyTextInput(introduction, "Beskrivelse må være fylt ut");
    }

    private boolean hasIntervalOverlapConflict(ClassificationVersion classificationVersion, DateRange range,
            ClassificationFacade classificationFacade) {
        List<ClassificationVersion> existingVersions = reloadToAvoidLazyInitializationException(classificationVersion
                .getOwnerClassification(), classificationFacade).getClassificationVersions();
        for (ClassificationVersion existingVersion : existingVersions) {
            if ((existingVersion.isDraft() ^ DraftUtil.isDraft(range))
                    || existingVersion.equals(classificationVersion)) {
                continue;
            } else if (existingVersion.isDraft() && DraftUtil.isDraft(range)) {
                Notification.show("Du kan kun ha ett utkast per klassifikasjon", Notification.Type.WARNING_MESSAGE);
                return true;
            }
            if (!classificationVersion.equals(existingVersion) && range.overlaps(existingVersion.getDateRange())) {
                String name = existingVersion.getNameInPrimaryLanguage();
                dateOverlapError(name);
                return true;
            }
        }
        return false;
    }

    private ClassificationSeries reloadToAvoidLazyInitializationException(ClassificationSeries classification,
            ClassificationFacade classificationFacade) {
        return classificationFacade.getRequiredClassificationSeries(classification.getId());
    }

    private void removeErrorMarkings() {
        fromDate.removeStyleName(KlassTheme.TEXTFIELD_ERROR);
        toDate.removeStyleName(KlassTheme.TEXTFIELD_ERROR);
        derivedFrom.removeStyleName(KlassTheme.TEXTFIELD_ERROR);
        introduction.removeStyleName(KlassTheme.TEXTFIELD_ERROR);
    }


    private boolean verifyTextInput(AbstractTextField field, String errorMsg) {
        if (field == null || field.getValue().trim().isEmpty()) {
            showErrorField(field);
            Notification.show(errorMsg);
            return false;
        } else {
            return true;
        }
    }



    private void dateOverlapError(String overlapName) {
        Notification.show("Det er overlapp med en allerede eksisterende versjon (" + overlapName + ')');
        showErrorField(fromDate);
    }

    protected void getIntroduction(ClassificationVersion classificationVersion) {
        Language primaryLanguage = classificationVersion.getPrimaryLanguage();
        classificationVersion.setIntroduction(introduction.getValue(), primaryLanguage);
        classificationVersion.setIntroduction(secondaryIntroduction.getValue(), Language.getSecondLanguage(
                primaryLanguage));
        classificationVersion.setIntroduction(thirdIntroduction.getValue(), Language.getThirdLanguage(primaryLanguage));
    }

    protected void getAlias(ClassificationVersion classificationVersion) {
        Language primaryLanguage = classificationVersion.getPrimaryLanguage();
        classificationVersion.setAlias(alias.getValue(), primaryLanguage);
        classificationVersion.setAlias(secondaryAlias.getValue(), Language.getSecondLanguage(primaryLanguage));
        classificationVersion.setAlias(thirdAlias.getValue(), Language.getThirdLanguage(primaryLanguage));
    }

    protected void getLegalBase(ClassificationVersion classificationVersion) {
        Language primaryLanguage = classificationVersion.getPrimaryLanguage();
        classificationVersion.setLegalBase(legalBase.getValue(), primaryLanguage);
        classificationVersion.setLegalBase(secondaryLegalBase.getValue(), Language.getSecondLanguage(primaryLanguage));
        classificationVersion.setLegalBase(thirdLegalBase.getValue(), Language.getThirdLanguage(primaryLanguage));
    }

    protected void getPublications(ClassificationVersion classificationVersion) {
        Language primaryLanguage = classificationVersion.getPrimaryLanguage();
        classificationVersion.setPublications(publications.getValue(), primaryLanguage);
        classificationVersion.setPublications(secondaryPublications.getValue(), Language.getSecondLanguage(
                primaryLanguage));
        classificationVersion.setPublications(thirdPublications.getValue(), Language.getThirdLanguage(primaryLanguage));
    }

    protected void getDerivedFrom(ClassificationVersion classificationVersion) {
        Language primaryLanguage = classificationVersion.getPrimaryLanguage();
        classificationVersion.setDerivedFrom(derivedFrom.getValue(), primaryLanguage);
        classificationVersion.setDerivedFrom(secondaryDerivedFrom.getValue(), Language.getSecondLanguage(
                primaryLanguage));
        classificationVersion.setDerivedFrom(thirdDerivedFrom.getValue(), Language.getThirdLanguage(primaryLanguage));
    }

    private void resetForm() {
        copyFromPrimary = true;
        isCodeList = false;
        fromDate.clear();
        toDate.clear();
        introduction.clear();
        legalBase.clear();
        publications.clear();
        derivedFrom.clear();
        secondaryIntroduction.clear();
        secondaryLegalBase.clear();
        secondaryPublications.clear();
        secondaryDerivedFrom.clear();
        thirdIntroduction.clear();
        thirdDerivedFrom.clear();
        thirdLegalBase.clear();
        thirdPublications.clear();
        secondaryLanguageLayout.setVisible(false);
        thirdLanguageLayout.setVisible(false);
    }

    private void disableCodelistFields(boolean codeList) {
        derivedFrom.setVisible(!codeList);
        derivedFrom.setEnabled(!codeList);
        legalBase.setVisible(!codeList);
        legalBase.setEnabled(!codeList);
        publications.setVisible(!codeList);
        publications.setEnabled(!codeList);
    }

    private void disableSecondaryCodelistFields(boolean codeList) {
        secondaryLegalBase.setVisible(!codeList);
        secondaryPublications.setVisible(!codeList);
        secondaryDerivedFrom.setVisible(!codeList);
        secondaryLegalBase.setVisible(!codeList);
        secondaryPublications.setVisible(!codeList);
        secondaryDerivedFrom.setVisible(!codeList);
    }

    private void disableThirdCodelistFields(boolean codeList) {
        thirdLegalBase.setVisible(!codeList);
        thirdPublications.setVisible(!codeList);
        thirdDerivedFrom.setVisible(!codeList);
        thirdLegalBase.setVisible(!codeList);
        thirdPublications.setVisible(!codeList);
        thirdDerivedFrom.setVisible(!codeList);
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        ComponentUtil.setReadOnlyRecursively(this, readOnly);
    }

    @Override
    public boolean hasChanges() {

        DateRange newRange = DateRange.create(TimeUtil.toLocalDate(fromDate.getValue()),
                VaadinUtil.convertToExclusive(TimeUtil.toLocalDate(toDate.getValue())));

        Language primaryLanguage = classificationVersion.getPrimaryLanguage();
        Language secondLanguage = Language.getSecondLanguage(primaryLanguage);
        Language thirdLanguage = Language.getThirdLanguage(primaryLanguage);

        EqualsBuilder builder = new EqualsBuilder();
        builder.append(classificationVersion.getDateRange(), newRange)
                //
                .append(classificationVersion.getIntroduction(primaryLanguage), introduction.getValue())
                .append(classificationVersion.getIntroduction(secondLanguage), secondaryIntroduction.getValue())
                .append(classificationVersion.getIntroduction(thirdLanguage), thirdIntroduction.getValue())
                //
                .append(classificationVersion.getLegalBase(primaryLanguage), legalBase.getValue())
                .append(classificationVersion.getLegalBase(secondLanguage), secondaryLegalBase.getValue())
                .append(classificationVersion.getLegalBase(thirdLanguage), thirdLegalBase.getValue())
                //
                .append(classificationVersion.getPublications(primaryLanguage), publications.getValue())
                .append(classificationVersion.getPublications(secondLanguage), secondaryPublications.getValue())
                .append(classificationVersion.getPublications(thirdLanguage), thirdPublications.getValue())
                //
                .append(classificationVersion.getDerivedFrom(primaryLanguage), derivedFrom.getValue())
                .append(classificationVersion.getDerivedFrom(secondLanguage), secondaryDerivedFrom.getValue())
                .append(classificationVersion.getDerivedFrom(thirdLanguage), thirdDerivedFrom.getValue());

        return !builder.isEquals();
    }
}
