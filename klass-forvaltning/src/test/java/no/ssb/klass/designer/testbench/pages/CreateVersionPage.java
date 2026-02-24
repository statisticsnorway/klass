package no.ssb.klass.designer.testbench.pages;

import org.openqa.selenium.WebDriver;

import com.vaadin.testbench.elements.TextAreaElement;

import no.ssb.klass.designer.editing.version.CreateVersionView;

public class CreateVersionPage extends AbstractEditingPage {
    protected CreateVersionPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void verifyPage() {
        verifyCurrentPageIs(CreateVersionView.NAME);
    }

    public void setDescription(String description) {
        clearAndSendKeys($(TextAreaElement.class).caption("Beskrivelse").first(), description);
    }
}
