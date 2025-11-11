package no.ssb.klass.api.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.PagedModel;

import java.util.Collection;

/**
 * @author Mads Lundemo, SSB.
 *     <p>This class overrides the XML anotations for content and links, We do this so we can change
 *     tag names and distinguish a list of elements from the elements within the list
 *     <p>ex. links - link - link links
 */
@JacksonXmlRootElement(localName = "pagedEntities")
@XmlRootElement(name = "pagedEntities")
public class KlassPagedResources<T> extends PagedModel<T> {

    public KlassPagedResources(PagedModel<T> p) {
        super(p.getContent(), p.getMetadata(), p.getLinks());
    }

    @Override
    @XmlAnyElement
    @XmlElementWrapper
    @JacksonXmlElementWrapper(localName = "contents")
    public Collection<T> getContent() {
        return super.getContent();
    }

    @Override
    @XmlElement(name = "link", namespace = Link.ATOM_NAMESPACE)
    @JacksonXmlElementWrapper(localName = "links")
    @JacksonXmlProperty(localName = "link")
    public Links getLinks() {
        return super.getLinks();
    }
}
