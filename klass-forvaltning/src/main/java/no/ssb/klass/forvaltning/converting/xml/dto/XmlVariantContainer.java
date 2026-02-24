package no.ssb.klass.forvaltning.converting.xml.dto;

import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.google.common.base.Strings;

import no.ssb.klass.core.model.ClassificationItem;
import no.ssb.klass.core.model.ConcreteClassificationItem;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.model.ReferencingClassificationItem;

/**
 * @author Mads Lundemo, SSB.
 */
@JacksonXmlRootElement(localName = "variant")
public class XmlVariantContainer {

    public static final String KODE = "kode";
    public static final String NAVN_BOKMAAL = "navn_bokmål";
    public static final String NAVN_NYNORSK = "navn_nynorsk";
    public static final String NAVN_ENGELSK = "navn_engelsk";
    public static final String KILDE_KODE = "kilde_kode";
    public static final String FORELDER = "forelder";

    @JacksonXmlProperty(localName = "element")
    @JacksonXmlElementWrapper(useWrapping = false)
    private XmlVariantItem[] items;

    public XmlVariantContainer(List<XmlVariantItem> itemList) {
        items = new XmlVariantItem[itemList.size()];
        itemList.toArray(items);
    }

    protected XmlVariantContainer() {
    }

    void setItems(XmlVariantItem[] items) {
        this.items = items;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class XmlVariantItem implements XmlCodeHierarchy {

        @JacksonXmlProperty(localName = KODE)
        private String code;

        @JacksonXmlProperty(localName = NAVN_BOKMAAL)
        private String nameNB;
        @JacksonXmlProperty(localName = NAVN_NYNORSK)
        private String nameNN;
        @JacksonXmlProperty(localName = NAVN_ENGELSK)
        private String nameEN;

        @JacksonXmlProperty(localName = KILDE_KODE)
        private String sourceCode;
        @JacksonXmlProperty(localName = FORELDER)
        private String parentCode;

        public XmlVariantItem(ClassificationItem item) {
            if (item instanceof ReferencingClassificationItem) {
                populate((ReferencingClassificationItem) item);
            } else if (item instanceof ConcreteClassificationItem) {
                populate((ConcreteClassificationItem) item);
            }
        }

        private void populate(ReferencingClassificationItem item) {
            sourceCode = item.getCode();
            if (item.getParent() != null) {
                parentCode = item.getParent().getCode();
            }

        }

        private void populate(ConcreteClassificationItem item) {
            code = item.getCode();
            nameNB = item.getOfficialName(Language.NB);
            nameNN = item.getOfficialName(Language.NN);
            nameEN = item.getOfficialName(Language.EN);
        }

        protected XmlVariantItem() {

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

        public void setCode(String code) {
            this.code = code;
        }

        public void setParentCode(String parentCode) {
            this.parentCode = parentCode;
        }

        public void setNameNB(String nameNB) {
            this.nameNB = nameNB;
        }

        public void setNameNN(String nameNN) {
            this.nameNN = nameNN;
        }

        public void setNameEN(String nameEN) {
            this.nameEN = nameEN;
        }

        public String getSourceCode() {
            return StringUtils.trim(sourceCode);
        }

        public void setSourceCode(String sourceCode) {
            this.sourceCode = sourceCode;
        }

        @JsonIgnore
        public boolean isEmpty() {
            return new EqualsBuilder()
                    .append(code, null)
                    .append(parentCode, null)
                    .append(nameNB, null)
                    .append(nameNN, null)
                    .append(nameEN, null)
                    .append(sourceCode, null)
                    .isEquals();
        }

        @JsonIgnore
        public boolean isReferenced() {
            return !Strings.isNullOrEmpty(sourceCode) && Strings.isNullOrEmpty(code);
        }
    }
}
