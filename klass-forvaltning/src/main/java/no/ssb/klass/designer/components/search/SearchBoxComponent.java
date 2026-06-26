package no.ssb.klass.designer.components.search;

import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.CustomComponent;


/**
 * @author Mads Lundemo, SSB.
 */
public class SearchBoxComponent extends CustomComponent {

    private SearchBoxDesign design = new SearchBoxDesign();

    public SearchBoxComponent() {
        setCompositionRoot(design);
        configureSearchBoxComponents();
    }

    public void setSearchText(String text) {
        design.searchField.setValue(text);
    }

    private void configureSearchBoxComponents() {

        design.searchField.setInputPrompt("Søk i kodeverk");
        design.searchField.setNullRepresentation("");
        design.searchField.setValidationVisible(false);
        design.searchField.addValidator(new StringLengthValidator("søkefeltet kan ikke være blankt", 1, null, false));
        design.searchField.setTextChangeEventMode(AbstractTextField.TextChangeEventMode.EAGER);

    }


}
