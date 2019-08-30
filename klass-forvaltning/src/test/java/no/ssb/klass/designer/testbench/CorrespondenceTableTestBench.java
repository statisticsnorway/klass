package no.ssb.klass.designer.testbench;

import static org.junit.Assert.*;

import org.junit.Test;

import no.ssb.klass.core.model.Language;
import no.ssb.klass.designer.testbench.pages.ClassificationListPage;
import no.ssb.klass.designer.testbench.pages.CreateCorrespondenceTablePage;
import no.ssb.klass.designer.testbench.pages.EditCorrespondenceTablePage;
import no.ssb.klass.designer.testbench.pages.EditCorrespondenceTablePage.EditCorrespondenceTableCodesSubpage;
import no.ssb.klass.designer.testbench.pages.EditCorrespondenceTablePage.EditCorrespondenceTableMetadataSubpage;

/**
 * Test creating and editing CorrespondenceTables
 */
public class CorrespondenceTableTestBench extends AbstractTestBench {
    @Test
    public void editCorrespondenceTableTest() {
        ClassificationListPage classificationListPage = getHomePage().selectOmrade("Region");
        classificationListPage.selectClassification(STANDARD_FOR_POLITIDISTRIKT);
        classificationListPage.selectVersion(POLITIDISTRIKT_2002);

        testCreateCorrespondenceTable(classificationListPage);
        testEditCorrespondenceTableMetadata(classificationListPage);
        testEditCorrespondenceTableCodes(classificationListPage);
    }

    private void testEditCorrespondenceTableCodes(ClassificationListPage classificationListPage) {
        EditCorrespondenceTableCodesSubpage correspondenceTableCodesPage;

        // Cancel when updated codes
        correspondenceTableCodesPage = classificationListPage.openCorrespondenceTable(
                POLITIDISTRIKT_2002_KOMMUNEINNDELING_2006).selectCodesTab(EditCorrespondenceTableCodesSubpage.class);
        correspondenceTableCodesPage.dragAndDrop("0101 - Halden", "01 - Oslo");
        correspondenceTableCodesPage.clickAvbrytAndAcceptConfirmationBox();

        // Filter source codes (Kilde)
        correspondenceTableCodesPage = classificationListPage.openCorrespondenceTable(
                POLITIDISTRIKT_2002_KOMMUNEINNDELING_2006).selectCodesTab(EditCorrespondenceTableCodesSubpage.class);
        correspondenceTableCodesPage.filterSourceCodes("oslo");
        assertEquals(2, correspondenceTableCodesPage.countRowsInSource());
        assertEquals(true, correspondenceTableCodesPage.hasSourceCode("01 - Oslo"));
        assertEquals(true, correspondenceTableCodesPage.hasSourceCode("0301 - Oslo"));
        correspondenceTableCodesPage.clickAvbryt();

        // Filter target codes (Mål)
        correspondenceTableCodesPage = classificationListPage.openCorrespondenceTable(
                POLITIDISTRIKT_2002_KOMMUNEINNDELING_2006).selectCodesTab(EditCorrespondenceTableCodesSubpage.class);
        correspondenceTableCodesPage.filterTargetCodes("halden");
        assertEquals(1, correspondenceTableCodesPage.countRowsInTarget());
        correspondenceTableCodesPage.clickAvbryt();

        // Create new correspondenceMap
        correspondenceTableCodesPage = classificationListPage.openCorrespondenceTable(
                POLITIDISTRIKT_2002_KOMMUNEINNDELING_2006).selectCodesTab(EditCorrespondenceTableCodesSubpage.class);
        correspondenceTableCodesPage.dragAndDrop("0101 - Halden", "01 - Oslo");
        correspondenceTableCodesPage.filterSourceCodes("oslo");
        assertEquals(3, correspondenceTableCodesPage.countRowsInSource());
        assertEquals(true, correspondenceTableCodesPage.hasSourceCode("0101 - Halden"));
        correspondenceTableCodesPage.saveAndHandleChangelog();
        correspondenceTableCodesPage.getBreadcrumb().clickCorrespondenceTable();

        // Delete created correspondenceMap
        correspondenceTableCodesPage = classificationListPage.openCorrespondenceTable(
                POLITIDISTRIKT_2002_KOMMUNEINNDELING_2006).selectCodesTab(EditCorrespondenceTableCodesSubpage.class);
        correspondenceTableCodesPage.deleteSourceCodeAndAcceptConfirmationBox("0101 - Halden");
        correspondenceTableCodesPage.filterSourceCodes("oslo");
        assertEquals(2, correspondenceTableCodesPage.countRowsInSource());
        correspondenceTableCodesPage.saveAndHandleChangelog();
        correspondenceTableCodesPage.getBreadcrumb().clickCorrespondenceTable();
    }

