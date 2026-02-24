package no.ssb.klass.forvaltning.converting.xml.fullversionexport;

import static no.ssb.klass.forvaltning.converting.xml.fullversionexport.DataContainer.*;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import no.ssb.klass.forvaltning.converting.xml.dto.XmlCorrespondenceContainer;
import no.ssb.klass.forvaltning.converting.xml.dto.XmlVariantContainer;
import no.ssb.klass.forvaltning.converting.xml.dto.XmlVersionContainer;
import no.ssb.klass.forvaltning.converting.xml.dto.XmlVersionWithChildrenContainer;
import no.ssb.klass.core.model.ClassificationItem;
import no.ssb.klass.core.model.ClassificationVariant;
import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.model.ConcreteClassificationItem;
import no.ssb.klass.core.model.CorrespondenceMap;
import no.ssb.klass.core.model.CorrespondenceTable;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.model.ReferencingClassificationItem;
import no.ssb.klass.core.service.ClassificationService;

/**
 * @author Mads Lundemo, SSB.
 *
 *         This class will build a data structure in such a way that Jackson will output an XML that Excel will display
 *         in a usefull manner
 *
 */
public class XmlVersionWithChildrenBuilder {

    private List<DataContainer> correspondenceTables = new LinkedList<>();
    private List<DataContainer> variants = new LinkedList<>();
    private DataContainer version = new DataContainer();

    private boolean notes;
    private boolean shortNames;

    private int columnNumber = 0;
    private ClassificationService classificationService;

    public XmlVersionWithChildrenBuilder(ClassificationService classificationService,
            ClassificationVersion classificationVersion) {
        this.classificationService = classificationService;

        notes = classificationVersion.isIncludeNotes();
        shortNames = classificationVersion.hasShortNames();

        populateVersionDataContainer(classificationVersion);
        createCorrespondenceTableContainers(classificationVersion);
        createVariantContainers(classificationVersion);

    }

    private void createVariantContainers(ClassificationVersion classificationVersion) {
        for (ClassificationVariant classificationVariant : classificationVersion.getClassificationVariants()) {
            ClassificationVariant variant = classificationService.getClassificationVariant(classificationVariant
                    .getId());
            DataContainer container = new DataContainer();
            container.setClassificationName(variant.getNameInPrimaryLanguage());
            List<String> columns = FullExportColumnUtil.getVariantColumnsInExportOrder();
            generateColumns(container, columns);
            variants.add(container);

            for (ClassificationItem item : variant.getAllClassificationItems()) {
                container.createNextRow(item.getCode());
                if (item instanceof ReferencingClassificationItem) {
                    String parentCode = item.getParent() == null ? "" : item.getParent().getCode();
                    container.setFieldValue(XmlVariantContainer.FORELDER, parentCode);
                    container.setFieldValue(XmlVariantContainer.KILDE_KODE, item.getCode());
                } else if (item instanceof ConcreteClassificationItem) {
                    container.setFieldValue(XmlVariantContainer.KODE, item.getCode());
                    container.setFieldValue(XmlVariantContainer.NAVN_BOKMAAL, item.getOfficialName(Language.NB));
                    container.setFieldValue(XmlVariantContainer.NAVN_NYNORSK, item.getOfficialName(Language.NN));
                    container.setFieldValue(XmlVariantContainer.NAVN_ENGELSK, item.getOfficialName(Language.EN));
                } else {
                    throw new RuntimeException("Unknown ClassificationItem type");
                }
            }

        }
    }

