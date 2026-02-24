package no.ssb.klass.designer.testbench.pages;

import org.openqa.selenium.WebDriver;

import com.vaadin.testbench.elements.TextAreaElement;

import no.ssb.klass.designer.editing.variant.CreateVariantView;

public class CreateVariantPage extends AbstractEditingPage {
    protected CreateVariantPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void verifyPage() {
        verifyCurrentPageIs(CreateVariantView.NAME);
    }

    public void setBeskrivelse(String beskrivelse) {
        clearAndSendKeys($(TextAreaElement.class).caption("Beskrivelse").first(), beskrivelse);
    }
}
