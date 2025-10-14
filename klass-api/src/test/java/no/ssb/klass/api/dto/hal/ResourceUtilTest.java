package no.ssb.klass.api.dto.hal;

import no.ssb.klass.api.controllers.ClassificationController;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

import static org.junit.jupiter.api.Assertions.*;

public class ResourceUtilTest {
    @Test
    public void createSearchLinkUsingTemplate() {
        WebMvcLinkBuilder linkBuilder = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ClassificationController.class)
                .search("query", null, true, null));
        String link = ResourceUtil.createUriTemplate(linkBuilder, "query", "includeCodelists").toString();
        // For some reason, the link is different when running the test from command line and from IDE
        link = link.replace("http://localhost", "");
        assertEquals("/api/klass/v1/classifications/search{?query,includeCodelists}", link);
    }
}
