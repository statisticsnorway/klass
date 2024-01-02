package no.ssb.klass.designer.admin;

import java.io.InputStream;
import java.util.List;

import no.ssb.klass.core.service.dto.StatisticalEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.vaadin.spring.annotation.PrototypeScope;

import com.vaadin.data.Item;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Table;

import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.model.StatisticalUnit;
import no.ssb.klass.core.service.StatisticsService;
import no.ssb.klass.core.service.dto.ClassificationReportDto;
import no.ssb.klass.core.service.dto.StatisticalUnitDto;
import no.ssb.klass.core.util.AdminUtils;
import no.ssb.klass.designer.ClassificationReportData;

@SpringComponent
@PrototypeScope
@SuppressWarnings("serial")
public class StatisticalUnitView extends StatisticalUnitDesign {

    private StatisticsService statisticsService;
    private ContentUseStatView mainView;
    
    public StatisticalUnitView() {
        createStatisticalUnitTable();
    }
    
    private void createStatisticalUnitTable() {
        statisticalUnittable.setWidth(100, Unit.PERCENTAGE);
        statisticalUnittable.setHeight(-1, Unit.PIXELS);
        statisticalUnittable.setPageLength(0);
        statisticalUnittable.addContainerProperty("Name", String.class, null, "Enhetstype", null, null);
        statisticalUnittable.addContainerProperty("Value", Integer.class, null, "<b>Antall knytninger</b>", null, null);
        statisticalUnittable.addContainerProperty("LookAll", Button.class, null, " ", null, Table.Align.CENTER);
        statisticalUnittable.addContainerProperty("ExportToExcel", Button.class, null, " ", null, Table.Align.CENTER);
        statisticalUnittable.setColumnWidth("LookAll", 200);
        statisticalUnittable.setColumnWidth("ExportToExcel", 200);
    }
    
    public void init(StatisticsService statisticsService, ContentUseStatView contentUseStatView) {
        this.statisticsService = statisticsService;
        this.mainView = contentUseStatView;
    }

    public void showStatisticalUnitReport() {
        showStatisticalUnitReport(getReportData());
    }

    private void showStatisticalUnitReport(UsageStatisticsData reportData) {
        statisticalUnittable.removeAllItems();
        reportData.reset();
        do {
            printRow(reportData);
            reportData.nextPost();
        } while (!reportData.isLast());  
    }

    @SuppressWarnings("unchecked")
    private void printRow(UsageStatisticsData usageStatisticsData) {
        Object id = statisticalUnittable.addItem();
        Item row = statisticalUnittable.getItem(id);
        String name = usageStatisticsData.getName();
        row.getItemProperty("Name").setValue(name);
        row.getItemProperty("Value").setValue(usageStatisticsData.getCount().intValue());
        StatisticalUnit statisticalUnit = ((StatisticalUnitDto)usageStatisticsData.getLine()).getStatisticalUnit();
        Button lookAllButton = lookAllButton(statisticalUnit);
        Button exportToExcelButton = exportToExcelButton(statisticalUnit);
        row.getItemProperty("LookAll").setValue(lookAllButton);
        row.getItemProperty("ExportToExcel").setValue(exportToExcelButton);
    }
    

    private UsageStatisticsData getReportData() {
        Page<StatisticalEntity> overView = statisticsService.getStaticalUnitsOverView(new PageRequest(0, Integer.MAX_VALUE));
        return new UsageStatisticsData(overView);
    }
    
    private Button lookAllButton(StatisticalUnit statisticalUnit) {
        Button lookAllButton = new Button("Se alle");
        lookAllButton.setData(statisticalUnit);
        lookAllButton.addClickListener(e -> lookAll(statisticalUnit));
        return lookAllButton;
    }
    
    private void lookAll(StatisticalUnit statisticalUnit) {
        createDetailedReport(statisticalUnit);
    }

    private Button exportToExcelButton(StatisticalUnit statisticalUnit) {
        Button exportToExcelButton = new Button("Last ned til Excel");
        exportToExcelButton.setData(statisticalUnit);
        String fileName = statisticalUnit.getName(Language.getDefault()) + ".csv";
        StreamResource streamResource = new StreamResource(() -> generateReport(statisticalUnit), fileName);
        
        FileDownloader fileDownloader = new FileDownloader(streamResource);
        fileDownloader.extend(exportToExcelButton);
        return exportToExcelButton;
    }
    
    private InputStream generateReport(StatisticalUnit statisticalUnit) {

        List<ClassificationReportDto> reportData = statisticsService.getAllClassificationSeriesForStaticalUnit(statisticalUnit);
        ClassificationReportData<ClassificationReportDto> classificationReport = new ClassificationReportData<>(reportData);
        classificationReport.sort();
        
        String exportToExcelData = classificationReport.exportToExcel(UseStatisticsView.CSV_SEPARATOR);
        return AdminUtils.createInputStream(exportToExcelData);
    }
    
    private void createDetailedReport(StatisticalUnit statisticalUnit) {
        List<ClassificationReportDto> reportData = statisticsService.getAllClassificationSeriesForStaticalUnit(statisticalUnit);
        ClassificationReportData<ClassificationReportDto> classificationReport = new ClassificationReportData<>(reportData);
        classificationReport.sort();
        
        mainView.classificationUnitReportView.makeDetailedReport(statisticalUnit, classificationReport, this);
        this.setVisible(false);
        mainView.classificationUnitReportView.setVisible(true);

    }
}
