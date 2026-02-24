package no.ssb.klass.designer.components.common.layouts;

import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;

public class HorizontalSpacedLayout extends HorizontalLayout {
    public HorizontalSpacedLayout() {
        setSpacing(true);
    }

    public HorizontalSpacedLayout(Component... children) {
        this();
        addComponents(children);
    }
}
