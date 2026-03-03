package no.ssb.klass.designer.editing.codetables.utils;

import java.util.Set;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

import no.ssb.klass.core.model.ClassificationItem;
import no.ssb.klass.core.model.StatisticalClassification;
import no.ssb.klass.designer.service.ClassificationFacade;

public final class CodeTableUtils {
    private CodeTableUtils() {
        // Utility class
    }

    public static boolean existsReferencesToClassificationItem(ClassificationFacade classificationFacade,
            ClassificationItem classificationItem) {
        try {
            CodeTableUtils.verifyNoReferencesToClassificationItem(classificationFacade, classificationItem);
        } catch (IllegalArgumentException e) {
            Notification.show(e.getMessage(), Type.ERROR_MESSAGE);
            return true;
        }
        return false;
    }

    public static void verifyNoReferencesToClassificationItem(ClassificationFacade classificationFacade,
            ClassificationItem classificationItem) {
        Set<String> references = classificationFacade.findReferencesOfClassificationItem(classificationItem);
        if (!references.isEmpty()) {
            StringBuilder builder = new StringBuilder("Kan ikke slette kode: " + classificationItem.getCode()
                    + "\n\nDen er referert til fra variantene/korrespondancetabellene:\n");
            for (String reference : references) {
                builder.append("-  " + reference + "\n");
            }
            throw new IllegalArgumentException(builder.toString());
        }
        for (ClassificationItem child : classificationItem.getLevel().getStatisticalClassification()
                .getChildrenOfClassificationItem(classificationItem)) {
            verifyNoReferencesToClassificationItem(classificationFacade, child);
        }
    }

    public static void verifyNoReferencesToClassificationItems(ClassificationFacade classificationFacade,
            StatisticalClassification statisticalClassification) {
        Set<String> references = classificationFacade.findReferencesOfClassificationItems(statisticalClassification);
        if (!references.isEmpty()) {
            StringBuilder builder = new StringBuilder("Kan ikke slette koder. "
                    + "\n\nDet en eller flere er brukt i fra variantene/korrespondancetabellene:\n");
            for (String reference : references) {
                builder.append("-  " + reference + "\n");
            }
            throw new IllegalArgumentException(builder.toString());
        }
    }
}
