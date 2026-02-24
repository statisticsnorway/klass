package no.ssb.klass.designer.windows;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.Objects;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.annotation.PrototypeScope;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.vaadin.data.Validator;
import com.vaadin.server.UserError;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

import no.ssb.klass.core.model.ClassificationVariant;
import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.designer.editing.variant.CreateVariantView;
import no.ssb.klass.designer.service.ClassificationFacade;
import no.ssb.klass.designer.util.VaadinUtil;

/**
 * @author Mads Lundemo, SSB.
 */
@SpringComponent
@PrototypeScope
class NewVariantWindowLogic extends NewVariantWindowDesign {

    private Window parent;
    private ClassificationVersion version;

    @Autowired
    private ClassificationFacade classificationFacade;

    NewVariantWindowLogic(Window parent) {
        this.parent = parent;
        actionButtons.setConfirmText("Opprett");
        actionButtons.addCancelClickListener(event -> parent.close());
        actionButtons.addConfirmClickListener(event -> createVariant());
        actionButtons.setConfirmAsPrimaryButton();

    }

    public void init(Long versionId) {
        version = classificationFacade.getRequiredClassificationVersion(versionId);
        headerLabel.setValue(version.getNameInPrimaryLanguage());
        if (!version.isDraft()) {
            LocalDate from = version.getDateRange().getFrom();
            fromMonth.setValue(String.format("%02d", from.getMonthValue()));
            fromYear.setValue(String.format("%04d", from.getYear()));
        }

    }

    private void createVariant() {
        if (validDate() && validName() && noDraftNameConflict()) {
            if (dateFieldsAreEmpty()) {
                VaadinUtil.navigateTo(CreateVariantView.NAME,
                        ImmutableMap.of(
                                CreateVariantView.PARAM_VERSION_ID, String.valueOf(version.getId()),
                                CreateVariantView.PARAM_NAME, nameTextField.getValue(),
                                CreateVariantView.PARAM_DRAFT, "true"));
            } else {
                VaadinUtil.navigateTo(CreateVariantView.NAME,
                        ImmutableMap.of(
                                CreateVariantView.PARAM_VERSION_ID, String.valueOf(version.getId()),
                                CreateVariantView.PARAM_NAME, nameTextField.getValue(),
                                CreateVariantView.PARAM_FROM, getFromDateParam(),
                                CreateVariantView.PARAM_TO, getToDateParam()));
            }
            parent.close();
        }
    }

    private boolean noDraftNameConflict() {
        if (dateFieldsAreEmpty()) {
            String name = nameTextField.getValue();
            if (version.getClassificationVariants().stream().anyMatch(draftWithNameExists(name))) {
                String message = "Det finnes allerede et utkast med dette navnet";
                Notification.show(message);
                nameTextField.setComponentError(new UserError(message));
                return false;
            }
        }
        return true;
    }

    private Predicate<ClassificationVariant> draftWithNameExists(String name) {
        return v -> Objects.equals(name, v.getNameBase(v.getPrimaryLanguage())) && v.isDraft();
    }

    private boolean dateFieldsAreEmpty() {
        return fromMonth.isEmpty() && fromYear.isEmpty() && toMonth.isEmpty() && toYear.isEmpty();
    }

    private String getFromDateParam() {

        return String.format("%04d-%02d-01", Integer.parseInt(fromYear.getValue()), Integer.parseInt(fromMonth
                .getValue()));
    }

    private String getToDateParam() {
        if (toMonth.isEmpty()) {
            return LocalDate.MAX.toString();
        }
        return String.format("%04d-%02d-01", Integer.parseInt(toYear.getValue()), Integer.parseInt(toMonth.getValue()));
    }

    private boolean validName() {
        try {
            nameTextField.setComponentError(null);
            nameTextField.validate();
            return true;
        } catch (Validator.InvalidValueException ex) {
            String message = Strings.isNullOrEmpty(ex.getMessage()) ? "Påkrevd felt" : ex.getMessage();
            nameTextField.setComponentError(new UserError(message));
            return false;
        }
    }

    private boolean validDate() {
        fromMonth.setComponentError(null);
        fromYear.setComponentError(null);
        toMonth.setComponentError(null);
        toYear.setComponentError(null);

        // if draft
        if (dateFieldsAreEmpty()) {
            return true;
        }

        if (!validateMonth(fromMonth)) {
            return false;
        }
        if (!validateYear(fromYear)) {
            return false;
        }
        if (!toMonth.isEmpty() || !toYear.isEmpty()) {
            if (!validateMonth(toMonth)) {
                return false;
            }
            if (!validateYear(toYear)) {
                return false;
            }
        }
        return true;

    }

    private boolean validateYear(TextField yearField) {
        try {
            Year.parse(yearField.getValue());
        } catch (Exception e) {
            yearField.setComponentError(new UserError("Ugyldig dato"));
            return false;
        }
        return true;
    }

    private boolean validateMonth(TextField monthField) {
        try {
            Month.of(Integer.parseInt(monthField.getValue()));
        } catch (Exception e) {
            monthField.setComponentError(new UserError("Ugyldig dato"));
            return false;
        }
        return true;
    }

}
