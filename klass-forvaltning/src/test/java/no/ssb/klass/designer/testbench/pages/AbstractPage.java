package no.ssb.klass.designer.testbench.pages;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.TestBenchTestCase;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.testbench.elements.TableRowElement;

/**
 * A Page represents one screen of the web application.
 * <p>
 * This implements the Page Object pattern.
 * <p>
 * Excerpt of pattern: Pages can be thought of as facing in two directions simultaneously. Facing towards the developer
 * of a test, they represent the services offered by a particular page. Facing away from the developer, they should be
 * the only thing that has a deep knowledge of the structure of the HTML of a page (or part of a page) It's simplest to
 * think of the methods on a Page Object as offering the "services" that a page offers rather than exposing the details
 * and mechanics of the page.
 * <p>
 * Note from the excerpt above that a page can be part of a page.
 *
 *
 * @author karsten.vileid
 *
 */
abstract class AbstractPage extends TestBenchTestCase {
    protected AbstractPage(WebDriver driver) {
        setDriver(driver);
        verifyPage();
    }

    /**
     * Assert that browser is at the expected page. Subclasses is expecte to use {@link #verifyCurrentPageHas(By)} for
     * verification of pages and {@link #verifyCurrentPageIs(String)} for verification of subpages.
     * 
     * @throws IllegalStateException
     *             if not currently on correct page
     */
    protected abstract void verifyPage();

    /**
     * Verifies that browser is at currently at the Vaadin page (view)
     */
    protected void verifyCurrentPageIs(String expectedPage) {
        String currentPage = getCurrentPage();
        if (!expectedPage.equals(currentPage)) {
            throw new IllegalStateException("Current page: " + currentPage + " is not same as expected page: "
                    + expectedPage);
        }
    }

    private String getCurrentPage() {
        String url = getDriver().getCurrentUrl();
        url = StringUtils.substringBefore(url, "?");
        url = StringUtils.stripEnd(url, "/");
        String currentPage = "";
        if (url.contains("#")) {
            currentPage = StringUtils.substringAfter(url, "#");
            currentPage = StringUtils.stripStart(currentPage, "!");
        }
        return currentPage;
    }

    /**
     * To be used by subpages, where page represents only a part of a page (vaadin view) in the browser.
     */
    protected void verifyCurrentPageHas(By by) {
        if (findElements(by).isEmpty()) {
            throw new IllegalStateException("Current page does not contain elements selected by: " + by);
        }
    }

    /**
     * Clears text in textbox and enters text in one operation.
     * <p>
     * Useful when there is validation on a textbox which hinders the normal usage of; <br/>
     * webElement.clear() <br/>
     * webElement.sendKeys(text) <br/>
     * where can get problem when textbox is first cleared, and validation disallows no input.
     *
     * @param webElement
     * @param text
     */
    protected void clearAndSendKeys(WebElement webElement, String text) {
        clearKeys(webElement);
        webElement.sendKeys(text);
    }

    protected void clearKeys(WebElement webElement) {
        webElement.sendKeys(selectAllKey());
        webElement.sendKeys(Keys.DELETE);
    }

    private String selectAllKey() {
        String os = System.getProperty("os.name");
        if (os.equals("Mac OS X")) {
            return Keys.chord(Keys.COMMAND, "a");
        } else {
            return Keys.chord(Keys.CONTROL, "a");
        }
    }

    protected List<TableRowElement> findRows(TableElement table) {
        List<TableRowElement> rows = new ArrayList<>();
        for (WebElement webElement : table.findElements(By.xpath(".//table[@class=\"v-table-table\"]/tbody/tr"))) {
            rows.add(wrap(TableRowElement.class, webElement));
        }
        return rows;
    }

    /**
     * Finds row in table having row[column]=text. If no row found throws exception
     */
    protected TableRowElement findRequiredRowInTable(TableElement table, int column, String text) {
        for (TableRowElement row : findRows(table)) {
            if (text.equals(row.getCell(column).getText())) {
                return row;
            }
        }
        throw new IllegalArgumentException("Found no row with text: " + text);
    }

    protected void sleepSeconds(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    protected <T extends AbstractPage> T createInstance(Class<T> pageType) {
        try {
            return pageType.getDeclaredConstructor(WebDriver.class).newInstance(getDriver());
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
