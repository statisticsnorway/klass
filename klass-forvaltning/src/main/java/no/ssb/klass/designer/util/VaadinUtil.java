package no.ssb.klass.designer.util;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.Page;
import com.vaadin.shared.Position;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;

import no.ssb.klass.core.util.TimeUtil;
import no.ssb.klass.designer.ClassificationListView;
import no.ssb.klass.designer.components.BreadcumbPanel.Breadcrumb;
import no.ssb.klass.designer.ui.KlassUI;

public final class VaadinUtil {
    private VaadinUtil() {
        // Utility class
    }

    @SuppressWarnings("unchecked")
    public static <T> T getContainerDataSource(Table table) {
        return (T) table.getContainerDataSource();
    }

    public static void navigateTo(String navigationState) {
        setPreviousView(UI.getCurrent().getNavigator().getState());
        UI.getCurrent().getNavigator().navigateTo(navigationState);
    }

    public static void navigateTo(String navigationState, Map<String, String> parameters) {
        navigateTo(navigationState + "/" + ParameterUtil.encodeParameters(parameters));
    }

    public static String previousPagename() {
        return getKlassState().getPreviousView();
    }

    public static void navigateToCurrentPage() {
        navigateTo(UI.getCurrent().getNavigator().getState());
    }

    public static IndexedContainer getWrappedContainer(Table table) {
        return (IndexedContainer) ((GeneratedPropertyContainer) table.getContainerDataSource()).getWrappedContainer();
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(Item item) {
        BeanItem<T> beanItem = (BeanItem<T>) item;
        return beanItem.getBean();
    }

    public static List<String> naturalSort(Collection<String> collection) {
        List<String> list = new ArrayList<>(collection);
        Collections.sort(list);
        return list;
    }

    public static void setPreviousView(String oldViewName) {
        getKlassState().setPreviousView(oldViewName);
    }

    public static Optional<Component> getLastComponent(AbstractOrderedLayout layout) {
        if (layout.getComponentCount() == 0) {
            return Optional.empty();
        }
        return Optional.of(layout.getComponent(layout.getComponentCount() - 1));
    }

    public static void showSavedMessage() {
        showMessage("Endringene dine har nå blitt lagret");
    }

    public static void showMessage(String text) {
        Notification notification = new Notification(text);
        notification.setPosition(Position.TOP_CENTER);
        notification.setDelayMsec(4000);
        notification.show(Page.getCurrent());
    }

    public static KlassState getKlassState() {
        return ((KlassUI) UI.getCurrent()).getKlassState();
    }

    /**
     * Presented to user the <b>to</b> date of a dateRange shall be viewed as inclusive.
     */
    public static LocalDate convertToInclusive(LocalDate to) {
        if (TimeUtil.isMaxDate(to)) {
            return to;
        }
        return to.minusMonths(1);
    }

    /**
     * Convert <b>to</to> date presented to user as inclusive, to an exclusive date as used internally within Klass
     */
    public static LocalDate convertToExclusive(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return localDate.plusMonths(1);
    }

    public static void navigateTo(Breadcrumb breadcrumb) {
        VaadinUtil.getKlassState().setClassificationListViewSelection(breadcrumb.getClassificationListViewSelection());
        VaadinUtil.navigateTo(ClassificationListView.NAME, ImmutableMap.of(ClassificationListView.PARAM_FAMILY_ID,
                breadcrumb.getClassificationFamilyId().toString()));
    }
}
