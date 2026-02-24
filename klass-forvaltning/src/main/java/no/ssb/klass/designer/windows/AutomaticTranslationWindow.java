package no.ssb.klass.designer.windows;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.model.StatisticalClassification;
import no.ssb.klass.designer.components.common.ConfirmOrCancelComponent;
import no.ssb.klass.designer.editing.TranslationListener;
import no.ssb.klass.designer.components.common.layouts.VerticalSpacedLayout;

public class AutomaticTranslationWindow extends Window {
    private final CheckBox secondaryLanguage;
    private final CheckBox thirdLanguage;

    public AutomaticTranslationWindow(StatisticalClassification statisticalClassification,
            TranslationListener translationListener) {
        secondaryLanguage = createCheckBox(statisticalClassification, Language.getSecondLanguage(
                statisticalClassification.getPrimaryLanguage()));
        thirdLanguage = createCheckBox(statisticalClassification, Language.getThirdLanguage(statisticalClassification
                .getPrimaryLanguage()));
        Label header = new Label("Kopier språk");
        header.addStyleName(ValoTheme.LABEL_H2);
        ConfirmOrCancelComponent actionButtons = createConfirmOrCancelComponent(translationListener);
        VerticalLayout layout = new VerticalSpacedLayout(header, secondaryLanguage, thirdLanguage,
                new Label(), actionButtons);
        layout.setMargin(true);
        HorizontalLayout extraMarginLayout = new HorizontalLayout(layout);
        extraMarginLayout.setMargin(new MarginInfo(false, true, false, true));
        setContent(extraMarginLayout);
        center();
        setModal(true);
    }

    private ConfirmOrCancelComponent createConfirmOrCancelComponent(TranslationListener translationListener) {
        ConfirmOrCancelComponent actionButtons = new ConfirmOrCancelComponent();
        actionButtons.setConfirmText("Kopier");
        actionButtons.addCancelClickListener((event) -> close());
        actionButtons.addConfirmClickListener((event) -> {
            translationListener.translate(secondaryLanguage.getValue(), thirdLanguage.getValue());
            close();
        });
        return actionButtons;
    }

    private CheckBox createCheckBox(StatisticalClassification statisticalClassification, Language language) {
        String caption = "Kopier " + language.getDisplayName() + " fra " + statisticalClassification
                .getPrimaryLanguage().getDisplayName() + ". ";
        if (statisticalClassification.hasTranslations(language)) {
            String warning = "<span style=\"color: red;\">Dine eksisterende oversettelser vil bli overskrevet</span>";
            caption += warning;
        }
        CheckBox checkBox = new CheckBox(caption);
        checkBox.setCaptionAsHtml(true);
        return checkBox;
    }
}
