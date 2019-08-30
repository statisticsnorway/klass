package no.ssb.klass.forvaltning.converting.xml;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.io.IOException;
import java.io.InputStream;

import no.ssb.klass.testutil.TestDataProvider;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import no.ssb.klass.forvaltning.converting.exception.ImportException;
import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.util.DateRange;
import no.ssb.klass.testutil.TestUtil;

/**
 * @author Mads Lundemo, SSB.
 */
public class ClassificationVersionXmlServiceTest {

    private ClassificationVersionXmlService service = new ClassificationVersionXmlService();

    @Test
    public void convertToXml() throws IOException {
        ClassificationVersion classificationVersion = TestDataProvider.createClassificationVersionWithTranslations();

        try (InputStream inputStream = service.toXmlStream(classificationVersion)) {
            String xml = IOUtils.toString(inputStream);
            // xsd check
            assertThat(xml, containsString("version.xsd"));
            assertThat(xml, containsString("xmlns=\"http://klass.ssb.no/version\""));
            // first code
            assertThat(xml, containsString("<kode>030101</kode>"));
            assertThat(xml, containsString("<forelder></forelder>"));
            assertThat(xml, containsString("<navn_bokmål>Norge</navn_bokmål>"));
            assertThat(xml, containsString("<navn_nynorsk>Noreg</navn_nynorsk>"));
            assertThat(xml, containsString("<navn_engelsk>Norway</navn_engelsk>"));
            assertThat(xml, containsString("<kortnavn_bokmål>no</kortnavn_bokmål>"));
            assertThat(xml, containsString("<kortnavn_nynorsk>nn</kortnavn_nynorsk>"));
            assertThat(xml, containsString("<kortnavn_engelsk>en</kortnavn_engelsk>"));
            assertThat(xml, containsString("<noter_bokmål>ikke</noter_bokmål>"));
            assertThat(xml, containsString("<noter_nynorsk>ikkje</noter_nynorsk>"));
            assertThat(xml, containsString("<noter_engelsk>not</noter_engelsk>"));
            // last code
            assertThat(xml, containsString("<kode>030103</kode>"));
            assertThat(xml, containsString("<forelder>030102</forelder>"));
            assertThat(xml, containsString("<navn_bokmål>bulldoser</navn_bokmål>"));
            assertThat(xml, containsString("<navn_nynorsk>stålstut</navn_nynorsk>"));
            assertThat(xml, containsString("<navn_engelsk>bulldozer</navn_engelsk>"));
            assertThat(xml, containsString("<kortnavn_bokmål>bs</kortnavn_bokmål>"));
            assertThat(xml, containsString("<kortnavn_nynorsk>st</kortnavn_nynorsk>"));
            assertThat(xml, containsString("<kortnavn_engelsk>bz</kortnavn_engelsk>"));
            assertThat(xml, containsString("<noter_bokmål></noter_bokmål>"));
            assertThat(xml, containsString("<noter_nynorsk></noter_nynorsk>"));
            assertThat(xml, containsString("<noter_engelsk></noter_engelsk>"));

        }
    }

    @Test
    public void convertFromXml() throws IOException, ImportException {
        ClassificationVersion testData = TestDataProvider.createClassificationVersionWithTranslations();
        try (InputStream inputStream = service.toXmlStream(testData)) {

            ClassificationVersion version = TestUtil.createClassificationVersion(DateRange.create("2014-01-01", null));
            ClassificationSeries classification = TestUtil.createCodelist("a", "b");
            classification.addClassificationVersion(version);

            assertThat(version.getAllClassificationItems().size(), is(0));
            service.fromXmlStreamAndMerge(inputStream, version);
            assertThat(version.getAllClassificationItems().size(), is(3));
            assertThat(version.getAllClassificationItems().get(2).getCode(), is("030103"));
            assertThat(version.getAllClassificationItems().get(2).getLevel().getLevelNumber(), is(3));
            assertThat(version.getAllClassificationItems().get(2).getOfficialName(Language.NN), is("stålstut"));
            assertThat(version.getAllClassificationItems().get(2).getShortName(Language.EN), is("bz"));
        }

    }
}
