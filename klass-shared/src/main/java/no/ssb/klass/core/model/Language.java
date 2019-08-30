package no.ssb.klass.core.model;

public enum Language {
    NB("Bokmål"),
    NN("Nynorsk"),
    EN("Engelsk");

    private final String displayName;

    public String getDisplayName() {
        return displayName;
    }

    public String getLanguageCode() {
        return name().toLowerCase();
    }


    Language(String displayName) {
        this.displayName = displayName;
    }

    public static Language getDefault() {
        return NB;
    }

    public static Language[] getDefaultPrioritizedOrder() {
        return getPrioritizedOrder(getDefault());
    }

    public static Language[] getPrioritizedOrder(Language primaryLanguage) {
        switch (primaryLanguage) {
        case NB:
            return new Language[] { NB, NN, EN };
        case NN:
            return new Language[] { NN, NB, EN };
        case EN:
            return new Language[] { EN, NB, NN };
        default:
            throw new IllegalArgumentException("Unknown language: " + primaryLanguage);
        }
    }

    public static Language[] getAllSupportedLanguages() {
        return getDefaultPrioritizedOrder();
    }

    public static Language getSecondLanguage(Language primaryLanguage) {
        return getPrioritizedOrder(primaryLanguage)[1];
    }

    public static Language getThirdLanguage(Language primaryLanguage) {
        return getPrioritizedOrder(primaryLanguage)[2];
    }

}
