package no.ssb.klass.api.dto.hal;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.hateoas.Link;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import no.ssb.klass.core.model.ClassificationFamily;
import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.ClassificationType;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.api.controllers.ClassificationController;

@JacksonXmlRootElement(localName = "classificationFamily")
@JsonPropertyOrder({"name", "id", "classifications", "links"})
public class ClassificationFamilyResource extends KlassResource {
    private final String name;
    private final List<ClassificationSummaryResource> classifications;

    public ClassificationFamilyResource(ClassificationFamily classificationFamily, Language language, String ssbSection,
            ClassificationType classificationType) {
        super(classificationFamily.getId());
        this.name = classificationFamily.getName(language);
        List<ClassificationSeries> classifications = classificationFamily
                .getClassificationSeriesBySectionAndClassificationType(ssbSection, classificationType, true);
        this.classifications = ClassificationSummaryResource.convert(classifications, language);
        addLink(createSelfLink(classificationFamily.getId()));
    }

    public String getName() {
        return name;
    }

    @JacksonXmlElementWrapper(localName = "classifications")
    @JacksonXmlProperty(localName = "classification")
    public List<ClassificationSummaryResource> getClassifications() {
        return classifications;
    }

    private Link createSelfLink(Long id) {
        return linkTo(methodOn(ClassificationController.class).classificationFamily(id, null, null, null))
                .withSelfRel().expand();
    }
}
