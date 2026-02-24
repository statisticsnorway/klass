package no.ssb.klass.designer.admin.util;

public interface SubscriberReportDescription<E extends Enum<?>> {
    String getDisplayName();

    String getFilename();

    E getChoice();
}