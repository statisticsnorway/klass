package no.ssb.klass.designer.admin;

import com.vaadin.data.Item;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;

import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.model.StatisticalUnit;
import no.ssb.klass.core.service.dto.ClassificationReportDto;
import no.ssb.klass.designer.ClassificationReportData;

@SuppressWarnings("serial")
public class ClassificationUnitReportView extends ClassificationUnitReportDesign {

    private StatisticalUnitView statisticalUnitView;
    
    public ClassificationUnitReportView() {
        backButton.addClickListener(e -> resetStatisticView()); 
    }


    
    
    private void resetStatisticView() {
        detailedUseLayout.removeAllComponents();
        this.setVisible(false);
        statisticalUnitView.setVisible(true);
    }




    public void makeDetailedReport(StatisticalUnit statisticalUnit, ClassificationReportData<ClassificationReportDto> classifications,
            StatisticalUnitView statisticalUnitView) {
        this.statisticalUnitView = statisticalUnitView;
        headingText.setValue("Statistisk enhet: " + statisticalUnit.getName(Language.getDefault()));
        numberOfRowsHeader.setValue("Antall klassifikasjoner: " + classifications.genNumberOfRows());
        Table report = null;
        detailedUseReport.clear();
        classifications.reset();
        if (!initReport(classifications)) {
            return;
        }
        do {
            report = ReportUtil.initHeader(classifications, detailedUseLayout);
            do {
                printCommonRows(classifications, report);
                classifications.nextPost();
            } while (!classifications.isLast() && !classifications.checkAndSetNextSection());
        } while (!classifications.isLast());

    }
    
    private boolean initReport(ClassificationReportData<?> classificationReport) {
        detailedUseLayout.removeAllComponents();
        if (classificationReport.isLast()) {
            noClassificationsFound();
            return false;
        }
        return true;
    }
    
    private void noClassificationsFound() {
        Label noDataFoundMessage = new Label("Ingen kodeverk funnet");
        detailedUseLayout.addComponent(noDataFoundMessage);
    }
    
    @SuppressWarnings("unchecked")
    private Item printCommonRows(ClassificationReportData<?> classificationReport, Table report) {
        Object id = report.addItem();
        Item row = report.getItem(id);
        row.getItemProperty("Classification").setValue(classificationReport.getName());
        row.getItemProperty("Type").setValue(classificationReport.getType());
        row.getItemProperty("ContactPerson").setValue(classificationReport.getContactPerson());
        return row;
    }
    
}
