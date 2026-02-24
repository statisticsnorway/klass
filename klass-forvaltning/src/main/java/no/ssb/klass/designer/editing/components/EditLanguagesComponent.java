package no.ssb.klass.designer.editing.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;

import no.ssb.klass.core.model.Language;
import no.ssb.klass.designer.util.KlassTheme;

/**
 * Used to toggle whether translation of languages shall be displayed or not. And if displayed to toggle which language
 * shall be be translated.
 * 
 * @author karsten.vileid
 */
public class EditLanguagesComponent extends CustomComponent {
    private static final String EDIT_LANGUAGES = "Rediger språk";
    private static final String HIDE_LANGUAGES = "Skjul språk";
    private boolean isLanguagesVisible;
    private Language currentLanguage;
    private final HorizontalLayout languageSelectLayout;
    private final Component editLanguagesButton;
    private final Button secondaryLanguageButton;
    private final Button thirdLanguageButton;
    private final Button automaticTranslationButton;
    private final List<LanguageChangeListener> languageChangeListeners = new ArrayList<>();
    private final List<EditLanguagesListener> editLanguagesListeners = new ArrayList<>();

    public EditLanguagesComponent() {
        secondaryLanguageButton = createLanguageButton();
        thirdLanguageButton = createLanguageButton();
        automaticTranslationButton = createAutomaticTranslationButton();
        automaticTranslationButton.setVisible(false);
        languageSelectLayout = new HorizontalLayout(secondaryLanguageButton, new Label("|"), thirdLanguageButton,
                automaticTranslationButton);
        editLanguagesButton = createEditLanguagesButton();
        HorizontalLayout layout = new HorizontalLayout(languageSelectLayout, editLanguagesButton);
        layout.setWidth("100%");
        layout.setMargin(new MarginInfo(false, true, false, true));
        layout.setComponentAlignment(languageSelectLayout, Alignment.MIDDLE_CENTER);
        layout.setComponentAlignment(editLanguagesButton, Alignment.MIDDLE_RIGHT);
        layout.setExpandRatio(languageSelectLayout, 1);
        setCompositionRoot(layout);
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        automaticTranslationButton.setEnabled(!readOnly);
    }

    private Button createAutomaticTranslationButton() {
        Button button = new Button(FontAwesome.WRENCH);
        button.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        button.setHeight("100%");
        button.setId("testbench-automatic-translation");
        return button;
    }

    private void showAutomaticTranslationButton(Language primaryLanguage,
            AutomaticTranslationListener automaticTranslationListener) {
        automaticTranslationButton.setDescription("Klikk for å kopiere oversettelese fra " + primaryLanguage
                .getDisplayName());
        removeAllClickListeners(automaticTranslationButton);
        automaticTranslationButton.addClickListener(event -> automaticTranslationListener.automaticTranslation());
        automaticTranslationButton.setVisible(true);
    }

    public void init(Language primaryLanguage) {
        updateLanguageButtons(primaryLanguage);
        hideLanguages();
    }

    public void initWithAutomaticTranslation(Language primaryLanguage,
            AutomaticTranslationListener automaticTranslationListener) {
        showAutomaticTranslationButton(primaryLanguage, automaticTranslationListener);
        init(primaryLanguage);
    }

    public void removeAllClickListeners(Button button) {
        for (Object listener : button.getListeners(ClickEvent.class)) {
            button.removeClickListener((ClickListener) listener);
        }
    }

    private void updateLanguageButtons(Language primaryLanguage) {
        secondaryLanguageButton.setCaption(Language.getSecondLanguage(primaryLanguage).getDisplayName());
        secondaryLanguageButton.setData(Language.getSecondLanguage(primaryLanguage));
        thirdLanguageButton.setCaption(Language.getThirdLanguage(primaryLanguage).getDisplayName());
        thirdLanguageButton.setData(Language.getThirdLanguage(primaryLanguage));
    }

    private void updateCurrentLanguage(Language language) {
        if (currentLanguage == language) {
            return;
        }
        currentLanguage = language;
        highlightButton(language);
        for (LanguageChangeListener languageChangeListener : languageChangeListeners) {
            languageChangeListener.languageChange(language);
        }
    }

    private void highlightButton(Language selectedLanguage) {
        for (Button button : Arrays.asList(secondaryLanguageButton, thirdLanguageButton)) {
            if (button.getData() == selectedLanguage) {
                button.addStyleName("v-label-bold");
            } else {
                button.removeStyleName("v-label-bold");
            }
        }
    }

    public void showLanguagesAndNotify(boolean visible, boolean secondNotThirdLanguage) {
        showLanguages(visible, secondNotThirdLanguage);
        for (EditLanguagesListener editLanguagesListener : editLanguagesListeners) {
            editLanguagesListener.editLanguages(isLanguagesVisible);
        }
    }

    private void hideLanguages() {
        showLanguages(false, true);
    }

    private void showLanguages(boolean visible, boolean secondNotThirdLanguage) {
        isLanguagesVisible = visible;
        languageSelectLayout.setVisible(isLanguagesVisible);
        updateEditLanguagesCaption();

        if (!isLanguagesVisible) {
            currentLanguage = null;
        } else {
            if (secondNotThirdLanguage) {
                secondaryLanguageButton.click();
            } else {
                thirdLanguageButton.click();
            }
        }
    }

    protected void updateEditLanguagesCaption() {
        editLanguagesButton.setCaption(isLanguagesVisible ? HIDE_LANGUAGES : EDIT_LANGUAGES);
    }

    protected Component createEditLanguagesButton() {
        Button button = createButton();
        button.setCaption(EDIT_LANGUAGES);
        button.addClickListener(event -> showLanguagesAndNotify(!isLanguagesVisible,
                isSecondNotThirdLanguageSelected()));
        return button;
    }

    private Button createLanguageButton() {
        Button button = createButton();
        button.addClickListener(event -> updateCurrentLanguage((Language) event.getButton().getData()));
        return button;
    }

    protected Button createButton() {
        Button button = new Button();
        button.setPrimaryStyleName(KlassTheme.ACTION_TEXT_BUTTON);
        return button;
    }

    public boolean isLanguagesVisible() {
        return isLanguagesVisible;
    }

    public boolean isSecondNotThirdLanguageSelected() {
        return currentLanguage == null || currentLanguage == secondaryLanguageButton.getData();
    }

    /**
     * Add listener that is notified when selected language changes
     */
    public void addLanguageChangeListener(LanguageChangeListener languageChangeListener) {
        languageChangeListeners.add(languageChangeListener);
    }

    /**
     * Add listener that is notified when toggles between edit and hide mode
     */
    public void addEditLanguagesListener(EditLanguagesListener editLanguagesListener) {
        editLanguagesListeners.add(editLanguagesListener);
    }

    /**
     * Listens for when language selection changes
     */
    public interface LanguageChangeListener {
        /**
         * @param newLanguage
         *            the newly selected language
         */
        void languageChange(Language newLanguage);
    }

    /**
     * Listens for when edit languages becomes visible or not
     */
    public interface EditLanguagesListener {
        /**
         * @param isEditMode
         *            true if entered edit mode, false if languages are now hidden
         */
        void editLanguages(boolean isEditMode);
    }

    /**
     * Listens for when automaticTranslation icon is clicked
     */
    public interface AutomaticTranslationListener {
        void automaticTranslation();
    }
}
