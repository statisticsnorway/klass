package no.ssb.klass.designer.editing.variant;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.annotation.PrototypeScope;

import com.google.common.collect.Iterables;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

import no.ssb.klass.core.model.Changelog;
import no.ssb.klass.core.model.ClassificationVariant;
import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.util.DraftUtil;
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
@SpringView(name = EditVariantEditorView.NAME)
@SuppressWarnings("serial")
public class EditVariantEditorView extends EditVariantEditorDesign implements EditingView {
    Logger log = LoggerFactory.getLogger(EditVariantEditorView.class);

    protected List<Language> supportedLanguages = Arrays.asList(Language.NB, Language.NN, Language.EN);

    public static final String NAME = "variantEditor";
    public static final String PARAM_ID = "variantId";

    private boolean ignoreChanges = false;

    @Autowired
    private ClassificationFacade classificationFacade;
    @Autowired
    private UserContext userContext;

    private ClassificationVariant variant;

    public EditVariantEditorView() {
        actionButtons.setConfirmText("Lagre");
        actionButtons.addConfirmClickListener(this::saveClick);
        actionButtons.addCancelClickListener(this::cancelClick);
        metadataEditor.setDatesVisible(true);
        metadataEditor.addPublicationComponent();
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        EditingState editingState = VaadinUtil.getKlassState().getAndClearEditingState();
        Long variantId = ParameterUtil.getRequiredLongParameter(PARAM_ID, event.getParameters());
        variant = classificationFacade.getRequiredClassificationVariant(variantId);
        metadataEditor.init(variant.getPrimaryLanguage());
        metadataEditor.setContactPerson(variant.getContactPerson());
        for (Language language : supportedLanguages) {
            metadataEditor.setName(language, variant.getNameBase(language));
            metadataEditor.setDescription(language, variant.getIntroduction(language));
        }
        metadataEditor.setDateRange(variant.getDateRange());
        metadataEditor.setPublication(variant);
        header.setTypeText(TypeAndNameHeaderComponent.TEXT_VARIANT_OF + variant.getClassificationVersion()
                .getNameInPrimaryLanguage());
        header.setNameText(variant.getNameInPrimaryLanguage());
        checkUserAccess();
        // NB. checkUserAccess() must be before codeEditor.init(..), if not a NewCodeEditor will be
        // added even when user does not have permission to alter variant.
        codeEditor.init(variant);
        if (editingState.isCodeEditorNotMetadataVisible()) {
            accordion.setSelectedTab(codeEditor);
            codeEditor.restorePreviousEditingState(editingState);
        } else {
            metadataEditor.restorePreviousEditingState(editingState);
        }
    }

    @Override
    public List<Breadcrumb> getBreadcrumbs() {
        return Breadcrumb.newBreadcrumbs(variant);
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
        if (!userContext.canUserAlterObject(variant)) {
            actionButtons.disableConfirmButton(true, "Du har ikke lov til å endre denne varianten");
            metadataEditor.setReadOnly(true);
            codeEditor.setReadOnly(true);
        } else {
            actionButtons.disableConfirmButton(false, "");
        }
    }

    private void saveClick(Button.ClickEvent clickEvent) {
        codeEditor.prepareSave();
        if (metadataEditor.validate()) {
            if (draftNameConflict()) {
                String message = "Det finnes allerede et utkast med dette navnet";
                Notification.show(message);
                return;
            }
            boolean wasPublished = variant.isPublishedInAnyLanguage();
            if (!updateVariantMetadataValues()) {
                return;
            }
            if (wasPublished) {
                UI.getCurrent().addWindow(new DescriptionOfChangeWindow(userContext,
                        (changelog) -> {
                            addChangelogToVariant(changelog, variant);
                            saveVariant(variant, InformSubscribers.createWhenPublished(changelog));
                        }));
            } else {
                saveVariant(variant, InformSubscribers.createWhenWasUnpublished(variant));
            }
        }
    }

    private boolean draftNameConflict() {
        if (DraftUtil.isDraft(metadataEditor.getDateRange())) {
            String name = metadataEditor.getName(variant.getPrimaryLanguage());
            ClassificationVersion version = classificationFacade.getRequiredClassificationVersion(variant
                    .getClassificationVersion().getId());
            if (version.getClassificationVariants().stream().filter(v -> !v.equals(variant)).anyMatch(
                    draftWithNameExists(name))) {
                return true;
            }
        }
        return false;
    }

    private Predicate<ClassificationVariant> draftWithNameExists(String name) {
        return v -> Objects.equals(name, v.getNameBase(v.getPrimaryLanguage())) && v.isDraft();
    }

    private void addChangelogToVariant(Optional<Changelog> changelog, ClassificationVariant classificationVariant) {
        if (changelog.isPresent()) {
            classificationVariant.addChangelog(changelog.get());
        }
    }

    private void saveVariant(ClassificationVariant classificationVariant, InformSubscribers informSubscribers) {
        classificationFacade.saveAndIndexVariant(classificationVariant, informSubscribers);
        VaadinUtil.showSavedMessage();
        if (accordion.getSelectedTab() == codeEditor) {
            VaadinUtil.getKlassState().setEditingState(codeEditor.currentEditingState());
        } else {
            VaadinUtil.getKlassState().setEditingState(metadataEditor.currentEditingState());
        }
        ignoreChanges = true;
        VaadinUtil.navigateToCurrentPage();
    }

    private boolean updateVariantMetadataValues() {
        for (Language language : supportedLanguages) {
            variant.setNameBase(language, metadataEditor.getName(language));
            variant.setIntroduction(metadataEditor.getDescription(language), language);
        }
        variant.setDateRange(metadataEditor.getDateRange());
        variant.updateContactPerson(metadataEditor.getContactPerson());
        return metadataEditor.checkAndSetPublished(variant);
    }

    private void cancelClick(Button.ClickEvent clickEvent) {
        VaadinUtil.navigateTo(Iterables.getLast(getBreadcrumbs()));
    }
}
