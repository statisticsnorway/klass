package no.ssb.klass.designer.testbench.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.AccordionElement;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.CustomComponentElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.testbench.elements.TableRowElement;
import com.vaadin.testbench.elements.TextAreaElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.testbench.elements.TreeTableElement;
import com.vaadin.testbench.elements.WindowElement;

import no.ssb.klass.core.model.Language;
import no.ssb.klass.designer.editing.correspondencetable.EditCorrespondenceTableView;

public class EditCorrespondenceTablePage extends AbstractEditingPage {

    protected EditCorrespondenceTablePage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void verifyPage() {
        verifyCurrentPageIs(EditCorrespondenceTableView.NAME);
    }

    public EditChangeTableCodesSubpage selectCodesTabForChangeTable() {
        $(AccordionElement.class).first().openTab(1);
        return new EditChangeTableCodesSubpage(getDriver());
    }

    public void selectEditLanguages() {
        $(ButtonElement.class).caption("Rediger språk").first().click();
    }

    public void selectLanguage(Language language) {
        $(ButtonElement.class).caption(language.getDisplayName()).first().click();
    }

    /**
     * Represents the accordion tab where metadata for a correspondenceTable may be edited
     */
    public static class EditCorrespondenceTableMetadataSubpage extends EditCorrespondenceTablePage {

        protected EditCorrespondenceTableMetadataSubpage(WebDriver driver) {
            super(driver);
        }

        @Override
        protected void verifyPage() {
            verifyCurrentPageHas(By.id("metadata-editor"));
        }

        public void setBeskrivelse(String beskrivelse) {
            clearAndSendKeys($(TextAreaElement.class).caption("Beskrivelse").first(), beskrivelse);
        }

        public String getBeskrivelse() {
            return $(TextAreaElement.class).caption("Beskrivelse").first().getValue();
        }

        public void updateTranslationForBeskrivelse(String beskrivelse) {
            clearAndSendKeys($(TextAreaElement.class).caption("Beskrivelse").get(1), beskrivelse);
        }

        public String getTranslationForBeskrivelse() {
            return $(TextAreaElement.class).caption("Beskrivelse").get(1).getValue();
        }
    }

    abstract static class AbstractCodesSubpage extends EditCorrespondenceTablePage {
        protected AbstractCodesSubpage(WebDriver driver) {
            super(driver);
        }

        @Override
        protected void verifyPage() {
            verifyCurrentPageHas(By.id("code-editor"));
        }

        public boolean hasSourceCode(String codeAndTitle) {
            // Throws exception if not found
            findRequiredRowWithCodeAndTitle(codeAndTitle, getTable("source-codes"));
            return true;
        }

        protected TableElement getTable(String idOfCodeTable) {
            return $(CustomComponentElement.class).id(idOfCodeTable).$(TreeTableElement.class).first();
        }

        public void filterSourceCodes(String filterString) {
            clearAndSendKeys(getFilterBox("source-codes"), filterString);
            sleepSeconds(2);
        }

        private TextFieldElement getFilterBox(String idOfCodeTable) {
            return $(CustomComponentElement.class).id(idOfCodeTable).$(TextFieldElement.class).first();
        }

        public void clearSourceCodesFilter() {
            filterSourceCodes("");
        }

        public void filterTargetCodes(String filterString) {
            clearAndSendKeys(getFilterBox("target-codes"), filterString);
            sleepSeconds(2);
        }

        public void clearTargetCodesFilter() {
            filterTargetCodes("");
        }

        public int countRowsInSource() {
            return countRowsInTable(getTable("source-codes"));
        }

        public int countRowsInTarget() {
            return countRowsInTable(getTable("target-codes"));
        }
    }

    /**
     * Represents the accordion tab where correspondenceMaps for a correspondenceTable may be edited
     */
    public static class EditCorrespondenceTableCodesSubpage extends AbstractCodesSubpage {
        private static final int DELETE_BUTTON_COLUMN = 1;

        protected EditCorrespondenceTableCodesSubpage(WebDriver driver) {
            super(driver);
        }

        public void dragAndDrop(String targetCode, String sourceCode) {
            TableRowElement target = findRequiredRowWithCodeAndTitle(targetCode, getTable("target-codes"));
            TableRowElement source = findRequiredRowWithCodeAndTitle(sourceCode, getTable("source-codes"));
            new Actions(getDriver()).dragAndDrop(target, source).perform();
        }

        public void deleteSourceCodeAndAcceptConfirmationBox(String sourceCode) {
            findRequiredRowWithCodeAndTitle(sourceCode, getTable("source-codes")).getCell(DELETE_BUTTON_COLUMN)
                    .$(ButtonElement.class).first().click();
            $(WindowElement.class).$(ButtonElement.class).caption("Slett").first().click();
        }
    }

    /**
     * Represents the accordion tab where correspondenceMaps for a changeTable may be edited
     */
    public static class EditChangeTableCodesSubpage extends AbstractCodesSubpage {
        private static final int MAP_TABLE_SOURCE_COLUMN = 0;
        private static final int MAP_TABLE_TARGET_COLUMN = 1;
        private static final int MAP_TABLE_DELETE_COLUMN = 2;

        protected EditChangeTableCodesSubpage(WebDriver driver) {
            super(driver);
        }

        public void deleteSourceCodeAndAcceptConfirmationBox(String sourceCode) {
            findRequiredRowWithCodeAndTitle(sourceCode, getTable("source-codes")).getCell(1).$(ButtonElement.class)
                    .first().click();
            $(WindowElement.class).$(ButtonElement.class).caption("Slett").first().click();
        }

        public void clickOpprettNyKorrespondanse() {
            $(ButtonElement.class).caption("Opprett ny korrespondanse").first().click();
        }

        public void dragAndDropSource(String sourceCodeAndTitle) {
            TableRowElement source = findRequiredRowWithCodeAndTitle(sourceCodeAndTitle, getTable("source-codes"));
            TableRowElement map = findRequiredRowInTable($(TreeTableElement.class).id("map-codes"),
                    MAP_TABLE_SOURCE_COLUMN, "<Trekk kildekode hit>");
            new Actions(getDriver()).dragAndDrop(source, map).perform();
        }

        public void dragAndDropTarget(String targetCodeAndTitle) {
            TableRowElement target = findRequiredRowWithCodeAndTitle(targetCodeAndTitle, getTable("target-codes"));
            TableRowElement map = findRequiredRowInTable($(TreeTableElement.class).id("map-codes"),
                    MAP_TABLE_TARGET_COLUMN, "<Trekk målkode hit>");
            new Actions(getDriver()).dragAndDrop(target, map).perform();
        }

        public int countRowsInMap() {
            return findRows($(TableElement.class).id("map-codes")).size();
        }

        public void deleteCorrespondenceMapAndAcceptConfirmationBox(String sourceCodeAndTitle) {
            findRequiredRowInTable($(TreeTableElement.class).id("map-codes"), MAP_TABLE_SOURCE_COLUMN,
                    sourceCodeAndTitle).getCell(MAP_TABLE_DELETE_COLUMN).$(ButtonElement.class).first().click();
            $(WindowElement.class).$(ButtonElement.class).caption("Slett").first().click();
        }
    }
}