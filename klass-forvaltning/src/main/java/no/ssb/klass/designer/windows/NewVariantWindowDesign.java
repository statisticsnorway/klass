package no.ssb.klass.designer.windows;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;

import no.ssb.klass.designer.components.common.ConfirmOrCancelComponent;

/**
 * !! DO NOT EDIT THIS FILE !!
 * 
 * This class is generated by Vaadin Designer and will be overwritten.
 * 
 * Please make a subclass with logic and additional interfaces as needed, e.g class LoginView extends LoginDesign
 * implements View { }
 */
@DesignRoot
@AutoGenerated
@SuppressWarnings("serial")
public class NewVariantWindowDesign extends VerticalLayout {
    protected Label headerLabel;
    protected TextField fromMonth;
    protected TextField fromYear;
    protected TextField toMonth;
    protected TextField toYear;
    protected TextField nameTextField;
    protected ConfirmOrCancelComponent actionButtons;

    public NewVariantWindowDesign() {
        Design.read(this);
    }
}
