package no.ssb.klass.designer.testbench.pages;

import static com.google.common.base.Preconditions.*;

import org.openqa.selenium.WebDriver;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.CustomComponentElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.TableRowElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.testbench.elements.TreeTableElement;
import com.vaadin.testbench.elements.WindowElement;

import no.ssb.klass.core.model.Language;
import no.ssb.klass.designer.editing.version.VersionMainView;

public class EditVersionPage extends AbstractEditingPage {
    protected EditVersionPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void verifyPage() {
        verifyCurrentPageIs(VersionMainView.NAME);
    }

    public void selectEditLanguages() {
        $(ButtonElement.class).caption("Rediger språk").first().click();
    }

    public void selectLanguage(Language language) {
        $(ButtonElement.class).caption(language.getDisplayName()).first().click();
    }

    /**
     * Represents the accordion tab where metadata for a version may be edited
     */
    public static class EditVersionMetadataSubpage extends EditVersionPage {

        protected EditVersionMetadataSubpage(WebDriver driver) {
            super(driver);
        }

        @Override
        protected void verifyPage() {
            verifyCurrentPageHas(By.id("metadata-editor"));
        }

        public void setLovhjemmel(String keysToSend) {
            clearAndSendKeys($(TextFieldElement.class).caption("Lovhjemmel").first(), keysToSend);
        }

        public String getLovHjemmel() {
            return $(TextFieldElement.class).caption("Lovhjemmel").first().getValue();
        }

        public void updateTranslationForLovhjemmel(String keysToSend) {
            clearAndSendKeys($(TextFieldElement.class).caption("Lovhjemmel").get(1), keysToSend);
        }

        public String getTranslationForLovhjemmel() {
            return $(TextFieldElement.class).caption("Lovhjemmel").get(1).getValue();
        }
    }

    /**
     * Represents the accordion tab where codes for a version may be edited
     */
    public static class EditVersionCodesSubpage extends EditVersionPage {

        protected EditVersionCodesSubpage(WebDriver driver) {
            super(driver);
        }

        @Override
        protected void verifyPage() {
            verifyCurrentPageHas(By.id("code-editor"));
        }

        public void updateCode(String codeAndTitle, String newTitle) {
            updateCodeInSpecifiedTable(codeAndTitle, newTitle, "primary-codes");
        }

        public boolean hasCode(String codeAndTitle) {
            // Throws exception if not found
            findRequiredRowWithCodeAndTitle(codeAndTitle, "primary-codes");
            return true;
        }

        private TableRowElement findRequiredRowWithCodeAndTitle(String codeAndTitle, String idOfCodeTable) {
            checkArgument(codeAndTitle.contains(" - "), "Must input both code and title separated with ' - '");
            // return findRequiredRowWithCodeAndTitle(codeAndTitle,
            // $(CustomComponentElement.class).id(idOfCodeTable).$(TreeTableElement.class).id(idOfCodeTable));
            return findRequiredRowWithCodeAndTitle(codeAndTitle, getTable(idOfCodeTable));
        }

        public void addLevel() {
            $(ButtonElement.class).caption("Opprett nivå").first().click();
        }

        public void deleteLevelAndAcceptConfirmationBox() {
            $(ButtonElement.class).caption("Slett nivå").first().click();
            $(WindowElement.class).$(ButtonElement.class).caption("Slett").first().click();
        }

        public int getNumberOfLevels() {
            return $(CustomComponentElement.class).id("primary-levels").$(TextFieldElement.class).all().size();
        }

        public void updateTranslationForCode(String codeAndTitle, String newTitle) {
            updateCodeInSpecifiedTable(codeAndTitle, newTitle, "translation-codes");
        }

        private void updateCodeInSpecifiedTable(String codeAndTitle, String newTitle, String idOfCodeTable) {
            TableRowElement row = findRequiredRowWithCodeAndTitle(codeAndTitle, idOfCodeTable);
            row.getCell(0).$(LabelElement.class).first().click();
            clearAndSendKeys(getTable(idOfCodeTable).$(TextFieldElement.class).get(1), newTitle);
        }

        private TreeTableElement getTable(String idOfCodeTable) {
            return $(CustomComponentElement.class).id(idOfCodeTable).$(TreeTableElement.class).first();
        }

        public boolean hasTranslationCode(String codeAndTitle) {
            // Throws exception if not found
            findRequiredRowWithCodeAndTitle(codeAndTitle, "translation-codes");
            return true;
        }
    }
}
