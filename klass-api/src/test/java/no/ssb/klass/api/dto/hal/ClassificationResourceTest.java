package no.ssb.klass.api.dto.hal;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.api.util.RestConstants;
import no.ssb.klass.testutil.TestUtil;

public class ClassificationResourceTest {

    @Before
    public void setup() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
    }

    @Test
    public void create() {
        // given
        final long id = 1;
        final String name = "name";

        // when
        ClassificationResource subject = new ClassificationResource(createClassification(id, name), Language
                .getDefault(), null);

        // then
        assertEquals(name, subject.getName());
        assertEquals("http://localhost" + RestConstants.REST_PREFIX + "/classifications/" + id, subject.getLink("self")
                .getHref());
        assertEquals(0, subject.getVersions().size());
    }

    private ClassificationSeries createClassification(long id, String name) {
        ClassificationSeries classification = TestUtil.createClassificationWithId(id, name);
        classification.setId(id);
        return classification;
    }
}
