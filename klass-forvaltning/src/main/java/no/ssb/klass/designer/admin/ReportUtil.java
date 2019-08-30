package no.ssb.klass.designer.admin;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

import no.ssb.klass.designer.ClassificationReportData;

public abstract class ReportUtil {
    private ReportUtil() {        
    }
    
    static Table initHeader(ClassificationReportData<?> classificationReport, VerticalLayout reportLayout) {
        Table report;
        addCaption(classificationReport, reportLayout);
        report = createClassificationStatiticalReport(reportLayout);
        report.addContainerProperty("Classification", String.class, null, "<b>Kodeverk</b>", null, Table.Align.LEFT);
        report.addContainerProperty("Type", String.class, null, "<b>Type</b>", null, Table.Align.LEFT);
        report.addContainerProperty("ContactPerson", String.class, null, "<b>Kontaktperson</b>", null,
                Table.Align.LEFT);
        report.setColumnWidth("Type", 125);
        report.setColumnWidth("ContactPerson", 300);
        return report;
    }
    
    static void addCaption(ClassificationReportData<?> classificationReport, VerticalLayout reportLayout) {
        Label section = new Label(classificationReport.getSection());
        VerticalLayout captionLayout = new VerticalLayout();
        captionLayout.setWidth(100, Unit.PERCENTAGE);
        captionLayout.setHeight(-1, Unit.PIXELS);
        captionLayout.setMargin(new MarginInfo(true, false, false, false));
        captionLayout.addComponent(section);
        reportLayout.addComponent(captionLayout);
    }
    
    static Table createClassificationStatiticalReport(VerticalLayout reportLayout) {
        Table report = new Table();
        report.setWidth(100, Unit.PERCENTAGE);
        report.setHeight(-1, Unit.PIXELS);
        report.setPageLength(0);
        reportLayout.addComponent(report);
        return report;
    }
}
