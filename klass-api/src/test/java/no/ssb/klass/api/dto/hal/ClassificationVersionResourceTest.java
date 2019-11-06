package no.ssb.klass.api.dto.hal;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.google.common.collect.Lists;

import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.api.util.RestConstants;
import no.ssb.klass.testutil.TestUtil;

public class ClassificationVersionResourceTest {

    @Before
    public void before() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
    }

    @Test
    public void create() {
        // given
        final long id = 1;
        ClassificationVersion version = TestUtil.createClassificationVersionWithTable(id, TestUtil.anyDateRange(), "name");

        // when
        ClassificationVersionResource subject = new ClassificationVersionResource(version, Language.getDefault(),
                new LinkedList<>(), false);

        // then
        assertEquals(1, subject.getLinks().size());
        assertEquals("http://localhost" + RestConstants.REST_PREFIX + "/versions/1", subject.getLink("self").getHref());
        assertEquals(version.getName(Language.getDefault()), subject.getName());
    }

    @Test
    public void convert() {
        // given
        ClassificationVersion version = TestUtil.createClassificationVersionWithTable(1, TestUtil.anyDateRange(), "name");

        // when
        List<ClassificationVersionSummaryResource> result = ClassificationVersionSummaryResource.convert(Lists
                .newArrayList(version), Language.getDefault(), null);
        // then
        assertEquals(1, result.size());
    }
}
