package no.ssb.klass.designer.testbench.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.vaadin.testbench.elements.NativeSelectElement;

/**
 * This represents the filtering row, where classification types and sections may be selected/filtered
 */
public class GlobalFilterPage extends AbstractPage {
    protected GlobalFilterPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void verifyPage() {
        verifyCurrentPageHas(By.className("select-row"));
    }

    public void selectKlassifikasjoner() {
        getClassificationTypeSelectBox().selectByText("Klassifikasjon");
    }

    public void selectKodelister() {
        getClassificationTypeSelectBox().selectByText("Kodeliste");
    }

    public void selectAlleTyperKodeverk() {
        getClassificationTypeSelectBox().selectByText("Alle typer kodeverk");
    }

    public void selectSeksjon(String seksjon) {
        getSectionSelectBox().selectByText(seksjon);
    }

    public void selectAlleSeksjoner() {
        getSectionSelectBox().selectByText("Alle seksjoner");
    }

    private NativeSelectElement getClassificationTypeSelectBox() {
        return $(NativeSelectElement.class).id("classification-type-selector");
    }

    private NativeSelectElement getSectionSelectBox() {
        return $(NativeSelectElement.class).id("section-selector");
    }
}
