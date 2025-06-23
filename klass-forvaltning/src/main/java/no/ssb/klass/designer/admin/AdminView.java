package no.ssb.klass.designer.admin;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import org.vaadin.spring.annotation.PrototypeScope;


@PrototypeScope
@SpringView(name = AdminView.NAME)
public class AdminView extends AdminDesign implements View {

    public static final String NAME = "AdminView";

    @Override
    public void enter(ViewChangeEvent event) {
        sectionsTab.init();
        statisticsTab.init();
        subscriberUnitTab.init();
        loggMessageTab.init();
    }
}
