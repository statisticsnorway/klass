package no.ssb.klass.forvaltning.converting.xml.fullversionexport;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Mads Lundemo, SSB.
 *
 *         Data container used to sturcuture classifications data in such a way that its easy to link them and build
 *         rows for our XML output
 */
class DataContainer {

    static final int NOT_FOUND = -1;

    private final LinkedHashMap<String, String> columnIdToFieldNameMap = new LinkedHashMap<>();
    private final LinkedHashMap<String, String> fieldNameToColumnIdMap = new LinkedHashMap<>();
    private final Map<String, Integer> versionCodeToRowIdMap = new HashMap<>();
    private final List<Map<String, String>> rows = new LinkedList<>();

    private String classificationName;

    /**
     * Version, CorrespondenceTable or Variant Name
     * 
     * @return Name
     */
    String getClassificationName() {
        return classificationName;
    }

    /**
     * Version, CorrespondenceTable or Variant Name
     * 
     * @param classificationName
     */
    void setClassificationName(String classificationName) {
        this.classificationName = classificationName;
    }

    /**
     * Map between ColumnId (ex. kolonne0001) and field name (ex. mål_kode)
     * 
     * @return map
     */
    LinkedHashMap<String, String> getColumnIdToFieldNameMap() {
        return columnIdToFieldNameMap;
    }

    /**
     * returns column id (ex: kolonne0001) for given field value (ex. mål_kode)
     * 
     * @return map
     */
    String getColumnIdByFieldName(String field) {
        return fieldNameToColumnIdMap.get(field);
    }

    /**
     * will add a column to this data container. Note: all columns should be added before populating rows
     * 
     * @param columnId
     *            columnId (ex: kolonne0001)
     * @param fieldName
     *            name of the data field you plan to fill this column with.
     */
    void addColumn(String columnId, String fieldName) {
        columnIdToFieldNameMap.put(columnId, fieldName);
        fieldNameToColumnIdMap.put(fieldName, columnId);
    }

    private Map<String, String> emptyRow() {
        Map<String, String> blankRow = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : columnIdToFieldNameMap.entrySet()) {
            blankRow.put(entry.getKey(), "");
        }
        return blankRow;
    }

    /**
     * Creates a new row with empty strings for all columns.
     *
     * @param code
     *            Value to link this row to rows in other data containers (version, variants and correspondenceTables)
     */
    void createNextRow(String code) {
        rows.add(emptyRow());
        setRowCode(code, currentRowId());
    }

    /**
     * Sets value for given field
     * 
     * @param field
     *            field name (ex. mål_kode)
     * @param data
     *            value you want your row to contain in this field/column
     */
    void setFieldValue(String field, String data) {
        String columnName = fieldNameToColumnIdMap.get(field);
        rows.get(currentRowId()).put(columnName, data);
    }

    /**
     * Some fields may need to store multiple values, this method will append your value to an exsisting field
     * 
     * @param rowId
     *            id for the row containing the field you want to change
     * @param field
     *            name of the field you want to change
     * @param data
     *            the data you want to append
     */
    void addFieldValue(int rowId, String field, String data) {
        String columnName = fieldNameToColumnIdMap.get(field);
        String original = rows.get(rowId).get(columnName);
        rows.get(rowId).put(columnName, original + "\n" + data);
    }

    Map<String, String> getRow(int i) {
        if (rows.size() <= i) {
            return emptyRow();
        }
        return rows.get(i);
    }

    /**
     * @return The number of rows this dataContainer contains
     */
    int numberOfRows() {
        return rows.size();
    }

    private int currentRowId() {
        return rows.size() - 1;
    }

    private void setRowCode(String code, int row) {
        versionCodeToRowIdMap.put(code, row);
    }

    /**
     * @return Row Id for row linked with provided code
     */
    int getRowIdByCode(String code) {
        if (versionCodeToRowIdMap.containsKey(code)) {
            return versionCodeToRowIdMap.get(code);
        }
        return NOT_FOUND;
    }

}
