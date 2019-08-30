package no.ssb.klass.designer.editing.codetables.filters;

import com.google.common.base.Strings;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;

import no.ssb.klass.core.model.ClassificationItem;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.designer.editing.codetables.codeeditors.NewCodeEditor;
import no.ssb.klass.designer.editing.codetables.codeeditors.UpdateCodeEditor;
import no.ssb.klass.designer.editing.codetables.containers.CustomHierarchicalContainer;

/**
 * @author Mads Lundemo, SSB.
 */
public class HierarchyCodeEditorFilter implements Container.Filter {

    final Object propertyId;
    final Language language;
    final String filterString;
    final boolean ignoreCase;
    final CustomHierarchicalContainer container;

    public HierarchyCodeEditorFilter(Language language, CustomHierarchicalContainer container, Object propertyId,
            String filterString,
            boolean ignoreCase) {
        this.container = container;
        this.propertyId = propertyId;
        this.language = language;
        this.filterString = ignoreCase ? filterString.toLowerCase() : filterString;
        this.ignoreCase = ignoreCase;
    }

    @Override
    public boolean passesFilter(Object itemId, Item item) {
        if (Strings.isNullOrEmpty(filterString)) {
            return true;
        }
        final Property<?> codeProperty = item.getItemProperty("code");
        if (codeProperty.getValue() instanceof NewCodeEditor) {
            // dont hide new code editors
            NewCodeEditor codeEditor = (NewCodeEditor) codeProperty.getValue();

            // return codeEditor.isDirty();

            Object parentId = container.getUnfilteredParent(codeEditor.getItemId());
            Item parentItem = container.getUnfilteredItem(parentId);
            return parentId == null || passesFilter(parentId, parentItem);

        }
        final Property<?> p = item.getItemProperty(propertyId);
        if (p == null) {
            return false;
        }
        Object propertyValue = p.getValue();
        if (propertyValue == null) {
            return false;
        }
        if (propertyValue instanceof UpdateCodeEditor) {
            // dont hide update editors with changes
            // TODO check editor for original content and evaluate
            UpdateCodeEditor codeEditor = (UpdateCodeEditor) propertyValue;
            // return codeEditor.isDirty();
            return true;
        }

        if (propertyValue instanceof ClassificationItem) {
            String code = ((ClassificationItem) propertyValue).getCode();
            String name = ((ClassificationItem) propertyValue).getOfficialName(language);
            if (ignoreCase) {
                code = code.toLowerCase();
                name = name.toLowerCase();
            }

            if (code.startsWith(filterString)) {
                return true;
            }
            if (name.contains(filterString)) {
                return true;
            }

            Object parentId = container.getUnfilteredParent(itemId);
            Item parentItem = container.getUnfilteredItem(parentId);
            if (parentId != null && passesFilter(parentId, parentItem)) {
                return true;
            }
            return false;
        }
        return false;

    }

    @Override
    public boolean appliesToProperty(Object propertyId) {
        return this.propertyId.equals(propertyId);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        // Only ones of the objects of the same class can be equal
        if (!(obj instanceof HierarchyCodeEditorFilter)) {
            return false;
        }
        final HierarchyCodeEditorFilter o = (HierarchyCodeEditorFilter) obj;

        // Checks the properties one by one
        if (propertyId != o.propertyId && o.propertyId != null
                && !o.propertyId.equals(propertyId)) {
            return false;
        }
        if (filterString != o.filterString && o.filterString != null
                && !o.filterString.equals(filterString)) {
            return false;
        }
        if (ignoreCase != o.ignoreCase) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return (propertyId != null ? propertyId.hashCode() : 0)
                ^ (filterString != null ? filterString.hashCode() : 0);
    }

    /**
     * Returns the property identifier to which this filter applies.
     *
     * @return property id
     */
    public Object getPropertyId() {
        return propertyId;
    }

    /**
     * Returns the filter string.
     *
     * Note: this method is intended only for implementations of lazy string filters and may change in the future.
     *
     * @return filter string given to the constructor
     */
    public String getFilterString() {
        return filterString;
    }

    /**
     * Returns whether the filter is case-insensitive or case-sensitive.
     *
     * Note: this method is intended only for implementations of lazy string filters and may change in the future.
     *
     * @return true if performing case-insensitive filtering, false for case-sensitive
     */
    public boolean isIgnoreCase() {
        return ignoreCase;
    }

}
