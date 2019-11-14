package no.ssb.klass.forvaltning.converting.xml;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import no.ssb.klass.testutil.TestDataProvider;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import no.ssb.klass.forvaltning.converting.exception.ImportException;
import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.model.CorrespondenceMap;
import no.ssb.klass.core.model.CorrespondenceTable;
import no.ssb.klass.testutil.TestUtil;

/**
 * @author Mads Lundemo, SSB.
 */

public class CorrespondenceTableXmlServiceTest {

    private CorrespondenceTableXmlService service = new CorrespondenceTableXmlService();



    @Test
    public void convertFromXml() throws IOException, ImportException {
        ClassificationSeries classification = TestDataProvider.createClassificationKommuneinndeling();
        CorrespondenceTable testData = TestDataProvider.createAndAddChangeCorrespondenceTable(classification);

        try (InputStream inputStream = service.toXmlStream(testData)) {

            ClassificationVersion version = classification.getClassificationVersions().get(1);
            CorrespondenceTable correspondenceTable = TestUtil.createCorrespondenceTable(version, version);

            assertThat(correspondenceTable.getCorrespondenceMaps().size(), is(0));
            service.fromXmlStreamAndMerge(inputStream, correspondenceTable);
            assertThat(correspondenceTable.getCorrespondenceMaps().size(), is(2));

            CorrespondenceMap item1 = correspondenceTable.getCorrespondenceMaps().get(0);
            assertThat(item1.getSource().get().getCode(), is("1739"));
            assertThat(item1.getTarget().get().getCode(), is("1739"));
            CorrespondenceMap item2 = correspondenceTable.getCorrespondenceMaps().get(1);
            assertThat(item2.getSource().get().getCode(), is("1939"));
            assertThat(item2.getTarget().get().getCode(), is("1939"));

        }
    }

    @Test
    public void convertToXml() throws IOException {
        ClassificationSeries classification = TestDataProvider.createClassificationKommuneinndeling();
        CorrespondenceTable correspondenceTable = TestDataProvider.createAndAddChangeCorrespondenceTable(
                classification);

        try (InputStream inputStream = service.toXmlStream(correspondenceTable)) {
            String xml = IOUtils.toString(inputStream);
            // xsd check
            assertThat(xml, containsString("correspondenceTable.xsd"));
            assertThat(xml, containsString("xmlns=\"http://klass.ssb.no/correspondenceTable\""));
            // first correspondence
            assertThat(xml, containsString("<Korrespondanse>"));
            assertThat(xml, containsString("<kilde_kode>1739</kilde_kode>"));
            assertThat(xml, containsString("<kilde_tittel>Raarvihke Røyrvik</kilde_tittel>"));
            assertThat(xml, containsString("<mål_kode>1739</mål_kode>"));
            assertThat(xml, containsString("<mål_tittel>Røyrvik</mål_tittel>"));
            assertThat(xml, containsString("</Korrespondanse>"));
            // last correspondence
            assertThat(xml, containsString("<kilde_kode>1939</kilde_kode>"));
            assertThat(xml, containsString("<kilde_tittel>Omasvuotna Storfjord Omasvuonon</kilde_tittel>"));
            assertThat(xml, containsString("<mål_kode>1939</mål_kode>"));
            assertThat(xml, containsString("<mål_tittel>Storfjord</mål_tittel>"));

        }
    }

