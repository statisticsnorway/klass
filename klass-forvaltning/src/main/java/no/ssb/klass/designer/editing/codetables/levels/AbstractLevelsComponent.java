package no.ssb.klass.designer.editing.codetables.levels;

import java.util.Optional;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.model.Level;
import no.ssb.klass.core.model.StatisticalClassification;
import no.ssb.klass.designer.editing.codetables.events.LevelCreatedEvent;
import no.ssb.klass.designer.editing.codetables.events.LevelDeletedEvent;
import no.ssb.klass.designer.util.VaadinUtil;

/**
 * AbstractCodeTable lists levels for a version. It subscribes to events such as LevelDeletedEvent, LevelCreatedEvent.
 */
public abstract class AbstractLevelsComponent extends CustomComponent {
    private static final String LEVEL_TEXTFIELD = "level-textfield";
    protected final HorizontalLayout levels;
    protected StatisticalClassification version;
    protected EventBus eventBus;
    protected Language language;
    protected boolean hasChanged;

    public AbstractLevelsComponent() {
        levels = new HorizontalLayout();
    }

    public void refresh() {
        populateLevels();
    }

    protected final void populateLevels() {
        levels.removeAllComponents();
        Optional<Level> currentLevel = version.getFirstLevel();
        while (currentLevel.isPresent()) {
            levels.addComponent(createLevelTextField(currentLevel.get(), language));
            currentLevel = version.getNextLevel(currentLevel.get());
        }
        hasChanged = false;
    }

    protected TextField createLevelTextField(Level level, Language language) {
        TextField levelTextField = new TextField();
        String name = level.getName(language);
        levelTextField.setValue(name);
        levelTextField.setData(level);
        updateWidth(levelTextField, name);
        levelTextField.addStyleName(LEVEL_TEXTFIELD);
        levelTextField.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
        levelTextField.addValueChangeListener(this::changeLevelName);
        return levelTextField;
    }

    private void changeLevelName(ValueChangeEvent event) {
        TextField levelTextField = (TextField) event.getProperty();
        Level level = (Level) levelTextField.getData();
        String name = levelTextField.getValue();
        level.setName(name, language);
        updateWidth(levelTextField, name);
        hasChanged = true;
    }

    protected void updateWidth(TextField levelTextField, String name) {
        if (name.length() > 5) {
            levelTextField.setWidth(Math.max(5, name.length() * 0.65f), Unit.EM);
        } else {
            levelTextField.setWidth(5, Unit.EM);
        }
    }

    @Subscribe
    public void levelDeleted(LevelDeletedEvent event) {
        levels.removeComponent(VaadinUtil.getLastComponent(levels).get());
        hasChanged = true;
    }

    @Subscribe
    public void levelCreated(LevelCreatedEvent event) {
        TextField levelTextField = createLevelTextField(event.getCreatedLevel(), language);
        levels.addComponent(levelTextField);
        hasChanged = true;
    }

    public boolean hasChanges() {
        return hasChanged;
    }
}
