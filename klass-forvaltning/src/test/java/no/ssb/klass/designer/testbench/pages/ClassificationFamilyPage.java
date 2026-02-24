package no.ssb.klass.designer.testbench.pages;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridLayoutElement;
import com.vaadin.testbench.elements.HorizontalLayoutElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.TextFieldElement;

import no.ssb.klass.designer.ClassificationFamilyView;

public class ClassificationFamilyPage extends AbstractPage {
    private static final int FAVORITE_REMOVE_BUTTON = 0;
    private static final int FAVORITE_LINK_BUTTON = 1;

    public ClassificationFamilyPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void verifyPage() {
        verifyCurrentPageIs(ClassificationFamilyView.NAME);
    }

    public ClassificationListPage selectOmrade(String omradeName) {
        $(ButtonElement.class).caption(omradeName).first().click();
        return new ClassificationListPage(getDriver());
    }

    public int countClassificationsForOmrade(String omradeName) {
        LabelElement sizeLabel = $(LabelElement.class).id(omradeName + "-size");
        return Integer.parseInt(StringUtils.substringBetween(sizeLabel.getText(), "(", ")"));
    }

    public GlobalFilterPage getGlobalFilter() {
        return new GlobalFilterPage(getDriver());
    }

    public SearchResultPage search(String searchString) {
        HorizontalLayoutElement searchBox = $(HorizontalLayoutElement.class).id("search-box");
        searchBox.$(TextFieldElement.class).first().sendKeys(searchString);
        searchBox.$(ButtonElement.class).first().click();
        return new SearchResultPage(getDriver());
    }

    public boolean hasFavourite(String favoriteName) {
        return getFavoriteRow(favoriteName) != null;
    }

    public void removeFavorite(String favoriteName) {
        getFavoriteRow(favoriteName).$(ButtonElement.class).get(FAVORITE_REMOVE_BUTTON).click();
    }

    public void clickFavorite(String favoriteName) {
        getFavoriteRow(favoriteName).$(ButtonElement.class).get(FAVORITE_LINK_BUTTON).click();
    }

    private HorizontalLayoutElement getFavoriteRow(String favoriteName) {
        if (findElements(By.id("favoriteGrid")).isEmpty()) {
            return null;
        }
        GridLayoutElement favoriteGrid = wrap(GridLayoutElement.class, findElement(By.className("favoriteGrid")));
        for (HorizontalLayoutElement favorite : favoriteGrid.$(HorizontalLayoutElement.class).all()) {
            if (favorite.$(ButtonElement.class).caption(favoriteName).exists()) {
                return favorite;
            }
        }
        return null;
    }
}
