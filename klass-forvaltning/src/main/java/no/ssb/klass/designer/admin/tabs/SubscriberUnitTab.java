package no.ssb.klass.designer.admin.tabs;

import java.io.InputStream;
import java.util.Map;

import no.ssb.klass.core.service.dto.SubscriberStatisticsDto;
import no.ssb.klass.designer.admin.util.SubscriberModeChoice;
import no.ssb.klass.designer.admin.util.SubscriberReportDescription;
import no.ssb.klass.designer.util.ChoiceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.vaadin.data.Item;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Table;

import no.ssb.klass.core.service.StatisticsService;
import no.ssb.klass.core.service.dto.StatisticalEntity;
import no.ssb.klass.core.util.AdminUtils;
import no.ssb.klass.designer.admin.util.SubscriberStatisticsRows;
import no.ssb.klass.designer.util.KlassTheme;

/**
 * Created by jro on 23.02.2017.
 */
public class SubscriberUnitTab extends SubscriberUnitTabDesign {

    private static final int LINES_PER_PAGE = 100;
    public static final char CSV_SEPARATOR = ';';
    @Autowired
    private StatisticsService statisticsService;
    private SubscriberModeChoice choice;
    private String type;

    public void init() {
        subscriberUnitTable.setVisible(true);
        makeDetailedElementsVisible(false);
        createSubscriberStatistics();
        showSubscriberUnitReport();

    }

    private void createSubscriberStatistics() {
        subscriberUnitTable.setWidth(100, Unit.PERCENTAGE);
        subscriberUnitTable.setHeight(-1, Unit.PIXELS);
        subscriberUnitTable.setPageLength(0);
        subscriberUnitTable.addContainerProperty("Description", String.class, null, " ", null, null);
        subscriberUnitTable.addContainerProperty("Value", Integer.class, null, "<b>Antall</b>", null, null);
        subscriberUnitTable.addContainerProperty("LookAll", Button.class, null, "Brukere", null, Table.Align.CENTER);
        subscriberUnitTable.addContainerProperty("LookUsers", Button.class, null, "Domener ", null, Table.Align.CENTER);
        subscriberUnitTable.addContainerProperty("LookClass", Button.class, null, "Klassifikasjoner ", null,
                Table.Align.CENTER);
        subscriberUnitTable.addContainerProperty("ExportToExcel", Button.class, null, " ", null, Table.Align.CENTER);
        subscriberUnitTable.setColumnWidth("LookAll", 150);
        subscriberUnitTable.setColumnWidth("LookUsers", 150);
        subscriberUnitTable.setColumnWidth("LookClass", 150);
        subscriberUnitTable.setColumnWidth("ExportToExcel", 150);
    }

    private void createDetailedSubscriberStatistics() {
        backButton.addClickListener(e -> backToMain());
        detailedSubscriberTable.setWidth(100, Unit.PERCENTAGE);
        detailedSubscriberTable.setHeight(-1, Unit.PIXELS);
        detailedSubscriberTable.setPageLength(0);
        detailedSubscriberTable.addContainerProperty("Name", String.class, null, "", null, Table.Align.LEFT);
        detailedSubscriberTable.addContainerProperty("Value", Long.class, null, "", null,
                Table.Align.RIGHT);
        detailedSubscriberTable.setColumnWidth("Value", 200);
    }

    private void showSubscriberUnitReport(SubscriberStatisticsRows reportData) {
        subscriberUnitTable.removeAllItems();
        for (Map.Entry<SubscriberReportDescription<SubscriberModeChoice>, Integer> row : reportData.getRows()
                .entrySet()) {
            addReportRow(row.getKey(), row.getValue());
        }
    }

    public void showSubscriberUnitReport() {
        showSubscriberUnitReport(getReportData());
    }

    private SubscriberStatisticsRows getReportData() {
        SubscriberStatisticsDto statistics = statisticsService.getSubscriberStatistics();
        return  new SubscriberStatisticsRows(statistics.numberOfSubscribers, 
                statistics.numberOfInternalSubscribers, 
                statistics.numberOfExternalSubscribers);
    }

    @SuppressWarnings("unchecked")
    private void addReportRow(SubscriberReportDescription<SubscriberModeChoice> subscriberReportDescription,
            int value) {
        Button lookAllButton = lookAllButton(subscriberReportDescription);
        Button lookAllUsers = lookAllUsers(subscriberReportDescription);
        Button lookAllClass = lookAllClass(subscriberReportDescription);
        Button exportToExcelButton = exportToExcelButton(subscriberReportDescription);
        lookAllButton.setWidth(140, Unit.PIXELS);
        lookAllUsers.setWidth(140, Unit.PIXELS);
        lookAllClass.setWidth(140, Unit.PIXELS);
        exportToExcelButton.setWidth(140, Unit.PIXELS);
        Object id = subscriberUnitTable.addItem();
        Item row = subscriberUnitTable.getItem(id);
        row.getItemProperty("Description").setValue(subscriberReportDescription.getDisplayName());
        row.getItemProperty("Value").setValue(value);
        if (value == 0) {
            lookAllButton.setEnabled(false);
            lookAllUsers.setEnabled(false);
            lookAllClass.setEnabled(false);
            exportToExcelButton.setEnabled(false);
        }
        row.getItemProperty("LookAll").setValue(lookAllButton);
        row.getItemProperty("LookUsers").setValue(lookAllUsers);
        row.getItemProperty("LookClass").setValue(lookAllClass);
        row.getItemProperty("ExportToExcel").setValue(exportToExcelButton);
    }

