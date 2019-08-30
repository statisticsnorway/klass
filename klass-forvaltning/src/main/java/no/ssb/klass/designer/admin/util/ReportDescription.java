package no.ssb.klass.designer.admin.util;

public interface ReportDescription<E extends Enum<?>> {
    String getDisplayName();

    String getFilename();

    E getChoice();
}