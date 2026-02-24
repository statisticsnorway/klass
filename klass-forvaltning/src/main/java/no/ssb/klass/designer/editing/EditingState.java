package no.ssb.klass.designer.editing;

/**
 * Enables an EditingView to know what was displayed before being saved. For example if codes or metadata tab was
 * visible, or if any translation was currently visible. This is used to reconstruct an EditingView to previous state
 * after being saved. Hence if translations were displayed before save, then shall be displayed also after being saved.
 */
public final class EditingState {
    private final boolean languageTabVisible;
    private final boolean secondNotThirdLanguageVisible;
    private final boolean codeEditorNotMetadataVisible;

    private EditingState(boolean languageTabVisible, boolean secondNotThirdLanguageVisible,
            boolean codeEditorNotMetadataVisible) {
        this.languageTabVisible = languageTabVisible;
        this.secondNotThirdLanguageVisible = secondNotThirdLanguageVisible;
        this.codeEditorNotMetadataVisible = codeEditorNotMetadataVisible;
    }

    /**
     * @return true if second language (when translating) was visible, false if not
     */
    public boolean isSecondNotThirdLanguageVisible() {
        return secondNotThirdLanguageVisible;
    }

    /**
     * @return true if language tab (used for translations) was visible, false if not
     */
    public boolean isLanguageTabVisible() {
        return languageTabVisible;
    }

    /**
     * @return true if codeEditor was visible, false if metadata editor was visible
     */
    public boolean isCodeEditorNotMetadataVisible() {
        return codeEditorNotMetadataVisible;
    }

    public static EditingState newDefault() {
        return new EditingState(false, true, true);
    }

    public static EditingState newCodeEditorEditingState(boolean languageTabVisible,
            boolean secondNotThirdLanguageVisible) {
        return new EditingState(languageTabVisible, secondNotThirdLanguageVisible, true);
    }

    public static EditingState newMetadataEditingState(boolean languageTabVisible,
            boolean secondNotThirdLanguageVisible) {
        return new EditingState(languageTabVisible, secondNotThirdLanguageVisible, false);
    }
}
