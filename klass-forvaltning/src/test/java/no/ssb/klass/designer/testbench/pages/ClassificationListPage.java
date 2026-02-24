package no.ssb.klass.designer.testbench.pages;

import java.time.LocalDate;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.elements.CustomComponentElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.testbench.elements.TableRowElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.testbench.elements.WindowElement;

import no.ssb.klass.core.util.DateRange;
import no.ssb.klass.designer.ClassificationListView;

public class ClassificationListPage extends AbstractPage {

    protected ClassificationListPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void verifyPage() {
        verifyCurrentPageIs(ClassificationListView.NAME);
    }

    public void selectClassification(String name) {
        TableElement classifications = selectTableWithDomId("classifications");
        TableRowElement classification = findRequiredRowInTable(classifications,
                ClassificationTable.NAME_COLUMN, name);
        classification.getCell(ClassificationTable.NAME_COLUMN).click();
    }

    public void selectVersion(String name) {
        TableElement versions = selectTableWithDomId("versions");
        TableRowElement version = findRequiredRowInTable(versions, VersionTable.NAME_COLUMN, name);

        version.getCell(VersionTable.NAME_COLUMN).click();
    }

    public void selectVariant(String name) {
        TableElement variants = selectTableWithDomId("variants");
        TableRowElement variant = findRequiredRowInTable(variants, VariantTable.NAME_COLUMN, name);
        variant.getCell(VariantTable.NAME_COLUMN).click();
    }

    private TableElement selectTableWithDomId(String domId) {
        return $(CustomComponentElement.class).id(domId).$(TableElement.class).first();
    }

    public void selectAsFavorite(String name) {
        TableElement classifications = selectTableWithDomId("classifications");
        TableRowElement classification = findRequiredRowInTable(classifications, ClassificationTable.NAME_COLUMN,
                name);
        classification.getCell(ClassificationTable.FAVORITE_COLUMN).$(ButtonElement.class).first().click();
    }

    public ClassificationFamilyPage tilbakeTilHovedsiden() {
        $(ButtonElement.class).caption("Tilbake til hovedsiden").first().click();
        return new ClassificationFamilyPage(getDriver());
    }

    public int countClassifications() {
        TableElement classifications = selectTableWithDomId("classifications");
        return findRows(classifications).size();
    }

    public int countVersions() {
        TableElement versions = selectTableWithDomId("versions");
        return findRows(versions).size();
    }

    public int countVariants() {
        TableElement variants = selectTableWithDomId("variants");
        return findRows(variants).size();
    }

    public int countCorrespondenceTables() {
        return countVariants();
    }

    public void deleteVariant(String name) {
        TableElement variants = selectTableWithDomId("variants");
        findRequiredRowInTable(variants, VariantTable.NAME_COLUMN, name)
                .getCell(VariantTable.DELETE_BUTTON_COLUMN).$(ButtonElement.class).first().click();
        acceptDeleteConfirmationBox();
    }

    public void deleteCorrespondenceTable(String name) {
        deleteVariant(name);
    }

    public void deleteVersion(String name) {
        TableElement versions = selectTableWithDomId("versions");
        findRequiredRowInTable(versions, VersionTable.NAME_COLUMN, name)
                .getCell(VersionTable.DELETE_BUTTON_COLUMN).$(ButtonElement.class).first().click();
        acceptDeleteConfirmationBox();
    }

    public void deleteClassification(String name) {
        TableElement classifications = selectTableWithDomId("classifications");
        findRequiredRowInTable(classifications, ClassificationTable.NAME_COLUMN, name)
                .getCell(ClassificationTable.DELETE_COLUMN).$(ButtonElement.class).first().click();
        acceptDeleteConfirmationBox();
    }

    private void acceptDeleteConfirmationBox() {
        $(WindowElement.class).$(ButtonElement.class).caption("Slett").first().click();
    }

    public void filterClassifications(String filterText) {
        getClassificationFilterBox().sendKeys(filterText);
        sleepSeconds(2);
    }

    public void clearClassificationsFilter() {
        TextFieldElement filterBox = getClassificationFilterBox();
        clearKeys(filterBox);
        sleepSeconds(2);
    }

    private TextFieldElement getClassificationFilterBox() {
        return $(CustomComponentElement.class).id("classifications").$(TextFieldElement.class).first();
    }

    public GlobalFilterPage getGlobalFilter() {
        return new GlobalFilterPage(getDriver());
    }

    public EditClassificationPage openClassification(String classificationName) {
        TableElement classifications = selectTableWithDomId("classifications");
        findRequiredRowInTable(classifications, ClassificationTable.NAME_COLUMN, classificationName)
                .getCell(ClassificationTable.OPEN_COLUMN).$(ButtonElement.class).first().click();
        return new EditClassificationPage(getDriver());
    }

    public EditVersionPage openVersion(String versionName) {
        TableElement versions = selectTableWithDomId("versions");
        findRequiredRowInTable(versions, VersionTable.NAME_COLUMN, versionName)
                .getCell(VersionTable.OPEN_COLUMN).$(ButtonElement.class).first().click();
        return new EditVersionPage(getDriver());
    }

