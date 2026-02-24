package no.ssb.klass.designer.admin;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import no.ssb.klass.designer.admin.util.ReportDescription;
import no.ssb.klass.designer.admin.util.UseStatisticsModeChoice;
import no.ssb.klass.designer.util.ChoiceMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.vaadin.data.Item;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Table;

import no.ssb.klass.core.service.StatisticsService;
import no.ssb.klass.core.service.dto.StatisticalEntity;
import no.ssb.klass.designer.util.KlassTheme;

@SuppressWarnings("serial")
public class DetailedUseReportView extends DetailedUseReportDesign {
    private static final String DATE_INPUT_MASK = "dd.MM.yyyy";
    private static final int LINES_PER_PAGE = 100;

    private UseStatisticsView useStatisticsView;
    private LocalDate fromSearchDate;
    private LocalDate toSearchDate;
    private ReportDescription<UseStatisticsModeChoice> reportMode;
    private StatisticsService statisticsService;
    
    public DetailedUseReportView() {
        backButton.addClickListener(e -> backToMainUsageView());
        detailedUseReport.setWidth(100, Unit.PERCENTAGE);
        detailedUseReport.setHeight(-1, Unit.PIXELS);
        detailedUseReport.setPageLength(0);
        detailedUseReport.addContainerProperty("Name", String.class, null, "<b>Kodeverk</b>", null, Table.Align.LEFT);
        detailedUseReport.addContainerProperty("Value", Long.class, null, "", null,
                Table.Align.RIGHT);
        detailedUseReport.setColumnWidth("Value", 200);
    }
    
    private void backToMainUsageView() {
        this.setVisible(false);
        useStatisticsView.setVisible(true);
    }

    @SuppressWarnings("unchecked")
    private void printRow(UsageStatisticsData usageStatisticsData) {
        Object id = detailedUseReport.addItem();
        Item row = detailedUseReport.getItem(id);
        row.getItemProperty("Name").setValue(usageStatisticsData.getName());
        row.getItemProperty("Value").setValue(usageStatisticsData.getCount());
    }

    public void makeDetailedReport(StatisticsService statisticsService,
            ReportDescription<UseStatisticsModeChoice> reportMode, LocalDate fromSearchDate, LocalDate toSearchDate,
            UseStatisticsView useStatisticsView) {
        this.useStatisticsView = useStatisticsView;
        this.statisticsService = statisticsService;
        this.reportMode = reportMode;
        this.fromSearchDate = fromSearchDate;
        this.toSearchDate = toSearchDate;
        String header = reportMode.getChoice() == UseStatisticsModeChoice.TOTAL_CLASSIFIC ? "Antall uthentinger" : "Antall ganger brukt";
        detailedUseReport.setColumnHeader("Value", "<b>" + header + "</b>");
        pagingButtons.removeAllComponents();
        headingText.setValue(getHeadingText(reportMode, fromSearchDate, toSearchDate));
        PageRequest pageRequest = new PageRequest(0, LINES_PER_PAGE);
        Page<StatisticalEntity> reportData = showReportPage(pageRequest);
        numberOfRowsHeader.setValue("Totalt antall: " + reportData.getTotalElements());
        if (reportData.getTotalPages() > 1) {
            for (int noNextButtons = 0; noNextButtons < reportData.getTotalPages(); noNextButtons++) {
                Button nextButton = new Button(String.valueOf(noNextButtons + 1));
                pagingButtons.addComponent(nextButton);
                nextButton.setData(noNextButtons);                
                boolean selected = noNextButtons == reportData.getNumber();
                nextButton.setPrimaryStyleName(selected ? KlassTheme.PAGE_NUMBER_HIGHLIGHTED : KlassTheme.BUTTON_AS_LINK);
                nextButton.setStyleName(KlassTheme.PAGE_NUMBER);
                nextButton.addClickListener(e->nextButton(e));
            }
        }
    }

    private void nextButton(ClickEvent e) {
        for (int i = 0; i < pagingButtons.getComponentCount(); i++) {
            Button button = (Button)pagingButtons.getComponent(i);
            if (e.getButton().equals(button)) {
                button.setPrimaryStyleName(KlassTheme.PAGE_NUMBER_HIGHLIGHTED);
            } else {
                button.setPrimaryStyleName(KlassTheme.BUTTON_AS_LINK);
            }
        }
        int pageNumber = (int)e.getButton().getData();
        showReportPage(new PageRequest(pageNumber, LINES_PER_PAGE));
    }

    private Page<StatisticalEntity> showReportPage(PageRequest pageRequest) {

        detailedUseReport.removeAllItems();
        UsageStatisticsData usageStatisticsData =  
                new UsageStatisticsData(statisticsService.getUsageStatistics(fromSearchDate, toSearchDate,
                        ChoiceMapper.map(reportMode.getChoice()), pageRequest));
        usageStatisticsData.reset();
        do {
            printRow(usageStatisticsData);
            usageStatisticsData.nextPost();
        } while (!usageStatisticsData.isLast());  
        return usageStatisticsData.getReportData();
    }
    
    private String getHeadingText(ReportDescription<UseStatisticsModeChoice> reportMode, LocalDate fromSearchDate,
                                  LocalDate toSearchDate) {
        return reportMode.getDisplayName() + ":  " + fromSearchDate.format(DateTimeFormatter.ofPattern(
                DATE_INPUT_MASK)) + " - " + toSearchDate.format(DateTimeFormatter.ofPattern(DATE_INPUT_MASK));
    }
}
