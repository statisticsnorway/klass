package no.ssb.klass.forvaltning.converting.xml.dto;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import no.ssb.klass.forvaltning.converting.exception.ImportException;
import no.ssb.klass.core.model.ClassificationItem;
import no.ssb.klass.core.model.CorrespondenceMap;
import no.ssb.klass.core.model.Language;

/**
 * @author Mads Lundemo, SSB.
 */

@JacksonXmlRootElement(localName = "Korrespondansetabell")
public class XmlCorrespondenceContainer {

    public static final String KILDE_KODE = "kilde_kode";
    public static final String KILDE_TITTEL = "kilde_tittel";
    public static final String MAAL_KODE = "mål_kode";
    public static final String MAAL_TITTEL = "mål_tittel";

    @JacksonXmlProperty(localName = "Korrespondanse")
    @JacksonXmlElementWrapper(useWrapping = false)
    private XmlCorrespondenceContainer.XmlCorrespondenceItem[] items;

    @JsonIgnore
    private String name;

    public XmlCorrespondenceContainer(List<XmlCorrespondenceContainer.XmlCorrespondenceItem> itemList) {
        items = new XmlCorrespondenceContainer.XmlCorrespondenceItem[itemList.size()];
        itemList.toArray(items);
    }

    protected XmlCorrespondenceContainer() {

    }

    void setItems(XmlCorrespondenceItem[] items) {
        this.items = items;
    }

    XmlCorrespondenceItem[] getItems() {
        return items;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class XmlCorrespondenceItem {

        private static final Pattern IS_MULTI_CELL_PATTERN = Pattern.compile("(.+\\n+)");
        private static final Predicate<String> IS_MULTI_CELL_PREDICATE = IS_MULTI_CELL_PATTERN.asPredicate();

        private static final Pattern EXTRACT_MULTI_CELL_PATTERN = Pattern.compile("((?<code>(.+))\\n*)");

        @JacksonXmlProperty(localName = KILDE_KODE)
        private String fromCode;

        @JacksonXmlProperty(localName = KILDE_TITTEL)
        private String fromName;

        @JacksonXmlProperty(localName = MAAL_KODE)
        private String toCode;

        @JacksonXmlProperty(localName = MAAL_TITTEL)
        private String toName;

        public XmlCorrespondenceItem(CorrespondenceMap map, Language sourceLanguage, Language targetLanguage) {
            if (map.getSource().isPresent()) {
                ClassificationItem source = map.getSource().get();
                fromCode = source.getCode();
                fromName = source.getOfficialName(sourceLanguage);
            }
            if (map.getTarget().isPresent()) {
                ClassificationItem target = map.getTarget().get();
                toCode = target.getCode();
                toName = target.getOfficialName(targetLanguage);
            }
        }

        private XmlCorrespondenceItem(String sourceCode, String targetCode) {
            fromCode = sourceCode;
            toCode = targetCode;
        }

        protected XmlCorrespondenceItem() {
        }

        public String getFromCode() {
            return StringUtils.trim(fromCode);
        }

        public String getFromName() {
            return StringUtils.trim(fromName);
        }

        public String getToCode() {
            return StringUtils.trim(toCode);
        }

        public String getToName() {
            return StringUtils.trim(toName);
        }

        @JsonIgnore
        public boolean isMultiCell() {
            if (getToCode() == null || getFromCode() == null) {
                return false;
            }
            return IS_MULTI_CELL_PREDICATE.test(getToCode()) || IS_MULTI_CELL_PREDICATE.test(getFromCode());

        }

        @JsonIgnore
        public Collection<XmlCorrespondenceItem> splitMultiCell() throws ImportException {
            List<XmlCorrespondenceItem> items = new LinkedList<>();

            List<String> toCodes = extractCodes(toCode);
            List<String> fromCodes = extractCodes(fromCode);

            validateMultiCellData(toCodes, fromCodes);
            if (toCodes.size() > 1) {
                String from = fromCodes.get(0);
                for (String to : toCodes) {
                    XmlCorrespondenceItem newItem = new XmlCorrespondenceItem(from, to);
                    items.add(newItem);
                }
            } else if (fromCodes.size() > 1) {
                String to = toCodes.get(0);
                for (String from : fromCodes) {
                    XmlCorrespondenceItem newItem = new XmlCorrespondenceItem(from, to);
                    items.add(newItem);
                }
            }
            return items;
        }

        @JsonIgnore
        private void validateMultiCellData(List<String> toCodes, List<String> fromCodes) throws ImportException {
            if (toCodes.isEmpty() ^ fromCodes.isEmpty()) {
                throw new ImportException("Kan ikke importere korrespondansetabell; kode mangler");
            }
            if (toCodes.size() > 1 && fromCodes.size() > 1) {
                throw new ImportException("Import støtter ikke flere verdier i både "
                        + XmlCorrespondenceContainer.KILDE_KODE + " og "
                        + XmlCorrespondenceContainer.MAAL_KODE + "\n kun en til mange konbinasjoner");
            }
        }

        @JsonIgnore
        private List<String> extractCodes(String multiCell) {
            Matcher matcher = EXTRACT_MULTI_CELL_PATTERN.matcher(multiCell);
            List<String> codes = new LinkedList<>();
            while (matcher.find() && matcher.group("code") != null) {
                codes.add(matcher.group("code").trim());
            }
            return codes;
        }

        @JsonIgnore
        public boolean isEmpty() {
            return new EqualsBuilder()
                    .append(fromCode, null)
                    .append(fromName, null)
                    .append(toCode, null)
                    .append(toName, null)
                    .isEquals();
        }
    }
}
