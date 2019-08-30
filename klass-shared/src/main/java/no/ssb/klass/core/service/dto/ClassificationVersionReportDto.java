package no.ssb.klass.core.service.dto;

import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.model.User;

public class ClassificationVersionReportDto extends ClassificationReportDto {
    private final ClassificationVersion version;
    
    public ClassificationVersionReportDto(ClassificationVersion version, ClassificationSeries classification, User user) {
        super(classification, user);
        this.version = version;
    }
    
    public boolean isPublishedInNo() {
        return version.isPublished(Language.NB);
    }
    
    public boolean isPublishedInNn() {
        return version.isPublished(Language.NN);
    }
    
    public boolean isPublishedInEn() {
        return version.isPublished(Language.EN);
    }
    
    public String getVersionName() {
        return version.getNameInPrimaryLanguage();
    }
}