    private void createCorrespondenceTableContainers(ClassificationVersion classificationVersion) {
        for (CorrespondenceTable correspondenceObj : classificationVersion.getCorrespondenceTables()) {
            CorrespondenceTable correspondenceTable =
                    classificationService.getCorrespondenceTable(correspondenceObj.getId());

            if (isCorrespondenceBetweenSameClassification(correspondenceTable)) {
                // we ignore tables mapping changes between versions
                continue;
            }

            DataContainer container = new DataContainer();
            container.setClassificationName(correspondenceTable.getNameInPrimaryLanguage());
            List<String> columns = FullExportColumnUtil.getCorrespondenceColumnsInExportOrder();
            generateColumns(container, columns);
            correspondenceTables.add(container);

            Language targetLanguage = correspondenceTable.getTarget().getPrimaryLanguage();
            for (CorrespondenceMap item : correspondenceTable.getCorrespondenceMaps()) {

                // merge lines with same (source- / version-) code
                boolean wasMerged = mergeIfRequired(container, item, targetLanguage);

                if (!wasMerged) {
                    String code = item.getSource().isPresent() ? item.getSource().get().getCode() : "";
                    container.createNextRow(code);

                    if (item.getTarget().isPresent()) {
                        ClassificationItem target = item.getTarget().get();
                        container.setFieldValue(XmlCorrespondenceContainer.MAAL_KODE, target.getCode());
                        container.setFieldValue(XmlCorrespondenceContainer.MAAL_TITTEL, target.getOfficialName(
                                targetLanguage));
                    }
                }

            }
        }
    }

    private boolean mergeIfRequired(DataContainer container, CorrespondenceMap item, Language targetLanguage) {
        if (item.getSource().isPresent()) {
            ClassificationItem source = item.getSource().get();
            int id = container.getRowIdByCode(source.getCode());
            if (id != NOT_FOUND) {
                if (item.getTarget().isPresent()) {
                    ClassificationItem target = item.getTarget().get();
                    container.addFieldValue(id, XmlCorrespondenceContainer.MAAL_KODE, target.getCode());
                    container.addFieldValue(id, XmlCorrespondenceContainer.MAAL_TITTEL, target.getOfficialName(
                            targetLanguage));
                }
                return true;
            }
        }
        return false;
    }

    private boolean isCorrespondenceBetweenSameClassification(CorrespondenceTable correspondenceTable) {
        return Objects.equals(correspondenceTable.getSource().getOwnerClassification().getId(), correspondenceTable
                .getTarget().getClassification().getId());
    }

    private void populateVersionDataContainer(ClassificationVersion classificationVersion) {
        version.setClassificationName(classificationVersion.getNameInPrimaryLanguage());
        List<String> versionColumns = FullExportColumnUtil.getVersionColumnsInExportOrder(shortNames, notes);
        generateColumns(version, versionColumns);
        for (ClassificationItem item : classificationVersion.getAllClassificationItems()) {
            version.createNextRow(item.getCode());
            version.setFieldValue(XmlVersionContainer.CODE, item.getCode());
            String parentCode = item.getParent() == null ? "" : item.getParent().getCode();
            version.setFieldValue(XmlVersionContainer.PARENT, parentCode);
            version.setFieldValue(XmlVersionContainer.NAME_NB, item.getOfficialName(Language.NB));
            version.setFieldValue(XmlVersionContainer.NAME_NN, item.getOfficialName(Language.NN));
            version.setFieldValue(XmlVersionContainer.NAME_EN, item.getOfficialName(Language.EN));
            if (notes) {
                version.setFieldValue(XmlVersionContainer.NOTES_NB, item.getNotes(Language.NB));
                version.setFieldValue(XmlVersionContainer.NOTES_NN, item.getNotes(Language.NN));
                version.setFieldValue(XmlVersionContainer.NOTES_EN, item.getNotes(Language.EN));
            }
            if (shortNames) {
                version.setFieldValue(XmlVersionContainer.SHORT_NAME_NB, item.getShortName(Language.NB));
                version.setFieldValue(XmlVersionContainer.SHORT_NAME_NN, item.getShortName(Language.NN));
                version.setFieldValue(XmlVersionContainer.SHORT_NAME_EN, item.getShortName(Language.EN));
            }
        }
    }

    private void generateColumns(DataContainer container, List<String> column) {
        for (String columnName : column) {
            container.addColumn(column(columnNumber++), columnName);
        }
    }

