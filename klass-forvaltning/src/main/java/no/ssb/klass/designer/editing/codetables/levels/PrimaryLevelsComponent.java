package no.ssb.klass.designer.editing.codetables.levels;

import java.util.ArrayList;
import java.util.Optional;

import com.google.common.eventbus.EventBus;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import no.ssb.klass.core.model.ClassificationItem;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.model.Level;
import no.ssb.klass.core.model.StatisticalClassification;
import no.ssb.klass.designer.editing.codetables.events.CodeDeletedEvent;
import no.ssb.klass.designer.editing.codetables.events.LevelCreatedEvent;
import no.ssb.klass.designer.editing.codetables.events.LevelDeletedEvent;
import no.ssb.klass.designer.editing.codetables.utils.CodeTableUtils;
import no.ssb.klass.designer.service.ClassificationFacade;
import no.ssb.klass.designer.util.ConfirmationDialog;

/**
 * PrimaryLevelsComponent lists levels in primaryLanguage for a version. It allows to create and delete (only last)
 * levels.
 * <p>
 * Generates events for following actions:
 * <ul>
 * <li>CodeDeletedEvent</li>
 * <li>LevelDeletedEvent</li>
 * <li>LevelCreatedEvent</li>
 * <li>OpenHierarchyEvent</li>
 * </ul>
 */
public class PrimaryLevelsComponent extends AbstractLevelsComponent {

    private final Button deleteLevelButton;
    private final Button createLevelButton;
    private ClassificationFacade classificationFacade;

    public PrimaryLevelsComponent() {
        createLevelButton = new Button("Opprett nivå");
        createLevelButton.addStyleName(ValoTheme.BUTTON_SMALL);
        createLevelButton.setDescription("Legg til nivå");
        createLevelButton.addClickListener(event -> createLevel());

        deleteLevelButton = new Button("Slett nivå");
        deleteLevelButton.addStyleName(ValoTheme.BUTTON_SMALL);
        deleteLevelButton.setDescription("Slett siste nivå");
        deleteLevelButton.addClickListener(event -> confirmDeleteLevel());

        HorizontalLayout createDeleteLayout = new HorizontalLayout(createLevelButton, deleteLevelButton);
        createDeleteLayout.setSpacing(true);

        HorizontalLayout levelsLayout = new HorizontalLayout(levels, createDeleteLayout);
        levelsLayout.setComponentAlignment(createDeleteLayout, Alignment.MIDDLE_CENTER);
        Panel panel = new Panel(levelsLayout);
        
        panel.addStyleName(ValoTheme.PANEL_BORDERLESS);
        VerticalLayout root = new VerticalLayout(panel);
        // root.setMargin(new MarginInfo(false, true, false, false));
        root.setExpandRatio(panel, 1);
        root.setSizeFull();
        setCompositionRoot(root);
    }

    public void init(EventBus eventBus, StatisticalClassification version, Language language,
            ClassificationFacade classificationFacade) {
        this.classificationFacade = classificationFacade;
        this.eventBus = eventBus;
        this.version = version;
        this.language = language;
        populateLevels();
    }

    public void hideLevels() {
        deleteLevelButton.setVisible(false);
        createLevelButton.setVisible(false);
        levels.setVisible(false);
    }

    private void confirmDeleteLevel() {
        Optional<Level> lastLevel = version.getLastLevel();
        if (!lastLevel.isPresent()) {
            return;
        }
        String body = "Slette nivå: <b>" + lastLevel.get().getName(language)
                + "</b> og alle elementene som ligger under denne?";
        ConfirmationDialog confWindow = new ConfirmationDialog("Slett", body, (answerYes) -> {
            if (answerYes) {
                deleteLevel(lastLevel.get());
            }
        });
        UI.getCurrent().addWindow(confWindow);
    }

    private void deleteLevel(Level level) {
        for (ClassificationItem classificationItem : new ArrayList<>(level.getClassificationItems())) {
            if (CodeTableUtils.existsReferencesToClassificationItem(classificationFacade, classificationItem)) {
                return;
            }
        }
        for (ClassificationItem classificationItem : new ArrayList<>(level.getClassificationItems())) {
            version.deleteClassificationItem(classificationItem);
            eventBus.post(new CodeDeletedEvent(classificationItem));
        }
        version.deleteLevel(level);
        eventBus.post(new LevelDeletedEvent(level));
    }

    private void createLevel() {
        Level level = version.addNextLevel();
        eventBus.post(new LevelCreatedEvent(level));
    }


}
