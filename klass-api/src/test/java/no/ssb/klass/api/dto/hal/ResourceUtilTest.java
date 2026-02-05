package no.ssb.klass.api.dto.hal;

import static org.junit.jupiter.api.Assertions.*;

import no.ssb.klass.api.controllers.ClassificationController;
import no.ssb.klass.api.util.RestConstants;

import org.junit.jupiter.api.Test;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

public class ResourceUtilTest {
    @Test
    public void createSearchLinkUsingTemplate() {
        WebMvcLinkBuilder linkBuilder =
                WebMvcLinkBuilder.linkTo(
                        WebMvcLinkBuilder.methodOn(ClassificationController.class)
                                .search("query", null, true, null));
        String link =
                ResourceUtil.createUriTemplate(linkBuilder, "query", "includeCodelists").toString();
        // For some reason, the link is different when running the test from command line and from
        // IDE
        link = link.replace("http://localhost", "");
        assertEquals(
                RestConstants.CONTEXT_AND_VERSION_V1
                        + "/classifications/search{?query,includeCodelists}",
                link);
    }
}
