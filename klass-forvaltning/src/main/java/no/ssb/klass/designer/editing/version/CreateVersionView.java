package no.ssb.klass.designer.editing.version;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.annotation.PrototypeScope;

import com.google.common.collect.ImmutableMap;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.util.DateRange;
import no.ssb.klass.core.util.DraftUtil;
import no.ssb.klass.designer.ClassificationListView;
import no.ssb.klass.designer.EditingView;
import no.ssb.klass.designer.components.BreadcumbPanel.Breadcrumb;
import no.ssb.klass.designer.components.common.ConfirmOrCancelComponent;
import no.ssb.klass.designer.service.ClassificationFacade;
import no.ssb.klass.designer.service.InformSubscribers;
import no.ssb.klass.designer.user.UserContext;
import no.ssb.klass.designer.util.ParameterUtil;
import no.ssb.klass.designer.util.VaadinUtil;

@PrototypeScope
@SpringView(name = CreateVersionView.NAME)
@SuppressWarnings("serial")
public class CreateVersionView extends VerticalLayout implements EditingView {
    Logger log = LoggerFactory.getLogger(CreateVersionView.class);

    public static final String NAME = "versionEditor";
    public static final String PARAM_CLASSIFICATION_ID = "classificationId";
    public static final String PARAM_DRAFT = "draft";
    public static final String VALID_FROM_KEY = "validFrom";
    public static final String VALID_TO_KEY = "validTo";

    private boolean ignoreChanges = false;

    @Autowired
    private ClassificationFacade classificationFacade;

    @Autowired
    private UserContext userContext;

    private final VersionEditorView versionEditorView;
    private ClassificationVersion classificationVersion;

    public CreateVersionView() {
        ConfirmOrCancelComponent actionButtons = new ConfirmOrCancelComponent();
        actionButtons.setConfirmText("Lagre");
        actionButtons.addConfirmClickListener(event -> save());
        actionButtons.addCancelClickListener(event -> cancel());
        HorizontalLayout actionButtonsWrap = new HorizontalLayout(actionButtons);
        actionButtonsWrap.setMargin(new MarginInfo(false, false, true, true));
        versionEditorView = new VersionEditorView();

        addComponents(versionEditorView, actionButtonsWrap);
    }

    @Override
    public void enter(ViewChangeEvent event) {
        DateRange dateRange = DraftUtil.getDraftDateRange();
        if (!ParameterUtil.hasParameter(PARAM_DRAFT, event.getParameters())) {
            dateRange = ParameterUtil.getOptionalDataRange(VALID_FROM_KEY, VALID_TO_KEY, event.getParameters());
        }
        classificationVersion = createVersion(getClassificationSeries(event.getParameters()), dateRange);
        versionEditorView.init(classificationVersion);
        versionEditorView.enableAlias(userContext.isAdministrator());
    }

    @Override
    public List<Breadcrumb> getBreadcrumbs() {
        return Breadcrumb.newBreadcrumbs(classificationVersion);
    }

    @Override
    public boolean hasChanges() {
        return !ignoreChanges && versionEditorView.hasChanges();

    }

    @Override
    public void ignoreChanges() {
        ignoreChanges = true;
    }

    private ClassificationVersion createVersion(ClassificationSeries classificationSeries, DateRange dateRange) {
        ClassificationVersion version = new ClassificationVersion(dateRange);
        classificationSeries.addClassificationVersion(version);
        if (DraftUtil.isDraft(dateRange)) {
            return classificationSeries.getClassificationVersionDraft();
        } else {
            return classificationSeries.getVersionByRange(dateRange);
        }
    }

    private ClassificationSeries getClassificationSeries(String parametersString) {
        Long classificationId = ParameterUtil.getRequiredLongParameter(PARAM_CLASSIFICATION_ID, parametersString);
        return classificationFacade.getRequiredClassificationSeries(classificationId);
    }

    private void save() {
        if (!versionEditorView.updateVersion(classificationVersion, classificationFacade)) {
            return;
        }
        ignoreChanges = true;
        ClassificationVersion version = classificationFacade.saveAndIndexVersion(classificationVersion,
                InformSubscribers.createNotInformSubscribers());
        VaadinUtil.showSavedMessage();
        VaadinUtil.getKlassState().setEditingState(versionEditorView.currentEditingState());
        VaadinUtil.navigateTo(VersionMainView.NAME, ImmutableMap.of(VersionMainView.PARAM_VERSION_ID,
                version.getId().toString()));
    }

    private void cancel() {
        String familyId = classificationVersion.getOwnerClassification().getClassificationFamily().getId().toString();
        VaadinUtil.navigateTo(ClassificationListView.NAME, ImmutableMap.of(ClassificationListView.PARAM_FAMILY_ID,
                familyId));
    }
}
