package no.ssb.klass.designer.testbench.pages;

import static com.google.common.base.Preconditions.*;

import org.openqa.selenium.WebDriver;

import com.vaadin.testbench.elements.AccordionElement;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.testbench.elements.TableRowElement;
import com.vaadin.testbench.elements.WindowElement;

/**
 * Represents GUI views that implements {@link no.ssb.klass.designer.EditingView}, such as creating and editing
 * classification versions
 */
public abstract class AbstractEditingPage extends AbstractPage {

    protected AbstractEditingPage(WebDriver driver) {
        super(driver);
    }

    public BreadcrumbPage getBreadcrumb() {
        return new BreadcrumbPage(getDriver());
    }

    public ClassificationListPage clickAvbryt() {
        return clickAvbryt(false);
    }

    public ClassificationListPage clickAvbrytAndAcceptConfirmationBox() {
        return clickAvbryt(true);
    }

    private ClassificationListPage clickAvbryt(boolean expectConfirmationBox) {
        $(ButtonElement.class).caption("avbryt").first().click();
        if (expectConfirmationBox) {
            $(WindowElement.class).$(ButtonElement.class).caption("Ja forkast endringene").first().click();
        }
        return new ClassificationListPage(getDriver());
    }

    public void saveAndHandleChangelog() {
        $(ButtonElement.class).caption("Lagre").first().click();
        $(WindowElement.class).$(ButtonElement.class).caption("ikke relevant").first().click();
    }

    public <T extends AbstractPage> T save(Class<T> pageClass) {
        save();
        return createInstance(pageClass);
    }

    public final void save() {
        $(ButtonElement.class).caption("Lagre").first().click();
    }

    public <T extends AbstractPage> T selectMetadataTab(Class<T> pageType) {
        $(AccordionElement.class).first().openTab(0);
        return createInstance(pageType);
    }

    public <T extends AbstractPage> T selectCodesTab(Class<T> pageType) {
        $(AccordionElement.class).first().openTab(1);
        return createInstance(pageType);
    }

    protected final int countRowsInTable(TableElement table) {
        return findRows(table).size();
    }

    protected TableRowElement findRequiredRowWithCodeAndTitle(String codeAndTitle, TableElement table) {
        final int nameColumn = 0;
        checkArgument(codeAndTitle.contains(" - "), "Must input both code and title separated with ' - '");
        return findRequiredRowInTable(table, nameColumn, codeAndTitle);
    }
}
