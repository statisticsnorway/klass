package no.ssb.klass.api.dto.hal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import no.ssb.klass.core.model.ClassificationFamily;
import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.testutil.TestUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class ClassificationSummaryResourceTest {

    @BeforeEach
    public void setup() {
        RequestContextHolder.setRequestAttributes(
                new ServletRequestAttributes(new MockHttpServletRequest()));
    }

    @Test
    public void createWithoutClassificationFamily() {
        // given
        ClassificationSeries classification = TestUtil.createClassification("name");
        classification.setId(123L);

        // when
        ClassificationSummaryResource subject =
                new ClassificationSummaryResource(Language.NB, classification);

        // then
        assertNull(subject.getClassificationFamilyId());
    }

    @Test
    public void createWithClassificationFamily() {
        // given
        ClassificationSeries classification = TestUtil.createClassification("name");
        classification.setId(123L);
        ClassificationFamily family = TestUtil.createClassificationFamily("family");
        family.setId(456L);
        classification.setClassificationFamily(family);

        // when
        ClassificationSummaryResource subject =
                new ClassificationSummaryResource(Language.NB, classification);

        // then
        assertEquals(Long.valueOf(456L), subject.getClassificationFamilyId());
    }

    @Test
    public void descriptionIncludedWhenRequested() {
        // given
        ClassificationSeries classification = TestUtil.createClassification("name");
        classification.setId(123L);

        // when - default constructor does not include description
        ClassificationSummaryResource withoutDescription =
                new ClassificationSummaryResource(Language.NB, classification);

        // then
        assertNull(withoutDescription.getDescription());

        // when - request description explicitly
        ClassificationSummaryResource withDescription =
                new ClassificationSummaryResource(Language.NB, classification, true);

        // then
        assertNotNull(withDescription.getDescription());
    }
}
