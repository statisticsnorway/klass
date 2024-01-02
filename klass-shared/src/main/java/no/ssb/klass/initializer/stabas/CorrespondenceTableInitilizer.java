package no.ssb.klass.initializer.stabas;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.ssb.klass.core.model.ClassificationItem;
import no.ssb.klass.core.model.CorrespondenceMap;
import no.ssb.klass.core.model.CorrespondenceTable;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.service.ClassificationService;
import no.ssb.klass.core.util.Translatable;
import no.ssb.ns.meta.correspondence.CorrespondenceItemGroupType;
import no.ssb.ns.meta.correspondence.CorrespondenceItemRelationType;
import no.ssb.ns.meta.correspondence.CorrespondenceItemType;
import no.ssb.ns.meta.correspondence.CorrespondenceTableType;
import no.ssb.ns.meta.correspondence.ObjectFactory;

class CorrespondenceTableInitilizer {
    private static final Logger log = LoggerFactory.getLogger(CorrespondenceTableInitilizer.class);
    private final Unmarshaller jaxbUnmarshaller;
    private final ClassificationService classificationService;
    private final StabasConfiguration stabasConfiguration;
    private final Set<String> requiredVersionIds;
    private final Map<String, ClassificationVersionWrapper> versions;

    CorrespondenceTableInitilizer(ClassificationService classificationService, StabasConfiguration stabasConfiguration)
            throws Exception {
        this.classificationService = classificationService;
        this.stabasConfiguration = stabasConfiguration;
        this.jaxbUnmarshaller = JAXBContext.newInstance(ObjectFactory.class).createUnmarshaller();
        this.requiredVersionIds = new HashSet<>();
        this.versions = new HashMap<>();
        populateRequiredVersions();
    }

    /**
     * Checks if version is a required version (a required version is one that is referenced by a correspondence table).
     * If so the version is stored to later be used when creating correspondence table
     */
    public void preparedVersion(ClassificationVersionWrapper version) {
        if (requiredVersionIds.contains(version.getStabasVersionId())) {
            versions.put(version.getStabasVersionId(), version);
        }
    }

    public void run() throws IOException {
        try (ZipFile zippedCorrespondenceTables = new ZipFile(StabasUtils.openFile(stabasConfiguration
                .getCorrespondenceTablesZipFile()))) {
            for (ZipEntry correspondenceTableEntry : StabasUtils.listEntriesInZip(zippedCorrespondenceTables)) {
                try {
                    log.info("Importing correspondencetable: " + correspondenceTableEntry.getName());
                    CorrespondenceTableType stabasCorrespondenceTable = unmarshallCorrespondenceTable(StabasUtils
                            .getInputStream(zippedCorrespondenceTables, correspondenceTableEntry));
                    if (!stabasCorrespondenceTable.isShowInternet() && !stabasCorrespondenceTable.isShowIntranet()) {
                        log.warn("Not importing not published correspondencetable: " + correspondenceTableEntry
                                .getName());
                        continue;
                    }
                    if (isReferencingFilteredVariant(stabasCorrespondenceTable)) {
                        continue;
                    }
                    CorrespondenceTable correspondenceTable = createCorrespondenceTable(stabasCorrespondenceTable);
                    classificationService.saveNotIndexCorrespondenceTable(correspondenceTable);
                } catch (Exception e) {
                    log.error("Failed correspondence table: " + correspondenceTableEntry.getName(), e);
                }
            }
        }
    }

    private boolean isReferencingFilteredVariant(CorrespondenceTableType stabasCorrespondenceTable) {
        return isReferencingFilteredVariant(stabasCorrespondenceTable.getClassVersionSourceId())
                || isReferencingFilteredVariant(stabasCorrespondenceTable.getClassVersionTargetId());
    }

    private boolean isReferencingFilteredVariant(String fullStabasId) {
        if (VariantFilter.isVariant(StabasUtils.parseUrn(fullStabasId))) {
            log.warn("Not importing correspondencetable that references a filtered variant: " + StabasUtils.parseUrn(
                    fullStabasId));
            return true;
        }
        return false;
    }

