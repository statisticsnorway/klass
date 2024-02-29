package no.ssb.klass.api.dto.hal;

import static java.util.stream.Collectors.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.core.Relation;

import com.fasterxml.jackson.annotation.JsonFormat;

import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.api.controllers.ClassificationController;

@Relation(collectionRelation = "classifications")
@JsonPropertyOrder({"name", "id", "classificationType", "lastModified", "links"})
public class ClassificationSummaryResource extends KlassResource {
    private final String name;
    private final String classificationType;
    private final Date lastModified;

    protected ClassificationSummaryResource(Language language, ClassificationSeries classification) {
        super(classification.getId());
        this.name = classification.getName(language);
        this.classificationType = classification.getClassificationType().getDisplayName(language);
        this.lastModified = classification.getLastModified();
        addLink(createSelfLink(classification.getId()));
    }

    public ClassificationSummaryResource(ClassificationSeries classification) {
        this(classification.getPrimaryLanguage(), classification);
    }

    private Link createSelfLink(Long id) {
        return linkTo(methodOn(ClassificationController.class).classification(id, null, null)).withSelfRel().expand();
    }

    public String getClassificationType() {
        return classificationType;
    }

    public String getName() {
        return name;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    public Date getLastModified() {
        return lastModified;
    }

    public static List<ClassificationSummaryResource> convert(List<ClassificationSeries> classifications,
            Language language) {
        return classifications.stream().map(classification -> new ClassificationSummaryResource(language,
                classification)).collect(toList());
    }
}
