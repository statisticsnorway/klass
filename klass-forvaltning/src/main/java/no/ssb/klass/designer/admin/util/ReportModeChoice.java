package no.ssb.klass.designer.admin.util;

public enum ReportModeChoice implements ReportDescription<ReportModeChoice> {
    TOTAL("Totalt antall kodeverk", "total.csv"),
    PUBLISHED("Publiserte kodeverk", "publisert.csv"),
    UNPUBLISHED("Upubliserte kodeverk", "upublisert.csv"),
    MISSING_LANG("Publiserte versjoner som mangler språk", "mangler_språk.csv");

    private final String displayName;
    private final String filename;

    ReportModeChoice(String displayName, String filename) {
        this.displayName = displayName;
        this.filename = filename;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public ReportModeChoice getChoice() {
        return this;
    }

    @Override
    public String getFilename() {
        return filename;
    }
}