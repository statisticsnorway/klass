package no.ssb.klass.api.dto.hal;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.api.controllers.ClassificationController;
import no.ssb.klass.api.util.CustomLocalDateSerializer;
import org.springframework.hateoas.Link;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

public class ClassificationVersionSummaryResource extends KlassResource {
    private final String name;
    private final LocalDate validFrom;
    private final LocalDate validTo;
    private final Date lastModified;
    private final List<String> published;

    protected ClassificationVersionSummaryResource(ClassificationVersion version, Language language, Boolean includeFuture) {
        this.name = version.getName(language);
        this.validFrom = version.getDateRange().getFrom();
        LocalDate to = version.getDateRange().getTo();
        this.validTo = (to.isEqual(LocalDate.MAX) ||(version.isCurrentVersion() && !includeFuture)) ? null : to;
        this.lastModified = version.getLastModified();
        this.published = Arrays.stream(Language.getDefaultPrioritizedOrder())
                .filter(version::isPublished)
                .map(Language::getLanguageCode)
                .collect(toList());
        addLink(createSelfLink(version.getId()));
    }

    @JsonSerialize(using = CustomLocalDateSerializer.class)
    public LocalDate getValidFrom() {
        return validFrom;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    public LocalDate getValidTo() {
        return validTo;
    }

    private Link createSelfLink(long id) {
        return linkTo(methodOn(ClassificationController.class).versions(id, null, null)).withSelfRel();
    }

    @JacksonXmlElementWrapper(localName = "publishedLanguages")
    @JacksonXmlProperty(localName = "published")
    public List<String> getPublished() {
        return published;
    }

    public String getName() {
        return name;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    public Date getLastModified() {
        return lastModified;
    }

    public static List<ClassificationVersionSummaryResource> convert(List<ClassificationVersion> versions,
                                                                     Language language, Boolean includeFuture) {
        return versions.stream()
                .filter(version -> version.showVersion(includeFuture))
                .map(version -> new ClassificationVersionSummaryResource(version, language, includeFuture)).collect(
                        Collectors.toList());
    }
}
