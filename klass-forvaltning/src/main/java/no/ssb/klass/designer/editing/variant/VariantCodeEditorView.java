package no.ssb.klass.designer.editing.variant;

import java.util.ArrayList;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.vaadin.spring.annotation.PrototypeScope;

import com.google.common.eventbus.EventBus;
import com.vaadin.data.Property;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.UI;

import no.ssb.klass.forvaltning.converting.xml.ClassificationVariantXmlService;
import no.ssb.klass.core.model.ClassificationVariant;
import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.designer.editing.EditingState;
import no.ssb.klass.designer.editing.HasEditingState;
import no.ssb.klass.designer.editing.TranslationListener;
import no.ssb.klass.designer.editing.components.ImportExportComponent;
import no.ssb.klass.designer.listeners.SharedEscapeShortcutListener;
import no.ssb.klass.designer.service.ClassificationFacade;
import no.ssb.klass.designer.util.ComponentUtil;
import no.ssb.klass.designer.windows.AutomaticTranslationWindow;

@SpringComponent
@PrototypeScope
public class VariantCodeEditorView extends VariantCodeEditorDesign implements HasEditingState {

    private static final String SINGLE_ELEMENTS = "Enkeltkoder";
    private static final String ELEMENTS_WITH_CHILDREN = "Koder med underelementer";
    private EventBus eventbus;
    private ClassificationVariant variant;
    private ClassificationVersion version;
    private ImportExportComponent<ClassificationVariant> importExportComponent;

    @Autowired
    private ClassificationVariantXmlService xmlService;

    @Autowired
    private ClassificationFacade classificationFacade;

    @Autowired
    private ApplicationContext applicationContext;

    private SharedEscapeShortcutListener shortcutListener;

    public VariantCodeEditorView() {
        shortcutListener = new SharedEscapeShortcutListener();
        eventbus = new EventBus("variant");
        eventbus.register(variantCodeTable);
        eventbus.register(variantLevels);
        eventbus.register(translatableLevels);
        eventbus.register(originalVersion);
        eventbus.register(translationCodeTable);
        selectEditLanguageOrVariant.addEditLanguagesListener(this::switchCodeTables);
        selectEditLanguageOrVariant.addLanguageChangeListener(this::switchLanguage);

    }

    private void configureDragDrop() {
        originalVersion.getTable().setMultiSelect(true);

        dragAndDropMode.removeAllItems();
        dragAndDropMode.addItem(SINGLE_ELEMENTS);
        dragAndDropMode.addItem(ELEMENTS_WITH_CHILDREN);
        dragAndDropMode.addValueChangeListener(this::dragDropModeChanged);
        dragAndDropMode.select(SINGLE_ELEMENTS);
    }

    private void dragDropModeChanged(Property.ValueChangeEvent valueChangeEvent) {
        variantCodeTable.setDragIncludeChildren(!Objects.equals(valueChangeEvent.getProperty().getValue(),
                SINGLE_ELEMENTS));
    }

    private void switchLanguage(Language language) {
        translationCodeTable.commitDirtyCodeEditors();
        translationCodeTable.init(eventbus, variant, language);
        translatableLevels.init(eventbus, variant, language);
    }

    private void switchCodeTables(boolean isLanguagesVisible) {
        originalVersion.setVisible(!isLanguagesVisible);
        originalVersionName.setVisible(!isLanguagesVisible);
        translationCodeTable.setVisible(isLanguagesVisible);
        versionLevels.setVisible(!isLanguagesVisible);
        translatableLevels.setVisible(isLanguagesVisible);

    }

    public void init(ClassificationVariant variant) {
        this.variant = variant;
        this.version = reloadToAvoidLazyInitializationException(variant.getClassificationVersion());

        primaryLanguage.setValue(variant.getPrimaryLanguage().getDisplayName());
        variantCodeTable.init(eventbus, variant, version, variant.getPrimaryLanguage(), classificationFacade);
        variantLevels.init(eventbus, variant, variant.getPrimaryLanguage(), classificationFacade);
        translatableLevels.init(eventbus, variant, Language.getSecondLanguage(variant.getPrimaryLanguage()));
        variantName.setValue(variant.getNameInPrimaryLanguage());



        originalVersion.init(eventbus, version, variant.getPrimaryLanguage());
        originalVersion.markAsReferenced(variant.getAllClassificationItems());
        originalVersionName.setValue(version.getNameInPrimaryLanguage());
        versionLevels.init(eventbus, version, variant.getPrimaryLanguage());

        selectEditLanguageOrVariant.initWithAutomaticTranslation(variant.getPrimaryLanguage(), () -> UI.getCurrent()
                .addWindow(createAutomaticTranslationWindow(variant)));
        translationCodeTable.init(eventbus, variant, Language.getSecondLanguage(variant.getPrimaryLanguage()));
        switchCodeTables(false);

        importExportComponent = new ImportExportComponent<>(
                applicationContext, xmlService, importButton, exportButton);
        importExportComponent.init(variant, "Variant");
        importExportComponent.setOnCompleteCallback(this::updateView);
        importExportComponent.setClearEntityCallback(this::clearBeforeImport);
        configureDragDrop();
        originalVersion.addToSharedActionListener(shortcutListener);
        variantCodeTable.addToSharedActionListener(shortcutListener);
        translationCodeTable.addToSharedActionListener(shortcutListener);

    }

    private void clearBeforeImport(ClassificationVariant variant) {
        new ArrayList<>(variant.getAllConcreteClassificationItems())
                .forEach(variant::deleteClassificationItem);

    }

    @Override
    public void restorePreviousEditingState(EditingState editingState) {
        if (editingState.isLanguageTabVisible()) {
            selectEditLanguageOrVariant.showLanguagesAndNotify(true, editingState.isSecondNotThirdLanguageVisible());
        }
    }

    @Override
    public EditingState currentEditingState() {
        return EditingState.newCodeEditorEditingState(selectEditLanguageOrVariant.isLanguagesVisible(),
                selectEditLanguageOrVariant.isSecondNotThirdLanguageSelected());
    }

    @Override
    public boolean hasChanges() {
        return variantCodeTable.hasChanges() || originalVersion.hasChanges() || translationCodeTable.hasChanges();
    }

    private AutomaticTranslationWindow createAutomaticTranslationWindow(ClassificationVariant variant) {
        return new AutomaticTranslationWindow(variant, new TranslationListener(variant) {
            @Override
            protected void translationPerformed(Language language) {
                translationCodeTable.init(eventbus, variant, language);
            }
        });
    }

    private ClassificationVersion reloadToAvoidLazyInitializationException(ClassificationVersion version) {
        return classificationFacade.getRequiredClassificationVersion(version.getId());
    }

    private void updateView(boolean success) {
        variantCodeTable.refresh();
        translationCodeTable.refresh();
        originalVersion.refresh();
        if (success) {
            variantCodeTable.getTable().markAsModified();
        }
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        ComponentUtil.setReadOnlyRecursively(this, readOnly);
        selectEditLanguageOrVariant.setReadOnly(readOnly);
        importButton.setEnabled(!readOnly);
    }

    public void prepareSave() {
        variantCodeTable.commitDirtyCodeEditors();
        translationCodeTable.commitDirtyCodeEditors();
    }
}
