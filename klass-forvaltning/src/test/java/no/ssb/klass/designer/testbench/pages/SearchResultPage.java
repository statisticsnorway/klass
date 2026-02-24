package no.ssb.klass.designer.testbench.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.VerticalLayoutElement;

public class SearchResultPage extends AbstractPage {
    protected SearchResultPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void verifyPage() {
        verifyCurrentPageIs("search");
    }

    public int countSearchResults() {
        return findElements(By.className("search-result")).size();
    }

    public ClassificationListPage selectSearchResult(String classificationName) {
        VerticalLayoutElement searchResult = wrap(VerticalLayoutElement.class, findElement(By.className(
                "search-result")));
        searchResult.$(ButtonElement.class).first().click();
        return new ClassificationListPage(getDriver());
    }
}
