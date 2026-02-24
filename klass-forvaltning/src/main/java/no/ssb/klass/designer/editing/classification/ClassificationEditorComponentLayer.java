package no.ssb.klass.designer.editing.classification;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.vaadin.ui.AbstractField;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

import no.ssb.klass.core.model.Language;

/**
 * @author Mads Lundemo, SSB.
 */
public abstract class ClassificationEditorComponentLayer extends ClassificationEditorDesign {

    protected List<AbstractField<?>> fields = Arrays.asList(primaryTitleTextfield, secondaryTitleTextfield,
            thirdTitleTextfield, classificationTypeCombobox, primaryDescriptionTextArea, secondaryDescriptionTextArea,
            thirdDescriptionTextArea, familiesCombobox, contactPersonCombobox, statisticalUnitSelector,
            primaryLanguageCombobox, includeShortnameCheckbox, includeNotesCheckbox, copyrightCheckbox);

    protected List<TextField> titleFields = Arrays.asList(primaryTitleTextfield, secondaryTitleTextfield,
            thirdTitleTextfield);

    protected List<TextField> titlePrefixFields = Arrays.asList(primaryTitlePrefixTextfield,
            secondaryTitlePrefixTextfield, thirdTitlePrefixTextfield);

    protected List<TextArea> descriptionFields = Arrays.asList(primaryDescriptionTextArea, secondaryDescriptionTextArea,
            thirdDescriptionTextArea);

    protected List<Language> supportedLanguages = Arrays.asList(Language.NB, Language.NN, Language.EN);

    protected TextField getTitleTextField(Language language) {
        for (TextField field : titleFields) {
            if (Objects.equals(field.getData(), language)) {
                return field;
            }
        }
        throw new RuntimeException("Unable to locate title field for language " + language);
    }

    protected TextField getTitlePrefixTextField(Language language) {
        for (TextField field : titlePrefixFields) {
            if (Objects.equals(field.getData(), language)) {
                return field;
            }
        }
        throw new RuntimeException("Unable to locate titleprefix field for language " + language);
    }

    protected TextArea getDescriptionTextArea(Language language) {
        for (TextArea area : descriptionFields) {
            if (Objects.equals(area.getData(), language)) {
                return area;
            }
        }
        throw new RuntimeException("Unable to locate description field for language " + language);
    }

    protected void updateReadOnlyTextField(TextField textField, String text) {
        textField.setReadOnly(false);
        textField.setValue(text);
        textField.setReadOnly(true);
    }
}
