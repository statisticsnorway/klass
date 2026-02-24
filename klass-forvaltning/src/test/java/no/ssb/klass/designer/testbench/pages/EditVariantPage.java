package no.ssb.klass.designer.testbench.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.CustomComponentElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.testbench.elements.TableRowElement;
import com.vaadin.testbench.elements.TextAreaElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.testbench.elements.TreeTableElement;
import com.vaadin.testbench.elements.WindowElement;

import no.ssb.klass.core.model.Language;
import no.ssb.klass.designer.editing.variant.EditVariantEditorView;

public class EditVariantPage extends AbstractEditingPage {
    private static final String TRANSLATION_CODES = "testbench-translation-codes";
    private static final String VARIANT_CODES = "testbench-variant-codes";
    private static final String VERSION_CODES = "testbench-version-codes";

    protected EditVariantPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void verifyPage() {
        verifyCurrentPageIs(EditVariantEditorView.NAME);
    }

    public void selectEditLanguages() {
        $(ButtonElement.class).caption("Rediger språk").first().click();
    }

    public void selectLanguage(Language language) {
        $(ButtonElement.class).caption(language.getDisplayName()).first().click();
    }

    public static class EditVariantCodesSubpage extends EditVariantPage {
        protected EditVariantCodesSubpage(WebDriver driver) {
            super(driver);
        }

        @Override
        protected void verifyPage() {
            verifyCurrentPageHas(By.id("code-editor"));
        }

        public void dragAndDrop(String versionCode, String groupingCode) {
            TableRowElement source = findRequiredRowWithCodeAndTitle(versionCode, getTable(VERSION_CODES));
            TableRowElement target = findRequiredRowWithCodeAndTitle(groupingCode, getTable(VARIANT_CODES));
            new Actions(getDriver()).dragAndDrop(source, target).perform();
        }

        protected TableElement getTable(String idOfCodeTable) {
            // return $(TableElement.class).id(idOfCodeTable);
            return $(CustomComponentElement.class).id(idOfCodeTable).$(TreeTableElement.class).first();

        }

        public void createGroupingCode(String code, String title) {
            TableRowElement row = findRows(getTable(VARIANT_CODES)).get(0);
            row.$(TextFieldElement.class).get(0).sendKeys(code);
            row.$(TextFieldElement.class).get(1).sendKeys(title + "\n");
        }

        public int countRowsInVariant() {
            return countRowsInTable(getTable(VARIANT_CODES));
        }

        public void doAutomaticTranslationOfNynorsk() {
            $(ButtonElement.class).id("testbench-automatic-translation").click();
            $(WindowElement.class).$(CheckBoxElement.class).caption("Kopier Nynorsk fra Bokmål. ").first().findElement(
                    By.tagName("label")).click();
            $(WindowElement.class).$(ButtonElement.class).caption("Kopier").first().click();
        }

        public int countRowsInTranslation() {
            return countRowsInTable(getTable(TRANSLATION_CODES));
        }

        public String getTranslationForSingleCode() {
            return getTable(TRANSLATION_CODES).getRow(0).getCell(0).getText();
        }

        public void translateSingleCode(String title) {
            getTable(TRANSLATION_CODES).getRow(0).getCell(0).$(LabelElement.class).first().click();
            getTable(TRANSLATION_CODES).getRow(0).$(TextFieldElement.class).get(1).sendKeys(title + "\n");
        }

        public void deleteGroupingCodeAndAcceptConfirmationBox(String codeAndTitle) {
            TableRowElement row = findRequiredRowInTable(getTable(VARIANT_CODES), 0, codeAndTitle);
            row.getCell(1).$(ButtonElement.class).first().click();
            $(WindowElement.class).$(ButtonElement.class).caption("Slett").first().click();
        }
    }

    public static class EditVariantMetadataSubpage extends EditVariantPage {
        protected EditVariantMetadataSubpage(WebDriver driver) {
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
}
