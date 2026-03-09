package no.ssb.klass.designer.editing.correspondencetable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.vaadin.spring.annotation.PrototypeScope;

import com.google.common.eventbus.EventBus;
import com.vaadin.spring.annotation.SpringComponent;

import no.ssb.klass.forvaltning.converting.xml.CorrespondenceTableXmlService;
import no.ssb.klass.core.model.CorrespondenceTable;
import no.ssb.klass.designer.editing.EditingState;
import no.ssb.klass.designer.editing.components.ImportExportComponent;
import no.ssb.klass.designer.util.ComponentUtil;

@SpringComponent
@PrototypeScope
public class CorrespondenceTableCodeEditorView extends CorrespondenceTableCodeEditorDesign implements
        HasCorrespondenceTableComponent {

    private ImportExportComponent<CorrespondenceTable> importExportComponent;

    private final EventBus eventbus;

    @Autowired
    private CorrespondenceTableXmlService correspondenceTableXmlService;

    @Autowired
    private ApplicationContext applicationContext;

    public CorrespondenceTableCodeEditorView() {
        this.eventbus = new EventBus("correspondence-table");
        eventbus.register(sourceVersion);
        eventbus.register(targetVersion);
    }

    @Override
    public void init(CorrespondenceTable correspondenceTable) {
        targetVersionName.setValue("Mål: " + correspondenceTable.getTarget().getNameInPrimaryLanguage());
        targetVersion.init(eventbus, correspondenceTable);

        sourceVersionName.setValue("Kilde: " + correspondenceTable.getSource().getNameInPrimaryLanguage());
        sourceVersion.init(eventbus, correspondenceTable);

        importExportComponent = new ImportExportComponent<>(
                applicationContext, correspondenceTableXmlService, importButton, exportButton);
        importExportComponent.init(correspondenceTable, "Korrespondansetabell");
        importExportComponent.setOnCompleteCallback(this::updateView);

    }

    private void updateView(boolean importSuccessful) {
        sourceVersion.refresh();
        targetVersion.refresh();
        sourceVersion.markAsDirtyRecursive();
        targetVersion.markAsDirtyRecursive();
        if (importSuccessful) {
            sourceVersion.getTable().markAsModified();
            targetVersion.getTable().markAsModified();
        }
    }

    @Override
    public void restorePreviousEditingState(EditingState editingState) {
        // No editing state, i.e. no translations etc for correspondence maps
    }

    @Override
    public EditingState currentEditingState() {
        return EditingState.newCodeEditorEditingState(false, true);
    }

    @Override
    public boolean hasChanges() {
        return sourceVersion.hasChanges() || targetVersion.hasChanges();
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        importButton.setEnabled(readOnly);
        ComponentUtil.setReadOnlyRecursively(this, readOnly);
    }

    @Override
    public void prepareSave() {
    }
}