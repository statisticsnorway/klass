package no.ssb.klass.api.dto.hal;

import no.ssb.klass.api.controllers.ClassificationController;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

import static org.junit.jupiter.api.Assertions.*;

public class ResourceUtilTest {
    @Test
    public void createSearchLinkUsingTemplate() {
        WebMvcLinkBuilder linkBuilder = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ClassificationController.class)
                .search("query", null, true, null, null));
        String link = ResourceUtil.createUriTemplate(linkBuilder, "query", "includeCodelists").toString();
        assertTrue(link.endsWith("/v1/classifications/search{?query,includeCodelists}"));
    }
    @Test
    public void createSearchLinkWithPathSubstitution() {
        WebMvcLinkBuilder linkBuilder = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ClassificationController.class)
                .search("query", null, true, null, null));
        String link = ResourceUtil.createUriTemplateBuilder(linkBuilder).basePath("/api/klass/")
                .variables("query", "includeCodelists").build().toString();
        assertTrue(link.endsWith("/api/klass/v1/classifications/search{?query,includeCodelists}"));
    }
}
