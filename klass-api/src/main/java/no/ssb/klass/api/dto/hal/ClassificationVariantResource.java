package no.ssb.klass.api.dto.hal;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import no.ssb.klass.core.model.ClassificationVariant;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.api.util.CustomLocalDateSerializer;

@JacksonXmlRootElement(localName = "classificationVariant")
public class ClassificationVariantResource extends ClassificationVariantSummaryResource {
    private final LocalDate validFrom;
    private final LocalDate validTo;
    private final String introduction;
    private final ContactPersonResource contactPerson;
    private final String owningSection;

    private final List<CorrespondenceTableSummaryResource> correspondenceTables;
    private final List<ChangelogResource> changelogs;
    private final List<LevelResource> levels;
    private final List<ClassificationItemResource> classificationItems;

    public ClassificationVariantResource(ClassificationVariant variant, Language language) {
        super(variant, language);
        this.validFrom = variant.getDateRange().getFrom();
        LocalDate to = variant.getDateRange().getTo();
        this.validTo = (to.isEqual(LocalDate.MAX)) ? null : to;
        this.introduction = variant.getIntroduction(language);
        this.contactPerson = new ContactPersonResource(variant.getContactPerson());
        this.owningSection = variant.getContactPerson().getSection();

        this.levels = LevelResource.convert(variant.getLevels(), language);
        this.correspondenceTables = CorrespondenceTableSummaryResource.convert(variant.getCorrespondenceTables(),
                language);
        this.classificationItems = ClassificationItemResource.convert(variant, language);
        this.changelogs = ChangelogResource.convert(variant.getChangelogs());
    }

    @JsonSerialize(using = CustomLocalDateSerializer.class)
    public LocalDate getValidFrom() {
        return validFrom;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    public LocalDate getValidTo() {
        return validTo;
    }

    public String getIntroduction() {
        return introduction;
    }

    public ContactPersonResource getContactPerson() {
        return contactPerson;
    }

    public String getOwningSection() {
        return owningSection;
    }

    @JacksonXmlElementWrapper(localName = "correspondenceTables")
    @JacksonXmlProperty(localName = "correspondenceTable")
    public List<CorrespondenceTableSummaryResource> getCorrespondenceTables() {
        return correspondenceTables;
    }

    @JacksonXmlElementWrapper(localName = "changelogs")
    @JacksonXmlProperty(localName = "changelog")
    public List<ChangelogResource> getChangelogs() {
        return changelogs;
    }

    @JacksonXmlElementWrapper(localName = "levels")
    @JacksonXmlProperty(localName = "level")
    public List<LevelResource> getLevels() {
        return levels;
    }

    @JacksonXmlElementWrapper(localName = "classificationItems")
    @JacksonXmlProperty(localName = "classificationItem")
    public List<ClassificationItemResource> getClassificationItems() {
        return classificationItems;
    }
}
