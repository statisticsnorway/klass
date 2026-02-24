package no.ssb.klass.designer.editing.codetables;

import java.util.List;

import com.google.common.eventbus.EventBus;

import no.ssb.klass.core.model.ClassificationItem;
import no.ssb.klass.core.model.CorrespondenceTable;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.model.StatisticalClassification;
import no.ssb.klass.designer.editing.codetables.utils.CorrespondenceTableHelper;

/**
 * Lists classificationItems for target version of CorrespondenceTable. ClassificationItems may be dragged and dropped
 * onto source version of CorrespondenceTable.
 */
public class TargetCorrespondenceTableCodeTable extends ReadOnlyCodeTable {
    private CorrespondenceTable correspondenceTable;

    @Override
    public final void init(EventBus eventbus, StatisticalClassification contentSource, Language language) {
        CorrespondenceTableHelper.throwUnsupportedException();
    }

    public final void init(EventBus eventbus, CorrespondenceTable correspondenceTable) {
        this.correspondenceTable = correspondenceTable;
        super.init(eventbus, correspondenceTable.getTarget(), correspondenceTable.getTarget().getPrimaryLanguage());
    }

    @Override
    protected final void populateContainer() {
        List<ClassificationItem> classificationItems = CorrespondenceTableHelper.findClassificationItems(
                correspondenceTable.getTarget(), correspondenceTable.getTargetLevel());
        for (ClassificationItem classificationItem : classificationItems) {
            addClassificationItemToTable(classificationItem);
            getContainer().setChildrenAllowed(classificationItem.getUuid(), false);
        }
    }
}
