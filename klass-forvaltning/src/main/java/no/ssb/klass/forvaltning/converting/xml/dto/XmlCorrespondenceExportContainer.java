package no.ssb.klass.forvaltning.converting.xml.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * "hack" to add schemaLocation since Jackson does not seem to support it in current version
 *
 * @author Mads Lundemo, SSB.
 */
// CHECKSTYLE:OFF
public class XmlCorrespondenceExportContainer extends XmlCorrespondenceContainer {

    @JacksonXmlProperty(localName = "xmlns", isAttribute = true)
    public final String namespace = "http://klass.ssb.no/correspondenceTable";

    @JacksonXmlProperty(localName = "xmlns:xsi", isAttribute = true)
    public final String schemaNamespace = "http://www.w3.org/2001/XMLSchema-instance";

    @JacksonXmlProperty(localName = "xsi:schemaLocation", isAttribute = true)
    public String schemaLocation;

    @JsonIgnore
    public void setSchemaBaseUrl(String SchemaBaseUrl) {
        schemaLocation = namespace + " " + SchemaBaseUrl + "/correspondenceTable.xsd";
    }

    public XmlCorrespondenceExportContainer(List<XmlCorrespondenceItem> itemList) {
        super(itemList);
    }
}
// CHECKSTYLE:ON