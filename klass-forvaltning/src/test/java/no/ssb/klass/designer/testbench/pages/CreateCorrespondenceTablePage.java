package no.ssb.klass.designer.testbench.pages;

import org.openqa.selenium.WebDriver;

import com.vaadin.testbench.elements.TextAreaElement;

import no.ssb.klass.designer.editing.correspondencetable.CreateCorrespondenceTableView;

public class CreateCorrespondenceTablePage extends AbstractEditingPage {

    protected CreateCorrespondenceTablePage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void verifyPage() {
        verifyCurrentPageIs(CreateCorrespondenceTableView.NAME);
    }

    public void setBeskrivelse(String beskrivelse) {
        clearAndSendKeys($(TextAreaElement.class).caption("Beskrivelse").first(), beskrivelse);
    }
}
