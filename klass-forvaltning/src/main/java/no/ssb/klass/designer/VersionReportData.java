package no.ssb.klass.designer;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import no.ssb.klass.core.service.dto.ClassificationVersionReportDto;

public class VersionReportData extends ClassificationReportData<ClassificationVersionReportDto> {
    
    public static final String VALUE_PUBLISHED = "Ja";
    public static final String VALUE_UNPUBLISHED = "Nei";
    
    public VersionReportData(List<ClassificationVersionReportDto> reportData) {
       super(reportData);
    }

    public String isPublishedNo() {
        return line.isPublishedInNo() ? VALUE_PUBLISHED : VALUE_UNPUBLISHED;
    }
    
    public String isPublishedNn() {
        return line.isPublishedInNn() ? VALUE_PUBLISHED : VALUE_UNPUBLISHED;
    }
    
    public String isPublishedEn() {
        return line.isPublishedInEn() ? VALUE_PUBLISHED : VALUE_UNPUBLISHED;
    }

    
    @Override
    public String getName() {
        return line.getVersionName();
    }
    
    @Override
    public String toString() {
        char separator = '\t';
        return "Number of records: " + reportData.size() + "\n" + toString(separator, true);
    }
    
    /**
     * Can be used to Excel export
     * @param separator CSV separator, typical default value is ';'
     * @return
     */
    @Override
    public String exportToExcel(char separator) {
        return toString(separator, false);
    }
    
    /**
     * Sort this report object after increasing section and name
     */
    @Override
    public void sort() {
        if (reportData.size() > 0) {
            reset();
            Collections.sort(reportData, new Comparator<ClassificationVersionReportDto>() {
    
                @Override
                public int compare(ClassificationVersionReportDto a, ClassificationVersionReportDto b) {
                    String sectionA = a.getSection();
                    String sectionB = b.getSection();
                    if (sectionA.equals(sectionB)) {
                        String nameA = a.getVersionName();
                        String nameB = b.getVersionName();
                        return nameA.compareTo(nameB);
                    }
                    return sectionA.compareTo(sectionB);
                }
                
            });
        }
    }
    
    private String toString(char separator, boolean debug) {        
        reset();
        StringBuilder resultString = new StringBuilder();
        for (int i = 0; i < reportData.size(); i++) {
            if (debug) {
                resultString.append(line.getId()).append(separator);
            }
            checkAndSetNextSection();
            resultString.append(getSection()).append(separator).append(getName()).append(separator).append(getType())
                    .append(separator).append(getContactPerson()).append(separator)
                    .append(isPublishedNo()).append(separator).append(isPublishedNn()).append(separator).append(isPublishedNo())
                    .append(separator).append('\n');
            nextPost();
        }        
        reset();
        return resultString.toString();
    }
}
