package no.ssb.klass.designer.components.search;

import com.google.common.collect.ImmutableMap;
import com.vaadin.data.Validator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.CustomComponent;

import no.ssb.klass.designer.ClassificationSearchView;
import no.ssb.klass.designer.listeners.EnterListener;
import no.ssb.klass.designer.util.VaadinUtil;

/**
 * @author Mads Lundemo, SSB.
 */
public class SearchBoxComponent extends CustomComponent {

    private SearchBoxDesign design = new SearchBoxDesign();

    public SearchBoxComponent() {
        setCompositionRoot(design);
        configureListeners();
        configureSearchBoxComponents();
    }

    private void configureListeners() {
        design.searchButton.addClickListener(event -> gotoSearchView());
        design.searchField.addTextChangeListener(event -> design.searchField.setValidationVisible(false));
        design.searchField.addShortcutListener(new EnterListener(design.searchField) {
            @Override
            protected void enterPressed() {
                gotoSearchView();
            }
        });

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

    private void gotoSearchView() {
        try {
            design.searchField.validate();
            VaadinUtil.navigateTo(ClassificationSearchView.NAME, ImmutableMap.of(ClassificationSearchView.PARAM_QUERY,
                    design.searchField.getValue(), "r", String.valueOf(Math.random())));
        } catch (Validator.InvalidValueException e) {
            design.searchField.setValidationVisible(true);
        }
    }

}
