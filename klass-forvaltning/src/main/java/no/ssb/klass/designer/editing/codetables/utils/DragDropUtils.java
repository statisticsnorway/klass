package no.ssb.klass.designer.editing.codetables.utils;

import java.util.Set;

import com.google.common.collect.Sets;
import com.vaadin.event.DataBoundTransferable;
import com.vaadin.ui.Table;

public final class DragDropUtils {
    private DragDropUtils() {
        // Utility class
    }

    /**
     * Gets itemIds of the rows dragged from source table.
     */
    public static Set<Object> getSourceItemIds(DataBoundTransferable transferable) {
        Table sourceTable = (Table) transferable.getSourceComponent();
        Set<Object> sourceItemIds = getSelectedRowsFromSourceTable(sourceTable);
        // It is possible to drag a not selected row from the source table. Hence the selected rows might not
        // include the dragged row. In this case only return the dragged row, and not the selected rows.
        return sourceItemIds.contains(transferable.getItemId()) ? sourceItemIds
                : Sets.newHashSet(transferable.getItemId());
    }

    @SuppressWarnings("unchecked")
    private static Set<Object> getSelectedRowsFromSourceTable(Table sourceTable) {
        return sourceTable.isMultiSelect() ? (Set<Object>) sourceTable.getValue()
                : Sets.newHashSet(sourceTable.getValue());
    }
}
