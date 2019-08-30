package no.ssb.klass.designer;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import no.ssb.klass.core.model.ClassificationType;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.service.dto.ClassificationReportDto;

public class ClassificationReportData<T extends ClassificationReportDto> {

    protected List<T> reportData;
    protected int index;
    protected String currentSection;
    protected T line;
    
    public ClassificationReportData(List<T> reportData) {
        this.reportData = reportData;
        reset();
    }
    
    
    /**
     * Reset the internal state of this object, must be done for every new report
     */
    public void reset() {
        index = 0;        
        if (reportData.size() > 0) {
            line = reportData.get(index);
            currentSection = line.getSection();
        }
    }

    /**
     * Since this report module is grouped by section, this check must be done after each line, to 
     * guarantee that a new group is reached.
     * @return true if the new line is a new group.
     */
    public boolean checkAndSetNextSection() {
        if (currentSection == null) {
            return true;
        } else {
            String section = line.getSection();
            if (section.equals(currentSection)) {
                return false;
            } else {
                currentSection = section;
                return true;
            }
        }
    }
    
    /**
     * will return true if the last line is reached, or if there is no data in the report
     * @return true if the end of this report object is reached, or this report is empty
     */
    public boolean isLast() {
        int size = reportData.size();
        return size == 0 || index >= size;
    }
    
    public String getSection() {
        return currentSection;
    }
    
    public String getName() {
        return line.getName();
    }
    
    public String getType() {
        return getClassificationType().getDisplayName(Language.NB);
    }
    
    public String getContactPerson() {
        String contactPerson = line.getContactPerson();
        return contactPerson;
    }
    
    /**
     * Get the next line of this report object
     */
    public void nextPost() {
        index++;
        if (index < reportData.size()) {
            line = reportData.get(index);
        }
    }

    public int genNumberOfRows() {
        return reportData.size();
    }
    
    /**
     * Remove all Classifications from this collections (all classifications) 
     * who also exits in the other collection (published classifications).
     * @param publishedAllLanguage
     */
    public void diff(ClassificationReportData<? extends ClassificationReportDto> publishedAllLanguage) {
        reportData = reportData.parallelStream()
                .filter(t -> !containsReportData(publishedAllLanguage.reportData, t))
                .collect(Collectors.toList());
        reset();
    }

    private boolean containsReportData(List<? extends ClassificationReportDto> list, T t) {
        return list.stream().anyMatch(dto -> dto.getId().equals(t.getId()));
    }

    /**
     * Sort this report object after increasing section and name
     */
    public void sort() {
        if (reportData.size() > 0) {
            reset();
            Collections.sort(reportData, new Comparator<ClassificationReportDto>() {
    
                @Override
                public int compare(ClassificationReportDto a, ClassificationReportDto b) {
                    String sectionA = a.getSection();
                    String sectionB = b.getSection();
                    if (sectionA.equals(sectionB)) {
                        String nameA = a.getName();
                        String nameB = b.getName();
                        return nameA.compareTo(nameB);
                    }
                    return sectionA.compareTo(sectionB);
                }
                
            });
        }
    }
    
    /**
     * Default toString() method, used in debug etc.
     */
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
    public String exportToExcel(char separator) {
        return toString(separator, false);
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
                    .append(separator).append(getContactPerson()).append(separator).append('\n');
            nextPost();
        }        
        reset();
        return resultString.toString();
    }
    
    
    protected ClassificationType getClassificationType() {
        return line.getClassificationType();
    }
}
