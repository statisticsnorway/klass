package no.ssb.klass.forvaltning.converting.xml.abstracts;

import static no.ssb.klass.core.model.StatisticalClassification.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import no.ssb.klass.forvaltning.converting.exception.ImportException;
import no.ssb.klass.forvaltning.converting.xml.dto.XmlCodeHierarchy;
import no.ssb.klass.core.model.ClassificationItem;
import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.model.StatisticalClassification;

/**
 * @author Mads Lundemo, SSB.
 */
public abstract class XmlCodeHierarchyService<T, U> extends AbstractXmlService<T> {

    protected abstract ClassificationItem createClassificationItem(U xmlItem, ClassificationVersion owner)
            throws ImportException;

    protected void mergeItemsWithClassification(StatisticalClassification classification,
            Map<ClassificationItem, ClassificationItem> itemMap) throws ImportException {
        for (Map.Entry<ClassificationItem, ClassificationItem> entry : itemMap.entrySet()) {
            ClassificationItem item = entry.getKey();
            ClassificationItem parent = entry.getValue();
            int levelId = findLevel(parent, itemMap);
            while (classification.getLevels().size() < levelId) {
                classification.addNextLevel();
            }
            classification.addClassificationItem(item, levelId, parent);

        }
    }

    private int findLevel(ClassificationItem parent, Map<ClassificationItem, ClassificationItem> itemMap)
            throws ImportException {
        if (parent == null) {
            return FIRST_LEVEL_NUMBER;
        } else if (parent.getLevel() != null) {
            return parent.getLevel().getLevelNumber() + 1;
        } else {
            if (itemMap.containsKey(parent)) {
                return findLevel(itemMap.get(parent), itemMap) + 1;
            } else {
                throw new ImportException("Kunne ikke bestemme nivå for kode" + parent.getCode());
            }
        }
    }

    protected Map<ClassificationItem, ClassificationItem> createClassificationItems(
            StatisticalClassification classification, List<XmlCodeHierarchy> values) throws ImportException {

        Map<ClassificationItem, ClassificationItem> itemMap = new HashMap<>();
        for (XmlCodeHierarchy xmlItem : values) {
            if (!xmlItem.isEmpty()) {
                createIfMissingItem(xmlItem, classification, values, itemMap);
            }
        }
        return itemMap;
    }

    @SuppressWarnings("unchecked")
    private ClassificationItem createIfMissingItem(XmlCodeHierarchy xmlItem, StatisticalClassification classification,
            List<XmlCodeHierarchy> values, Map<ClassificationItem, ClassificationItem> itemMap)
            throws ImportException {

        Optional<Map.Entry<ClassificationItem, ClassificationItem>> existing = itemMap.entrySet().stream()
                .filter(entry -> Objects.equals(entry.getKey().getCode(), xmlItem.getCode()))
                .findFirst();

        if (existing.isPresent()) {
            return existing.get().getKey();
        }

        ClassificationItem variantItem = createClassificationItem((U) xmlItem, (ClassificationVersion) classification);
        String parentCode = xmlItem.getParentCode();

        if (StringUtils.isEmpty(parentCode)) {
            // no parent (OK)
            itemMap.put(variantItem, null);
            return variantItem;
        } else {
            if (isParentInImport(xmlItem, values)) {
                // parent should exist in import file (OK)
                ClassificationItem parent = findAndCreateParent(xmlItem, classification, values, itemMap);
                itemMap.put(variantItem, parent);
                return variantItem;
            } else if (classification.hasClassificationItem(parentCode)) {
                // parent exists in version (OK)
                ClassificationItem parent = classification.findItem(parentCode);
                itemMap.put(variantItem, parent);
                return variantItem;
            } else {
                throw new ImportException("Kan ikke finne element med kode" + xmlItem.getParentCode());
            }
        }

    }

    private boolean isParentInImport(XmlCodeHierarchy xmlItem, List<XmlCodeHierarchy> values) throws ImportException {
        return values.stream().anyMatch(xmlVersionItem -> Objects.equals(xmlVersionItem.getCode(), xmlItem
                .getParentCode()));
    }

    private ClassificationItem findAndCreateParent(XmlCodeHierarchy xmlItem, StatisticalClassification classification,
            List<XmlCodeHierarchy> values, Map<ClassificationItem, ClassificationItem> itemMap) throws ImportException {

        Optional<XmlCodeHierarchy> parent = values.stream()
                .filter(xmlVersionItem -> Objects.equals(xmlVersionItem.getCode(), xmlItem.getParentCode()))
                .findFirst();

        if (!parent.isPresent()) {
            throw new ImportException("Kan ikke finne element med kode" + xmlItem.getParentCode());
        } else {
            return createIfMissingItem(parent.get(), classification, values, itemMap);
        }
    }

}
