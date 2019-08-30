package no.ssb.klass.forvaltning.converting.xml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import no.ssb.klass.solr.config.ConfigurationProfiles;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.base.Strings;

import no.ssb.klass.forvaltning.converting.exception.ImportException;
import no.ssb.klass.forvaltning.converting.xml.abstracts.XmlCodeHierarchyService;
import no.ssb.klass.forvaltning.converting.xml.dto.XmlCodeHierarchy;
import no.ssb.klass.forvaltning.converting.xml.dto.XmlVariantContainer.XmlVariantItem;
import no.ssb.klass.forvaltning.converting.xml.dto.XmlVariantExportContainer;
import no.ssb.klass.core.model.ClassificationItem;
import no.ssb.klass.core.model.ClassificationVariant;
import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.model.ConcreteClassificationItem;
import no.ssb.klass.core.model.ReferencingClassificationItem;
import no.ssb.klass.core.service.ClassificationService;
import no.ssb.klass.core.util.Translatable;

/**
 * @author Mads Lundemo, SSB.
 */
@Service
@Profile(ConfigurationProfiles.FRONTEND_ONLY)
public class ClassificationVariantXmlService extends XmlCodeHierarchyService<ClassificationVariant, XmlVariantItem> {

    @Autowired
    private ClassificationService classificationService;

    @Override
    public InputStream toXmlStream(ClassificationVariant variant) {
        XmlVariantExportContainer container = variantToDto(variant);
        ObjectWriter writer = getObjectWriter(XmlVariantExportContainer.class);
        return createInputStream(container, writer);
    }

    @Override
    public void fromXmlStreamAndMerge(InputStream stream, ClassificationVariant variant) throws ImportException {
        ClassificationVersion version =
                classificationService.getClassificationVersion(variant.getClassificationVersion().getId());
        List<XmlVariantItem> values = readInputStream(stream, XmlVariantItem.class);
        checkForMissingTitles(values);
        checkForMissingCodes(values);
        checkForDuplicatedCodes(values);
        Map<ClassificationItem, ClassificationItem> itemMap =
                createClassificationItems(version, values.stream()
                        .map(xmlVariantItem -> (XmlCodeHierarchy) xmlVariantItem)
                        .collect(Collectors.toList()));
        mergeItemsWithClassification(variant, itemMap);
    }

    private void checkForDuplicatedCodes(List<XmlVariantItem> values) throws ImportException {
        List<String> lookupTableCode = new ArrayList<>();
        List<String> lookupTableSourceCode = new ArrayList<>();
        for (XmlVariantItem variantItem : values) {
            String code = variantItem.getCode();
            String sourceCode = variantItem.getSourceCode();
            if (code != null) {
                if (lookupTableCode.contains(code)) {
                    throw new ImportException(
                            "Alle elementer må ha en unik kode, følgende kode er brukt flere ganger : " + code);
                }
                lookupTableCode.add(code);
            } else if (sourceCode != null) {
                if (lookupTableSourceCode.contains(sourceCode)) {
                    throw new ImportException(
                            "Alle elementer må ha en unik kode, følgende kode er brukt flere ganger : "
                                    + sourceCode);
                }
                lookupTableSourceCode.add(sourceCode);
            }
        }
    }

    private void checkForMissingCodes(List<XmlVariantItem> values) throws ImportException {
        for (XmlVariantItem variantItem : values) {
            if (!variantItem.isEmpty()
                    && Strings.isNullOrEmpty(variantItem.getCode())
                    && Strings.isNullOrEmpty(variantItem.getSourceCode())) {
                throw new ImportException("Ett eller flere elementer mangler kode");
            }
        }
    }

    private void checkForMissingTitles(List<XmlVariantItem> values) throws ImportException {
        for (XmlVariantItem variantItem : values) {
            if (!variantItem.isEmpty() && !variantItem.isReferenced()
                    && Strings.isNullOrEmpty(variantItem.getNameNB())
                    && Strings.isNullOrEmpty(variantItem.getNameNN())
                    && Strings.isNullOrEmpty(variantItem.getNameEN())) {
                throw new ImportException("Element med kode " + variantItem.getCode() + " mangler tittel");
            }
        }
    }

    private XmlVariantExportContainer variantToDto(ClassificationVariant variant) {
        List<XmlVariantItem> list = variant.getAllClassificationItems()
                .stream()
                .map(XmlVariantItem::new)
                .sorted((a, b) -> {
                    // sorting by matching parent
                    String group1a = !Strings.isNullOrEmpty(a.getCode()) ? a.getCode() : a.getParentCode();
                    String group1b = !Strings.isNullOrEmpty(b.getCode()) ? b.getCode() : b.getParentCode();
                    int compare = ObjectUtils.compare(group1a, group1b);
                    return compare != 0 ? compare : ObjectUtils.compare(a.getSourceCode(), b.getSourceCode());
                })
                .collect(Collectors.toList());

        XmlVariantExportContainer container = new XmlVariantExportContainer(list);
        container.setSchemaBaseUrl(SchemaBaseUrl);
        return container;
    }

    @Override
    protected ClassificationItem createClassificationItem(XmlVariantItem xmlItem, ClassificationVersion version)
            throws ImportException {
        if (StringUtils.isEmpty(xmlItem.getCode())) {
            ClassificationItem item = getClassificationReferenceItem(xmlItem, version);
            return new ReferencingClassificationItem(item);
        } else {
            return new ConcreteClassificationItem(xmlItem.getCode(),
                    new Translatable(xmlItem.getNameNB(), xmlItem.getNameNN(), xmlItem.getNameEN()), Translatable
                            .empty());
        }
    }

    private ClassificationItem getClassificationReferenceItem(XmlVariantItem xmlItem, ClassificationVersion version)
            throws ImportException {
        try {
            return version.findItem(xmlItem.getSourceCode());
        } catch (IllegalArgumentException e) {
            throw new ImportException("Kan ikke importere variant. "
                    + "Det ble ikke funnet noe element med kode\"" + xmlItem.getSourceCode()
                    + "\" i versjon " + version.getNameInPrimaryLanguage());
        }
    }
}
