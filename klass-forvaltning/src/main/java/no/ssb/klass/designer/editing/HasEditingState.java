package no.ssb.klass.designer.editing;

/**
 * Indicates if a component has editing state.
 * <p>
 * Examples of editing state:
 * <ul>
 * <li>whether translations are visible or not</li>
 * <li>if currently is editing codes or metadata</li>
 * </ul>
 */
public interface HasEditingState {
    /**
     * Restore editing state to its previous state.
     * 
     * @param previousEditingState
     */
    void restorePreviousEditingState(EditingState previousEditingState);

    /**
     * Makes a snapshot of current editing state
     * 
     * @return snapshot of current editing state
     */
    EditingState currentEditingState();

    boolean hasChanges();
}
