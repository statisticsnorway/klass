package no.ssb.klass.designer.editing.version;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.annotation.PrototypeScope;

import com.google.common.collect.Iterables;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.UI;

import no.ssb.klass.core.model.Changelog;
import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.designer.EditingView;
import no.ssb.klass.designer.components.BreadcumbPanel.Breadcrumb;
import no.ssb.klass.designer.editing.EditingState;
import no.ssb.klass.designer.editing.components.PublicationChoiceEditor;
import no.ssb.klass.designer.service.ClassificationFacade;
import no.ssb.klass.designer.service.InformSubscribers;
import no.ssb.klass.designer.user.UserContext;
import no.ssb.klass.designer.util.ParameterUtil;
import no.ssb.klass.designer.util.VaadinUtil;
import no.ssb.klass.designer.windows.DescriptionOfChangeWindow;

@PrototypeScope
@SpringView(name = VersionMainView.NAME)
@SuppressWarnings("serial")
public class VersionMainView extends VersionMainEditor implements EditingView {

    public static final String NAME = "editVersion";
    public static final String PARAM_VERSION_ID = "versionId";

    private boolean ignoreChanges = false;

    @Autowired
    private ClassificationFacade classificationFacade;

    @Autowired
    private UserContext userContext;

    private PublicationChoiceEditor publicationChoiceEditor;

    public VersionMainView() {
        publicationChoiceEditor = new PublicationChoiceEditor(new MarginInfo(true, false, true, true));
        actionButtons.addConfirmClickListener(event -> checkAndSaveVersion());
        actionButtons.addCancelClickListener(this::cancelClick);
        versionAccordion.addSelectedTabChangeListener(eventTabChange -> metadataEditor.updateVersion(codeEditor
                .getClassificationVersion(), classificationFacade));
    }

    @Override
    public void enter(ViewChangeEvent event) {
        EditingState editingState = VaadinUtil.getKlassState().getAndClearEditingState();
        Long versionId = ParameterUtil.getRequiredLongParameter(PARAM_VERSION_ID, event.getParameters());
        ClassificationVersion classificationVersion = classificationFacade.getRequiredClassificationVersion(versionId);
        metadataEditor.init(classificationVersion);
        metadataEditor.enableAlias(userContext.isAdministrator());
        metadataEditor.addComponent(publicationChoiceEditor);
        publicationChoiceEditor.init(userContext.isAdministrator(), classificationVersion);
        checkUserAccess(classificationVersion);
        // NB. checkUserAccess() must be before codeEditor.init(..), if not a NewCodeEditor will be
        // added even when user does not have permission to alter version.
        codeEditor.init(classificationVersion);
        if (editingState.isCodeEditorNotMetadataVisible()) {
            codeEditor.restorePreviousEditingState(editingState);
            versionAccordion.setSelectedTab(codeEditor);
        } else {
            metadataEditor.restorePreviousEditingState(editingState);
        }
    }

    @Override
    public List<Breadcrumb> getBreadcrumbs() {
        return Breadcrumb.newBreadcrumbs(codeEditor.getClassificationVersion());
    }

    @Override
    public boolean hasChanges() {
        return !ignoreChanges && (metadataEditor.hasChanges() || codeEditor.hasChanges());
    }

    @Override
    public void ignoreChanges() {
        ignoreChanges = true;
    }

    private void checkUserAccess(ClassificationVersion classificationVersion) {
        if (!userContext.canUserAlterObject(classificationVersion)) {
            actionButtons.disableConfirmButton(true, "Du har ikke lov til å endre denne versjonen");
            codeEditor.setReadOnly(true);
            metadataEditor.setReadOnly(true);
        }
    }

    private void checkAndSaveVersion() {
        codeEditor.prepareSave();
        ClassificationVersion classificationVersion = codeEditor.getClassificationVersion();
        boolean wasPublished = classificationVersion.isPublishedInAnyLanguage();
        if (metadataEditor.updateVersion(classificationVersion, classificationFacade) && publicationChoiceEditor
                .checkAndSetPublished(userContext.isAdministrator(), classificationVersion)) {
            if (wasPublished) {
                UI.getCurrent().addWindow(new DescriptionOfChangeWindow(userContext,
                        (changelog) -> {
                            addChangelogToVersion(changelog, classificationVersion);
                            saveVersion(classificationVersion, InformSubscribers.createWhenPublished(changelog));
                        }));
            } else {
                saveVersion(classificationVersion, InformSubscribers.createWhenWasUnpublished(classificationVersion));
            }
        }
    }

    private void addChangelogToVersion(Optional<Changelog> changelog, ClassificationVersion classificationVersion) {
        if (changelog.isPresent()) {
            classificationVersion.addChangelog(changelog.get());
        }
    }

    private void saveVersion(ClassificationVersion version, InformSubscribers informSubscribers) {
        classificationFacade.saveAndIndexVersion(version, informSubscribers);
        VaadinUtil.showSavedMessage();
        if (versionAccordion.getSelectedTab() == codeEditor) {
            VaadinUtil.getKlassState().setEditingState(codeEditor.currentEditingState());
        } else {
            VaadinUtil.getKlassState().setEditingState(metadataEditor.currentEditingState());
        }
        ignoreChanges = true;
        VaadinUtil.navigateToCurrentPage();
    }

    private void cancelClick(Button.ClickEvent clickEvent) {
        VaadinUtil.navigateTo(Iterables.getLast(getBreadcrumbs()));
    }
}
