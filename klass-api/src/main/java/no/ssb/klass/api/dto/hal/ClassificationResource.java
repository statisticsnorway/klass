package no.ssb.klass.api.dto.hal;

import static java.util.stream.Collectors.*;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import java.time.LocalDate;
import java.util.List;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.api.controllers.ClassificationController;
import no.ssb.klass.api.util.RestConstants;

@JacksonXmlRootElement(localName = "classification")
public class ClassificationResource extends ClassificationSummaryResource {
    private final String description;
    private final String primaryLanguage;
    private final boolean copyrighted;
    private final boolean includeShortName;
    private final boolean includeNotes;
    private final ContactPersonResource contactPerson;
    private final String owningSection;
    private final List<String> statisticalUnits;
    private final List<ClassificationVersionSummaryResource> versions;

    public ClassificationResource(ClassificationSeries classification, Language language, Boolean includeFuture) {
        super(language, classification);
        this.description = classification.getDescription(language);
        this.primaryLanguage = classification.getPrimaryLanguage().getLanguageCode();
        this.copyrighted = classification.isCopyrighted();
        this.includeShortName = classification.isIncludeShortName();
        this.includeNotes = classification.isIncludeNotes();
        this.contactPerson = new ContactPersonResource(classification.getContactPerson());
        this.owningSection = classification.getContactPerson().getSection();
        this.statisticalUnits = classification.getStatisticalUnits().stream().map(unit -> unit.getName(language))
                .collect(toList());

        this.versions = ClassificationVersionSummaryResource.convert(classification.getPublicClassificationVersions(),
                language, includeFuture);
        addLink(createCodesRelation(classification.getId()));
        addLink(createCodesAtRelation(classification.getId()));
        addLink(createVariantRelation(classification.getId()));
        addLink(createVariantAtRelation(classification.getId()));
        addLink(createCorrespondsRelation(classification.getId()));
        addLink(createCorrespondsAtRelation(classification.getId()));
        addLink(createChangesRelation(classification.getId()));
    }

    public String getDescription() {
        return description;
    }

    public String getPrimaryLanguage() {
        return primaryLanguage;
    }

    public boolean isCopyrighted() {
        return copyrighted;
    }

    public boolean isIncludeShortName() {
        return includeShortName;
    }

    public boolean isIncludeNotes() {
        return includeNotes;
    }

    public ContactPersonResource getContactPerson() {
        return contactPerson;
    }

    public String getOwningSection() {
        return owningSection;
    }

    @JacksonXmlElementWrapper(localName = "statisticalUnits")
    @JacksonXmlProperty(localName = "statisticalUnit")
    public List<String> getStatisticalUnits() {
        return statisticalUnits;
    }

    @JacksonXmlElementWrapper(localName = "classificationVersions")
    @JacksonXmlProperty(localName = "classificationVersion")
    public List<ClassificationVersionSummaryResource> getVersions() {
        return versions;
    }

    private Link createVariantAtRelation(Long id) {
        ControllerLinkBuilder linkBuilder = linkTo(ControllerLinkBuilder.methodOn(ClassificationController.class).variantAt(id, "name",
                LocalDate.now(), ",", null, "level", "selectCodes", "presentationNamePattern", Language.getDefault(), null));
        return new Link(createUriTemplate(linkBuilder, "variantName", date(), "csvSeparator", "level", "selectCodes",
                "presentationNamePattern"), "variantAt");
    }

    private Link createVariantRelation(Long id) {
        ControllerLinkBuilder linkBuilder = linkTo(ControllerLinkBuilder.methodOn(ClassificationController.class).variant(id, "name",
                LocalDate.now(), LocalDate.now(), ",",null, "level", "selectCodes", "presentationNamePattern", Language
                        .getDefault(), null));
        return new Link(createUriTemplate(linkBuilder, "variantName", from(), to(), "csvSeparator", "level",
                "selectCodes", "presentationNamePattern"), "variant");
    }

    private Link createCodesAtRelation(Long id) {
        ControllerLinkBuilder linkBuilder = linkTo(ControllerLinkBuilder.methodOn(ClassificationController.class).codesAt(id, LocalDate.now(),
                ",",null, "level", "selectCodes", "presentationNamePattern", Language.getDefault(), null));
        return new Link(createUriTemplate(linkBuilder, date(), "csvSeparator", "level", "selectCodes",
                "presentationNamePattern"), "codesAt");
    }

    private Link createCodesRelation(Long id) {
        ControllerLinkBuilder linkBuilder = linkTo(ControllerLinkBuilder.methodOn(ClassificationController.class).codes(id, LocalDate.now(),
                LocalDate.now(), ",",null, "level", "selectCodes", "presentationNamePattern", Language.getDefault(), null));
        return new Link(createUriTemplate(linkBuilder, from(), to(), "csvSeparator", "level", "selectCodes",
                "presentationNamePattern"), "codes");
    }

    private Link createChangesRelation(Long id) {
        ControllerLinkBuilder linkBuilder = linkTo(ControllerLinkBuilder.methodOn(ClassificationController.class).changes(id, LocalDate.now(),
                LocalDate.now(), ",",null, Language.getDefault(), null));
        return new Link(createUriTemplate(linkBuilder, from(), to(), "csvSeparator"), "changes");
    }

    private Link createCorrespondsAtRelation(Long id) {
        ControllerLinkBuilder linkBuilder = linkTo(ControllerLinkBuilder.methodOn(ClassificationController.class).correspondsAt(id, 2L,
                LocalDate.now(), ",",null, Language.getDefault(), null));
        return new Link(createUriTemplate(linkBuilder, "targetClassificationId", date(), "csvSeparator"),
                "correspondsAt");
    }

    private Link createCorrespondsRelation(Long id) {
        ControllerLinkBuilder linkBuilder = linkTo(ControllerLinkBuilder.methodOn(ClassificationController.class).corresponds(id, 2L,
                LocalDate.now(), LocalDate.now(), ",",null, Language.getDefault(), null));
        return new Link(createUriTemplate(linkBuilder, "targetClassificationId", from(), to(), "csvSeparator"),
                "corresponds");
    }

    private String to() {
        return "to=<" + RestConstants.DATE_FORMAT + ">";
    }

    private String from() {
        return "from=<" + RestConstants.DATE_FORMAT + ">";
    }

    private String date() {
        return "date=<" + RestConstants.DATE_FORMAT + ">";
    }
}
