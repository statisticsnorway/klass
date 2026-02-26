package no.ssb.klass.designer.windows;

import java.time.LocalDate;
import java.util.List;

import javax.validation.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.annotation.PrototypeScope;

import com.google.common.collect.ImmutableMap;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.util.DateRange;
import no.ssb.klass.core.util.DraftUtil;
import no.ssb.klass.designer.components.common.ConfirmOrCancelComponent;
import no.ssb.klass.designer.components.common.layouts.HorizontalSpacedLayout;
import no.ssb.klass.designer.components.common.layouts.VerticalSpacedLayout;
import no.ssb.klass.designer.editing.version.CreateVersionView;
import no.ssb.klass.designer.editing.version.VersionMainView;
import no.ssb.klass.designer.service.ClassificationFacade;
import no.ssb.klass.designer.util.KlassTheme;
import no.ssb.klass.designer.util.VaadinUtil;

@SuppressWarnings("serial")
@PrototypeScope
@SpringComponent
public class NewVersionWindow extends Window {

    private static final boolean MANDATORY = true;
    private static final boolean OPTIONAL = false;
    private static final int DEFAULT_DATE = 1;
    private static final int DEFAULT_MONTH = 1;
    private static final String MONTH_PADDING = "mm";
    private static final String YEAR_PADDING = "åååå";

    // ?
    private ClassificationFacade classificationFacade;

    private LocalDate validFrom;
    private LocalDate validTo;
    private CheckBox copyVersionChoice;
    private ClassificationSeries classificationSeries;

    public NewVersionWindow() {
        center();
        setModal(true);
    }

    public void init(String classificationId, String versionName, ClassificationFacade classificationFacade) {
        this.classificationFacade = classificationFacade;
        classificationSeries = classificationFacade.getRequiredClassificationSeries(Long.parseLong(classificationId));

        // Label headings part
        Label headerLabelSelected = new Label('"' + versionName + '"');
        headerLabelSelected.addStyleName(ValoTheme.LABEL_BOLD);
        HorizontalLayout headingLayout = new HorizontalSpacedLayout(new Label("Opprett ny versjon av"),
                headerLabelSelected);
        headingLayout.setMargin(new MarginInfo(true, true, true, true));

        // Input part
        HorizontalLayout validityPeriodLayout = new HorizontalLayout(new Label("Gyldighetsperiode:"));
        validityPeriodLayout.setMargin(new MarginInfo(false, true, false, true));
        Label fromLabel = new Label("Fra og med");
        TextField fromMonth = newMonthInputField();
        TextField fromYear = newYearInputField();
        Label toLabel = new Label(" til og med");
        TextField toMonth = newMonthInputField();
        TextField toYear = newYearInputField();
        HorizontalLayout validityLayout = new HorizontalSpacedLayout(fromLabel, fromMonth, fromYear, toLabel, toMonth,
                toYear);
        validityLayout.setMargin(new MarginInfo(false, true, false, true));
        validityLayout.setComponentAlignment(fromLabel, Alignment.MIDDLE_CENTER);
        validityLayout.setComponentAlignment(toLabel, Alignment.MIDDLE_CENTER);

        // Copy checkbox part
        copyVersionChoice = new CheckBox("Kopier elementer fra nåværende versjon");
        copyVersionChoice.setPrimaryStyleName("v-checkbox");
        copyVersionChoice.addValueChangeListener(event -> checkIfVersionExists(copyVersionChoice.getValue()));

        // Buttons part
        ConfirmOrCancelComponent actionButtons = new ConfirmOrCancelComponent();
        actionButtons.setConfirmText("Opprett");
        actionButtons.setConfirmAsPrimaryButton();
        actionButtons.addCancelClickListener(event -> close());
        actionButtons.addConfirmClickListener(event -> {
            if (checkValidityPeriod(fromMonth, fromYear, toMonth, toYear)) {
                close();
                if (!copyVersionChoice.getValue()) {
                    if (DraftUtil.isDraft(validFrom, validTo)) {
                        VaadinUtil.navigateTo(CreateVersionView.NAME, ImmutableMap.of(
                                CreateVersionView.PARAM_CLASSIFICATION_ID, classificationId,
                                CreateVersionView.PARAM_DRAFT, "true"));
                    } else {
                        VaadinUtil.navigateTo(CreateVersionView.NAME, ImmutableMap.of(
                                CreateVersionView.PARAM_CLASSIFICATION_ID, classificationId,
                                CreateVersionView.VALID_FROM_KEY, validFrom.toString(),
                                CreateVersionView.VALID_TO_KEY, validTo == null ? "null" : validTo.toString()));
                    }
                } else {
                    DateRange range = createDateRange(validFrom, validTo);
                    ClassificationVersion newestVersion = classificationSeries.getNewestVersion();
                    if (newestVersion != null) {
                        Long id = classificationFacade.copyClassificationVersion(newestVersion, range).getId();
                        VaadinUtil.navigateTo(VersionMainView.NAME, ImmutableMap.of(VersionMainView.PARAM_VERSION_ID, id
                                .toString()));
                    } else {
                        Notification.show("Det finnes ingen versjoner for klassifikasjon " + classificationSeries
                                .getDescriptionInPrimaryLanguage());
                    }
                }
            }
        });

        HorizontalLayout buttonLayout = new HorizontalSpacedLayout(actionButtons);
        buttonLayout.setMargin(true);

        fromMonth.focus();
        fromMonth.selectAll();
        Label draftLabel = new Label("(La gyldighetsperioden være tom for å opprette et utkast)");
        draftLabel.setStyleName(ValoTheme.TEXTFIELD_ALIGN_CENTER);
        VerticalLayout content = new VerticalSpacedLayout(headingLayout, validityPeriodLayout, validityLayout,
                draftLabel,
                copyVersionChoice, buttonLayout);
        content.setComponentAlignment(draftLabel, Alignment.MIDDLE_CENTER);
        content.setComponentAlignment(copyVersionChoice, Alignment.MIDDLE_CENTER);
        setContent(content);
    }

