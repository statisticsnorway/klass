package no.ssb.klass.designer.testbench.pages;

import org.openqa.selenium.WebDriver;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.PasswordFieldElement;
import com.vaadin.testbench.elements.TextFieldElement;

public class LoginPage extends AbstractPage {

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void verifyPage() {
        String currentUrl = getDriver().getCurrentUrl();
        if (!currentUrl.contains("login")) {
            throw new IllegalStateException("Not currently at login page. Current url: " + currentUrl);
        }
    }

    public ClassificationFamilyPage loginAsAdmin() {
        return loginAs("admin1", "admin1");
    }

    public ClassificationFamilyPage loginAs(String username, String password) {
        TextFieldElement brukernavnTextField = $(TextFieldElement.class).caption("Brukernavn").first();
        brukernavnTextField.setValue(username);
        PasswordFieldElement passordPasswordField = $(PasswordFieldElement.class).caption("Passord").first();
        passordPasswordField.setValue(password);
        ButtonElement loginButton = $(ButtonElement.class).caption("Log inn").first();
        loginButton.click();
        return new ClassificationFamilyPage(getDriver());
    }
}
