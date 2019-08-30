package no.ssb.klass.api.dto.hal;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.core.Relation;

import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.repository.ClassificationFamilySummary;
import no.ssb.klass.api.controllers.ClassificationController;

@Relation(collectionRelation = "classificationFamilies")
public class ClassificationFamilySummaryResource extends KlassResource {
    private String name;
    private int numberOfClassifications;

    public ClassificationFamilySummaryResource(ClassificationFamilySummary summary, Language language) {
        this.name = summary.getClassificationFamilyName(language);
        this.numberOfClassifications = summary.getNumberOfClassifications();
        addLink(createSelfLink(summary.getId()));
    }

    private Link createSelfLink(Long id) {
        return linkTo(methodOn(ClassificationController.class).classificationFamily(id, null, null, null))
                .withSelfRel();
    }

    public String getName() {
        return name;
    }

    public int getNumberOfClassifications() {
        return numberOfClassifications;
    }
}
