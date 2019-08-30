package no.ssb.klass.designer.testbench;

import static org.junit.Assert.*;

import org.junit.Test;

import no.ssb.klass.designer.testbench.pages.ClassificationFamilyPage;
import no.ssb.klass.designer.testbench.pages.ClassificationListPage;

public class FavoritesTestBench extends AbstractTestBench {
    @Test
    public void favoriteTest() {
        ClassificationFamilyPage homePage = getHomePage();

        // Create favorite
        ClassificationListPage listPage = homePage.selectOmrade("Region");
        listPage.selectAsFavorite(STANDARD_FOR_POLITIDISTRIKT);
        homePage = listPage.tilbakeTilHovedsiden();
        assertEquals(true, homePage.hasFavourite(STANDARD_FOR_POLITIDISTRIKT));

        // Follow favorite link
        homePage.clickFavorite(STANDARD_FOR_POLITIDISTRIKT);
        homePage = listPage.tilbakeTilHovedsiden();

        // Remove favorite
        homePage.removeFavorite(STANDARD_FOR_POLITIDISTRIKT);
        assertEquals(false, homePage.hasFavourite(STANDARD_FOR_POLITIDISTRIKT));
    }
}
