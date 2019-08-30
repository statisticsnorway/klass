package no.ssb.klass.designer;

import static com.google.common.base.Preconditions.*;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.vaadin.spring.security.VaadinSecurity;

import com.google.common.collect.Iterables;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.UI;

import no.ssb.klass.core.model.ClassificationType;
import no.ssb.klass.core.model.User;
import no.ssb.klass.designer.admin.AdminView;
import no.ssb.klass.designer.admin.ContentUseStatView;
import no.ssb.klass.designer.components.BreadcumbPanel.Breadcrumb;
import no.ssb.klass.designer.components.ClassificationListViewSelection;
import no.ssb.klass.designer.service.ClassificationFacade;
import no.ssb.klass.designer.service.KlassLoginService;
import no.ssb.klass.designer.user.UserContext;
import no.ssb.klass.designer.util.ConfirmationDialog;
import no.ssb.klass.designer.util.KlassTheme;
import no.ssb.klass.designer.util.ParameterUtil;
import no.ssb.klass.designer.util.VaadinUtil;

@SpringUI
@SuppressWarnings("serial")
public class MainView extends MainDesign implements ViewChangeListener {
    private Navigator navigator;

    private final SpringViewProvider springViewProvider;
    private final KlassLoginService klassLoginService;
    private final VaadinSecurity vaadinSecurity;
    private final UserContext userContext;

    private ConfirmationDialog confirmationDialog;

    @Autowired
    public MainView(ClassificationFacade classificationFacade, SpringViewProvider springViewProvider,
            VaadinSecurity vaadinSecurity, KlassLoginService vaadinLoginService, UserContext userContext) {
        this.springViewProvider = springViewProvider;
        this.klassLoginService = vaadinLoginService;
        this.vaadinSecurity = vaadinSecurity;
        this.userContext = userContext;
        verifyUser();
        configureNavigator();
        MainFilterLogic.configureFilterPanel(selectKodeverk, selectSection, classificationFacade);
        configureTopPanel();
        configureUserMenu();
    }

    private void configureUserMenu() {
        MenuBar menuBar = new MenuBar();
        menuBar.addStyleName(KlassTheme.USER_MENU);
        MenuBar.MenuItem menuItem = menuBar.addItem(userContext.getUsername(), null);
        menuItem.setIcon(FontAwesome.USER);
        if (userContext.isAdministrator()) {
            menuItem.addItem("Administrator", c -> createAdminView());
        }
        menuItem.addItem("Innhold og bruksstatistikk", c -> createInnholdBruksstatistikkView());
        menuItem.addItem("Log ut", this::logout);
        userIcon.addComponent(menuBar);
    }

    private void logout(MenuBar.MenuItem menuItem) {
        vaadinSecurity.logout();
    }

    private void createAdminView() {
        VaadinUtil.navigateTo(AdminView.NAME);
    }

    private void createInnholdBruksstatistikkView() {
        VaadinUtil.navigateTo(ContentUseStatView.NAME);
    }

    private void verifyUser() {
        Authentication authentication = vaadinSecurity.getAuthentication();
        if (!userContext.hasUser() && vaadinSecurity.isRememberMeAuthenticated()) {
            // create user based on remembered credentials (Remember me)
            try {
                User user = klassLoginService.getUserFromAuthentication(authentication);
                userContext.setUser(user);
            } catch (Exception e) {
                vaadinSecurity.logout();
            }
        }
    }

    public void configureNavigator() {
        navigator = new Navigator(UI.getCurrent(), content);
        navigator.addViewChangeListener(this);
        navigator.setErrorView(new NavigateErrorView());
        navigator.addProvider(springViewProvider);

        VaadinUtil.getKlassState().setClassificationFilter(new ClassificationFilter(selectSection, selectKodeverk));
    }

    public void configureTopPanel() {
        klassLogo.addClickListener(event -> navigator.navigateTo(ClassificationFamilyView.NAME));
    }

    @Override
    public boolean beforeViewChange(ViewChangeEvent event) {
        Boolean newIsFilterView = event.getNewView() instanceof FilteringView;
        Boolean newIsEditorView = event.getNewView() instanceof EditingView;
        Boolean oldIsEditorView = event.getOldView() instanceof EditingView;
        Boolean newIsErrorView = event.getNewView() instanceof ErrorView;

        if (oldIsEditorView && !newIsErrorView) {
            if (checkForChanges(event)) {
                return false;
            }
        }
        filterPanel.setVisible(newIsFilterView);
        breadcrumbPanel.setVisible(newIsEditorView);
        // true = allow change of view
        return true;
    }

    private boolean checkForChanges(ViewChangeEvent event) {
        EditingView view = (EditingView) event.getOldView();
        if (view.hasChanges()) {
            // avoid multiple dialogboxes
            if (confirmationDialog != null && confirmationDialog.isVisible()) {
                confirmationDialog.close();
            }
            // show warning
            String message = "Det er gjort endringer som ikke er lagret.<br/>"
                    + "Er du sikker på å forlate siden uten å lagre?";
            confirmationDialog = new ConfirmationDialog("Ja forkast endringene", message, answerYes -> {
                if (answerYes) {
                    view.ignoreChanges();
                    VaadinUtil.navigateTo(event.getViewName(),
                            ParameterUtil.decodeParameters(event.getParameters()));
                }
            });
            confirmationDialog.setWidth(500, Unit.PIXELS);
            UI.getCurrent().addWindow(confirmationDialog);
            return true;
        }
        return false;
    }

    @Override
    public void afterViewChange(ViewChangeEvent event) {
        if (event.getNewView() instanceof EditingView) {
            EditingView editingView = (EditingView) event.getNewView();
            List<Breadcrumb> breadcrumbs = editingView.getBreadcrumbs();
            breadcrumbPanel.renderBreadcrumbs(breadcrumbs);
            VaadinUtil.getKlassState().setClassificationListViewSelection(getClassificationListViewSelection(
                    breadcrumbs));
        }
    }

    private ClassificationListViewSelection getClassificationListViewSelection(List<Breadcrumb> breadcrumbs) {
        return Iterables.getLast(breadcrumbs).getClassificationListViewSelection();
    }

    /**
     * Provides filters for limiting ClassificationSeries that shall be displayed
     */
    public static class ClassificationFilter {
        private final NativeSelect section;
        private final NativeSelect classificationType;

        ClassificationFilter(NativeSelect section, NativeSelect classificationType) {
            this.section = checkNotNull(section);
            this.classificationType = checkNotNull(classificationType);
        }

        public String getCurrentSection() {
            return (String) section.getValue();
        }

        public ClassificationType getCurrentClassificationType() {
            return (ClassificationType) classificationType.getValue();
        }
    }
}
