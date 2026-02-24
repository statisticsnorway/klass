package no.ssb.klass.designer.components.common.layouts;

import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

public class VerticalSpacedLayout extends VerticalLayout {
    public VerticalSpacedLayout() {
        setSpacing(true);
    }

    public VerticalSpacedLayout(Component... children) {
        this();
        addComponents(children);
    }
}
