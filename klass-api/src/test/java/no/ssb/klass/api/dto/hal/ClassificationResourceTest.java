package no.ssb.klass.api.dto.hal;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.api.util.RestConstants;
import no.ssb.klass.testutil.TestUtil;

public class ClassificationResourceTest {

    @BeforeEach
    public void setup() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
    }

    @Test
    public void create() {
        // given
        final long id = 1;
        final String name = "name";
        final String basePath = "/api/klass";

        // when
        ClassificationResource subject = new ClassificationResource(createClassification(id, name), Language
                .getDefault(), null, "320");

        // then
        assertEquals(name, subject.getName());
        assertEquals("http://localhost" + basePath + RestConstants.API_VERSION_V1 + "/classifications/" + id, subject.getLink("self").orElseThrow(() ->
                new RuntimeException("No link found")).getHref());
        assertEquals("http://localhost" + basePath + RestConstants.API_VERSION_V1 + "/classifications/" + id + "/variant"
                + "{?variantName,from=<yyyy-MM-dd>,to=<yyyy-MM-dd>,csvSeparator,level,selectCodes,presentationNamePattern}",
                subject.getLink("variant").orElseThrow(() ->
                new RuntimeException("No link found")).getHref());
        assertEquals("http://localhost" + basePath + RestConstants.API_VERSION_V1 + "/classifications/" + id + "/variantAt"
                + "{?variantName,date=<yyyy-MM-dd>,csvSeparator,level,selectCodes,presentationNamePattern}",
                subject.getLink("variantAt").orElseThrow(() ->
                new RuntimeException("No link found")).getHref());
        assertEquals(0, subject.getVersions().size());
    }

    private ClassificationSeries createClassification(long id, String name) {
        ClassificationSeries classification = TestUtil.createClassificationWithId(id, name);
        classification.setId(id);
        return classification;
    }
}
