package no.ssb.klass.designer.testbench.pages;

import org.openqa.selenium.WebDriver;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.HorizontalLayoutElement;

public class BreadcrumbPage extends AbstractPage {
    private static final int HOME_BUTTON = 0;
    private static final int CLASSIFICATION_FAMILY_BUTTON = 1;
    private static final int CLASSIFICATION_BUTTON = 2;
    private static final int CLASSIFICATION_VERSION_BUTTON = 3;
    private static final int CLASSIFICATION_VARIANT_BUTTON = 4;
    private static final int CORRESPONDENCE_TABLE_BUTTON = 4;

    protected BreadcrumbPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void verifyPage() {
        verifyCurrentPageHas(By.className("breadcrumb-row"));
    }

    public ClassificationFamilyPage clickHome() {
        clickBreadcrumbButton(HOME_BUTTON);
        return new ClassificationFamilyPage(getDriver());
    }

    public ClassificationListPage clickOmrade() {
        clickBreadcrumbButton(CLASSIFICATION_FAMILY_BUTTON);
        return new ClassificationListPage(getDriver());
    }

    public ClassificationListPage clickClassification() {
        clickBreadcrumbButton(CLASSIFICATION_BUTTON);
        return new ClassificationListPage(getDriver());
    }

    public ClassificationListPage clickVersion() {
        clickBreadcrumbButton(CLASSIFICATION_VERSION_BUTTON);
        return new ClassificationListPage(getDriver());
    }

    public ClassificationListPage clickCorrespondenceTable() {
        clickBreadcrumbButton(CORRESPONDENCE_TABLE_BUTTON);
        return new ClassificationListPage(getDriver());
    }

    public ClassificationListPage clickVariant() {
        clickBreadcrumbButton(CLASSIFICATION_VARIANT_BUTTON);
        return new ClassificationListPage(getDriver());
    }

    private void clickBreadcrumbButton(int button) {
        getBreadcrumbRow().$(ButtonElement.class).get(button).click();
    }

    private HorizontalLayoutElement getBreadcrumbRow() {
        return wrap(HorizontalLayoutElement.class, findElement(By.className("breadcrumb-row")));
    }
}
