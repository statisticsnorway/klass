package no.ssb.klass.api.dto.hal;

import static java.util.stream.Collectors.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.Link;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import no.ssb.klass.core.model.ClassificationVariant;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.api.controllers.ClassificationController;

public class ClassificationVariantSummaryResource extends KlassResource {
    private final String name;
    private final ContactPersonResource contactPerson;
    private final String owningSection;
    private final Date lastModified;
    private final List<String> published;

    protected ClassificationVariantSummaryResource(ClassificationVariant variant, Language language) {
        this.name = variant.getFullName(language);
        this.lastModified = variant.getLastModified();
        this.contactPerson = new ContactPersonResource(variant.getContactPerson());
        this.owningSection = variant.getContactPerson().getSection();
        this.published = Arrays.stream(Language.getDefaultPrioritizedOrder())
                .filter(variant::isPublished)
                .map(Language::getLanguageCode)
                .collect(toList());
        addLink(createSelfLink(variant.getId()));
    }

    private Link createSelfLink(long id) {
        return linkTo(methodOn(ClassificationController.class).variants(id, null)).withSelfRel();
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

    @JacksonXmlElementWrapper(localName = "publishedLanguages")
    @JacksonXmlProperty(localName = "published")
    public List<String> getPublished() {
        return published;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    public Date getLastModified() {
        return lastModified;
    }

    public static List<ClassificationVariantSummaryResource> convert(List<ClassificationVariant> variants,
            Language language) {
        return variants.stream().map(variant -> new ClassificationVariantSummaryResource(variant, language)).collect(
                Collectors.toList());
    }
}
