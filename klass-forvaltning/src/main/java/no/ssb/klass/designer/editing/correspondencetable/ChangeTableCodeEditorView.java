package no.ssb.klass.designer.editing.correspondencetable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.vaadin.spring.annotation.PrototypeScope;

import com.google.common.eventbus.EventBus;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import no.ssb.klass.forvaltning.converting.xml.CorrespondenceTableXmlService;
import no.ssb.klass.core.model.CorrespondenceTable;
import no.ssb.klass.designer.components.common.layouts.VerticalSpacedLayout;
import no.ssb.klass.designer.editing.EditingState;
import no.ssb.klass.designer.editing.components.ImportExportComponent;
import no.ssb.klass.designer.util.ComponentUtil;

/**
 * A ChangeTable is a correspondenceTable whose source and target version have same classification. Hence the table
 * describes changes between 2 versions of a classification.
 */
@SpringComponent
@PrototypeScope
public class ChangeTableCodeEditorView extends ChangeTableCodeEditorDesign implements HasCorrespondenceTableComponent {
    private final EventBus eventbus;
    private ImportExportComponent<CorrespondenceTable> importExportComponent;
    @Autowired
    private CorrespondenceTableXmlService correspondenceTableXmlService;
    @Autowired
    private ApplicationContext applicationContext;
    private static final String HELP_TEXT =
            "Klass håndterer korrespondansetabeller mellom to versjoner fra ulike kodeverk og "
                    + "korrespondansetabeller mellom to versjoner av samme kodeverk på forskjellig måte. "
                    + "Når du velger å opprette en korrespondansetabell mellom to versjoner av samme kodeverk, "
                    + "får du en endrings-tabell i midten av skjermbildet. "
                    + "Det er i denne du skal vise endringene mellom de to versjonene ved å dra de relevante elementene over fra "
                    + "kildeversjonen (den du lager korrespondansen fra) til målversjonen (den du lager korrespondansen til). "
                    + "Vi anbefaler at du lager korrespondansen fra ny til gammel versjon, f.eks. fra Kommuneinndelingen 2014 "
                    + "til Kommuneinndelingen 2013. Dette vil være mest logisk siden de fleste brukerne vil ta utgangspunkt i en "
                    + "gjeldende versjon, og se hvordan den har endret seg fra forrige versjon. "
                    + "Du oppretter det antall korrespondanser du skal ha (enten ved oppstart eller underveis) ved å klikke "
                    + "på Opprett ny korrespondanse. Da dukker det opp felt for dette i endringstabellen. "
                    + "Dersom en kode A i en ny versjon, er satt sammen av kode B og C i den foregående versjonen, "
                    + "må kode A dras over to ganger fra kildeversjonen (må trekkes over til 'Trekk kildekode hit'-feltet), "
                    + "og så må kode B fra den gamle versjonen trekkes over til feltet 'Trekk over målkode hit' for den ene A-en, "
                    + "og C trekkes over på samme måte til den andre A-en. En får da vist at A i ny versjon, korresponderer både "
                    + "med B og C i den gamle (er sammensatt av disse). Dersom et element i den gamle versjonen er utgått, "
                    + "trekkes det over i endringstabellen mens korrespondansefeltet på venstre side blir stående tomt. "
                    + "Og tilsvarende dersom det er oppstått noen nye elementer (uten korrespondanse til de gamle) så trekkes de "
                    + "over i endringstabellen uten tilsvarende korrespondanse på høyre side. Når endringstabellen lagres, "
                    + "vil de tomme feltene fylles ut med hhv 'Utgått' og 'Ny'. \n"
                    + "Endringstabellen vil kun vise reelle endringer (sammenslåtte eller splittede elementer, og nye og utgåtte elementer). "
                    + "Elementer som er like i ny og gammel versjon, vises ikke.";

    public ChangeTableCodeEditorView() {
        this.eventbus = new EventBus("change-table");
        eventbus.register(sourceVersion);
        eventbus.register(targetVersion);
        addNewCorrespondence.addClickListener((event) -> correspondenceMap.addNewRow());
        descriptionButton.addClickListener(event -> createDescriptionWindow());
    }

    private void createDescriptionWindow() {
        Window descriptionWindow = new Window("Hjelpetekst for korrespondanser mellom versjoner");
        TextArea descriptionText = new TextArea();
        descriptionText.setValue(HELP_TEXT);
        descriptionText.setReadOnly(true);
        descriptionText.setSizeFull();
        Button okButton = new Button("OK", event -> descriptionWindow.close());
        VerticalLayout content = new VerticalSpacedLayout(descriptionText, okButton);
        content.setExpandRatio(descriptionText, 1);
        content.setMargin(true);
        content.setSizeFull();
        descriptionWindow.setContent(content);
        descriptionWindow.center();
        descriptionWindow.setHeight("400px");
        descriptionWindow.setWidth("600px");
        UI.getCurrent().addWindow(descriptionWindow);
    }

    @Override
    public void init(CorrespondenceTable correspondenceTable) {

        targetVersionName.setValue("Mål: " + correspondenceTable.getTarget().getNameInPrimaryLanguage());
        targetVersion.init(eventbus, correspondenceTable.getTarget());
        // setNotSelectable(targetVersion);

        sourceVersionName.setValue("Kilde: " + correspondenceTable.getSource().getNameInPrimaryLanguage());
        sourceVersion.init(eventbus, correspondenceTable.getSource());
        // setNotSelectable(sourceVersion);

        correspondenceMap.init(eventbus, correspondenceTable);

        importExportComponent = new ImportExportComponent<>(
                applicationContext, correspondenceTableXmlService, importButton, exportButton);
        importExportComponent.init(correspondenceTable, "Korrespondansetabell");
        importExportComponent.setOnCompleteCallback(this::updateView);

    }

    private void updateView(boolean success) {
        sourceVersion.refresh();
        targetVersion.refresh();
        correspondenceMap.refresh();

        sourceVersion.markAsDirtyRecursive();
        targetVersion.markAsDirtyRecursive();
        correspondenceMap.markAsDirtyRecursive();
        if (success) {
            correspondenceMap.markAsModified();
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
        return correspondenceMap.hasChanges();
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        addNewCorrespondence.setEnabled(!readOnly);
        ComponentUtil.setReadOnlyRecursively(this, readOnly);
    }

    @Override
    public void prepareSave() {
        correspondenceMap.commitNewCorrespondenceMaps();
    }
}