package no.ssb.klass.designer.editing.correspondencetable;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.annotation.PrototypeScope;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;

import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.model.CorrespondenceTable;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.util.Translatable;
import no.ssb.klass.designer.EditingView;
import no.ssb.klass.designer.components.BreadcumbPanel.Breadcrumb;
import no.ssb.klass.designer.components.common.TypeAndNameHeaderComponent;
import no.ssb.klass.designer.service.ClassificationFacade;
import no.ssb.klass.designer.service.InformSubscribers;
import no.ssb.klass.designer.util.ParameterUtil;
import no.ssb.klass.designer.util.VaadinUtil;

/**
 * @author Mads Lundemo, SSB.
 */
@SuppressWarnings("serial")
@PrototypeScope
@SpringView(name = CreateCorrespondenceTableView.NAME)
public class CreateCorrespondenceTableView extends CreateCorrespondenceTableDesign implements EditingView {
    Logger log = LoggerFactory.getLogger(CreateCorrespondenceTableView.class);

    public static final String NAME = "createCorrespondenceTable";

    public static final String PARAM_FROM_VERSION = "fromVersion";
    public static final String PARAM_FROM_LEVEL = "fromLevel";
    public static final String PARAM_TO_VERSION = "toVersion";
    public static final String PARAM_TO_LEVEL = "toLevel";

    private boolean ignoreChanges = false;

    @Autowired
    private ClassificationFacade classificationFacade;

    private ClassificationVersion fromVersion;
    private ClassificationVersion toVersion;
    private CorrespondenceTable correspondenceTable;
    private Long fromLevelNumber;
    private Long toLevelNumber;

    public CreateCorrespondenceTableView() {
        metadataEditor.setNameVisible(false);
        metadataEditor.setDescriptionRequired(false);
        metadataEditor.setDraftVisible(true);
        actionButtons.setConfirmText("Lagre");
        actionButtons.addConfirmClickListener(this::saveClick);
        actionButtons.addCancelClickListener(this::cancelClick);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        fromVersion = getClassificationVersionFromParameters(event, PARAM_FROM_VERSION);
        toVersion = getClassificationVersionFromParameters(event, PARAM_TO_VERSION);
        fromLevelNumber = getLevelNumberFromParameters(event, PARAM_FROM_LEVEL);
        toLevelNumber = getLevelNumberFromParameters(event, PARAM_TO_LEVEL);

        correspondenceTable = new CorrespondenceTable(Translatable.empty(),
                fromVersion, fromLevelNumber.intValue(), toVersion, toLevelNumber.intValue());

        metadataEditor.init(correspondenceTable.getPrimaryLanguage());
        metadataEditor.setContactPersonReadOnly(correspondenceTable.getContactPerson());
        header.setTypeText(TypeAndNameHeaderComponent.TEXT_CORRESPONDANCE_TABLE);
        header.setNameText(correspondenceTable.getNameInPrimaryLanguage());
    }

    @Override
    public List<Breadcrumb> getBreadcrumbs() {
        return Breadcrumb.newBreadcrumbs(correspondenceTable, true);
    }

    @Override
    public boolean hasChanges() {
        return !ignoreChanges && metadataEditor.hasChanges();
    }

    @Override
    public void ignoreChanges() {
        ignoreChanges = true;
    }

    private Long getLevelNumberFromParameters(ViewChangeEvent event, String parameterName) {
        return ParameterUtil.getRequiredLongParameter(parameterName, event.getParameters());
    }

    private ClassificationVersion getClassificationVersionFromParameters(ViewChangeListener.ViewChangeEvent event,
            String parameterName) {
        Long classificationVersionId = ParameterUtil.getRequiredLongParameter(parameterName, event.getParameters());
        return classificationFacade.getRequiredClassificationVersion(classificationVersionId);
    }

    private void saveClick(Button.ClickEvent clickEvent) {
        if (metadataEditor.validate()) {
            ignoreChanges = true;
            correspondenceTable.setDescription(metadataEditor.getDescription(Language.NB), Language.NB);
            correspondenceTable.setDescription(metadataEditor.getDescription(Language.NN), Language.NN);
            correspondenceTable.setDescription(metadataEditor.getDescription(Language.EN), Language.EN);
            correspondenceTable.setDraft(metadataEditor.getDraftCheckBoxValue());
            correspondenceTable = classificationFacade.saveAndIndexCorrespondenceTable(correspondenceTable,
                    InformSubscribers.createNotInformSubscribers());
            VaadinUtil.showSavedMessage();
            VaadinUtil.getKlassState().setEditingState(metadataEditor.currentEditingState());
            VaadinUtil.navigateTo(EditCorrespondenceTableView.NAME, ImmutableMap.of(
                    EditCorrespondenceTableView.PARAM_ID, correspondenceTable.getId().toString()));
        }
    }

    private void cancelClick(Button.ClickEvent clickEvent) {
        VaadinUtil.navigateTo(Iterables.getLast(getBreadcrumbs()));
    }
}
