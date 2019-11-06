package no.ssb.klass.api.controllers;

/**
 * @author Mads Lundemo, SSB.
 */
public class MonitorStatus {

    private final String name;
    private final boolean successful;
    private final String description;

    public MonitorStatus(String statusName, Boolean everythingOk, String optionalDescription) {
        this.name = statusName;
        this.successful = everythingOk;
        this.description = optionalDescription;
    }

    public MonitorStatus(String statusName, Boolean successful) {
        this(statusName, successful, "");
    }

    public String getName() {
        return name;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public String getDescription() {
        return description;
    }
}
