package no.ssb.klass.designer.components.search;

import com.google.common.collect.ImmutableMap;

import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.designer.ClassificationListView;
import no.ssb.klass.designer.components.ClassificationListViewSelection;
import no.ssb.klass.designer.util.VaadinUtil;

/**
 * @author Mads Lundemo, SSB.
 */
public class VersionSearchResult extends SearchResultComponent {

    public VersionSearchResult() {
        setCompositionRoot(design);
    }

    public void setVersion(ClassificationVersion version) {
        setLink(clickEvent -> createSearchClickHandler(version));
    }

    private void createSearchClickHandler(ClassificationVersion version) {
        VaadinUtil.getKlassState().setClassificationListViewSelection(ClassificationListViewSelection
                .newClassificationListViewSelection(version));
        VaadinUtil.navigateTo(ClassificationListView.NAME, ImmutableMap.of(ClassificationListView.PARAM_FAMILY_ID,
                version.getClassification().getClassificationFamily().getId().toString()));
    }

}
