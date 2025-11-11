package no.ssb.klass.api.dto.hal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.lang.Nullable;

public abstract class KlassResource extends PagedModel<RepresentationModel<?>> {

    @JsonProperty("id")
    private final Long id;

    public KlassResource(Long id) {
        this.id = id;
    }

    protected void addLink(Link link) {
        super.add(link);
    }

    @Override
    @JacksonXmlElementWrapper(localName = "links")
    @JacksonXmlProperty(localName = "link")
    public Links getLinks() {
        return super.getLinks();
    }

    @Override
    @JsonProperty("page")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Nullable
    public PageMetadata getMetadata() {
        return super.getMetadata();
    }
}
