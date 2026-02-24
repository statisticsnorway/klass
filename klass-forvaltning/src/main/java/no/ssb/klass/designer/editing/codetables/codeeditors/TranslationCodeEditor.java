package no.ssb.klass.designer.editing.codetables.codeeditors;

import com.google.common.eventbus.EventBus;
import com.vaadin.ui.themes.ValoTheme;

import no.ssb.klass.core.model.ClassificationItem;
import no.ssb.klass.core.model.ConcreteClassificationItem;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.model.StatisticalClassification;
import no.ssb.klass.designer.editing.codetables.events.TranslationUpdatedEvent;

/**
 * TranslationCodeEditor is a code editor that allows editing translations for a code element. The code itself can not
 * be changed.
 * <p>
 * Generates events for following actions:
 * <ul>
 * <li>CodeTranslationUpdatedEvent</li>
 * </ul>
 */
public class TranslationCodeEditor extends CodeEditor {
    private final ConcreteClassificationItem editItem;

    public TranslationCodeEditor(EventBus eventBus, StatisticalClassification version, ClassificationItem editItem,
            Language language) {
        super(eventBus, version, language);
        code.setValue(editItem.getCode());
        code.setReadOnly(true);
        code.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
        title.setValue(editItem.getOfficialName(language));
        title.focus();

        if (includeShortName) {
            shortname.setValue(editItem.getShortName(language));
            shortname.setVisible(true);
        }
        if (includeNotes) {
            notes.setValue(editItem.getNotes(language));
            notes.setVisible(true);
        }
        this.editItem = (ConcreteClassificationItem) editItem;
        storeInitialStateForDirtyChecking();
    }

    @Override
    protected Object createEvent() {
        editItem.setCode(code.getValue());
        editItem.setOfficialName(title.getValue(), language);
        if (includeShortName) {
            editItem.setShortName(shortname.getValue(), language);
        }
        if (includeNotes) {
            editItem.setNotes(notes.getValue(), language);
        }
        return new TranslationUpdatedEvent(editItem);
    }

    @Override
    protected boolean mustCheckIfAlreadyExists() {
        return false;
    }

    @Override
    protected ClassificationItem cancelEdit() {
        return editItem;
    }

    @Override
    protected boolean showValidityFields() {
        // dates should only be defined in primary language
        return false;
    }
}
