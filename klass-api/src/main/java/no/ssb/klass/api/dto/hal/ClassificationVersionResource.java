package no.ssb.klass.api.dto.hal;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.model.CorrespondenceTable;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.util.AlphaNumericalComparator;

@JacksonXmlRootElement(localName = "classificationVersion")
@JsonPropertyOrder({"name", "id", "validFrom", "validTo", "lastModified", "published", "introduction", "contactPerson",
        "owningSection", "legalBase", "publications", "derivedFrom", "correspondenceTables", "classificationVariants",
        "changelogs", "levels", "classificationItems", "links"})

public class ClassificationVersionResource extends ClassificationVersionSummaryResource {
    private final String introduction;
    private final ContactPersonResource contactPerson;
    private final String owningSection;
    private final String legalBase;
    private final String publications;
    private final String derivedFrom;

    private final List<CorrespondenceTableSummaryResource> correspondenceTables;
    private final List<ClassificationVariantSummaryResource> classificationVariants;
    private final List<ChangelogResource> changelogs;
    private final List<LevelResource> levels;
    private final List<ClassificationItemResource> classificationItems;

    public ClassificationVersionResource(ClassificationVersion version, Language language,
            List<CorrespondenceTable> corrTableVersionIsTarget, boolean includeFuture) {
        super(version, language, includeFuture);
        this.introduction = version.getIntroduction(language);
        this.contactPerson = new ContactPersonResource(version.getContactPerson());
        this.owningSection = version.getContactPerson().getSection();
        this.legalBase = version.getLegalBase(language);
        this.publications = version.getPublications(language);
        this.derivedFrom = version.getDerivedFrom(language);
        this.levels = LevelResource.convert(version.getLevels(), language);
        // Get both public source and target correspondences
        List<CorrespondenceTable> publicCorrespondenceTables = new LinkedList<>();
        List<CorrespondenceTable> corrTableVersionIsSource = version.getPublicCorrespondenceTables()
                .stream()
                .sorted(AlphaNumericalComparator.comparing(CorrespondenceTable::getNameInPrimaryLanguage, true))
                .collect(Collectors.toList());
        publicCorrespondenceTables.addAll(corrTableVersionIsSource);
        publicCorrespondenceTables.addAll(corrTableVersionIsTarget);
        if (!includeFuture) {
            publicCorrespondenceTables = publicCorrespondenceTables.stream().filter(this::filterFuture).collect(Collectors.toList());
        }


        this.correspondenceTables = CorrespondenceTableSummaryResource
                .convert(publicCorrespondenceTables, language);
        this.classificationVariants = ClassificationVariantSummaryResource
                .convert(version.getPublicClassificationVariants(), language);
        this.classificationItems = ClassificationItemResource.convert(version, language);
        this.changelogs = ChangelogResource.convert(version.getChangelogs());
    }

    private boolean filterFuture(CorrespondenceTable correspondenceTable) {
        LocalDate sourceFrom = correspondenceTable.getSource().getDateRange().getFrom();
        LocalDate targetFrom = correspondenceTable.getTarget().getDateRange().getFrom();
        // if either source or target From date is later than current date we consider this a future correspondence
        // and return false so that it is not included when filtering
        return  !(sourceFrom.isAfter(LocalDate.now()) || targetFrom.isAfter(LocalDate.now()));

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

    public String getLegalBase() {
        return legalBase;
    }

    public String getPublications() {
        return publications;
    }

    public String getDerivedFrom() {
        return derivedFrom;
    }


    @JacksonXmlElementWrapper(localName = "correspondenceTables")
    @JacksonXmlProperty(localName = "correspondenceTable")
    public List<CorrespondenceTableSummaryResource> getCorrespondenceTables() {
        return correspondenceTables;
    }

    @JacksonXmlElementWrapper(localName = "classificationVariants")
    @JacksonXmlProperty(localName = "classificationVariant")
    public List<ClassificationVariantSummaryResource> getClassificationVariants() {
        return classificationVariants;
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
