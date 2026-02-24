package no.ssb.klass.designer.components.common;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.themes.ValoTheme;

/**
 * @author Mads Lundemo, SSB.
 */
public class ConfirmOrCancelComponent extends CustomComponent {

    private ConfirmOrCancelDesign design = new ConfirmOrCancelDesign();

    public ConfirmOrCancelComponent() {
        setCompositionRoot(design);
    }

    public void setConfirmText(String confirmText) {
        design.confirmButton.setCaption(confirmText);
    }

    public void setConfirmAsPrimaryButton() {
        design.confirmButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        design.confirmButton.setClickShortcut(KeyCode.ENTER);
    }

    public void setCancelText(String cancelText) {
        design.cancelButton.setCaption(cancelText);
    }

    public void disableConfirmButton(Boolean disable, String reasonToolTip) {
        design.confirmButton.setEnabled(!disable);
        design.confirmButton.setDescription(reasonToolTip);

    }

    public void addConfirmClickListener(Button.ClickListener listener) {
        design.confirmButton.addClickListener(listener);
    }

    public void addCancelClickListener(Button.ClickListener listener) {
        design.cancelButton.addClickListener(listener);
    }

}
