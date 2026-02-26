package no.ssb.klass.designer;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.annotation.PrototypeScope;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;

import no.ssb.klass.core.model.ClassificationFamily;
import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.ClassificationVariant;
import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.model.CorrespondenceTable;
import no.ssb.klass.designer.MainView.ClassificationFilter;
import no.ssb.klass.designer.components.ClassificationListViewSelection;
import no.ssb.klass.designer.listeners.SharedEscapeShortcutListener;
import no.ssb.klass.designer.service.ClassificationFacade;
import no.ssb.klass.designer.util.ParameterUtil;
import no.ssb.klass.designer.util.VaadinUtil;
import no.ssb.klass.designer.user.UserContext;
import no.ssb.klass.forvaltning.converting.xml.FullVersionExportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@PrototypeScope
@SpringView(name = ClassificationListView.NAME)
@SuppressWarnings("serial")
public class ClassificationListView extends ClassificationListDesign implements FilteringView {
    private static final Logger log = LoggerFactory.getLogger(ClassificationListView.class);
    public static final String NAME = "list";
    public static final String PARAM_FAMILY_ID = "familyId";

    private ClassificationFacade classificationFacade;

    private UserContext userContext;


    private final ClassificationFilter classificationFilter;
    private final SharedEscapeShortcutListener sharedEscapeShortcutListener;

    private FullVersionExportService exportService;

    @Autowired
    public ClassificationListView(UserContext userContext, ClassificationFacade classificationFacade, FullVersionExportService exportService) {
        this.userContext = userContext;
        this.classificationFacade = classificationFacade;
        this.exportService = exportService;
        log.info("User context list view {}", userContext);
        sharedEscapeShortcutListener = new SharedEscapeShortcutListener();
        this.classificationFilter = VaadinUtil.getKlassState().getClassificationFilter();
        backButton.addClickListener(e -> VaadinUtil.navigateTo(ClassificationFamilyView.NAME));
        classificationTable.init(versionTable, variantTable, userContext, classificationFacade);
        versionTable.init(classificationTable, variantTable, userContext, classificationFacade, exportService);
        variantTable.init(versionTable, userContext);
        classificationTable.addToSharedActionListener(sharedEscapeShortcutListener);
        versionTable.addToSharedActionListener(sharedEscapeShortcutListener);
        variantTable.addToSharedActionListener(sharedEscapeShortcutListener);
    }

    @Override
    public void enter(ViewChangeEvent event) {
        log.info("User context list view {}", userContext);
        Long familyId = ParameterUtil.getRequiredLongParameter(PARAM_FAMILY_ID, event.getParameters());
        ClassificationFamily classificationFamily = classificationFacade.getRequiredClassificationFamily(familyId);
        updateFamilyLabel(classificationFamily.getName());
        resetAllTables();
        populateClassificationTable(classificationFamily);
        updateTableSelection();
    }

    private void updateTableSelection() {
        ClassificationListViewSelection selection = VaadinUtil.getKlassState().getClassificationListViewSelection();
        if (selection == null) {
            return;
        }
        ClassificationSeries series = classificationFacade.getRequiredClassificationSeries(selection
                .getClassificationId());
        classificationTable.selectItem(series);

        if (selection.getVersionId().isPresent()) {
            ClassificationVersion version = classificationFacade.getRequiredClassificationVersion(selection
                    .getVersionId().get());
            versionTable.selectItem(version);
            if (selection.getVariantId().isPresent()) {
                ClassificationVariant variant = classificationFacade.getRequiredClassificationVariant(selection
                        .getVariantId().get());
                variantTable.selectItem(variant);
            } else if (selection.getCorrespondenceTableId().isPresent()) {
                CorrespondenceTable correspondenceTable = classificationFacade.getRequiredCorrespondenceTable(
                        selection.getCorrespondenceTableId().get());
                variantTable.selectItem(correspondenceTable);
            }
        }
        VaadinUtil.getKlassState().clearClassificationListViewSelection();
    }

    private void updateFamilyLabel(String familyName) {
        familyLabel.setValue(familyName);
    }

    private void resetAllTables() {
        variantTable.reset();
        classificationTable.reset();
        versionTable.reset();
    }

    private void populateClassificationTable(ClassificationFamily classificationFamily) {
        log.info("Populating classification table with user context {}", userContext);
        List<ClassificationSeries> classifications = classificationFamily
                .getClassificationSeriesBySectionAndClassificationType(classificationFilter.getCurrentSection(),
                        classificationFilter.getCurrentClassificationType());

        classifications.sort((o1, o2) -> o1.getNameInPrimaryLanguage().compareToIgnoreCase(o2
                .getNameInPrimaryLanguage()));
        classificationTable.addClassifications(classifications);
        classificationTable.updateClassificationFamily(classificationFamily);
    }
}
