package no.ssb.klass.designer.components.search;

import com.google.common.collect.ImmutableMap;

import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.designer.ClassificationListView;
import no.ssb.klass.designer.components.ClassificationListViewSelection;
import no.ssb.klass.designer.util.VaadinUtil;

/**
 * @author Mads Lundemo, SSB.
 */
public class ClassificationSearchResult extends SearchResultComponent {

    public ClassificationSearchResult() {
        setCompositionRoot(design);
    }

    public void setClassification(ClassificationSeries classification) {
        setLink(clickEvent -> createSearchClickHandler(classification));
    }

    private void createSearchClickHandler(ClassificationSeries classification) {
        VaadinUtil.getKlassState().setClassificationListViewSelection(ClassificationListViewSelection
                .newClassificationListViewSelection(classification));
        VaadinUtil.navigateTo(ClassificationListView.NAME, ImmutableMap.of(ClassificationListView.PARAM_FAMILY_ID,
                classification.getClassificationFamily().getId().toString()));
    }

}
