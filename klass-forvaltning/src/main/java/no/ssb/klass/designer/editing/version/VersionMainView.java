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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import no.ssb.klass.forvaltning.converting.xml.ClassificationVersionXmlService;
import org.springframework.context.ApplicationContext;

@PrototypeScope
@SpringView(name = VersionMainView.NAME)
@SuppressWarnings("serial")
public class VersionMainView extends VersionMainEditor implements EditingView {
    private static final Logger log = LoggerFactory.getLogger(VersionMainView.class);

    public static final String NAME = "editVersion";
    public static final String PARAM_VERSION_ID = "versionId";

    private boolean ignoreChanges = false;

    private ClassificationFacade classificationFacade;
    private UserContext userContext;
    private PublicationChoiceEditor publicationChoiceEditor;

    // Added versionXmlService and applicationContext for injecting values into code editor - cbi
    private ClassificationVersionXmlService versionXmlService;
    private ApplicationContext applicationContext;

    @Autowired
    public VersionMainView(ClassificationFacade classificationFacade, UserContext userContext, ClassificationVersionXmlService versionXmlService, ApplicationContext applicationContext) {
        this.classificationFacade = classificationFacade;
        this.userContext = userContext;
        this.versionXmlService = versionXmlService;
        this.applicationContext = applicationContext;
        publicationChoiceEditor = new PublicationChoiceEditor(new MarginInfo(true, false, true, true));
        actionButtons.addConfirmClickListener(event -> checkAndSaveVersion());
        actionButtons.addCancelClickListener(this::cancelClick);
        versionAccordion.addSelectedTabChangeListener(eventTabChange -> metadataEditor.updateVersion(codeEditor
                .getClassificationVersion(), classificationFacade));
        log.debug(
                "Creating version main view with version xml service {}, application context {}, user context {} and " +
                        "classification facade {}",
                versionXmlService,
                applicationContext,
                userContext,
                classificationFacade
        );
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
        // Added set methods for CodeEditorView in order to inject these values - cbi
        codeEditor.setVersionXmlService(versionXmlService);
        codeEditor.setApplicationContext(applicationContext);
        codeEditor.setClassificationFacade(classificationFacade);
        codeEditor.init(classificationVersion);
        if (editingState.isCodeEditorNotMetadataVisible()) {
            codeEditor.restorePreviousEditingState(editingState);
            versionAccordion.setSelectedTab(codeEditor);
        } else {
            metadataEditor.restorePreviousEditingState(editingState);
        }
        log.info(
                "Enter version editor view with user context {}, version xml service {}, application context {} " +
                        "and classification facade {}", userContext, versionXmlService, applicationContext, classificationFacade);
        log.info("Enter version editor view with code editor {}", codeEditor);
        log.info("Enter version editor view with metadata editor {}", metadataEditor);
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

    @java.lang.Override
    public java.lang.String toString() {
        final java.lang.StringBuffer sb = new java.lang.StringBuffer("VersionMainView{");
        sb.append("ignoreChanges=").append(ignoreChanges);
        sb.append(", classificationFacade=").append(classificationFacade);
        sb.append(", userContext=").append(userContext);
        sb.append(", publicationChoiceEditor=").append(publicationChoiceEditor);
        sb.append(", versionXmlService=").append(versionXmlService);
        sb.append(", applicationContext=").append(applicationContext);
        sb.append('}');
        return sb.toString();
    }
}
