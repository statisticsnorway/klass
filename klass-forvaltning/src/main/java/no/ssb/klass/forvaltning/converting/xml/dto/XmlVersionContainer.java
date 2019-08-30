package no.ssb.klass.forvaltning.converting.xml.dto;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import no.ssb.klass.core.model.ClassificationItem;
import no.ssb.klass.core.model.Language;

/**
 * @author Mads Lundemo, SSB.
 */

@JacksonXmlRootElement(localName = "versjon")
public class XmlVersionContainer {

    public static final String DATE_FORMAT_STRING = "dd.MM.yyyy";
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern(DATE_FORMAT_STRING);

    public static final String CODE = "kode";
    public static final String PARENT = "forelder";

    public static final String NAME_NB = "navn_bokmål";
    public static final String NAME_NN = "navn_nynorsk";
    public static final String NAME_EN = "navn_engelsk";

    public static final String SHORT_NAME_NB = "kortnavn_bokmål";
    public static final String SHORT_NAME_NN = "kortnavn_nynorsk";
    public static final String SHORT_NAME_EN = "kortnavn_engelsk";

    public static final String NOTES_NB = "noter_bokmål";
    public static final String NOTES_NN = "noter_nynorsk";
    public static final String NOTES_EN = "noter_engelsk";

    public static final String VALID_FROM = "gyldig_fra";
    public static final String VALID_TO = "gyldig_til";

    @JacksonXmlProperty(localName = "element")
    @JacksonXmlElementWrapper(useWrapping = false)
    private XmlVersionItem[] items;

    public XmlVersionContainer(List<XmlVersionItem> itemList) {
        items = new XmlVersionItem[itemList.size()];
        itemList.toArray(items);
    }

    protected XmlVersionContainer() {

    }

    public void setItems(XmlVersionItem[] items) {
        this.items = items;
    }

    public List<XmlVersionItem> getItems() {
        return Arrays.asList(items);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class XmlVersionItem implements XmlCodeHierarchy {

        @JacksonXmlProperty(localName = CODE)
        private String code;
        @JacksonXmlProperty(localName = PARENT)
        private String parentCode;

        @JacksonXmlProperty(localName = NAME_NB)
        private String nameNB;
        @JacksonXmlProperty(localName = NAME_NN)
        private String nameNN;
        @JacksonXmlProperty(localName = NAME_EN)
        private String nameEN;

        @JacksonXmlProperty(localName = SHORT_NAME_NB)
        private String shortNameNB;
        @JacksonXmlProperty(localName = SHORT_NAME_NN)
        private String shortNameNN;
        @JacksonXmlProperty(localName = SHORT_NAME_EN)
        private String shortNameEN;

        @JacksonXmlProperty(localName = NOTES_NB)
        private String notesNB;
        @JacksonXmlProperty(localName = NOTES_NN)
        private String notesNN;
        @JacksonXmlProperty(localName = NOTES_EN)
        private String notesEN;

        @JacksonXmlProperty(localName = VALID_FROM)
        private String validFrom;
        @JacksonXmlProperty(localName = VALID_TO)
        private String validTo;

        public XmlVersionItem(ClassificationItem item) {
            code = item.getCode();
            parentCode = item.getParent() == null ? "" : item.getParent().getCode();

            nameNB = item.getOfficialName(Language.NB);
            nameNN = item.getOfficialName(Language.NN);
            nameEN = item.getOfficialName(Language.EN);

            shortNameNB = item.getShortName(Language.NB);
            shortNameNN = item.getShortName(Language.NN);
            shortNameEN = item.getShortName(Language.EN);

            notesNB = item.getNotes(Language.NB);
            notesNN = item.getNotes(Language.NN);
            notesEN = item.getNotes(Language.EN);

            validFrom = item.getValidFrom() == null ? "" : DATE_FORMAT.format(item.getValidFrom());
            validTo = item.getValidTo() == null ? "" : DATE_FORMAT.format(item.getValidTo());
        }

        protected XmlVersionItem() {

        }

        public String getCode() {
            return StringUtils.trim(code);
        }

        public String getParentCode() {
            return StringUtils.trim(parentCode);
        }

        public String getNameNB() {
            return StringUtils.trim(nameNB);
        }

        public String getNameNN() {
            return StringUtils.trim(nameNN);
        }

        public String getNameEN() {
            return StringUtils.trim(nameEN);
        }

        public String getShortNameNB() {
            return StringUtils.trim(shortNameNB);
        }

        public String getShortNameNN() {
            return StringUtils.trim(shortNameNN);
        }

        public String getShortNameEN() {
            return StringUtils.trim(shortNameEN);
        }

        public String getNotesNB() {
            return StringUtils.trim(notesNB);
        }

        public String getNotesNN() {
            return StringUtils.trim(notesNN);
        }

        public String getNotesEN() {
            return StringUtils.trim(notesEN);
        }

        public String getValidFrom() {
            return StringUtils.trim(validFrom);
        }

        public String getValidTo() {
            return StringUtils.trim(validTo);
        }

        @JsonIgnore
        public boolean isEmpty() {
            return new EqualsBuilder()
                    .append(code, null)
                    .append(parentCode, null)
                    .append(nameNB, null)
                    .append(nameNN, null)
                    .append(nameEN, null)
                    .append(shortNameNB, null)
                    .append(shortNameNN, null)
                    .append(shortNameEN, null)
                    .append(notesNB, null)
                    .append(notesNN, null)
                    .append(notesEN, null)
                    .append(validFrom, null)
                    .append(validTo, null)
                    .isEquals();
        }
    }
}