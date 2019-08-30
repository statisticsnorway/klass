package no.ssb.klass.designer.admin;

import org.springframework.security.access.annotation.Secured;
import org.vaadin.spring.annotation.PrototypeScope;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;

import no.ssb.klass.core.ldap.ActiveDirectoryRoles;

@SuppressWarnings("serial")
@PrototypeScope
@SpringView(name = AdminView.NAME)
@Secured(ActiveDirectoryRoles.KLASS_ADMINISTRATOR)
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
