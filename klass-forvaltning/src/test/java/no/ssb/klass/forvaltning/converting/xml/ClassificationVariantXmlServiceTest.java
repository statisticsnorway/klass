package no.ssb.klass.forvaltning.converting.xml;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import no.ssb.klass.forvaltning.converting.exception.ImportException;
import no.ssb.klass.core.model.ClassificationVariant;
import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.model.ConcreteClassificationItem;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.model.User;
import no.ssb.klass.core.service.ClassificationService;
import no.ssb.klass.core.util.DateRange;
import no.ssb.klass.core.util.Translatable;
import no.ssb.klass.testutil.TestUtil;

/**
 * @author Mads Lundemo, SSB.
 */
@RunWith(MockitoJUnitRunner.class)
public class ClassificationVariantXmlServiceTest {

    @Mock
    private ClassificationService classificationService;
    @InjectMocks
    private ClassificationVariantXmlService service = new ClassificationVariantXmlService();

    @Test
    public void convertToXml() throws IOException {
        ClassificationVariant variant = createTestData();

        try (InputStream inputStream = service.toXmlStream(variant)) {
            String xml = IOUtils.toString(inputStream);
            // xsd check
            assertThat(xml, containsString("variant.xsd"));
            assertThat(xml, containsString("xmlns=\"http://klass.ssb.no/variant\""));
            // first level code variant specific code
            assertThat(xml, containsString("<kode>A</kode>"));
            assertThat(xml, containsString("<navn_bokmål>GruppeA</navn_bokmål>"));
            assertThat(xml, containsString("<navn_nynorsk>AGruppa</navn_nynorsk>"));
            assertThat(xml, containsString("<navn_engelsk>GroupA</navn_engelsk>"));
            assertThat(xml, containsString("<kilde_kode/>"));
            assertThat(xml, containsString("<forelder/>"));
            // second level code from version
            assertThat(xml, containsString("<kode/>"));
            assertThat(xml, containsString("<navn_bokmål/>"));
            assertThat(xml, containsString("<navn_nynorsk/>"));
            assertThat(xml, containsString("<navn_engelsk/>"));
            assertThat(xml, containsString("<kilde_kode>1.1</kilde_kode>"));
            assertThat(xml, containsString("<forelder>A</forelder>"));

        }
    }

    @Test
    public void convertFromXml() throws IOException, ImportException {

        ClassificationVariant testData = createTestData();

        // add spaces to test trim feature
        ConcreteClassificationItem item = (ConcreteClassificationItem) testData.getAllClassificationItems().get(4);
        item.setCode(item.getCode() + "      ");
        item.setOfficialName(item.getOfficialName(Language.NB) + "      ", Language.NB);

        when(classificationService.getClassificationVersion(null)).thenReturn(testData
                .getClassificationVersion());
        try (InputStream inputStream = service.toXmlStream(testData)) {

            ClassificationVariant variant = TestUtil.createClassificationVariant("A", new User("B", "C", "D"));
            variant.setClassificationVersion(testData.getClassificationVersion());

            assertThat(variant.getAllClassificationItems().size(), is(0));
            service.fromXmlStreamAndMerge(inputStream, variant);
            assertThat(variant.getAllClassificationItems().size(), is(6));
            assertThat(variant.getLevels().size(), is(2));
            assertThat(variant.getLevels().get(0).getClassificationItems().size(), is(2));
            assertThat(variant.getLevels().get(1).getClassificationItems().size(), is(4));

            // structure check
            assertThat(variant.getAllClassificationItemsLevelForLevel().get(0).getCode(), is("A"));
            assertThat(variant.getAllClassificationItemsLevelForLevel().get(1).getCode(), is("B"));
            assertThat(variant.getAllClassificationItemsLevelForLevel().get(2).getCode(), is("1.1"));
            assertThat(variant.getAllClassificationItemsLevelForLevel().get(3).getCode(), is("1.2"));

            // content test
            assertThat(variant.getAllClassificationItemsLevelForLevel().get(0).getCode(), is("A"));
            assertThat(variant.getAllClassificationItemsLevelForLevel().get(0).getOfficialName(Language.NB), is(
                    "GruppeA"));
        }

    }

    private ClassificationVariant createTestData() {
        ClassificationVersion version = TestUtil.createClassificationVersion(DateRange.create("2014-01-01", null));
        ClassificationVariant variant = TestUtil.createClassificationVariant("Variant", new User("", "", ""));

        variant.setClassificationVersion(version);
        ConcreteClassificationItem groupA =
                new ConcreteClassificationItem("A", new Translatable("GruppeA", "AGruppa", "GroupA"),
                        Translatable.empty());

        variant.addClassificationItem(groupA, 1, null);
        variant.addClassificationItem(TestUtil.createClassificationItem("B", "gruppeB"), 1, null);


        version.addNextLevel();
        version.addClassificationItem(TestUtil.createClassificationItem("1.1", "elementA1"), 1, null);
        version.addClassificationItem(TestUtil.createClassificationItem("1.2", "elementA2"), 1, null);
        version.addClassificationItem(TestUtil.createClassificationItem("2.1", "elementB1"), 1, null);
        version.addClassificationItem(TestUtil.createClassificationItem("2.2", "elementB2"), 1, null);

        variant.addClassificationItem(TestUtil.createReferencingClassificationItem(version.findItem("1.1")), 2, variant
                .findItem("A"));
        variant.addClassificationItem(TestUtil.createReferencingClassificationItem(version.findItem("1.2")), 2, variant
                .findItem("A"));
        variant.addClassificationItem(TestUtil.createReferencingClassificationItem(version.findItem("2.1")), 2, variant
                .findItem("B"));
        variant.addClassificationItem(TestUtil.createReferencingClassificationItem(version.findItem("2.2")), 2, variant
                .findItem("B"));
        return variant;
    }
}
