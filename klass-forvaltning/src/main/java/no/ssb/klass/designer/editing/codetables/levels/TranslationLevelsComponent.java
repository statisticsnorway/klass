package no.ssb.klass.designer.editing.codetables.levels;

import com.google.common.eventbus.EventBus;
import com.vaadin.ui.Panel;
import com.vaadin.ui.themes.ValoTheme;

import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.model.StatisticalClassification;

/**
 * TranslationLevelsComponent lists levels for secondaryLanguage and thirdLanguage of a version. It only allows editing
 * name of level for current language, i.e. not add or delete any levels.
 */
public class TranslationLevelsComponent extends AbstractLevelsComponent {
    public TranslationLevelsComponent() {
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
}
