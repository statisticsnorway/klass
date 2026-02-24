package no.ssb.klass.designer.editing.codetables;

import org.vaadin.resetbuttonfortextfield.ResetButtonForTextField;

import com.google.common.eventbus.EventBus;
import com.vaadin.event.FieldEvents;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

import no.ssb.klass.core.model.BaseEntity;
import no.ssb.klass.designer.classificationlist.AbstractTable;
import no.ssb.klass.designer.editing.codetables.events.CloseHierarchyEvent;
import no.ssb.klass.designer.editing.codetables.events.OpenHierarchyEvent;
import no.ssb.klass.designer.listeners.CustomTextChangeEvent;
import no.ssb.klass.designer.listeners.HierarchyTextChangeListener;
import no.ssb.klass.designer.listeners.SharedEscapeShortcutListener;
import no.ssb.klass.designer.util.KlassTheme;

/**
 * @author Mads Lundemo, SSB.
 */
public abstract class CodeTableFilterWrapper<T extends BaseCodeTable, S extends BaseEntity> extends AbstractTable {

    protected TextField filterBox;
    protected Button hierarchyButton;
    protected EventBus eventBus;
    protected boolean hierarchyState = false;
    private T table;

    private FieldEvents.TextChangeListener textChangeListener;

    protected abstract T createTable();

    protected abstract void initTable(EventBus eventbus, T table, S entity);

    public void init(EventBus eventBus, S entity) {
        this.eventBus = eventBus;
        configureTable();
        filterBox = createFilter(table);
        rootLayout.addComponents(wrapFilterWithHierarchyButton(filterBox), table);
        rootLayout.setExpandRatio(table, 1);
        initTable(eventBus, table, entity);

    }

    private void closeHierarchy() {
        eventBus.post(new CloseHierarchyEvent(table));
    }

    private void openHierarchy() {
        eventBus.post(new OpenHierarchyEvent(table));
    }

    private HorizontalLayout wrapFilterWithHierarchyButton(TextField filterBox) {
        hierarchyButton = createHierarchyButton();
        HorizontalLayout layout = wrapFilter(filterBox);
        layout.addComponentAsFirst(hierarchyButton);
        layout.setExpandRatio(hierarchyButton, 0);
        layout.setMargin(new MarginInfo(false, true, false, false));
        layout.setComponentAlignment(hierarchyButton, Alignment.MIDDLE_LEFT);
        layout.setSpacing(true);
        layout.setExpandRatio(hierarchyButton, 0);
        return layout;

    }

    private void configureTable() {
        table = createTable();
        table.setSizeFull();
        table.setSelectable(true);
        table.addStyleName(ValoTheme.TABLE_NO_VERTICAL_LINES);
        table.addStyleName(ValoTheme.TABLE_NO_HEADER);
        if (isReadOnly()) {
            table.setReadOnly(true);
        }
    }

    private Button createHierarchyButton() {
        Button button = new Button();
        button.setHtmlContentAllowed(true);
        button.setCaptionAsHtml(true);
        button.addStyleName(ValoTheme.BUTTON_TINY);
        button.addStyleName(KlassTheme.ROW_BUTTON);
        button.addStyleName("small-margin-left");
        button.setCaption(FontAwesome.PLUS.getHtml() + "<b> / </b>" + FontAwesome.MINUS.getHtml());
        button.setDescription("Åpne/Lukke hierarki");
        button.addClickListener(this::hierarchyButtonClick);
        return button;
    }

    private void hierarchyButtonClick(Button.ClickEvent clickEvent) {
        if (hierarchyState) {
            closeHierarchy();
        } else {
            openHierarchy();
        }
        hierarchyState = !hierarchyState;
    }

    private TextField createFilter(Table classificationTable) {
        TextField filter = new TextField();
        filter.setTextChangeEventMode(AbstractTextField.TextChangeEventMode.TIMEOUT);
        filter.setTextChangeTimeout(500);
        filter.setInputPrompt("Filtrer elementer");
        filter.addStyleName(ValoTheme.TEXTFIELD_TINY);
        filter.setWidth("100%");
        textChangeListener = getTextChangeListener(classificationTable);
        ResetButtonForTextField extendFilterBox = ResetButtonForTextField.extend(filter);
        extendFilterBox.addResetButtonClickedListener(() -> textChangeListener.textChange(
                new CustomTextChangeEvent(filter)));
        filter.addTextChangeListener(textChangeListener);
        return filter;
    }

    public void addToSharedActionListener(SharedEscapeShortcutListener actionListener) {
        actionListener.addListener(filterBox, textChangeListener);
    }

    protected FieldEvents.TextChangeListener getTextChangeListener(Table classificationTable) {
        return new HierarchyTextChangeListener(classificationTable, BaseCodeTable.CODE_COLUMN);
    }

    public void refresh() {
        table.refresh();
    }

    public boolean hasChanges() {
        return table.hasChanges();
    }

    public T getTable() {
        return table;
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        if (table != null) {
            table.setReadOnly(readOnly);
        }
    }
}
