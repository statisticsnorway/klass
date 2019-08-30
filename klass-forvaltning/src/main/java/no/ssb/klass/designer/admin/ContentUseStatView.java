package no.ssb.klass.designer.admin;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import no.ssb.klass.core.service.dto.ClassificationReportDto;
import no.ssb.klass.core.service.dto.ClassificationVersionReportDto;
import no.ssb.klass.core.service.dto.ContentUseStatisticDto;
import no.ssb.klass.core.service.enums.ReportMode;
import no.ssb.klass.designer.admin.util.ReportDescription;
import no.ssb.klass.designer.admin.util.ReportModeChoice;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Item;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.Table;
import com.vaadin.ui.themes.ValoTheme;

import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.service.StatisticsService;
import no.ssb.klass.core.util.AdminUtils;
import no.ssb.klass.designer.ClassificationReportData;
import no.ssb.klass.designer.FilteringView;
import no.ssb.klass.designer.MainView.ClassificationFilter;
import no.ssb.klass.designer.VersionReportData;
import no.ssb.klass.designer.admin.util.ContentUseStatisticRows;
import no.ssb.klass.designer.util.VaadinUtil;

@UIScope
@SpringView(name = ContentUseStatView.NAME)
@SuppressWarnings("serial")
public class ContentUseStatView extends ContentUseStatDesign implements FilteringView {

    public static final String NAME = "InnholdBruksstatistikk";

    private final ClassificationFilter classificationFilter;
    private final StatisticsService statisticsService;
    private Table classificationStatiticalReport;
    private final Map<ReportModeChoice, ClassificationReportData<?>> reports;

    @Autowired
    public ContentUseStatView(StatisticsService statisticsService, UseStatisticsView useStatisticsView) {
        this.contentStatTab.addSelectedTabChangeListener(e -> getSelectedTab(e));
        this.classificationFilter = VaadinUtil.getKlassState().getClassificationFilter();
        this.statisticsService = statisticsService;
        this.classificationStatiticalReport = ReportUtil.createClassificationStatiticalReport(reportLayout);
        reports = new HashMap<>(4);
    }



    private void createClassificationReport(ClassificationReportData<?> classificationReport, String reportName) {
        Table report = null;
        classificationReport.reset();
        if (!initReport(classificationReport, reportName)) {
            return;
        }
        do {
            report = ReportUtil.initHeader(classificationReport, reportLayout);
            do {
                printCommonRows(classificationReport, report);
                classificationReport.nextPost();
            } while (!classificationReport.isLast() && !classificationReport.checkAndSetNextSection());
        } while (!classificationReport.isLast());
    }

    @SuppressWarnings("unchecked")
    private void createVersionReport(VersionReportData versionReport, String reportName) {
        Table report = null;
        versionReport.reset();
        if (!initReport(versionReport, reportName)) {
            return;
        }
        do {
            report = initVersionTableReport(versionReport);
            do {
                Item row = printCommonRows(versionReport, report);
                row.getItemProperty("Published_NO").setValue(versionReport.isPublishedNo());
                row.getItemProperty("Published_NN").setValue(versionReport.isPublishedNn());
                row.getItemProperty("Published_EN").setValue(versionReport.isPublishedEn());
                versionReport.nextPost();
            } while (!versionReport.isLast() && !versionReport.checkAndSetNextSection());
        } while (!versionReport.isLast());
    }

