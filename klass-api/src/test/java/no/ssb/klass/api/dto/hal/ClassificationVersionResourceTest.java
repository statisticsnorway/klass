package no.ssb.klass.api.dto.hal;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.common.collect.Lists;

import no.ssb.klass.api.util.RestConstants;
import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.testutil.TestUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.LinkedList;
import java.util.List;

public class ClassificationVersionResourceTest {

    @BeforeEach
    public void before() {
        RequestContextHolder.setRequestAttributes(
                new ServletRequestAttributes(new MockHttpServletRequest()));
    }

    @Test
    public void create() {
        // given
        final long id = 1;
        ClassificationVersion version =
                TestUtil.createClassificationVersionWithTable(id, TestUtil.anyDateRange(), "name");

        // when
        ClassificationVersionResource subject =
                new ClassificationVersionResource(
                        version, Language.getDefault(), new LinkedList<>(), false, "320");

        // then
        assertEquals(1, subject.getLinks().toList().size());
        assertEquals(
                "http://localhost" + RestConstants.API_VERSION_V1 + "/versions/1",
                subject.getLink("self")
                        .orElseThrow(() -> new RuntimeException("No link found"))
                        .getHref());
        assertEquals(version.getName(Language.getDefault()), subject.getName());
    }

    @Test
    public void convert() {
        // given
        ClassificationVersion version =
                TestUtil.createClassificationVersionWithTable(1, TestUtil.anyDateRange(), "name");

        // when
        List<ClassificationVersionSummaryResource> result =
                ClassificationVersionSummaryResource.convert(
                        Lists.newArrayList(version), Language.getDefault(), null);
        // then
        assertEquals(1, result.size());
    }
}