    public String getSelectedClassification() {
        TableElement classifications = selectTableWithDomId("classifications");
        return wrap(TableRowElement.class, classifications.findElement(By.className("v-selected"))).getCell(
                ClassificationTable.NAME_COLUMN).getText();
    }

    public String getSelectedVersion() {
        TableElement versions = selectTableWithDomId("versions");
        return wrap(TableRowElement.class, versions.findElement(By.className("v-selected"))).getCell(
                VersionTable.NAME_COLUMN).getText();
    }

    public EditCorrespondenceTablePage openCorrespondenceTable(String correspondenceTablesName) {
        return openVariantOrCorrespondenceTable(correspondenceTablesName, EditCorrespondenceTablePage.class);
    }

    private <T extends AbstractPage> T openVariantOrCorrespondenceTable(String name, Class<T> pageType) {
        TableElement variantOrCorrespondenceTable = selectTableWithDomId("variants");
        findRequiredRowInTable(variantOrCorrespondenceTable, VariantTable.NAME_COLUMN, name)
                .getCell(VariantTable.OPEN_COLUMN).$(ButtonElement.class).first().click();
        return createInstance(pageType);
    }

    public String getSelectedCorrespondenceTable() {
        TableElement correspondenceTables = selectTableWithDomId("variants");
        return wrap(TableRowElement.class, correspondenceTables.findElement(By.className("v-selected")))
                .getCell(VariantTable.NAME_COLUMN).getText();
    }

    public CreateVersionPage createVersion(DateRange dateRange) {
        $(CustomComponentElement.class).id("versions").$(ButtonElement.class).get(
                VersionTable.CREATE_VERSION_BUTTON)
                .click();

        WindowElement window = $(WindowElement.class).first();
        LocalDate toInclusive = dateRange.getTo().minusDays(1);
        clearAndSendKeys(window.$(TextFieldElement.class).get(0), "" + dateRange.getFrom().getMonthValue());
        clearAndSendKeys(window.$(TextFieldElement.class).get(1), "" + dateRange.getFrom().getYear());
        clearAndSendKeys(window.$(TextFieldElement.class).get(2), "" + toInclusive.getMonthValue());
        clearAndSendKeys(window.$(TextFieldElement.class).get(3), "" + toInclusive.getYear());
        window.$(ButtonElement.class).caption("Opprett").first().click();
        return new CreateVersionPage(driver);
    }

    public CreateClassificationPage createClassification() {
        $(CustomComponentElement.class).id("classifications").$(ButtonElement.class).get(
                ClassificationTable.CREATE_CLASSIFICATION_BUTTON).click();
        return new CreateClassificationPage(getDriver());
    }

    public CreateCorrespondenceTablePage createCorrespondenceTable(String classification,
            String version) {
        $(CustomComponentElement.class).id("variants").$(ButtonElement.class).get(
                VariantTable.CREATE_CORRESPONDENCE_TABLE_BUTTON).click();
        WindowElement window = $(WindowElement.class).first();
        window.$(ComboBoxElement.class).caption("Korrespondanse til").first().selectByText(classification);
        window.$(ComboBoxElement.class).caption("Versjon").first().selectByText(version);
        window.$(ComboBoxElement.class).caption("Til nivå").first().selectByText("Alle nivåer");
        window.$(ButtonElement.class).caption("Opprett").first().click();
        return new CreateCorrespondenceTablePage(getDriver());
    }

    public CreateVariantPage createVariant(String variantName) {
        $(CustomComponentElement.class).id("variants").$(ButtonElement.class).get(VariantTable.CREATE_VARIANT_BUTTON)
                .click();
        WindowElement window = $(WindowElement.class).first();
        window.$(TextFieldElement.class).caption("Navn").first().sendKeys(variantName);
        window.$(ButtonElement.class).caption("Opprett").first().click();
        return new CreateVariantPage(driver);
    }

    public EditVariantPage openVariant(String variantName) {
        return openVariantOrCorrespondenceTable(variantName, EditVariantPage.class);
    }

    private static class ClassificationTable {
        public static final int FAVORITE_COLUMN = 0;
        public static final int NAME_COLUMN = 1;
        public static final int OPEN_COLUMN = 2;
        public static final int DELETE_COLUMN = 3;

        public static final int CREATE_CLASSIFICATION_BUTTON = 0;
    }

    private static class VersionTable {
        public static final int NAME_COLUMN = 0;
        public static final int OPEN_COLUMN = 1;
        public static final int DELETE_BUTTON_COLUMN = 2;

        public static final int CREATE_VERSION_BUTTON = 1;
    }

    private static class VariantTable {
        public static final int NAME_COLUMN = 0;
        public static final int OPEN_COLUMN = 1;
        public static final int DELETE_BUTTON_COLUMN = 2;

        public static final int CREATE_CORRESPONDENCE_TABLE_BUTTON = 0;
        public static final int CREATE_VARIANT_BUTTON = 1;
    }
}
