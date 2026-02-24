package no.ssb.klass.designer.components.search;

import com.google.common.collect.ImmutableMap;

import no.ssb.klass.core.model.ClassificationVariant;
import no.ssb.klass.designer.ClassificationListView;
import no.ssb.klass.designer.components.ClassificationListViewSelection;
import no.ssb.klass.designer.util.VaadinUtil;

/**
 * @author Mads Lundemo, SSB.
 */
public class VaraintSearchResult extends SearchResultComponent {

    public VaraintSearchResult() {
        setCompositionRoot(design);
    }

    public void setVariant(ClassificationVariant variant) {
        setLink(clickEvent -> createSearchClickHandler(variant));
    }

    private void createSearchClickHandler(ClassificationVariant variant) {
        VaadinUtil.getKlassState().setClassificationListViewSelection(ClassificationListViewSelection
                .newClassificationListViewSelection(variant));
        VaadinUtil.navigateTo(ClassificationListView.NAME, ImmutableMap.of(ClassificationListView.PARAM_FAMILY_ID,
                variant.getOwnerClassification().getClassificationFamily().getId().toString()));
    }

}
