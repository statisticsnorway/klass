package no.ssb.klass.designer.editing.correspondencetable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.vaadin.spring.annotation.PrototypeScope;

import com.google.common.collect.Iterables;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.UI;

import no.ssb.klass.core.model.Changelog;
import no.ssb.klass.core.model.CorrespondenceTable;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.designer.EditingView;
import no.ssb.klass.designer.components.BreadcumbPanel.Breadcrumb;
import no.ssb.klass.designer.components.common.TypeAndNameHeaderComponent;
import no.ssb.klass.designer.editing.EditingState;
import no.ssb.klass.designer.service.ClassificationFacade;
import no.ssb.klass.designer.service.InformSubscribers;
import no.ssb.klass.designer.user.UserContext;
import no.ssb.klass.designer.util.ParameterUtil;
import no.ssb.klass.designer.util.VaadinUtil;
import no.ssb.klass.designer.windows.DescriptionOfChangeWindow;

/**
 * @author Mads Lundemo, SSB.
 */
@PrototypeScope
@SpringView(name = EditCorrespondenceTableView.NAME)
@SuppressWarnings("serial")
public class EditCorrespondenceTableView extends EditCorrespondenceTableDesign implements EditingView {
    Logger log = LoggerFactory.getLogger(EditCorrespondenceTableView.class);

    protected List<Language> supportedLanguages = Arrays.asList(Language.NB, Language.NN, Language.EN);

    public static final String NAME = "editCorrespondenceTable";
    public static final String PARAM_ID = "CorrespondenceId";

    private boolean ignoreChanges = false;

    @Autowired
    private ClassificationFacade classificationFacade;
    @Autowired
    private UserContext userContext;
    @Autowired
    private ApplicationContext applicationContext;

    private CorrespondenceTable correspondenceTable;
    private HasCorrespondenceTableComponent codeEditor;

    public EditCorrespondenceTableView() {
        metadataEditor.setNameVisible(false);
        metadataEditor.setDescriptionRequired(false);
        actionButtons.setConfirmText("Lagre");
        actionButtons.addConfirmClickListener(this::saveClick);
        actionButtons.addCancelClickListener(this::cancelClick);
        metadataEditor.addPublicationComponent();
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        EditingState editingState = VaadinUtil.getKlassState().getAndClearEditingState();
        Long correspondenceId = ParameterUtil.getRequiredLongParameter(PARAM_ID, event.getParameters());
        correspondenceTable = classificationFacade.getRequiredCorrespondenceTable(correspondenceId);
        metadataEditor.init(correspondenceTable.getPrimaryLanguage());
        metadataEditor.setPublication(correspondenceTable);
        metadataEditor.setContactPersonReadOnly(correspondenceTable.getContactPerson());
        metadataEditor.setDraft(correspondenceTable.isDraft());
        metadataEditor.setDraftVisible(!correspondenceTable.isPublishedInAnyLanguage());
        for (Language language : supportedLanguages) {
            metadataEditor.setName(language, correspondenceTable.getName(language));
            metadataEditor.setDescription(language, correspondenceTable.getDescription(language));
        }

        header.setTypeText(TypeAndNameHeaderComponent.TEXT_CORRESPONDANCE_TABLE);
        header.setNameText(correspondenceTable.getNameInPrimaryLanguage());

        if (correspondenceTable.isChangeTable()) {
            codeEditor = applicationContext.getBean(ChangeTableCodeEditorView.class);
        } else {
            codeEditor = applicationContext.getBean(CorrespondenceTableCodeEditorView.class);
        }
        checkUserAccess();
        codeEditor.init(correspondenceTable);
        codeEditorTab.setContent(codeEditor);
        if (editingState.isCodeEditorNotMetadataVisible()) {
            accordion.setSelectedTab(codeEditorTab);
            codeEditor.restorePreviousEditingState(editingState);
        } else {
            metadataEditor.restorePreviousEditingState(editingState);
        }
    }

    @Override
    public List<Breadcrumb> getBreadcrumbs() {
        return Breadcrumb.newBreadcrumbs(correspondenceTable, VaadinUtil.getKlassState()
                .isListingSourceVersionOfCorrespondenceTable());
    }

    @Override
    public boolean hasChanges() {
        return !ignoreChanges && (metadataEditor.hasChanges() || codeEditor.hasChanges());
    }

    @Override
    public void ignoreChanges() {
        ignoreChanges = true;
    }

    private void checkUserAccess() {
        if (correspondenceTable != null && correspondenceTable.getContactPerson() != null) {
            if (!userContext.canUserAlterObject(correspondenceTable)) {
                actionButtons.disableConfirmButton(true, "Du har ikke lov til å endre denne korrespondansetabellen");
                metadataEditor.setReadOnly(true);
                codeEditor.setReadOnly(true);
            } else {
                actionButtons.disableConfirmButton(false, "");
            }
        }
    }

    private void saveClick(Button.ClickEvent clickEvent) {
        codeEditor.prepareSave();
        if (metadataEditor.validate()) {
            boolean wasPublished = correspondenceTable.isPublishedInAnyLanguage();
            if (!updateMetadata()) {
                return;
            }
            if (wasPublished) {
                UI.getCurrent().addWindow(new DescriptionOfChangeWindow(userContext,
                        (changelog) -> {
                            addChangelogToCorrespondenceTable(changelog, correspondenceTable);
                            saveCorrespondenceTable(correspondenceTable, InformSubscribers.createWhenPublished(changelog));
                        }));
            } else {
                saveCorrespondenceTable(correspondenceTable, InformSubscribers.createWhenWasUnpublished(
                        correspondenceTable));
            }

        }
    }

    private void addChangelogToCorrespondenceTable(Optional<Changelog> changelog,
            CorrespondenceTable correspondenceTable) {
        if (changelog.isPresent()) {
            correspondenceTable.addChangelog(changelog.get());
        }
    }

    private void saveCorrespondenceTable(CorrespondenceTable correspondenceTable, InformSubscribers informSubscribers) {
        classificationFacade.saveAndIndexCorrespondenceTable(correspondenceTable, informSubscribers);
        VaadinUtil.showSavedMessage();
        if (accordion.getSelectedTab() == codeEditorTab) {
            VaadinUtil.getKlassState().setEditingState(codeEditor.currentEditingState());
        } else {
            VaadinUtil.getKlassState().setEditingState(metadataEditor.currentEditingState());
        }
        ignoreChanges = true;
        VaadinUtil.navigateToCurrentPage();
    }

    private boolean updateMetadata() {
        for (Language language : supportedLanguages) {
            correspondenceTable.setDescription(metadataEditor.getDescription(language), language);
        }

        correspondenceTable.setDraft(metadataEditor.getDraftCheckBoxValue());
        return metadataEditor.checkAndSetPublished(correspondenceTable);
    }

    private void cancelClick(Button.ClickEvent clickEvent) {
        VaadinUtil.navigateTo(Iterables.getLast(getBreadcrumbs()));
    }
}
