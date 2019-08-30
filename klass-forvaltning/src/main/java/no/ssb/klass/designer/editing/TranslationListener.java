package no.ssb.klass.designer.editing;

import no.ssb.klass.core.model.ConcreteClassificationItem;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.model.StatisticalClassification;

/**
 * Listens for when automatic translation is requested
 */
public abstract class TranslationListener {
    private final StatisticalClassification statisticalClassification;

    public TranslationListener(StatisticalClassification statisticalClassification) {
        this.statisticalClassification = statisticalClassification;
    }

    /**
     * @param secondaryLanguageSelected
     *            true if secondaryLanguage shall be automatically translated, false otherwise
     * @param thirdLanguageSelected
     *            true if thirdLanguage shall be automatically translated, false otherwise
     */
    public void translate(boolean secondaryLanguageSelected, boolean thirdLanguageSelected) {
        if (secondaryLanguageSelected) {
            performTranslation(statisticalClassification, Language.getSecondLanguage(statisticalClassification
                    .getPrimaryLanguage()));
        }
        if (thirdLanguageSelected) {
            performTranslation(statisticalClassification, Language.getThirdLanguage(statisticalClassification
                    .getPrimaryLanguage()));
        }
    }

    private void performTranslation(StatisticalClassification statisticalClassification, Language language) {
        Language primaryLanguage = statisticalClassification.getPrimaryLanguage();
        for (ConcreteClassificationItem item : statisticalClassification.getAllConcreteClassificationItems()) {
            item.setOfficialName(item.getOfficialName(primaryLanguage), language);
            item.setShortName(item.getShortName(primaryLanguage), language);
            item.setNotes(item.getNotes(primaryLanguage), language);
        }
        translationPerformed(language);
    }

    protected abstract void translationPerformed(Language language);
}
