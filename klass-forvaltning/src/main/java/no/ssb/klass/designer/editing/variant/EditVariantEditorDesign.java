package no.ssb.klass.designer.editing.variant;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;

import no.ssb.klass.designer.components.common.ConfirmOrCancelComponent;
import no.ssb.klass.designer.components.common.TypeAndNameHeaderComponent;
import no.ssb.klass.designer.components.metadata.MetadataEditorComponent;

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
public class EditVariantEditorDesign extends VerticalLayout {
    protected TypeAndNameHeaderComponent header;
    protected Accordion accordion;
    protected MetadataEditorComponent metadataEditor;
    protected VariantCodeEditorView codeEditor;
    protected ConfirmOrCancelComponent actionButtons;

    public EditVariantEditorDesign() {
        Design.read(this);
    }
}
