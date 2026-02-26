package no.ssb.klass.designer.editing.version;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.vaadin.spring.annotation.PrototypeScope;

import com.google.common.eventbus.EventBus;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.UI;

import no.ssb.klass.forvaltning.converting.exception.ImportException;
import no.ssb.klass.forvaltning.converting.xml.ClassificationVersionXmlService;
import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.designer.editing.EditingState;
import no.ssb.klass.designer.editing.HasEditingState;
import no.ssb.klass.designer.editing.TranslationListener;
import no.ssb.klass.designer.editing.codetables.utils.CodeTableUtils;
import no.ssb.klass.designer.editing.components.ImportExportComponent;
import no.ssb.klass.designer.listeners.SharedEscapeShortcutListener;
import no.ssb.klass.designer.service.ClassificationFacade;
import no.ssb.klass.designer.util.ComponentUtil;
import no.ssb.klass.designer.windows.AutomaticTranslationWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
@SpringComponent
@PrototypeScope
public class CodeEditorView extends CodeEditorDesign implements HasEditingState {
    private static final Logger log = LoggerFactory.getLogger(CodeEditorView.class);

    private ImportExportComponent<ClassificationVersion> importExportComponent;
    private ClassificationVersion version;
    private final EventBus eventbus;

    private ApplicationContext context;

    private ClassificationFacade classificationFacade;

    private ClassificationVersionXmlService versionXmlService;

    private SharedEscapeShortcutListener shortcutListener;

    @Autowired
    public CodeEditorView(ApplicationContext context, ClassificationFacade classificationFacade, ClassificationVersionXmlService versionXmlService) {
        this.context = context;
        this.classificationFacade = classificationFacade;
        this.versionXmlService = versionXmlService;
        shortcutListener = new SharedEscapeShortcutListener();
        eventbus = new EventBus("code-editor");
        eventbus.register(primaryCodeTable);
        eventbus.register(primaryLevels);
        eventbus.register(translationCodeTable);
        eventbus.register(translationLevels);

        editTranslations.addEditLanguagesListener(this::showTranslations);
        editTranslations.addLanguageChangeListener(language -> {
            translationCodeTable.commitDirtyCodeEditors();
            translationCodeTable.init(eventbus, version, language);
            translationLevels.init(eventbus, version, language);
        });
        log.info("Initializing CodeEditorView");
    }

    @Override
    public void restorePreviousEditingState(EditingState editingState) {
        if (editingState.isLanguageTabVisible()) {
            editTranslations.showLanguagesAndNotify(true, editingState.isSecondNotThirdLanguageVisible());
        }
    }

    @Override
    public EditingState currentEditingState() {
        return EditingState.newCodeEditorEditingState(editTranslations.isLanguagesVisible(), editTranslations
                .isSecondNotThirdLanguageSelected());
    }



    private void updateView(boolean success) {
        primaryCodeTable.refresh();
        translationCodeTable.refresh();
        primaryLevels.refresh();
        if (success) {
            primaryCodeTable.getTable().markAsModified();
        }
    }

    private void deleteExistingItemsBeforeImport(ClassificationVersion version) throws ImportException {
        // check if we can delete items
        try {
            CodeTableUtils.verifyNoReferencesToClassificationItems(classificationFacade, version);
        } catch (IllegalArgumentException e) {
            throw new ImportException(e.getMessage(), e);
        }
        // delete
        if (version.getFirstLevel().isPresent()) {
            // new ArrayList to avoid concurrentModificationException
            new ArrayList<>(version.getFirstLevel().get().getClassificationItems())
                    .forEach(version::deleteClassificationItem);
        }
    }

// consider autowired
    public void init(ClassificationVersion version) {
        this.version = version;
        primaryCodeTable.init(eventbus, version, version.getPrimaryLanguage(), classificationFacade);
        primaryLevels.init(eventbus, version, version.getPrimaryLanguage(), classificationFacade);
        editTranslations.initWithAutomaticTranslation(version.getPrimaryLanguage(),
                () -> UI.getCurrent().addWindow(createAutomaticTranslationWindow(version)));
        translationCodeTable.init(eventbus, version, Language.getSecondLanguage(version.getPrimaryLanguage()));
        translationLevels.init(eventbus, version, Language.getSecondLanguage(version.getPrimaryLanguage()));
        showTranslations(false);
        updatePrimaryLanguageLabel();

        importExportComponent = new ImportExportComponent<>(
                context, versionXmlService, importButton, exportButton);
        importExportComponent.init(version, "versjon");
        importExportComponent.setOnCompleteCallback(this::updateView);
        importExportComponent.setClearEntityCallback(this::deleteExistingItemsBeforeImport);

        primaryCodeTable.addToSharedActionListener(shortcutListener);
        translationCodeTable.addToSharedActionListener(shortcutListener);
        log.info("Initializing CodeEditorView version {}", version);

    }

    private AutomaticTranslationWindow createAutomaticTranslationWindow(ClassificationVersion version) {
        return new AutomaticTranslationWindow(version, new TranslationListener(version) {
            @Override
            protected void translationPerformed(Language language) {
                translationCodeTable.init(eventbus, version, language);
            }
        });
    }

    private void showTranslations(boolean show) {
        translationLevels.setVisible(show);
        translationCodeTable.setVisible(show);
    }

    private void updatePrimaryLanguageLabel() {
        log.info("Updating primary language label ()", version);
        primaryLanguage.setValue(version.getPrimaryLanguage().getDisplayName());
    }

    public ClassificationVersion getClassificationVersion() {
        return version;
    }


    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        ComponentUtil.setReadOnlyRecursively(this, readOnly);
        importButton.setEnabled(!readOnly);
        editTranslations.setReadOnly(readOnly);
    }


    public void prepareSave() {
        primaryCodeTable.commitDirtyCodeEditors();
        translationCodeTable.commitDirtyCodeEditors();
    }

    public boolean hasChanges() {
        return primaryLevels.hasChanges() || primaryCodeTable.hasChanges() || translationCodeTable.hasChanges();

    }
}