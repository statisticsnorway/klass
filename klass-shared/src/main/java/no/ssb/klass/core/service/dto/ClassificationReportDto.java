package no.ssb.klass.core.service.dto;

import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.ClassificationType;
import no.ssb.klass.core.model.User;

public class ClassificationReportDto {
    private final ClassificationSeries classification;
    private final User user;
    public ClassificationReportDto(ClassificationSeries classification, User user) {
        this.classification = classification;
        this.user = user;
    }
    
    public String getSection() {
        return user.getSection();
    }
    
    public String getContactPerson() {
        return user.getFullname();
    }
    
    public Long getId() {
        return classification.getId();
    }
    
    public String getName() {
        return classification.getNameInPrimaryLanguage();
    }
    
    public ClassificationType getClassificationType() {
        return classification.getClassificationType();
    }
}