    @Test
    public void convertToXml_with_outdated_codes() throws IOException {
        ClassificationSeries classification = TestDataProvider.createClassificationKommuneinndeling();
        CorrespondenceTable correspondenceTable =
                TestDataProvider.createAndAddChangeCorrespondenceTable_withOutdatedCodes(classification);

        try (InputStream inputStream = service.toXmlStream(correspondenceTable)) {
            String xml = IOUtils.toString(inputStream);
            // xsd check
            assertThat(xml, containsString("correspondenceTable.xsd"));
            assertThat(xml, containsString("xmlns=\"http://klass.ssb.no/correspondenceTable\""));
            // first correspondence
            assertThat(xml, containsString("<Korrespondanse>"));
            assertThat(xml, containsString("<kilde_kode/>"));
            assertThat(xml, containsString("<kilde_tittel/>"));
            assertThat(xml, containsString("<mål_kode>1739</mål_kode>"));
            assertThat(xml, containsString("<mål_tittel>Røyrvik</mål_tittel>"));
            assertThat(xml, containsString("</Korrespondanse>"));
            // second correspondence
            assertThat(xml, containsString("<Korrespondanse>"));
            assertThat(xml, containsString("<kilde_kode/>"));
            assertThat(xml, containsString("<kilde_tittel/>"));
            assertThat(xml, containsString("<mål_kode>1939</mål_kode>"));
            assertThat(xml, containsString("<mål_tittel>Storfjord</mål_tittel>"));
            assertThat(xml, containsString("</Korrespondanse>"));
            // last correspondence
            assertThat(xml, containsString("<Korrespondanse>"));
            assertThat(xml, containsString("<kilde_kode>1939</kilde_kode>"));
            assertThat(xml, containsString("<kilde_tittel>Omasvuotna Storfjord Omasvuonon</kilde_tittel>"));
            assertThat(xml, containsString("<mål_kode/>"));
            assertThat(xml, containsString("<mål_tittel/>"));
            assertThat(xml, containsString("</Korrespondanse>"));
        }
    }

    @Test
    public void convertFromXmlAndSplitMultiCells() throws IOException, ImportException {

        String xml = "<Korrespondansetabell xmlns=\"http://klass.ssb.no/correspondenceTable\" "
                + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                + " xsi:schemaLocation=\"http://klass.ssb.no/correspondenceTable null/correspondenceTable.xsd\">\n"
                + "  <Korrespondanse>\n"
                + "    <kilde_kode>1739</kilde_kode>\n"
                + "    <kilde_tittel>Raarvihke Røyrvik</kilde_tittel>\n"
                + "    <mål_kode>1739\n0104</mål_kode>\n"
                + "    <mål_tittel>Røyrvik</mål_tittel>\n" + "  </Korrespondanse>\n"
                + "  <Korrespondanse>\n"
                + "    <kilde_kode>1939\n0101</kilde_kode>\n"
                + "    <kilde_tittel>Omasvuotna Storfjord Omasvuonon</kilde_tittel>\n"
                + "    <mål_kode>1939</mål_kode>\n"
                + "    <mål_tittel>Storfjord</mål_tittel>\n"
                + "  </Korrespondanse>\n"
                + "</Korrespondansetabell>\n";

        ByteArrayInputStream inputStream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
        ClassificationSeries classification = TestDataProvider.createClassificationKommuneinndeling();

        ClassificationVersion version = classification.getClassificationVersions().get(1);
        CorrespondenceTable correspondenceTable = TestUtil.createCorrespondenceTable(version, version);

        assertThat(correspondenceTable.getCorrespondenceMaps().size(), is(0));
        service.fromXmlStreamAndMerge(inputStream, correspondenceTable);
        assertThat(correspondenceTable.getCorrespondenceMaps().size(), is(4));

        // NOTE output is sorted by code
        CorrespondenceMap item1 = correspondenceTable.getCorrespondenceMaps().get(0);
        assertThat(item1.getSource().get().getCode(), is("0101"));
        assertThat(item1.getTarget().get().getCode(), is("1939"));

        CorrespondenceMap item2 = correspondenceTable.getCorrespondenceMaps().get(1);
        assertThat(item2.getSource().get().getCode(), is("1739"));
        assertThat(item2.getTarget().get().getCode(), is("0104"));

        CorrespondenceMap item3 = correspondenceTable.getCorrespondenceMaps().get(2);
        assertThat(item3.getSource().get().getCode(), is("1739"));
        assertThat(item3.getTarget().get().getCode(), is("1739"));

        CorrespondenceMap item4 = correspondenceTable.getCorrespondenceMaps().get(3);
        assertThat(item4.getSource().get().getCode(), is("1939"));
        assertThat(item4.getTarget().get().getCode(), is("1939"));
    }
}
