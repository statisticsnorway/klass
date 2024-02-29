package no.ssb.klass.api.dto.hal;

import static java.util.stream.Collectors.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.hateoas.Link;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import no.ssb.klass.core.model.CorrespondenceTable;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.api.controllers.ClassificationController;

@JsonPropertyOrder({"name", "id", "contactPerson", "owningSection", "source", "sourceId", "target", "targetId", "changeTable",
        "lastModified", "published", "sourceLevel", "targetLevel", "links"})
public class CorrespondenceTableSummaryResource extends KlassResource {
    private final String name;
    private final ContactPersonResource contactPerson;
    private final String owningSection;
    private final String source;
    private final Long sourceId;
    private final String target;
    private final Long targetId;
    private final boolean changeTable;
    private final Date lastModified;
    private final List<String> published;
    private final LevelResource sourceLevel;
    private final LevelResource targetLevel;

    protected CorrespondenceTableSummaryResource(CorrespondenceTable correspondenceTable, Language language) {
        super(correspondenceTable.getId());
        this.name = correspondenceTable.getName(language);
        this.contactPerson = new ContactPersonResource(correspondenceTable.getContactPerson());
        this.owningSection = correspondenceTable.getContactPerson().getSection();
        this.source = correspondenceTable.getSource().getName(language);
        this.sourceId = correspondenceTable.getSource().getId();
        this.target = correspondenceTable.getTarget().getName(language);
        this.targetId = correspondenceTable.getTarget().getId();
        this.changeTable = Objects.equals(correspondenceTable.getSource().getClassification(), correspondenceTable
                .getTarget().getClassification());
        this.lastModified = correspondenceTable.getLastModified();
        this.published = Arrays.stream(Language.getDefaultPrioritizedOrder())
                .filter(correspondenceTable::isPublished)
                .map(Language::getLanguageCode)
                .collect(toList());
        this.sourceLevel = correspondenceTable.getSourceLevel().isPresent() ? new LevelResource(correspondenceTable
                .getSourceLevel().get(), language) : null;
        this.targetLevel = correspondenceTable.getTargetLevel().isPresent() ? new LevelResource(correspondenceTable
                .getTargetLevel().get(), language) : null;
        addLink(createSelfLink(correspondenceTable.getId()));
        addLink(createSourceLink(correspondenceTable.getSource().getId()));
        addLink(createTargetLink(correspondenceTable.getTarget().getId()));
    }

    private Link createSelfLink(long id) {
        return linkTo(methodOn(ClassificationController.class).correspondenceTables(id, null)).withSelfRel().expand();
    }

    private Link createSourceLink(long id) {
        return linkTo(methodOn(ClassificationController.class).versions(id, null, null)).withRel("source");
    }

    private Link createTargetLink(long id) {
        return linkTo(methodOn(ClassificationController.class).versions(id, null, null)).withRel("target");
    }

    public String getName() {
        return name;
    }

    public ContactPersonResource getContactPerson() {
        return contactPerson;
    }

    public String getOwningSection() {
        return owningSection;
    }

    public String getSource() {
        return source;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public String getTarget() {
        return target;
    }

    public Long getTargetId() {
        return targetId;
    }

    public LevelResource getSourceLevel() {
        return sourceLevel;
    }

    public LevelResource getTargetLevel() {
        return targetLevel;
    }

    public boolean isChangeTable() {
        return changeTable;
    }

    @JacksonXmlElementWrapper(localName = "publishedLanguages")
    @JacksonXmlProperty(localName = "published")
    public List<String> getPublished() {
        return published;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    public Date getLastModified() {
        return lastModified;
    }

    public static List<CorrespondenceTableSummaryResource> convert(List<CorrespondenceTable> correspondenceTables,
            Language language) {
        return correspondenceTables.stream().map(c -> new CorrespondenceTableSummaryResource(c, language)).collect(
                Collectors.toList());
    }
}
