package no.ssb.klass.designer.classificationlist;

import java.util.Arrays;

import org.vaadin.resetbuttonfortextfield.ResetButtonForTextField;

import com.vaadin.data.Item;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import no.ssb.klass.core.model.ClassificationEntityOperations;
import no.ssb.klass.designer.listeners.CustomTextChangeEvent;
import no.ssb.klass.designer.listeners.SharedEscapeShortcutListener;
import no.ssb.klass.designer.listeners.SimpleTextChangeListener;
import no.ssb.klass.designer.util.KlassTheme;

@SuppressWarnings("serial")
public abstract class AbstractTable extends CustomComponent {
    protected final VerticalLayout rootLayout;

    private SimpleTextChangeListener textChangeListener;
    private TextField filter;

    public AbstractTable() {
        rootLayout = new VerticalLayout();
        rootLayout.setSizeFull();
        setCompositionRoot(rootLayout);
    }

    protected Component createHeader(String headerText, Button... buttons) {
        Label header = new Label(headerText);
        header.addStyleName(ValoTheme.LABEL_BOLD);
        HorizontalLayout layout = new HorizontalLayout(header);
        layout.setExpandRatio(header, 1);
        layout.addComponents(buttons);
        layout.addStyleName("v-table-header-wrap table-header");
        layout.setWidth("100%");
        layout.setHeight("38px");
        layout.setMargin(new MarginInfo(false, false, false, true));
        layout.setComponentAlignment(header, Alignment.MIDDLE_LEFT);
        return layout;
    }

    protected Table createTable(AbstractPropertyContainer<?> container) {
        Table table = new Table();
        table.setSizeFull();
        table.setContainerDataSource(container);
        table.setSelectable(true);
        table.addStyleName(ValoTheme.TABLE_NO_VERTICAL_LINES);
        table.addStyleName(ValoTheme.TABLE_NO_HEADER);
        return table;
    }

    protected Table createTable(AbstractPropertyContainer<?> container, ItemClickListener... itemClickListeners) {
        Table table = createTable(container);
        Arrays.stream(itemClickListeners).forEach(table::addItemClickListener);
        return table;
    }

    protected HorizontalLayout wrapFilter(TextField filterBox) {
        HorizontalLayout layout = new HorizontalLayout(filterBox);
        layout.addStyleName("v-table-header-wrap");
        layout.setWidth("100%");
        layout.setHeight("38px");
        layout.setMargin(new MarginInfo(false, true, false, true));
        layout.setComponentAlignment(filterBox, Alignment.MIDDLE_LEFT);
        layout.setExpandRatio(filterBox, 1);
        return layout;
    }

    protected TextField createFilterBox(Table table, String text) {
        filter = new TextField();

        filter.setTextChangeEventMode(TextChangeEventMode.TIMEOUT);
        filter.setTextChangeTimeout(500);
        filter.setInputPrompt(text);
        filter.addStyleName(ValoTheme.TEXTFIELD_TINY);
        filter.setWidth("100%");

        textChangeListener = new SimpleTextChangeListener(table, AbstractPropertyContainer.NAME);
        filter.addTextChangeListener(textChangeListener);
        ResetButtonForTextField extendFilterBox = ResetButtonForTextField.extend(filter);
        extendFilterBox.addResetButtonClickedListener(() -> textChangeListener.textChange(
                new CustomTextChangeEvent(filter)));
        return filter;
    }

    public void addToSharedActionListener(SharedEscapeShortcutListener actionListener) {
        actionListener.addListener(filter, textChangeListener);
    }

    protected Button createAddElementButton(String tooltip) {
        Button addElementButton = new Button();
        addElementButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        addElementButton.addStyleName(KlassTheme.ICON_HIGHLIGHTED);
        addElementButton.setIcon(FontAwesome.PLUS_CIRCLE);
        addElementButton.setDescription(tooltip);
        return addElementButton;
    }

    protected Button createExportButton(String tooltip) {
        Button addElementButton = new Button();
        addElementButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        addElementButton.setIcon(FontAwesome.DOWNLOAD);
        addElementButton.addStyleName(KlassTheme.ICON_HIGHLIGHTED);
        addElementButton.setDescription(tooltip);
        return addElementButton;
    }

    protected final void selectItem(final ClassificationEntityOperations classificationEntity, Table table) {
        Object selectedItemId = classificationEntity.getUuid();
        if (table.getItem(selectedItemId) == null) {
            return;
        }
        table.select(selectedItemId);
        table.setCurrentPageFirstItemId(selectedItemId);
        onSelectItem(table.getItem(selectedItemId));
    }

    /**
     * Subclasses may override for additional steps when an item is selected
     * 
     * @param selectedItem
     */
    protected void onSelectItem(Item selectedItem) {
    }
}
