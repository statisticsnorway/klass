package no.ssb.klass.designer.admin.tabs;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.SelectionEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.model.StatisticalUnit;
import no.ssb.klass.core.model.User;
import no.ssb.klass.core.util.Translatable;
import no.ssb.klass.designer.service.ClassificationFacade;
import no.ssb.klass.designer.user.UserContext;
import no.ssb.klass.designer.util.ConfirmationDialog;
import no.ssb.klass.designer.util.VaadinUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by jro on 09.11.2016.
 */
public class StatisticalUnitTab extends StatisticalUnitTabDesign {

    @Autowired
    private UserContext userContext;
    @Autowired
    private ClassificationFacade classificationFacade;

    public StatisticalUnitTab() {

        saveButton.addClickListener(this::saveButtonClick);
        newButton.addClickListener(this::newButtonClick);
        deleteButton.addClickListener(this::deleteButtonClick);
    }


    public void init() {

        Container container = createClassificationUnitContainer();
        // Bind it to a component
        statisticalUnitGrid.setContainerDataSource((Container.Indexed) container);
        statisticalUnitGrid.getColumn("Id").setExpandRatio(1);
        statisticalUnitGrid.getColumn("Bokmål").setExpandRatio(3);
        statisticalUnitGrid.getColumn("Nynorsk").setExpandRatio(3);
        statisticalUnitGrid.getColumn("Engelsk").setExpandRatio(3);
        statisticalUnitGrid.getColumn("data").setHidden(true);
        statisticalUnitGrid.setCaption("Enhetstyper");
        statisticalUnitGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        statisticalUnitGrid.addSelectionListener(this::updateForm);
        saveButton.setEnabled(false);
        deleteButton.setEnabled(false);
        newButton.setEnabled(true);
        updateTableContent();
    }

    private Container createClassificationUnitContainer() {
        Container container = new IndexedContainer();
        // Initialize the container as required by the container type
        container.addContainerProperty("Id", Long.class, null);
        container.addContainerProperty("Bokmål", String.class, null);
        container.addContainerProperty("Nynorsk", String.class, null);
        container.addContainerProperty("Engelsk", String.class, null);
        container.addContainerProperty("data", StatisticalUnit.class, null);
        return container;
    }

    private void updateForm(SelectionEvent selectionEvent) {

        if (selectionEvent.getSelected().isEmpty()) {
            saveButton.setEnabled(false);
            deleteButton.setEnabled(false);
        } else {
            User currentUser = userContext.getDetachedUserObject();
            StatisticalUnit stat = getSelectedStatisticalUnit();
            nb.setValue(stat.getName(Language.NB));
            nn.setValue(stat.getName(Language.NN));
            en.setValue(stat.getName(Language.EN));
            boolean checkAllowed = checkAllowedToDeleteStatisticalUnit(currentUser, stat);
            if (checkAllowed) {
                deleteButton.setVisible(true);
                deleteButton.setEnabled(true);
            } else {
                deleteButton.setEnabled(false);
              }
            saveButton.setEnabled(true);
        }
    }

    private void saveButtonClick(Button.ClickEvent clickEvent) {
        if (statisticalUnitGrid.getSelectedRows().isEmpty()) {
            Notification.show("Ingen enhetstype er valgt", Notification.Type.WARNING_MESSAGE);
        } else {
            StatisticalUnit stat = getSelectedStatisticalUnit();
            if (Objects.equals(stat.getName(Language.NB), nb.getValue())
                    && Objects.equals(stat.getName(Language.NN), nn.getValue())
                    && Objects.equals(stat.getName(Language.EN), en.getValue())) {
                Notification.show("FEIL!! Enhetstype ikke endret", Notification.Type.ERROR_MESSAGE);
            } else {
                stat.setName(nb.getValue(), Language.NB);
                stat.setName(nn.getValue(), Language.NN);
                stat.setName(en.getValue(), Language.EN);
                classificationFacade.saveStatisticalUnit(stat);
                VaadinUtil.showSavedMessage();
                clearInputFields();
                updateTableContent();
            }
        }
    }
    private void deleteButtonClick(Button.ClickEvent clickEvent) {
        if (!statisticalUnitGrid.getSelectedRows().isEmpty()) {
            StatisticalUnit stat = getSelectedStatisticalUnit();
            StringBuilder confirmationText = new StringBuilder("Er du sikker på at du ønsker å slette ");
            confirmationText.append(stat.getName(Language.NB));
            ConfirmationDialog confWindow = new ConfirmationDialog("Sletter enhetstyper", "Ja", "Nei", (
                    answerYes) -> {
                if (answerYes) {
                    classificationFacade.deleteStatisticalUnit(stat);
                    VaadinUtil.showSavedMessage();
                    clearInputFields();
                    updateTableContent();
                }
            }, confirmationText.toString());
            UI.getCurrent().addWindow(confWindow);
        }
    }

    private void newButtonClick(Button.ClickEvent clickEvent) {
        if (statisticalUnitGrid.getSelectedRows().isEmpty()) {
            if (StringUtils.isEmpty(nb.getValue())) {
                Notification.show("Enhetstype - Bokmål må fylles ut", Notification.Type.WARNING_MESSAGE);
            } else if (StringUtils.isEmpty(nn.getValue())) {
                Notification.show("Enhetstype - Nynorsk må fylles ut", Notification.Type.WARNING_MESSAGE);
            } else if (StringUtils.isEmpty(en.getValue())) {
                Notification.show("Enhetstype - Engelsk må fylles ut", Notification.Type.WARNING_MESSAGE);
            } else {
                Translatable name = new Translatable(nb.getValue(), nn.getValue(), en.getValue());
                StatisticalUnit newStatUnit = new StatisticalUnit(name);
                classificationFacade.createAndSaveNewStatisticalUnit(newStatUnit);
                VaadinUtil.showSavedMessage();
                clearInputFields();
                updateTableContent();
            }
        } else {
            clearInputFields();
            updateTableContent();
        }
    }
    private boolean checkAllowedToDeleteStatisticalUnit(User user, StatisticalUnit stat) {
        List<StatisticalUnit> stats  = classificationFacade.findClassificationStatisticalUnits(stat);
        boolean statExistsInClass = false;
        if (stats.size() > 0) {
            statExistsInClass = true;
        }
        return user.isAdministrator() && !statExistsInClass;
    }

    private StatisticalUnit getSelectedStatisticalUnit() {
        Item item = statisticalUnitGrid.getContainerDataSource().getItem(statisticalUnitGrid.getSelectedRow());
        return (StatisticalUnit) item.getItemProperty("data").getValue();
    }

    private void updateTableContent() {
        List<StatisticalUnit> statisticalUnits = classificationFacade.findAllStatisticalUnits().stream()
                .sorted(Comparator.comparing(o -> o.getName(Language.NB))).collect(Collectors.toList());
        statisticalUnitGrid.getContainerDataSource().removeAllItems();
        for (StatisticalUnit su : statisticalUnits) {
            statisticalUnitGrid.addRow(
                    su.getId(),
                    su.getName(Language.NB),
                    su.getName(Language.NN),
                    su.getName(Language.EN),
                    su);
        }
    }
    private void clearInputFields() {
        statisticalUnitGrid.deselectAll();
        nb.clear();
        nn.clear();
        en.clear();
    }
}
