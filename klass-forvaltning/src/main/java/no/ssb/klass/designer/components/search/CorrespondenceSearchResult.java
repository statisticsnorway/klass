package no.ssb.klass.designer.components.search;

import com.google.common.collect.ImmutableMap;

import no.ssb.klass.core.model.CorrespondenceTable;
import no.ssb.klass.designer.ClassificationListView;
import no.ssb.klass.designer.components.ClassificationListViewSelection;
import no.ssb.klass.designer.util.VaadinUtil;

/**
 * @author Mads Lundemo, SSB.
 */
public class CorrespondenceSearchResult extends SearchResultComponent {

    public CorrespondenceSearchResult() {
        setCompositionRoot(design);
    }

    public void setCorrespondenceTable(CorrespondenceTable table) {
        setLink(clickEvent -> createSearchClickHandler(table));
    }

    private void createSearchClickHandler(CorrespondenceTable table) {
        VaadinUtil.getKlassState().setClassificationListViewSelection(ClassificationListViewSelection
                .newClassificationListViewSelection(table, table.getSource()));
        VaadinUtil.navigateTo(ClassificationListView.NAME, ImmutableMap.of(ClassificationListView.PARAM_FAMILY_ID,
                table.getOwnerClassification().getClassificationFamily().getId().toString()));
    }

}
