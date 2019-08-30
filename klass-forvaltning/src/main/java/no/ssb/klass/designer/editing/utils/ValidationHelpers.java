package no.ssb.klass.designer.editing.utils;

import java.time.LocalDate;

import com.vaadin.ui.AbstractField;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Notification;

import no.ssb.klass.core.util.TimeUtil;
import no.ssb.klass.designer.util.KlassTheme;

/**
 * @author Mads Lundemo, SSB.
 */
public final class ValidationHelpers {

    private ValidationHelpers() {

    }

    public static boolean verifyDateInput(DateField fromDate, DateField toDate) {
        return verifyDateInput(fromDate, toDate, false);
    }

    public static boolean verifyDateInput(DateField fromDate, DateField toDate, boolean acceptNullValues) {
        if (acceptNullValues && fromDate.isEmpty() && toDate.isEmpty()) {
            return true;
        }
        if (fromDate.getValue() != null) {
            LocalDate from = TimeUtil.toLocalDate(fromDate.getValue());
            if (toDate.getValue() != null) {
                LocalDate to = TimeUtil.toLocalDate(toDate.getValue());
                if (from.isAfter(to)) {
                    showErrorField(toDate);
                    Notification.show("Fra dato må være før eller lik til dato");
                    return false;
                }
            }
        } else {
            showErrorField(fromDate);
            Notification.show("Fra dato må være fylt ut");
            return false;
        }
        return true;
    }

    public static void showErrorField(AbstractField<?> field) {
        field.focus();
        field.addStyleName(KlassTheme.TEXTFIELD_ERROR);
    }
}
