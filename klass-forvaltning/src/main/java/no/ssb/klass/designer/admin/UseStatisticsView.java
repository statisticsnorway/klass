package no.ssb.klass.designer.admin;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import no.ssb.klass.core.service.dto.UsageStatisticsDto;
import no.ssb.klass.designer.admin.util.ReportDescription;
import no.ssb.klass.designer.admin.util.UseStatisticsModeChoice;
import no.ssb.klass.designer.util.ChoiceMapper;
import org.springframework.data.domain.PageRequest;
import org.vaadin.spring.annotation.PrototypeScope;

import com.vaadin.data.Item;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Table;

import no.ssb.klass.core.service.StatisticsService;
import no.ssb.klass.core.util.AdminUtils;
import no.ssb.klass.core.util.TimeUtil;
import no.ssb.klass.designer.admin.util.UsageStatisticsRows;

@SpringComponent
@PrototypeScope
@SuppressWarnings("serial")
public class UseStatisticsView extends UseStatisticsDesign {
    private static final String DATE_INPUT_MASK = "dd.MM.yyyy";
    public static final char CSV_SEPARATOR = ';';
    
    private StatisticsService statisticsService;
    private ContentUseStatView mainView;
    private final Map<UseStatisticsModeChoice, UsageStatisticsData> reports;

    public UseStatisticsView() {
        this.reports = new HashMap<>(3);
        createUseAndSearchStatistics();
    }

    private void createUseAndSearchStatistics() {
        mainReport.setWidth(100, Unit.PERCENTAGE);
        mainReport.setHeight(-1, Unit.PIXELS);
        mainReport.setPageLength(0);
        mainReport.addContainerProperty("Description", String.class, null, " ", null, null);
        mainReport.addContainerProperty("Value", Integer.class, null, "<b>Antall</b>", null, null);
        mainReport.addContainerProperty("LookAll", Button.class, null, " ", null, Table.Align.CENTER);
        mainReport.addContainerProperty("ExportToExcel", Button.class, null, " ", null, Table.Align.CENTER);
        mainReport.setColumnWidth("LookAll", 200);
        mainReport.setColumnWidth("ExportToExcel", 200);
    }

    protected void init(StatisticsService statisticsService, ContentUseStatView contentUseStatView) {
        this.statisticsService = statisticsService;
        this.mainView = contentUseStatView;
        initDateInput();
    }

    private void initDateInput() {
        fromDate.setDateFormat(DATE_INPUT_MASK);
        toDate.setDateFormat(DATE_INPUT_MASK);
        fromDate.setValue(new Date());
        toDate.setValue(new Date());
        fromDate.addValueChangeListener(e -> showClassificationReport());
        toDate.addValueChangeListener(e -> showClassificationReport());
    }

    private void showClassificationReport(UsageStatisticsRows usageStatistics) {
        mainReport.removeAllItems();  
        for (Entry<ReportDescription<UseStatisticsModeChoice>, Integer> row : usageStatistics.getRows().entrySet()) {
            addReportRow(row.getKey(), row.getValue());
        }
    }

    public void showClassificationReport() {
        showClassificationReport(getReportData());
    }

    private UsageStatisticsRows getReportData() {
        LocalDate fromSearchDate = TimeUtil.toLocalDate(fromDate.getValue());
        LocalDate toSearchDate = TimeUtil.toLocalDate(toDate.getValue());
        UsageStatisticsDto dto = statisticsService.getUsageStatistics(fromSearchDate, toSearchDate);
        return new UsageStatisticsRows(dto.numberOfClassifications, dto.numberOfSearchReturnedNull, dto.totalSearchWords);
    }

    @SuppressWarnings("unchecked")
    private void addReportRow(ReportDescription<UseStatisticsModeChoice> reportDescription, int value) {
        Button lookAllButton = lookAllButton(reportDescription);
        Button exportToExcelButton = exporToExcelButton(reportDescription);
        lookAllButton.setWidth(150, Unit.PIXELS);
        exportToExcelButton.setWidth(150, Unit.PIXELS);
        Object id = mainReport.addItem();
        Item row = mainReport.getItem(id);
        row.getItemProperty("Description").setValue(reportDescription.getDisplayName());
        row.getItemProperty("Value").setValue(value);
        if (value == 0) {
            lookAllButton.setEnabled(false);
            exportToExcelButton.setEnabled(false);
        }
        row.getItemProperty("LookAll").setValue(lookAllButton);
        row.getItemProperty("ExportToExcel").setValue(exportToExcelButton);
    }

    private Button exporToExcelButton(ReportDescription<UseStatisticsModeChoice> reportDescription) {
        Button exportToExcelButton = new Button("Last ned til Excel");
        exportToExcelButton.setData(reportDescription);
        
        StreamResource streamResource = new StreamResource(() -> generateReport(reportDescription.getChoice()), reportDescription.getFilename());
        
        FileDownloader fileDownloader = new FileDownloader(streamResource);
        fileDownloader.extend(exportToExcelButton);
        return exportToExcelButton;
    }

    private InputStream generateReport(UseStatisticsModeChoice useStatisticsModeChoice) {
        String exportToExcelData = null;
        UsageStatisticsData usageStatisticsData = reports.get(useStatisticsModeChoice);
        if (usageStatisticsData == null) {
            UseStatisticsModeChoice choice = useStatisticsModeChoice.getChoice(); 
            LocalDate fromSearchDate = TimeUtil.toLocalDate(fromDate.getValue());
            LocalDate toSearchDate = TimeUtil.toLocalDate(toDate.getValue());
            usageStatisticsData =  new UsageStatisticsData(statisticsService.getUsageStatistics(fromSearchDate, toSearchDate, 
                    ChoiceMapper.map(choice),  PageRequest.of(0, Integer.MAX_VALUE)));
            
            reports.put(choice, usageStatisticsData);
        }            
        exportToExcelData = usageStatisticsData.exportToExcel(CSV_SEPARATOR);
        return AdminUtils.createInputStream(exportToExcelData);
    }

    @SuppressWarnings("unchecked")
    private void lookAll(ClickEvent e) {
        LocalDate fromSearchDate = null;
        LocalDate toSearchDate = null;
        ReportDescription<UseStatisticsModeChoice> reportMode = (ReportDescription<UseStatisticsModeChoice>) e
                .getButton().getData();
        fromSearchDate = TimeUtil.toLocalDate(fromDate.getValue());
        toSearchDate = TimeUtil.toLocalDate(toDate.getValue());
        createDetailedReport(statisticsService, reportMode, fromSearchDate, toSearchDate);
    }

    private void createDetailedReport(StatisticsService statisticsService, ReportDescription<UseStatisticsModeChoice> 
            reportMode, LocalDate fromSearchDate, LocalDate toSearchDate) {

        mainView.useDetailedUseView.makeDetailedReport(statisticsService, reportMode, fromSearchDate, toSearchDate, this);
        this.setVisible(false);
        mainView.useDetailedUseView.setVisible(true);

    }
    
    private Button lookAllButton(ReportDescription<UseStatisticsModeChoice> reportDescription) {
        Button lookAllButton = new Button("Se alle");
        lookAllButton.setData(reportDescription);
        lookAllButton.addClickListener(e -> lookAll(e));
        return lookAllButton;
    }
    

}
