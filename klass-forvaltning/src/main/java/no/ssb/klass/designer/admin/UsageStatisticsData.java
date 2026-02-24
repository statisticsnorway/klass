package no.ssb.klass.designer.admin;

import org.springframework.data.domain.Page;

import no.ssb.klass.core.service.dto.StatisticalEntity;

public class UsageStatisticsData {
    private final Page<StatisticalEntity> reportData;
    private StatisticalEntity line;
    private int index;
    private long size;
    
    public UsageStatisticsData(Page<StatisticalEntity> reportData) {
        this.size = reportData.getTotalElements();
        this.reportData = reportData;
    }
    
    public void reset() {
        index = 0;
        if (reportData.getContent().size() > 0) {
            line = reportData.getContent().get(index);
        }
    }
    
    public boolean isLast() {
        int size = reportData.getContent().size();
        return size == 0 || index >= size;
    }
    
    public void nextPost() {
        index++;
        if (index < reportData.getContent().size()) {
            line = reportData.getContent().get(index);
        }
    }
    
    public String getName() {
        return line.getName();
    }
    
    public Long getCount() {
        return line.getCount();
    }
    
    public long getNumberOfLines() {
        return size;
    }
    
    public Page<StatisticalEntity> getReportData() {
        return reportData;
    }
    
    public String exportToExcel(char separator) {
        reset();
        StringBuilder resultString = new StringBuilder();
        for (int i = 0; i < reportData.getContent().size(); i++) {
            resultString.append(getName()).append(separator).append(getCount()).append(separator).append('\n');
            nextPost();
        }        
        reset();
        return resultString.toString();
    }
    
    public StatisticalEntity getLine() {
        return line;
    }
}


