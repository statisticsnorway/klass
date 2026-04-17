package no.ssb.klass.api.dto.hal;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import static java.util.stream.Collectors.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.validation.constraints.NotNull;

import no.ssb.klass.api.controllers.ClassificationController;
import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.Language;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.core.Relation;

import java.util.Date;
import java.util.List;

@Relation(collectionRelation = "classifications")
@JsonPropertyOrder({"name", "id", "classificationType", "lastModified", "links"})
public class ClassificationSummaryResource extends KlassResource {
    private final String name;
    private final String classificationType;
    private final Date lastModified;

    public ClassificationSummaryResource(Language language, ClassificationSeries classification) {
        super(classification.getId());
        @NotNull
        Language chosenLanguage = language != null ? language : classification.getPrimaryLanguage();
        this.name = classification.getName(chosenLanguage);
        this.classificationType =
                classification.getClassificationType().getDisplayName(chosenLanguage);
        this.lastModified = classification.getLastModified();
        addLink(createSelfLink(classification.getId()));
    }

    private Link createSelfLink(Long id) {
        return linkTo(methodOn(ClassificationController.class).classification(id, null, null))
                .withSelfRel()
                .expand();
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

    public static List<ClassificationSummaryResource> convert(
            List<ClassificationSeries> classifications, Language language) {
        return classifications.stream()
                .map(classification -> new ClassificationSummaryResource(language, classification))
                .collect(toList());
    }
}