    private Button lookAllButton(SubscriberReportDescription<SubscriberModeChoice> subscriberReportDescription) {
        Button lookAllButton = new Button("Se alle");
        lookAllButton.setData(subscriberReportDescription);
        lookAllButton.addClickListener(this::lookAll);
        return lookAllButton;
    }

    private Button lookAllUsers(SubscriberReportDescription<SubscriberModeChoice> subscriberReportDescription) {
        Button lookAllUsers = new Button("Se alle");
        lookAllUsers.setData(subscriberReportDescription);
        lookAllUsers.addClickListener(this::lookUsers);
        return lookAllUsers;
    }

    private Button lookAllClass(SubscriberReportDescription<SubscriberModeChoice> subscriberReportDescription) {
        Button lookAllClass = new Button("Se alle");
        lookAllClass.setData(subscriberReportDescription);
        lookAllClass.addClickListener(this::lookClass);
        return lookAllClass;
    }

    private Button exportToExcelButton(SubscriberReportDescription<SubscriberModeChoice> subscriberReportDescription) {
        Button exportToExcelButton = new Button("Last ned til Excel");
        exportToExcelButton.setData(subscriberReportDescription);
        StreamResource streamResource = new StreamResource(() -> generateReport(subscriberReportDescription
                .getChoice()), subscriberReportDescription.getFilename());
        FileDownloader fileDownloader = new FileDownloader(streamResource);
        fileDownloader.extend(exportToExcelButton);
        return exportToExcelButton;
    }

    private InputStream generateReport(SubscriberModeChoice subscriberModeChoice) {
        StringBuilder excelData = new StringBuilder();
        SubscriberModeChoice choice = subscriberModeChoice.getChoice();

        // subscribers
        Page<StatisticalEntity> subscriberStatisticsData =
                statisticsService.getSubscriberReport(ChoiceMapper.map(choice), new PageRequest(0, Integer.MAX_VALUE));
        excelData.append("Brukernavn").append(CSV_SEPARATOR).append("Antall abonnementer").append('\n');
        excelData.append(formatForExcel(subscriberStatisticsData));

        // domains
        subscriberStatisticsData =
                statisticsService.getSubscriberDomainReport(ChoiceMapper.map(choice), new PageRequest(0, Integer.MAX_VALUE));
        excelData.append('\n');
        excelData.append("Domene").append(CSV_SEPARATOR).append("Antall abonnementer").append('\n');
        excelData.append(formatForExcel(subscriberStatisticsData));

        // classifications
        subscriberStatisticsData =
                statisticsService.getSubscriberClassificationReport(ChoiceMapper.map(choice), new PageRequest(0, Integer.MAX_VALUE));
        excelData.append('\n');
        excelData.append("Klassifikasjon").append(CSV_SEPARATOR).append("Antall abonnementer").append('\n');
        excelData.append(formatForExcel(subscriberStatisticsData));

        return AdminUtils.createInputStream(excelData.toString());
    }

    public String formatForExcel(Page<StatisticalEntity> reportData) {
        StringBuilder resultString = new StringBuilder();
        reportData.getContent().forEach(entry -> resultString.append(entry.getName())
                .append(CSV_SEPARATOR)
                .append(entry.getCount())
                .append(CSV_SEPARATOR)
                .append('\n'));
        return resultString.toString();
    }

    @SuppressWarnings("unchecked")
    private void lookAll(Button.ClickEvent e) {
        type = "All";
        SubscriberReportDescription<SubscriberModeChoice> reportDescription =
                (SubscriberReportDescription<SubscriberModeChoice>) e.getButton().getData();
        createSubscriberDetailedReport(reportDescription, type);

    }

    @SuppressWarnings("unchecked")
    private void lookUsers(Button.ClickEvent e) {
        type = "User";
        SubscriberReportDescription<SubscriberModeChoice> reportDescription =
                (SubscriberReportDescription<SubscriberModeChoice>) e.getButton().getData();
        createSubscriberDetailedReport(reportDescription, type);
    }

