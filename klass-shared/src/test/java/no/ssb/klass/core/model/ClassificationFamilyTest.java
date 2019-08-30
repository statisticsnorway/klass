package no.ssb.klass.core.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import no.ssb.klass.testutil.TestUtil;

public class ClassificationFamilyTest {
    private ClassificationFamily subject;

    @Before
    public void setup() {
        subject = TestUtil.createClassificationFamily("name");
        ClassificationSeries classification = TestUtil.createClassification("classification");
        subject.addClassificationSeries(classification);
    }

    @Test
    @Ignore("Icons moved to forvaltning module")
    public void getIconPath() {
        // when
        String result = subject.getIconPath();

        // then
        assertTrue("icon path does not exist on classpath", new ClassPathResource(result).exists());
    }

    @Test
    public void getClassificationSeriesBySectionAndClassificationType() {
        // given
        final String allSections = null;
        final ClassificationType allClassificationTypes = null;
        ClassificationFamily classificationFamily = TestUtil.createClassificationFamily("classificationFamily");
        ClassificationSeries classification = TestUtil.createClassification("classification");
        classificationFamily.addClassificationSeries(classification);

        // then
        assertEquals(1, getClassificationSeries(allSections, allClassificationTypes));
        assertEquals(1, getClassificationSeries(allSections, classification.getClassificationType()));
        assertEquals(0, getClassificationSeries(allSections, TestUtil.oppositeClassificationType(classification
                .getClassificationType())));
        assertEquals(1, getClassificationSeries(classification.getContactPerson().getSection(),
                allClassificationTypes));
        assertEquals(0, getClassificationSeries("unknown section", allClassificationTypes));
    }

    private long getClassificationSeries(String section, ClassificationType classificationType) {
        return subject.getClassificationSeriesBySectionAndClassificationType(section, classificationType).size();
    }
}
