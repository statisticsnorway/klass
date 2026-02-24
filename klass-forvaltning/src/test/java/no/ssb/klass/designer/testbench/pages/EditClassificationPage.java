package no.ssb.klass.designer.testbench.pages;

import org.openqa.selenium.WebDriver;

import com.vaadin.testbench.elements.ComboBoxElement;

import no.ssb.klass.core.model.Language;
import no.ssb.klass.designer.editing.classification.ClassificationEditorView;

public class EditClassificationPage extends AbstractEditingPage {

    protected EditClassificationPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void verifyPage() {
        verifyCurrentPageIs(ClassificationEditorView.NAME);
    }

    public void changePrimaryLanguage(Language language) {
        $(ComboBoxElement.class).caption("Primærspråk").first().selectByText(language.getDisplayName());
    }

    public String getPrimaryLanguage() {
        return $(ComboBoxElement.class).caption("Primærspråk").first().getValue();
    }
}