    public XmlVersionWithChildrenContainer build() {
        XmlVersionWithChildrenContainer container = new XmlVersionWithChildrenContainer();
        container.setData(generateData());
        return container;
    }

    private List<Map<String, String>> generateData() {
        List<Map<String, String>> data = new LinkedList<>();

        Map<String, String> columnNames = getColumnNames();
        Map<String, String> elementNames = generateNameRow();
        data.add(elementNames);
        data.add(columnNames);

        for (int i = 0; i < version.numberOfRows(); i++) {
            String versionCode = version.getRow(i).get(version.getColumnIdByFieldName(XmlVersionContainer.CODE));
            Map<String, String> valueRow = new LinkedHashMap<>();
            // populate row with data
            addVersionFieldsToRow(valueRow, i);
            addCorrespondenceTableFieldsToRow(valueRow, versionCode);
            addVariantFieldsToRow(valueRow, versionCode);

            data.add(valueRow);
        }
        addRemainingVariantRows(data);

        return data;
    }

    private void addVersionFieldsToRow(Map<String, String> valueRow, int rowId) {
        valueRow.putAll(version.getRow(rowId));
    }

    private void addCorrespondenceTableFieldsToRow(Map<String, String> valueRow, String versionCode) {
        correspondenceTables.forEach(dataContainer -> {
            int rowId = dataContainer.getRowIdByCode(versionCode);
            if (rowId != NOT_FOUND) {
                valueRow.putAll(dataContainer.getRow(rowId));
            }
        });
    }

    private void addVariantFieldsToRow(Map<String, String> valueRow, String versionCode) {
        variants.forEach(dataContainer -> {
            int rowId = dataContainer.getRowIdByCode(versionCode);
            if (rowId != NOT_FOUND) {
                valueRow.putAll(dataContainer.getRow(rowId));
            }
        });
    }

    private void addRemainingVariantRows(List<Map<String, String>> rowList) {
        variants.forEach(dataContainer -> {
            for (int i = 0; i < dataContainer.numberOfRows(); i++) {
                Map<String, String> row = dataContainer.getRow(i);
                String code = row.get(dataContainer.getColumnIdByFieldName(XmlVariantContainer.KILDE_KODE));
                if (version.getRowIdByCode(code) == NOT_FOUND) {
                    Map<String, String> blankRow = blankRow();
                    blankRow.putAll(row);
                    rowList.add(blankRow);
                }
            }
        });
    }

    private Map<String, String> generateNameRow() {
        Map<String, String> elementNames = blankRow();
        // getKey(0) = fist column for given classification
        elementNames.put(getKey(version.getColumnIdToFieldNameMap(), 0), version.getClassificationName());

        for (DataContainer container : correspondenceTables) {
            elementNames.put(getKey(container.getColumnIdToFieldNameMap(), 0), container.getClassificationName());
        }

        for (DataContainer container : variants) {
            elementNames.put(getKey(container.getColumnIdToFieldNameMap(), 0), container.getClassificationName());
        }
        return elementNames;
    }

    private String getKey(LinkedHashMap<String, String> linkedHashMap, int number) {
        int i = 0;
        for (Map.Entry<String, String> entry : linkedHashMap.entrySet()) {
            if (number == i++) {
                return entry.getKey();
            }
        }
        return null;
    }

    private Map<String, String> getColumnNames() {
        Map<String, String> columnNames = new LinkedHashMap<>();
        columnNames.putAll(version.getColumnIdToFieldNameMap());
        for (DataContainer container : correspondenceTables) {
            columnNames.putAll(container.getColumnIdToFieldNameMap());
        }
        for (DataContainer container : variants) {
            columnNames.putAll(container.getColumnIdToFieldNameMap());
        }
        return columnNames;
    }

    private Map<String, String> blankRow() {
        Map<String, String> columnNames = getColumnNames();
        for (Map.Entry<String, String> entry : columnNames.entrySet()) {
            entry.setValue("");
        }
        return columnNames;
    }

    private String column(int i) {
        return String.format("kolonne%04d", i);
    }

}