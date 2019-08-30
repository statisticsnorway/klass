package no.ssb.klass.api.dto.hal;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import no.ssb.klass.core.model.CorrespondenceTable;
import no.ssb.klass.core.model.Language;

@JacksonXmlRootElement(localName = "correspondenceTable")
public class CorrespondenceTableResource extends CorrespondenceTableSummaryResource {
    private final String description;
    private final List<ChangelogResource> changelogs;


    private final List<CorrespondenceMapResource> correspondenceMaps;

    public CorrespondenceTableResource(CorrespondenceTable correspondenceTable, Language language) {
        super(correspondenceTable, language);
        this.description = correspondenceTable.getDescription(language);
        this.correspondenceMaps = CorrespondenceMapResource.convert(correspondenceTable.getCorrespondenceMaps(),
                language);
        this.changelogs = ChangelogResource.convert(correspondenceTable.getChangelogs());
    }

    public String getDescription() {
        return description;
    }

    @JacksonXmlElementWrapper(localName = "changelogs")
    @JacksonXmlProperty(localName = "changelog")
    public List<ChangelogResource> getChangelogs() {
        return changelogs;
    }



    @JacksonXmlElementWrapper(localName = "correspondenceMaps")
    @JacksonXmlProperty(localName = "correspondenceMap")
    public List<CorrespondenceMapResource> getCorrespondenceMaps() {
        return correspondenceMaps;
    }
}
