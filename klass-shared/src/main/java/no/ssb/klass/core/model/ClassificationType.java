package no.ssb.klass.core.model;

import no.ssb.klass.core.util.Translatable;

/**
 * @author Mads Lundemo, SSB.
 */
public enum ClassificationType {
    CODELIST(
            new Translatable("Kodeliste", "Kodeliste", "Codelist")),
    CLASSIFICATION(
            new Translatable("Klassifikasjon", "Klassifikasjon", "Classification"));

    private final Translatable displayName;

    public String getDisplayName(Language language) {
        return displayName.getString(language);
    }

    ClassificationType(Translatable displayName) {
        this.displayName = displayName;
    }
}
