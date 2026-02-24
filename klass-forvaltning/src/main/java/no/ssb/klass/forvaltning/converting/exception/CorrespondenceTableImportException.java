package no.ssb.klass.forvaltning.converting.exception;

import static no.ssb.klass.forvaltning.converting.xml.dto.XmlCorrespondenceContainer.*;

import java.util.List;

/**
 * @author Mads Lundemo, SSB.
 */
public class CorrespondenceTableImportException extends ImportException {

    public CorrespondenceTableImportException(List<XmlCorrespondenceItem> exceptions) {
        super(CreateMessage(exceptions));
    }

    private static String CreateMessage(List<XmlCorrespondenceItem> exceptions) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("Følgende korrespondanser kan ikke opprettes\n");
        for (XmlCorrespondenceItem item : exceptions) {
            stringBuffer.append(item.getFromCode()).append(" (").append(item.getFromName()).append(")")
                    .append(" - ")
                    .append(item.getToCode()).append(" (").append(item.getToName()).append(")")
                    .append("\n");

        }
        return stringBuffer.toString();
    }
}