    private DateRange createDateRange(LocalDate fromInclusive, LocalDate toInclusive) {
        return DateRange.create(fromInclusive, VaadinUtil.convertToExclusive(toInclusive));
    }

    private void checkIfVersionExists(boolean newValue) {
        if (newValue) {
            if (classificationSeries.getNewestVersion() == null) {
                Notification.show("Det finnes ingen versjoner for klassifikasjon " + classificationSeries
                        .getDescriptionInPrimaryLanguage());
            }
        }
    }

    private String checkDateOverlap() {
        DateRange range = createDateRange(validFrom, validTo);
        List<ClassificationVersion> overlapedVersions = classificationSeries.getClassificationVersionsInRange(range);
        StringBuilder overlapedVersionsNames = new StringBuilder();
        boolean first = true;
        for (ClassificationVersion version : overlapedVersions) {
            if (!first) {
                overlapedVersionsNames.append(", ");
            }
            overlapedVersionsNames.append(version.getNameInPrimaryLanguage());
            first = false;
        }
        return overlapedVersionsNames.toString();
    }

    private TextField newMonthInputField() {
        TextField fromMonth = newTextField("mm");
        fromMonth.setMaxLength(2);
        fromMonth.setColumns(3);
        return fromMonth;
    }

    private TextField newYearInputField() {
        TextField toYear = newTextField("åååå");
        toYear.setMaxLength(4);
        toYear.setColumns(4);
        return toYear;
    }

    private boolean checkValidityPeriod(TextField fromMonth, TextField fromYear, TextField toMonth, TextField toYear) {
        fromMonth.removeStyleName(KlassTheme.TEXTFIELD_ERROR);
        fromYear.removeStyleName(KlassTheme.TEXTFIELD_ERROR);
        toMonth.removeStyleName(KlassTheme.TEXTFIELD_ERROR);
        toYear.removeStyleName(KlassTheme.TEXTFIELD_ERROR);
        try {
            Integer fromYearValue = checkAndGetValue(OPTIONAL, "Fra år", fromYear, null);
            if (fromYearValue != null) {
                validFrom = LocalDate.of(fromYearValue, checkAndGetValue(OPTIONAL, "Fra måned", fromMonth,
                        DEFAULT_MONTH), DEFAULT_DATE);
            }
            Integer toYearValue = checkAndGetValue(OPTIONAL, "Til år", toYear, null);
            if (toYearValue != null) {
                validTo = LocalDate.of(toYearValue, checkAndGetValue(OPTIONAL, "Til måned", toMonth, DEFAULT_MONTH),
                        DEFAULT_DATE);
            }
        } catch (ValidationException e) {
            showErrorField(fromMonth);
            Notification.show("Sjekk datofeltene for feil!");
            return false; // Error parsing date?
        }

        if (validTo != null) {
            if (validTo.isBefore(validFrom)) {
                showErrorField(fromMonth);
                Notification.show("Fra dato må være før eller lik til dato!");
                return false; // To date is before from date
            }
        }
        if (validFrom == null && validTo == null) {
            ClassificationVersion draft = classificationSeries.getClassificationVersionDraft();
            if (draft != null) {
                Notification.show("Du kan kun ha ett utkast, du må fullføre dette før du kan lage ett nytt");
                return false;
            }

        } else {
            String overlapName = checkDateOverlap();
            if (!overlapName.isEmpty()) {
                showErrorField(fromMonth);
                Notification.show("Det er overlapp med en allerede eksisterende versjon (" + overlapName + ')');
                return false; // Overlap with another version
            }
        }
        return true;
    }

    private Integer checkAndGetValue(boolean mandatory, String name, TextField field, Integer defaultValue)
            throws ValidationException {
        if (checkIfEmpty(mandatory, name, field)) {
            try {
                Integer value = Integer.parseInt(field.getValue());
                if (field.getColumns() == 2 && (value < 1 || value > 12)) {
                    Notification.show(name + " Måned må ha verdi mellom 1 og 12");
                    showErrorField(field);
                    throw new ValidationException(); // Month range error
                }
                if (field.getColumns() == 4 && (value < 0 || value > 9999)) {
                    Notification.show("Ugyldig årstall!");
                    showErrorField(field);
                    throw new ValidationException(); // Year range error
                }
                return value;
            } catch (NumberFormatException e) {
                Notification.show(name + " har ugyldig verdi!");
                showErrorField(field);
                throw new ValidationException(); // Invalid input error
            }
        } else {
            return defaultValue;
        }
    }

    private boolean checkIfEmpty(boolean mandatory, String name, TextField field) throws ValidationException {
        if (field.getValue() == null || field.getValue().trim().isEmpty() || field.getValue().equals(MONTH_PADDING)
                || field.getValue().equals(YEAR_PADDING)) {
            if (mandatory) {
                Notification.show(name + " må fylles ut!");
                showErrorField(field);
                throw new ValidationException(); // Mandatory field without value
            } else {
                return false; // Optional field without value
            }
        } else {
            return true; // Filed with value
        }
    }

    private void showErrorField(TextField field) {
        field.focus();
        field.selectAll();
        field.addStyleName(KlassTheme.TEXTFIELD_ERROR);
    }

    private TextField newTextField(String value) {
        TextField textField = new TextField();
        textField.setInputPrompt(value);
        return textField;
    }

}