    private void testCreateCorrespondenceTable(ClassificationListPage classificationListPage) {
        CreateCorrespondenceTablePage correspondenceTablePage;

        // Cancel when no changes
        correspondenceTablePage = classificationListPage.createCorrespondenceTable(STANDARD_FOR_KOMMUNEINNDELING,
                KOMMUNEINNDELING_2002);
        correspondenceTablePage.clickAvbryt();

        // Cancel when updates
        correspondenceTablePage = classificationListPage.createCorrespondenceTable(STANDARD_FOR_KOMMUNEINNDELING,
                KOMMUNEINNDELING_2002);
        correspondenceTablePage.setBeskrivelse("beskrivelse");
        correspondenceTablePage.clickAvbrytAndAcceptConfirmationBox();

        // Create new correspondenceTable
        correspondenceTablePage = classificationListPage.createCorrespondenceTable(STANDARD_FOR_KOMMUNEINNDELING,
                KOMMUNEINNDELING_2002);
        correspondenceTablePage.setBeskrivelse("beskrivelse");
        EditCorrespondenceTablePage editCorrespondenceTablePage = correspondenceTablePage.save(
                EditCorrespondenceTablePage.class);
        // And cleanup (deleting the newly created correspondenceTable)
        editCorrespondenceTablePage.getBreadcrumb().clickCorrespondenceTable().deleteCorrespondenceTable(
                classificationListPage.getSelectedCorrespondenceTable());
    }

    private void testEditCorrespondenceTableMetadata(ClassificationListPage classificationListPage) {
        EditCorrespondenceTableMetadataSubpage correspondenceTableMetadataPage;

        // Cancel when no changes
        classificationListPage.openCorrespondenceTable(POLITIDISTRIKT_2002_KOMMUNEINNDELING_2006).clickAvbryt();

        // Cancel when updated metadata
        correspondenceTableMetadataPage = classificationListPage.openCorrespondenceTable(
                POLITIDISTRIKT_2002_KOMMUNEINNDELING_2006).selectMetadataTab(
                        EditCorrespondenceTableMetadataSubpage.class);
        correspondenceTableMetadataPage.setBeskrivelse("beskrivelse");
        classificationListPage = correspondenceTableMetadataPage.clickAvbrytAndAcceptConfirmationBox();

        // Edit metadata and save
        correspondenceTableMetadataPage = classificationListPage.openCorrespondenceTable(
                POLITIDISTRIKT_2002_KOMMUNEINNDELING_2006).selectMetadataTab(
                        EditCorrespondenceTableMetadataSubpage.class);
        correspondenceTableMetadataPage.setBeskrivelse("beskrivelse");
        correspondenceTableMetadataPage.saveAndHandleChangelog();
        assertEquals("beskrivelse", correspondenceTableMetadataPage.getBeskrivelse());
        correspondenceTableMetadataPage.setBeskrivelse("");
        correspondenceTableMetadataPage.saveAndHandleChangelog();
        correspondenceTableMetadataPage.getBreadcrumb().clickCorrespondenceTable();

        // Edit language nynorsk (metadata)
        correspondenceTableMetadataPage = classificationListPage.openCorrespondenceTable(
                POLITIDISTRIKT_2002_KOMMUNEINNDELING_2006).selectMetadataTab(
                        EditCorrespondenceTableMetadataSubpage.class);
        correspondenceTableMetadataPage.selectEditLanguages();
        correspondenceTableMetadataPage.selectLanguage(Language.NN);
        correspondenceTableMetadataPage.updateTranslationForBeskrivelse("beskrivelse nynorsk");
        correspondenceTableMetadataPage.saveAndHandleChangelog();
        assertEquals("beskrivelse nynorsk", correspondenceTableMetadataPage.getTranslationForBeskrivelse());
        correspondenceTableMetadataPage.updateTranslationForBeskrivelse("");
        correspondenceTableMetadataPage.saveAndHandleChangelog();
        correspondenceTableMetadataPage.getBreadcrumb().clickCorrespondenceTable();

        // Edit language english (metadata)
        correspondenceTableMetadataPage = classificationListPage.openCorrespondenceTable(
                POLITIDISTRIKT_2002_KOMMUNEINNDELING_2006).selectMetadataTab(
                        EditCorrespondenceTableMetadataSubpage.class);
        correspondenceTableMetadataPage.selectEditLanguages();
        correspondenceTableMetadataPage.selectLanguage(Language.EN);
        correspondenceTableMetadataPage.updateTranslationForBeskrivelse("beskrivelse english");
        correspondenceTableMetadataPage.saveAndHandleChangelog();
        assertEquals("beskrivelse english", correspondenceTableMetadataPage.getTranslationForBeskrivelse());
        correspondenceTableMetadataPage.updateTranslationForBeskrivelse("");
        correspondenceTableMetadataPage.saveAndHandleChangelog();
        correspondenceTableMetadataPage.getBreadcrumb().clickCorrespondenceTable();
    }
}
