package no.ssb.klass.designer.editing.codetables.codeeditors;

import com.google.common.eventbus.EventBus;

import no.ssb.klass.core.model.ClassificationItem;
import no.ssb.klass.core.model.ConcreteClassificationItem;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.model.Level;
import no.ssb.klass.core.model.StatisticalClassification;
import no.ssb.klass.core.util.TimeUtil;
import no.ssb.klass.core.util.Translatable;
import no.ssb.klass.designer.editing.codetables.events.CodeCreatedEvent;

/**
 * NewCodeEditor is a code editor that allows adding new codes. Only used in PrimaryCodeTable, since only
 * PrimaryCodeTable may add new codes.
 * <p>
 * Generates events for following actions:
 * <ul>
 * <li>CodeCreatedEvent</li>
 * </ul>
 */
public class NewCodeEditor extends CodeEditor {
    private static final Object ROOT_OBJECT = new Object();
    private final Object itemId;
    private final Level level;
    private final ClassificationItem parent;
    private final StatisticalClassification version;

    public NewCodeEditor(EventBus eventBus, StatisticalClassification version, ClassificationItem parent, Level level,
            Language language) {
        super(eventBus, version, language);
        this.level = level;
        this.parent = parent;
        this.itemId = parent != null ? parent : ROOT_OBJECT;
        this.version = version;
        storeInitialStateForDirtyChecking();
    }

    @Override
    protected Object createEvent() {
        ClassificationItem classificationItem = createClassificationItem(parent);
        version.addClassificationItem(classificationItem, level.getLevelNumber(), parent);
        clearInput();
        displayHiddenFields(true);
        code.focus();
        return new CodeCreatedEvent(classificationItem, itemId);
    }

    private void clearInput() {
        code.clear();
        title.clear();
        if (includeShortName) {
            shortname.clear();
        }
        if (includeNotes) {
            notes.clear();
        }
        if (showValidityFields()) {
            validFrom.clear();
            validTo.clear();
        }
        storeInitialStateForDirtyChecking();
    }

    private void displayHiddenFields(boolean display) {
        if (includeShortName) {
            shortname.setVisible(display);
        }
        if (includeNotes) {
            notes.setVisible(display);
        }
        if (showValidityFields()) {
            validityLayout.setVisible(display);
        }
    }

    @Override
    protected boolean mustCheckIfAlreadyExists() {
        return true;
    }

    public Object getItemId() {
        return itemId;
    }

    private ClassificationItem createClassificationItem(ClassificationItem parent) {
        ConcreteClassificationItem newItem = new ConcreteClassificationItem(code.getValue(), Translatable.create(title
                .getValue(), language), Translatable.empty());
        if (includeShortName) {
            newItem.setShortName(shortname.getValue(), language);
        }
        if (includeNotes) {
            newItem.setNotes(notes.getValue(), language);
        }
        if (showValidityFields()) {
            newItem.setValidFrom(TimeUtil.toLocalDate(validFrom.getValue()));
            newItem.setValidTo(TimeUtil.toLocalDate(validTo.getValue()));
        }
        return newItem;
    }

    @Override
    protected ClassificationItem cancelEdit() {
        clearInput();
        displayHiddenFields(false);
        return null;
    }

    public ClassificationItem getParentClassificationItem() {
        return parent;
    }

    public Level getLevel() {
        return level;
    }
}
