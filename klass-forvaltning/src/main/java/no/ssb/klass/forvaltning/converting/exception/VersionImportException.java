package no.ssb.klass.forvaltning.converting.exception;

import static no.ssb.klass.forvaltning.converting.xml.dto.XmlVersionContainer.*;

import java.util.List;

/**
 * @author Mads Lundemo, SSB.
 */
public class VersionImportException extends ImportException {

    public VersionImportException(String message, List<XmlVersionItem> exceptions) {
        super(message + CreateMessage(exceptions));
    }

    private static String CreateMessage(List<XmlVersionItem> exceptions) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("\n");
        for (XmlVersionItem item : exceptions) {
            stringBuffer.append(item.getCode()).append("\n");
        }
        return stringBuffer.toString();
    }
}
