package no.ssb.klass.api.dto.hal;

import java.util.List;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.UriTemplate;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public abstract class KlassResource extends ResourceSupport {
    protected void addLink(Link link) {
        getLinks().add(link);
    }

    protected UriTemplate createUriTemplate(ControllerLinkBuilder linkBuilder, String... parameters) {
        return ResourceUtil.createUriTemplate(linkBuilder, parameters);
    }

    @Override
    @JacksonXmlElementWrapper(localName = "links")
    @JacksonXmlProperty(localName = "link")
    public List<Link> getLinks() {
        return super.getLinks();
    }

}
