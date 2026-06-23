package no.ssb.klass.api.dto.hal;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import static java.util.stream.Collectors.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.validation.constraints.NotNull;

import no.ssb.klass.api.controllers.ClassificationController;
import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.Language;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.core.Relation;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Relation(collectionRelation = "classifications")
@JsonPropertyOrder({
    "name",
    "id",
    "classificationType",
    "classificationFamilyId",
    "lastModified",
    "description",
    "links"
})
public class ClassificationSummaryResource extends KlassResource {
    private final String name;
    private final String classificationType;
    private final Long classificationFamilyId;
    private final Date lastModified;
    private final String description;

    public ClassificationSummaryResource(Language language, ClassificationSeries classification) {
        this(language, classification, false);
    }

    public ClassificationSummaryResource(
            Language language, ClassificationSeries classification, boolean includeDescription) {
        super(classification.getId());
        @NotNull
        Language chosenLanguage = language != null ? language : classification.getPrimaryLanguage();
        this.name = classification.getName(chosenLanguage);
        this.classificationType =
                classification.getClassificationType().getDisplayName(chosenLanguage);
        this.classificationFamilyId = classification.getClassificationFamilyId();
        this.lastModified = classification.getLastModified();
        this.description =
                includeDescription ? classification.getDescription(chosenLanguage) : null;
        addLink(createSelfLink(classification.getId()));
    }

    private Link createSelfLink(Long id) {
        return linkTo(methodOn(ClassificationController.class).classification(id, null, null))
                .withSelfRel()
                .expand();
    }

    @JsonInclude(NON_NULL)
    public String getDescription() {
        return description;
    }

    public String getClassificationType() {
        return classificationType;
    }

    public String getName() {
        return name;
    }

    public Long getClassificationFamilyId() {
        return classificationFamilyId;
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ClassificationSummaryResource that = (ClassificationSummaryResource) o;
        return Objects.equals(name, that.name)
                && Objects.equals(classificationType, that.classificationType)
                && Objects.equals(classificationFamilyId, that.classificationFamilyId)
                && Objects.equals(lastModified, that.lastModified)
                && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                super.hashCode(),
                name,
                classificationType,
                classificationFamilyId,
                lastModified,
                description);
    }
}
