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

@PrototypeScope
@SpringView(name = ClassificationListView.NAME)
@SuppressWarnings("serial")
public class ClassificationListView extends ClassificationListDesign implements FilteringView {
    public static final String NAME = "list";
    public static final String PARAM_FAMILY_ID = "familyId";

    @Autowired
    private ClassificationFacade classificationFacade;

    private final ClassificationFilter classificationFilter;
    private final SharedEscapeShortcutListener sharedEscapeShortcutListener;

    public ClassificationListView() {
        sharedEscapeShortcutListener = new SharedEscapeShortcutListener();
        this.classificationFilter = VaadinUtil.getKlassState().getClassificationFilter();
        backButton.addClickListener(e -> VaadinUtil.navigateTo(ClassificationFamilyView.NAME));
        classificationTable.init(versionTable, variantTable);
        versionTable.init(classificationTable, variantTable);
        variantTable.init(versionTable);
        classificationTable.addToSharedActionListener(sharedEscapeShortcutListener);
        versionTable.addToSharedActionListener(sharedEscapeShortcutListener);
        variantTable.addToSharedActionListener(sharedEscapeShortcutListener);
    }

    @Override
    public void enter(ViewChangeEvent event) {
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
        List<ClassificationSeries> classifications = classificationFamily
                .getClassificationSeriesBySectionAndClassificationType(classificationFilter.getCurrentSection(),
                        classificationFilter.getCurrentClassificationType());

        classifications.sort((o1, o2) -> o1.getNameInPrimaryLanguage().compareToIgnoreCase(o2
                .getNameInPrimaryLanguage()));
        classificationTable.addClassifications(classifications);
        classificationTable.updateClassificationFamily(classificationFamily);
    }
}