    private Table initVersionTableReport(VersionReportData versionReport) {
        Table report = ReportUtil.initHeader(versionReport, reportLayout);
        report.addContainerProperty("Published_NO", String.class, null, "<b>Bokmål</b>", null, Table.Align.CENTER);
        report.addContainerProperty("Published_NN", String.class, null, "<b>Nynorsk</b>", null, Table.Align.CENTER);
        report.addContainerProperty("Published_EN", String.class, null, "<b>Engelsk</b>", null, Table.Align.CENTER);
        report.setColumnWidth("Published_NO", 75);
        report.setColumnWidth("Published_NN", 75);
        report.setColumnWidth("Published_EN", 75);
        report.setCellStyleGenerator(new Table.CellStyleGenerator() {

            @Override
            public String getStyle(Table source, Object itemId, Object propertyId) {
                if (propertyId != null) {
                    Item item = source.getItem(itemId);
                    if (item.getItemProperty(propertyId).getValue().getClass() == String.class) {
                        String cellValue = (String) item.getItemProperty(propertyId).getValue();
                        if (cellValue.equals(VersionReportData.VALUE_PUBLISHED)) {
                            return "reportPublishedColor";
                        } else if (cellValue.equals(VersionReportData.VALUE_UNPUBLISHED)) {
                            return "reportNotPublishedColor";
                        }
                    }
                }
                return null;
            }
        });
        return report;
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



    private boolean initReport(ClassificationReportData<?> classificationReport, String reportName) {
        reportLayout.removeAllComponents();
        detailedReportHeading.removeAllComponents();
        makeReportHeader(reportName);
        if (classificationReport.isLast()) {
            noClassificationsFound();
            return false;
        }
        return true;
    }



    private void noClassificationsFound() {
        Label noDataFoundMessage = new Label("Ingen kodeverk funnet");
        reportLayout.addComponent(noDataFoundMessage);
    }

    private void makeReportHeader(String reportName) {
        Button backToFilterButton = new Button("<Tilbake til filter");
        backToFilterButton.addStyleName(ValoTheme.BUTTON_LINK);
        backToFilterButton.addClickListener(e -> resetStatisticView());
        detailedReportHeading.addComponent(backToFilterButton);
        String sectionChoice = classificationFilter.getCurrentSection() != null
                ? classificationFilter.getCurrentSection() : "alle seksjoner";
        String classificationChoice = classificationFilter.getCurrentClassificationType() != null
                ? classificationFilter.getCurrentClassificationType().getDisplayName(Language.NB)
                : "alle typer kodeverk";
        Label header = new Label(reportName + '-' + classificationChoice + '-' + sectionChoice);
        detailedReportHeading.addComponent(header);
    }

    private void resetStatisticView() {
        reportLayout.removeAllComponents();
        detailedReportHeading.removeAllComponents();
        reportLayout.addComponent(classificationStatiticalReport);
    }

    @Override
    public void enter(ViewChangeEvent event) {
        reports.clear();
        generateReport();
    }

    private ContentUseStatisticRows getReportData() {
        ContentUseStatisticDto dto = statisticsService.generateContentUseStat(classificationFilter
                .getCurrentSection(), classificationFilter
                .getCurrentClassificationType());

        return new ContentUseStatisticRows(dto.numberOfClassifications, 
                dto.publishedClassifications,
                dto.unpublishedClassifications, 
                dto.publishedVersionsWithMissingLanguages);

    }

    private void getSelectedTab(SelectedTabChangeEvent stce) {
        generateReport();
    }

    private void generateReport() {
        reportLayout.removeAllComponents();
        classificationStatiticalReport = ReportUtil.createClassificationStatiticalReport(reportLayout);
        reportLayout.addComponent(classificationStatiticalReport);
        Layout selectedTab = (Layout) contentStatTab.getSelectedTab();
        Tab tab = contentStatTab.getTab(selectedTab);
        if (tab.getCaption().equals("Kodeverk")) {
            showClassificationReport(classificationStatiticalReport, getReportData());
        } else if (tab.getCaption().equals("Bruk")) {
            useStatisticsView.setVisible(true);
            useDetailedUseView.setVisible(false);
            statisticalUnitView.setVisible(false);
            classificationUnitReportView.setVisible(false);
            useStatisticsView.init(statisticsService, this);
            useStatisticsView.showClassificationReport();
        } else if (tab.getCaption().equals("Statistiske enheter")) {
            statisticalUnitView.setVisible(true);
            useDetailedUseView.setVisible(false);
            useStatisticsView.setVisible(false);
            classificationUnitReportView.setVisible(false);
            statisticalUnitView.init(statisticsService, this);
            statisticalUnitView.showStatisticalUnitReport();
        }
    }

    private void showClassificationReport(Table report, ContentUseStatisticRows contentUseStat) {
        report.removeAllItems();
        report.addContainerProperty("Description", String.class, null, " ", null, null);
        report.addContainerProperty("Value", Integer.class, null, "<b>Antall</b>", null, null);
        report.addContainerProperty("LookAll", Button.class, null, " ", null, Table.Align.CENTER);
        report.addContainerProperty("ExportToExcel", Button.class, null, " ", null, Table.Align.CENTER);
        report.setColumnWidth("LookAll", 200);
        report.setColumnWidth("ExportToExcel", 200);
        for (Entry<ReportDescription<ReportModeChoice>, Integer> row : contentUseStat.getRows().entrySet()) {
            addReportRow(report, row.getKey(), row.getValue());
        }
    }

    @SuppressWarnings("unchecked")
    private void addReportRow(Table report, ReportDescription<ReportModeChoice> reportDescription, int value) {
        Button lookAllButton = lookClassificationButton(reportDescription.getChoice());
        Button exportToExcelButton = exportClassificationToExcelButton(reportDescription);
        lookAllButton.setWidth(150, Unit.PIXELS);
        exportToExcelButton.setWidth(150, Unit.PIXELS);
        Object id = report.addItem();
        Item row = report.getItem(id);
        row.getItemProperty("Description").setValue(reportDescription.getDisplayName());
        row.getItemProperty("Value").setValue(value);
        if (value == 0) {
            lookAllButton.setEnabled(false);
            exportToExcelButton.setEnabled(false);
        }
        row.getItemProperty("LookAll").setValue(lookAllButton);
        row.getItemProperty("ExportToExcel").setValue(exportToExcelButton);
    }

    private Button lookClassificationButton(ReportDescription<ReportModeChoice> reportDescription) {
        Button lookAllButton = new Button("Se alle");
        lookAllButton.setData(reportDescription);
        lookAllButton.addClickListener(e -> lookClassification(e));
        return lookAllButton;
    }

    private Button exportClassificationToExcelButton(ReportDescription<ReportModeChoice> reportDescription) {
        Button exportToExcelButton = new Button("Last ned til Excel");
        exportToExcelButton.setData(reportDescription);
        StreamResource streamResource = new StreamResource(() -> generateReport(reportDescription.getChoice()),
                reportDescription.getFilename());

        FileDownloader fileDownloader = new FileDownloader(streamResource);
        fileDownloader.extend(exportToExcelButton);

        return exportToExcelButton;
    }

    private InputStream generateReport(ReportModeChoice reportModeChoice) {
        String exportToExcelData = null;
        ClassificationReportData<?> classificationReport = reports.get(reportModeChoice);
        if (classificationReport == null) {

            List<?> report;
            classificationReport = getClassificationReportData(reportModeChoice);
            reports.put(reportModeChoice, classificationReport);
        }
        if (classificationReport instanceof ClassificationReportData) {
            exportToExcelData = classificationReport.exportToExcel(UseStatisticsView.CSV_SEPARATOR);
        } else {
            exportToExcelData = ((VersionReportData) classificationReport).exportToExcel(UseStatisticsView.CSV_SEPARATOR);
        }
        return AdminUtils.createInputStream(exportToExcelData);
    }

    private ClassificationReportData<?> getClassificationReportData(ReportModeChoice reportModeChoice) {
        ClassificationReportData<?> classificationReport;
        List<?> report;
        switch (reportModeChoice) {
            case TOTAL:
                report = statisticsService.getPublishedClassificationReport(
                        classificationFilter.getCurrentSection(), classificationFilter
                                .getCurrentClassificationType(), ReportMode.TOTAL);
                classificationReport = new ClassificationReportData(report);
                break;
            case PUBLISHED:
                report = statisticsService.getPublishedClassificationReport(
                        classificationFilter.getCurrentSection(), classificationFilter
                                .getCurrentClassificationType(), ReportMode.PUBLISHED);
                classificationReport = new ClassificationReportData(report);
                break;
            case UNPUBLISHED:
                List<?> total = statisticsService.getPublishedClassificationReport(
                        classificationFilter.getCurrentSection(), classificationFilter
                                .getCurrentClassificationType(), ReportMode.TOTAL);
                List<?> published = statisticsService.getPublishedClassificationReport(
                        classificationFilter.getCurrentSection(), classificationFilter
                                .getCurrentClassificationType(), ReportMode.PUBLISHED);
                ClassificationReportData<ClassificationReportDto> publishedReport = new ClassificationReportData(published);
                classificationReport = new ClassificationReportData(total);
                classificationReport.diff(publishedReport);
                break;
            case MISSING_LANG:
                List<ClassificationVersionReportDto> publishedAnyLanguageList = 
                        statisticsService.getPublishedVersionReport(
                        classificationFilter.getCurrentSection(), classificationFilter
                                .getCurrentClassificationType(), ReportMode.ANY_LANGUAGE);
                
                List<ClassificationVersionReportDto> publishedAllLanguageList = 
                        statisticsService.getPublishedVersionReport(
                        classificationFilter.getCurrentSection(), classificationFilter
                                .getCurrentClassificationType(), ReportMode.ALL_LANGUAGE);

                classificationReport = new VersionReportData(publishedAnyLanguageList);
                
                ClassificationReportData<ClassificationVersionReportDto> publishedAllLanguage = 
                        new VersionReportData(publishedAllLanguageList);
                classificationReport.diff(publishedAllLanguage);
                break;
                default:
                    throw new RuntimeException("Unknown report");
                
        }

        classificationReport.sort();
        return classificationReport;
    }

    @SuppressWarnings("unchecked")
    private void lookClassification(ClickEvent e) {
        ReportDescription<ReportModeChoice> reportDescription = (ReportDescription<ReportModeChoice>) e.getButton()
                .getData();
        ClassificationReportData<?> classificationReport = reports.get(reportDescription.getChoice());
        if (classificationReport == null) {
            
            classificationReport = getClassificationReportData(reportDescription.getChoice());
            reports.put(reportDescription.getChoice(), classificationReport);
        }
        if (reportDescription.getChoice().equals(ReportModeChoice.MISSING_LANG)) {
            createVersionReport((VersionReportData) classificationReport, reportDescription.getDisplayName());
        } else {
            createClassificationReport(classificationReport, reportDescription.getDisplayName());
        }
    }
}
