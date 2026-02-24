package no.ssb.klass.designer.util;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import com.google.common.base.Strings;
import com.vaadin.data.Validator;
import com.vaadin.server.UserError;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Notification;

/**
 * @author Mads Lundemo, SSB.
 */
public final class PassiveValidationUtil {

    private PassiveValidationUtil() {
    }

    public static boolean validate(List<? extends AbstractField<?>> fieldsToValidate) {
        boolean valid = true;
        for (AbstractField<?> field : fieldsToValidate) {
            try {
                field.validate();
            } catch (Validator.InvalidValueException ex) {
                String message = Strings.isNullOrEmpty(ex.getMessage()) ? "Påkrevd felt" : ex.getMessage();
                field.setComponentError(new UserError(message));
                valid = false;
            }
        }
        return valid;
    }

    public static boolean validateAndShowWarnings(Map<? extends AbstractField<?>, String> fieldsToValidate) {
        return validateAndShowWarnings(fieldsToValidate, "");
    }

    public static boolean validateAndShowWarnings(Map<? extends AbstractField<?>, String> fieldsToValidate,
            String caption) {
        boolean valid = true;
        StringJoiner errors = new StringJoiner("\n");
        for (Map.Entry<? extends AbstractField<?>, String> entry : fieldsToValidate.entrySet()) {
            AbstractField<?> field = entry.getKey();
            try {
                field.validate();
            } catch (Validator.InvalidValueException ex) {
                String identifyingText = entry.getValue();
                String message = Strings.isNullOrEmpty(ex.getMessage())
                        ? identifyingText + " må fylles ut" : ex.getMessage();
                field.setComponentError(new UserError(message));
                errors.add(message);
                valid = false;
            }
        }
        if (!valid) {
            Notification.show(caption, errors.toString(), Notification.Type.WARNING_MESSAGE);
        }
        return valid;
    }

    public static void revalidateField(AbstractField<?> field) {
        field.setComponentError(null);
        try {
            field.validate();
        } catch (Validator.InvalidValueException ex) {
            String message = Strings.isNullOrEmpty(ex.getMessage()) ? "Påkrevd felt" : ex.getMessage();
            field.setComponentError(new UserError(message));
        }
    }
}
