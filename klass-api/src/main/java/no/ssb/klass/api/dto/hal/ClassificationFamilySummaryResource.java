package no.ssb.klass.api.dto.hal;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import no.ssb.klass.api.controllers.ClassificationController;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.repository.ClassificationFamilySummary;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.core.Relation;

@Relation(collectionRelation = "classificationFamilies")
@JsonPropertyOrder({"name", "id", "numberOfClassifications", "links"})
public class ClassificationFamilySummaryResource extends KlassResource {
    private String name;
    private int numberOfClassifications;

    public ClassificationFamilySummaryResource(
            ClassificationFamilySummary summary, Language language) {
        super(summary.getId());
        this.name = summary.getClassificationFamilyName(language);
        this.numberOfClassifications = summary.getNumberOfClassifications();
        addLink(createSelfLink(summary.getId()));
    }

    private Link createSelfLink(Long id) {
        return linkTo(
                        methodOn(ClassificationController.class)
                                .classificationFamily(id, null, null, null))
                .withSelfRel()
                .expand();
    }

    public String getName() {
        return name;
    }

    public int getNumberOfClassifications() {
        return numberOfClassifications;
    }
}
