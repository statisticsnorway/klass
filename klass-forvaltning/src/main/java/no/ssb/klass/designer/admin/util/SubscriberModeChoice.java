package no.ssb.klass.designer.admin.util;

public enum SubscriberModeChoice implements SubscriberReportDescription<SubscriberModeChoice> {
    TOTAL("Totalt antall abonnementer", "total.csv"),
    INTERNAL("Interne abonnementer", "internal.csv"),
    EXTERNAL("Eksterne abonnementer", "external.csv");

    private final String displayName;
    private final String filename;

    SubscriberModeChoice(String displayName, String filename) {
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
    public SubscriberModeChoice getChoice() {
        return this;
    }
}