package no.ssb.klass.designer.editing.codetables.levels;

import com.google.common.eventbus.EventBus;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.model.Level;
import no.ssb.klass.core.model.StatisticalClassification;

/**
 * ReadyonlyLevelsComponent displays levels for version when making a variant. It does not allow editing as we are not
 * editing the version.
 */
public class ReadonlyLevelsComponent extends AbstractLevelsComponent {
    public ReadonlyLevelsComponent() {
        Panel panel = new Panel(levels);
        panel.addStyleName(ValoTheme.PANEL_BORDERLESS);
        setCompositionRoot(panel);
    }

    public void init(EventBus eventBus, StatisticalClassification version, Language language) {
        this.eventBus = eventBus;
        this.version = version;
        this.language = language;
        populateLevels();
    }

    @Override
    protected TextField createLevelTextField(Level level, Language language) {
        TextField levelTextField = new TextField();
        String name = level.getName(language);
        levelTextField.setValue(name);
        levelTextField.setData(level);
        updateWidth(levelTextField, name);
        levelTextField.setReadOnly(true);
        levelTextField.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
        return levelTextField;
    }
}
