package no.ssb.klass.designer.testbench.pages;

import org.openqa.selenium.WebDriver;

import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.elements.TextAreaElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.testbench.elements.TwinColSelectElement;

import no.ssb.klass.core.model.ClassificationType;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.designer.editing.classification.ClassificationEditorView;

public class CreateClassificationPage extends AbstractEditingPage {

    protected CreateClassificationPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void verifyPage() {
        verifyCurrentPageIs(ClassificationEditorView.NAME);
    }

    public void setBeskrivelse(String beskrivelse) {
        clearAndSendKeys($(TextAreaElement.class).caption("Beskrivelse").first(), beskrivelse);
    }

    public void setKodeverksType(ClassificationType classificationType) {
        $(ComboBoxElement.class).caption("Kodeverkstype").first().selectByText(classificationType.getDisplayName(
                Language.NB));
    }

    public void setTittel(String tittel) {
        clearAndSendKeys($(TextFieldElement.class).id("classification-name"), tittel);
    }

    public void setEnhet(String enhet) {
        $(TwinColSelectElement.class).caption("Statistisk enheter").first().selectByText(enhet);
    }
}