    private CorrespondenceTable createCorrespondenceTable(CorrespondenceTableType stabasCorrespondenceTable) {
        ClassificationVersionWrapper sourceVersion = getVersion(stabasCorrespondenceTable.getClassVersionSourceId());
        ClassificationVersionWrapper targetVersion = getVersion(stabasCorrespondenceTable.getClassVersionTargetId());

        CorrespondenceTable correspondenceTable = newCorrespondenceTable(stabasCorrespondenceTable, sourceVersion,
                targetVersion);
        Map<String, CorrespondenceMap> correspondenceMapStore = new HashMap<>();
        for (CorrespondenceItemGroupType group : stabasCorrespondenceTable.getCorrespondenceItemGroup()) {
            for (CorrespondenceItemType correspondenceItem : group.getCorrespondenceItem()) {
                for (CorrespondenceItemRelationType relationItem : correspondenceItem.getCorrespondenceItemRelation()) {
                    ClassificationItem sourceClassificationItem;
                    ClassificationItem targetClassificationItem;
                    if (sourceVersion.hasItemWithStabasId(correspondenceItem.getId())) {
                        sourceClassificationItem = sourceVersion.findItemWithStabasId(correspondenceItem.getId());
                        targetClassificationItem = targetVersion.findItemWithStabasId(relationItem.getTargetItem());
                    } else {
                        sourceClassificationItem = sourceVersion.findItemWithStabasId(relationItem.getTargetItem());
                        targetClassificationItem = targetVersion.findItemWithStabasId(correspondenceItem.getId());
                    }
                    correspondenceMapStore.put(sourceClassificationItem.getId() + "-" + targetClassificationItem
                            .getId(), new CorrespondenceMap(sourceClassificationItem, targetClassificationItem));
                }
            }
        }
        for (CorrespondenceMap correspondenceMap : correspondenceMapStore.values()) {
            correspondenceTable.addCorrespondenceMap(correspondenceMap);
        }
        publish(correspondenceTable);

        return correspondenceTable;
    }

    private void publish(CorrespondenceTable correspondenceTable) {
        for (Language language : Language.values()) {
            if (correspondenceTable.canPublish(language).isEmpty()) {
                correspondenceTable.publish(language);
            }
        }
    }

    private CorrespondenceTable newCorrespondenceTable(CorrespondenceTableType stabasCorrespondenceTable,
            ClassificationVersionWrapper sourceVersion, ClassificationVersionWrapper targetVersion) {
        Translatable description = StabasUtils.createTranslatable(stabasCorrespondenceTable.getDescriptionGrp()
                .getDescription());
        int sourceLevelNumber = getLevelNumber(stabasCorrespondenceTable.getClassLevelSourceId(), sourceVersion);
        int targetLevelNumber = getLevelNumber(stabasCorrespondenceTable.getClassLevelTargetId(), targetVersion);
        return new CorrespondenceTable(description, sourceVersion.getVersion(), sourceLevelNumber, targetVersion
                .getVersion(), targetLevelNumber);
    }

    private int getLevelNumber(String stabasLevelId, ClassificationVersionWrapper version) {
        if (StabasUtils.parseUrn(stabasLevelId).trim().isEmpty()) {
            return 0;
        }
        return version.getLevel(stabasLevelId).getLevelNumber();
    }

    private ClassificationVersionWrapper getVersion(String fullVersionId) {
        String versionId = StabasUtils.parseUrn(fullVersionId);
        ClassificationVersionWrapper version = versions.get(versionId);
        if (version == null) {
            throw new IllegalArgumentException("No version with id: " + versionId);
        }
        return version;
    }

    private void populateRequiredVersions() throws IOException {
        try (ZipFile zippedCorredspondenceTables = new ZipFile(StabasUtils.openFile(stabasConfiguration
                .getCorrespondenceTablesZipFile()))) {
            for (ZipEntry correspondenceTableEntry : StabasUtils.listEntriesInZip(zippedCorredspondenceTables)) {
                CorrespondenceTableType correspondenceTable = unmarshallCorrespondenceTable(StabasUtils.getInputStream(
                        zippedCorredspondenceTables, correspondenceTableEntry));
                requiredVersionIds.add(StabasUtils.parseUrn(correspondenceTable.getClassVersionSourceId()));
                requiredVersionIds.add(StabasUtils.parseUrn(correspondenceTable.getClassVersionTargetId()));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private CorrespondenceTableType unmarshallCorrespondenceTable(InputStream inputStream) {
        try {
            JAXBElement<CorrespondenceTableType> jaxbElement = (JAXBElement<CorrespondenceTableType>) jaxbUnmarshaller
                    .unmarshal(inputStream);
            return jaxbElement.getValue();
        } catch (JAXBException e) {
            throw new RuntimeException("Failed to unmarshall", e);
        }
    }
}