    @SuppressWarnings("unchecked")
    private void lookClass(Button.ClickEvent e) {
        type = "Class";
        SubscriberReportDescription<SubscriberModeChoice> reportDescription =
                (SubscriberReportDescription<SubscriberModeChoice>) e.getButton().getData();
        createSubscriberDetailedReport(reportDescription, type);
    }

    private void backToMain() {
        makeDetailedElementsVisible(false);
        subscriberUnitTable.setVisible(true);
    }

    private void createSubscriberDetailedReport(SubscriberReportDescription<SubscriberModeChoice> reportDescription,
            String type) {
        subscriberUnitTable.setVisible(false);
        makeDetailedElementsVisible(true);
        createDetailedSubscriberStatistics();
        this.choice = reportDescription.getChoice();
        detailedSubscriberTable.setColumnHeader("Value", "<b>" + "Antall abonnementer" + "</b>");
        pagingButtons.removeAllComponents();
        headingText.setValue(getHeadingText(reportDescription));
        PageRequest pageRequest = new PageRequest(0, LINES_PER_PAGE);
        Page<StatisticalEntity> reportData = showReportPage(reportDescription, type, pageRequest);
        numberOfRowsHeader.setValue("Totalt antall: " + reportData.getTotalElements());
        if (reportData.getTotalPages() > 1) {
            for (int noNextButtons = 0; noNextButtons < reportData.getTotalPages(); noNextButtons++) {
                Button nextButton = new Button(String.valueOf(noNextButtons + 1));
                pagingButtons.addComponent(nextButton);
                nextButton.setData(noNextButtons);
                boolean selected = noNextButtons == reportData.getNumber();
                nextButton.setPrimaryStyleName(selected ? KlassTheme.PAGE_NUMBER_HIGHLIGHTED
                        : KlassTheme.BUTTON_AS_LINK);
                nextButton.setStyleName(KlassTheme.PAGE_NUMBER);
                nextButton.addClickListener(e -> nextButton(e));
            }
        }
    }

    private void nextButton(Button.ClickEvent e) {
        for (int i = 0; i < pagingButtons.getComponentCount(); i++) {
            Button button = (Button) pagingButtons.getComponent(i);
            if (e.getButton().equals(button)) {
                button.setPrimaryStyleName(KlassTheme.PAGE_NUMBER_HIGHLIGHTED);
            } else {
                button.setPrimaryStyleName(KlassTheme.BUTTON_AS_LINK);
            }
        }
        int pageNumber = (int) e.getButton().getData();
        showReportPage(choice, type, new PageRequest(pageNumber, LINES_PER_PAGE));
    }

    private Page<StatisticalEntity> showReportPage(SubscriberReportDescription<SubscriberModeChoice> reportDescription,
            String type, PageRequest pageRequest) {
        detailedSubscriberTable.removeAllItems();
        Page<StatisticalEntity> subscriberStatisticsData;
        this.type = type;
        switch (type) {
        case "All":
            subscriberStatisticsData =
                    statisticsService.getSubscriberReport(ChoiceMapper.map(reportDescription.getChoice()), pageRequest);
            detailedSubscriberTable.setColumnHeader("Name", "<b>" + "Brukere" + "</b>");
            subscriberStatisticsData.getContent().forEach(this::printRow);
            return subscriberStatisticsData;
        case "User":
            subscriberStatisticsData =
                    statisticsService.getSubscriberDomainReport(ChoiceMapper.map(reportDescription.getChoice()), pageRequest);
            detailedSubscriberTable.setColumnHeader("Name", "<b>" + "Domener" + "</b>");
            subscriberStatisticsData.getContent().forEach(this::printRow);
            return subscriberStatisticsData;
        case "Class":
            subscriberStatisticsData =
                    statisticsService.getSubscriberClassificationReport(ChoiceMapper.map(reportDescription.getChoice()), pageRequest);
            detailedSubscriberTable.setColumnHeader("Name", "<b>" + "Klassifikasjoner" + "</b>");
            subscriberStatisticsData.getContent().forEach(this::printRow);
            return subscriberStatisticsData;
        default:
            throw new RuntimeException("Unknown reportPage type :" + type);
        }

    }

    @SuppressWarnings("unchecked")
    private void printRow(StatisticalEntity entity) {
        Object id = detailedSubscriberTable.addItem();
        Item row = detailedSubscriberTable.getItem(id);
        row.getItemProperty("Name").setValue(entity.getName());
        row.getItemProperty("Value").setValue(entity.getCount());
    }

    private String getHeadingText(SubscriberReportDescription<SubscriberModeChoice> reportMode) {
        return reportMode.getDisplayName();
    }

    private void makeDetailedElementsVisible(boolean visible) {
        detailedSubscriberTable.setVisible(visible);
        detailedSubscriberLayout.setVisible(visible);
        backButton.setVisible(visible);
        headingText.setVisible(visible);
        numberOfRowsHeader.setVisible(visible);
        pagingButtons.setVisible(visible);
    }
}