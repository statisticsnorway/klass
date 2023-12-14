package no.ssb.klass.api.dto;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.core.ResolvableType;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.CollectionModel;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.springframework.hateoas.Links;

/**
 * This class overrides the XML anotations for content and links, We do this so we can change tag names and distinguish
 * a list of elements from the elements within the list
 * <p>
 * ex.
 * 
 * <pre>
 *   links
 *     - link
 *     - link
 *   links
 * </pre>
 */
@JacksonXmlRootElement(localName = "entities")
@XmlRootElement(name = "entities")
public class KlassResources<T> extends CollectionModel<T> {

    public KlassResources(Iterable<T> content, Link... links) {
        super(content, (Iterable) Arrays.asList(links), (ResolvableType) null);
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
