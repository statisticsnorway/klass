package no.ssb.klass.designer.admin.util;

public enum UseStatisticsModeChoice implements ReportDescription<UseStatisticsModeChoice> {
    TOTAL_CLASSIFIC("Totalt antall kodeverk hentet ut", "total_hentet_ut.csv"),
    NUMBEROF_SEARCH_RETURNED_NULL("Antall søk som returnerte nulltreff", "søk_med_null_treff.csv"),
    TOTAL_SEARCH_WORDS("Totalt antall søkeord benyttet", "totalt_antall_søkeord.csv");

    private final String displayName;
    private final String filename;

    UseStatisticsModeChoice(String displayName, String filename) {
        this.displayName = displayName;
        this.filename = filename;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getFilename() {
        return filename;
    }

    @Override
    public UseStatisticsModeChoice getChoice() {
        return this;
    }
}