package no.ssb.klass.designer;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.annotation.PrototypeScope;

import com.google.common.collect.ImmutableMap;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ClassResource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.repository.ClassificationFamilySummary;
import no.ssb.klass.designer.MainView.ClassificationFilter;
import no.ssb.klass.designer.components.ClassificationListViewSelection;
import no.ssb.klass.designer.service.ClassificationFacade;
import no.ssb.klass.designer.user.UserContext;
import no.ssb.klass.designer.util.FavoriteUtils;
import no.ssb.klass.designer.util.KlassTheme;
import no.ssb.klass.designer.util.VaadinUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PrototypeScope
@SpringView(name = ClassificationFamilyView.NAME)
@SuppressWarnings("serial")
public class ClassificationFamilyView extends ClassificationFamilyDesign implements FilteringView {

    private static final Logger log = LoggerFactory.getLogger(ClassificationFamilyView.class);

    public static final String NAME = ""; // empty name will make it default view for navigator

    private final ClassificationFilter classificationFilter;

    @Autowired
    private ClassificationFacade classificationFacade;
    @Autowired
    private UserContext userContext;

    public ClassificationFamilyView() {
        this.classificationFilter = VaadinUtil.getKlassState().getClassificationFilter();
        UI.getCurrent().getPage().addBrowserWindowResizeListener(event -> updateGrid(event.getWidth()));
        if(userContext != null) {
            log.info("userContext: {}", userContext);
        }
        log.info("ClassificationFamilyView initialized with userContext: {} and classification facade {}", userContext, classificationFacade);
    }

    private void updateGrid(int width) {
        if (width < 1250) {
            if (familyGrid.getColumns() == 2) {
                return;
            }
            showFamilyButtons(2);
        } else if (width < 1550) {
            if (familyGrid.getColumns() == 3) {
                return;
            }
            showFamilyButtons(3);
        } else {
            if (familyGrid.getColumns() == 4) {
                return;
            }
            showFamilyButtons(4);
        }
    }

    private void showFamilyButtons(int columns) {
        List<ClassificationFamilySummary> classificationFamilySummaries = classificationFacade
                .findAllClassificationFamilySummaries(classificationFilter.getCurrentSection(), classificationFilter
                        .getCurrentClassificationType());
        familyGrid.removeAllComponents();
        familyGrid.setWidthUndefined();
        familyGrid.setColumns(columns);
        for (ClassificationFamilySummary familySummary : classificationFamilySummaries) {
            HorizontalLayout familyButton = createFamilyButton(familySummary);
            familyGrid.addComponent(familyButton);
        }
    }

    private HorizontalLayout createFamilyButton(ClassificationFamilySummary familySummary) {
        Button button = new Button(familySummary.getClassificationFamilyName());
        button.setData(familySummary.getId().toString());
        button.addStyleName("borderless link-button family-icon");
        button.addClickListener(e -> VaadinUtil.navigateTo(ClassificationListView.NAME, ImmutableMap.of(
                ClassificationListView.PARAM_FAMILY_ID, (String) e.getButton().getData())));
        button.setIcon(new ClassResource(familySummary.getIconPath()));
        Label sizeLabel = new Label(" (" + familySummary.getNumberOfClassifications() + ")");
        sizeLabel.setId(familySummary.getClassificationFamilyName() + "-size");
        HorizontalLayout layout = new HorizontalLayout(button, sizeLabel);
        sizeLabel.setWidthUndefined();
        button.setWidthUndefined();
        layout.setWidthUndefined();
        layout.setComponentAlignment(sizeLabel, Alignment.MIDDLE_LEFT);
        return layout;
    }

    @Override
    public void enter(ViewChangeEvent event) {
        int width = UI.getCurrent().getPage().getBrowserWindowWidth();
        familyGrid.setColumns(100); // will force update in updateGrid
        updateGrid(width);
        showFavorites();
    }

    private void showFavorites() {
        if (userContext.hasFavorites()) {
            favoriteIntro.setVisible(false);
            favoriteLayout.setVisible(true);
            favoriteGrid.removeAllComponents();
            for (ClassificationSeries favorite : userContext.getFavorites()) {
                favoriteGrid.addComponent(createFavoriteList(favorite));
            }
        } else {
            favoriteLayout.setVisible(false);
            favoriteIntro.setVisible(true);
        }
    }

    private HorizontalLayout createFavoriteList(ClassificationSeries favorite) {
        Button button = new Button(FavoriteUtils.getFavoriteIcon(userContext.hasFavorites()));
        button.setData(favorite);
        button.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        button.setDescription("Fjern favoritt");
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                Button pushedFavorite = event.getButton();
                ClassificationSeries cs = (ClassificationSeries) pushedFavorite.getData();
                boolean isFavorite = userContext.toggleFavorite(cs);
                pushedFavorite.setIcon(FavoriteUtils.getFavoriteIcon(isFavorite));
                showFavorites();
            }
        });
        Button favoriteLink = new Button(favorite.getNameInPrimaryLanguage());
        favoriteLink.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        favoriteLink.addStyleName(KlassTheme.BUTTON_AS_LINK);
        favoriteLink.addClickListener(e -> favoriteClickHandler(favorite));

        HorizontalLayout layout = new HorizontalLayout(button, favoriteLink);
        layout.addStyleName("favorite");
        return layout;
    }

    private void favoriteClickHandler(ClassificationSeries classification) {
        VaadinUtil.getKlassState().setClassificationListViewSelection(ClassificationListViewSelection
                .newClassificationListViewSelection(classification));
        VaadinUtil.navigateTo(ClassificationListView.NAME, ImmutableMap.of(
                ClassificationListView.PARAM_FAMILY_ID, classification.getClassificationFamily().getId().toString()));
    }
}
