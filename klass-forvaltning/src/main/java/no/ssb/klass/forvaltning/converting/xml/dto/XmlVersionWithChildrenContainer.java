package no.ssb.klass.forvaltning.converting.xml.dto;

import java.util.List;
import java.util.Map;

/**
 * @author Mads Lundemo, SSB.
 *
 *         This data structure is for practical reasons made as a list of rows for easy XML import in Excel. Each map
 *         element in the data list is a map between column and its corresponding value.
 *
 *         tl;dr: one map -> one row in Excel
 */
public class XmlVersionWithChildrenContainer {

    private List<Map<String, String>> data;

    public void setData(List<Map<String, String>> data) {
        this.data = data;
    }

    public List<Map<String, String>> getData() {
        return data;
    }

}
