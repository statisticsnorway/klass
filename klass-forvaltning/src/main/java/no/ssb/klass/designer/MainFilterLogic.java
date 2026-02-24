package no.ssb.klass.designer;

import java.util.Arrays;

import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.NativeSelect;

import no.ssb.klass.core.model.ClassificationType;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.designer.service.ClassificationFacade;
import no.ssb.klass.designer.util.VaadinUtil;

public final class MainFilterLogic {
    private MainFilterLogic() {
    }

    public static void configureFilterPanel(NativeSelect selectKodeverk, NativeSelect selectSection,
            final ClassificationFacade classificationFacade) {
        selectKodeverk.setNullSelectionItemId("Alle typer kodeverk");
        selectKodeverk.setItemCaptionMode(AbstractSelect.ItemCaptionMode.EXPLICIT_DEFAULTS_ID);
        Arrays.stream(ClassificationType.values()).forEach(classificationType -> {
            selectKodeverk.addItem(classificationType);
            selectKodeverk.setItemCaption(classificationType, classificationType.getDisplayName(Language.NB));
        });
        selectKodeverk.addValueChangeListener(e -> VaadinUtil.navigateToCurrentPage());

        selectSection.setNullSelectionItemId("Alle seksjoner");
        selectSection.addItems(VaadinUtil.naturalSort(classificationFacade.findAllResponsibleSections()));
        selectSection.addValueChangeListener(e -> VaadinUtil.navigateToCurrentPage());
    }

}
