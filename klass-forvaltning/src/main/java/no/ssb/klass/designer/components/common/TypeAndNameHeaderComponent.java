package no.ssb.klass.designer.components.common;

import com.vaadin.ui.CustomComponent;

/**
 * @author Mads Lundemo, SSB.
 */
public class TypeAndNameHeaderComponent extends CustomComponent {

    public static final String TEXT_CORRESPONDANCE_TABLE = "Korrespondansetabell";
    public static final String TEXT_VARIANT_OF = "Variant av ";
    private TypeAndNameHeaderDesign design = new TypeAndNameHeaderDesign();

    public TypeAndNameHeaderComponent() {
        setCompositionRoot(design);
        setTypeText("");
        setNameText("");
    }

    public void setTypeText(String type) {
        design.typeTextfield.setValue(type);
    }

    public void setNameText(String name) {
        design.nameTextfield.setValue(name);
    }
}
