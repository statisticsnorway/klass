package no.ssb.klass.designer.editing.components;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

import no.ssb.klass.core.model.Language;

/**
 * Used to toggle whether translation of languages shall be displayed or not. And if displayed to toggle which language
 * shall be be translated.
 * 
 * @author karsten.vileid
 */
public class SelectEditLanguageOrVariantComponent extends EditLanguagesComponent {
    private Button editLanguages;
    private Button editVariant;

    @Override
    protected Component createEditLanguagesButton() {
        editLanguages = createButton();
        editLanguages.setCaption("Rediger språk");
        editLanguages.addClickListener(event -> showLanguagesAndNotify(true, true));
        editVariant = createButton();
        editVariant.setCaption("Rediger variant");
        editVariant.addClickListener(event -> showLanguagesAndNotify(false, true));
        return new HorizontalLayout(editLanguages, new Label("|"), editVariant);
    }

    @Override
    public void init(Language primaryLanguage) {
        super.init(primaryLanguage);
        highlightSelection(false);
    }

    @Override
    protected void updateEditLanguagesCaption() {
    }

    @Override
    public void showLanguagesAndNotify(boolean displayLanguages, boolean secondNotThirdLanguage) {
        super.showLanguagesAndNotify(displayLanguages, secondNotThirdLanguage);
        highlightSelection(displayLanguages);
    }

    private void highlightSelection(boolean displayLanguages) {
        if (displayLanguages) {
            editLanguages.addStyleName("v-label-bold");
            editVariant.removeStyleName("v-label-bold");
        } else {
            editVariant.addStyleName("v-label-bold");
            editLanguages.removeStyleName("v-label-bold");
        }
    }
}
