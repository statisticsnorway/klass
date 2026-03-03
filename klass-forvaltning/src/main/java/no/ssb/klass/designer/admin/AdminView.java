package no.ssb.klass.designer.admin;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import no.ssb.klass.core.service.StatisticsService;
import no.ssb.klass.core.service.UserService;
import no.ssb.klass.designer.ClassificationFamilyView;
import no.ssb.klass.designer.service.ClassificationFacade;
import no.ssb.klass.designer.user.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.annotation.PrototypeScope;


@PrototypeScope
@SpringView(name = AdminView.NAME)
public class AdminView extends AdminDesign implements View {
    private static final Logger log = LoggerFactory.getLogger(AdminView.class);


    public static final String NAME = "AdminView";
    private final UserService userService;
    private final UserContext userContext;
    private final ClassificationFacade classificationFacade;
    private final StatisticsService statisticsService;

    @Autowired
    public AdminView(
            UserService userService,
            UserContext userContext,
            ClassificationFacade classificationFacade,
            StatisticsService statisticsService
            ) {
        this.userService = userService;
        this.userContext = userContext;
        this.classificationFacade = classificationFacade;
        this.statisticsService = statisticsService;
    }

    @Override
    public void enter(ViewChangeEvent event) {
        log.info("Entering AdminView");
        sectionsTab.init(this.userService);
        statisticsTab.init(this.userContext, this.classificationFacade);
        subscriberUnitTab.init(this.statisticsService);
        loggMessageTab.init(this.userContext, this.classificationFacade);
    }
}
