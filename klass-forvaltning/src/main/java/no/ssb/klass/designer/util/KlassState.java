package no.ssb.klass.designer.util;

import no.ssb.klass.designer.MainView.ClassificationFilter;
import no.ssb.klass.designer.components.ClassificationListViewSelection;
import no.ssb.klass.designer.editing.EditingState;

/**
 * This is state that is enabled per UI (KlassUI). This is preferred to session state, since session state is shared
 * between multiple tabs in a browser.
 */
public class KlassState {
    private EditingState editingState;
    private ClassificationListViewSelection classificationListViewSelection;
    private ClassificationFilter classificationFilter;
    private String previousView;
    /*
     * When showing breadcrumbs for a correspondenceTable this indicates if source or target version of
     * correspondenceTable was currently viewed
     */
    private Boolean listingSourceVersionOfCorrespondenceTable;

    public boolean isListingSourceVersionOfCorrespondenceTable() {
        if (listingSourceVersionOfCorrespondenceTable == null) {
            return true;
        }
        return listingSourceVersionOfCorrespondenceTable;
    }

    public void setListingSourceVersionOfCorrespondenceTable(boolean listingSourceVersionOfCorrespondenceTable) {
        this.listingSourceVersionOfCorrespondenceTable = listingSourceVersionOfCorrespondenceTable;
    }

    public String getPreviousView() {
        return previousView;
    }

    public void setPreviousView(String previousView) {
        this.previousView = previousView;
    }

    public void setEditingState(EditingState editingState) {
        this.editingState = editingState;
    }

    public EditingState getAndClearEditingState() {
        EditingState editingState = this.editingState;
        if (editingState == null) {
            return EditingState.newDefault();
        }
        this.editingState = null;
        return editingState;
    }

    public ClassificationFilter getClassificationFilter() {
        return classificationFilter;
    }

    public void setClassificationFilter(ClassificationFilter classificationFilter) {
        this.classificationFilter = classificationFilter;
    }

    public ClassificationListViewSelection getClassificationListViewSelection() {
        return classificationListViewSelection;
    }

    public void setClassificationListViewSelection(ClassificationListViewSelection classificationListViewSelection) {
        this.classificationListViewSelection = classificationListViewSelection;
    }

    public void clearClassificationListViewSelection() {
        this.classificationListViewSelection = null;
        this.listingSourceVersionOfCorrespondenceTable = null;
    }
}
