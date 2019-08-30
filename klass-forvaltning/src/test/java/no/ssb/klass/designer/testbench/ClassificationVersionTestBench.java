package no.ssb.klass.designer.testbench;

import static org.junit.Assert.*;

import org.junit.Test;

import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.util.DateRange;
import no.ssb.klass.designer.testbench.pages.ClassificationListPage;
import no.ssb.klass.designer.testbench.pages.CreateVersionPage;
import no.ssb.klass.designer.testbench.pages.EditVersionPage;
import no.ssb.klass.designer.testbench.pages.EditVersionPage.EditVersionCodesSubpage;
import no.ssb.klass.designer.testbench.pages.EditVersionPage.EditVersionMetadataSubpage;

/**
 * Test creating and editing ClassificationVersions
 */
public class ClassificationVersionTestBench extends AbstractTestBench {
    @Test
    public void editVersionTest() {
        ClassificationListPage classificationListPage = getHomePage().selectOmrade("Region");
        classificationListPage.selectClassification(STANDARD_FOR_POLITIDISTRIKT);

        testCreateVersion(classificationListPage);
        testEditVersionMetadata(classificationListPage);
        testEditVersionCodes(classificationListPage);
    }

    private void testCreateVersion(ClassificationListPage classificationListPage) {
        CreateVersionPage versionPage;

        // Cancel when no changes
        versionPage = classificationListPage.createVersion(DateRange.create("1900-01-01", "1901-01-01"));
        versionPage.clickAvbryt();

        // Cancel when updates
        versionPage = classificationListPage.createVersion(DateRange.create("1900-01-01", "1901-01-01"));
        versionPage.setDescription("description");
        versionPage.clickAvbrytAndAcceptConfirmationBox();

        // Create new version
        versionPage = classificationListPage.createVersion(DateRange.create("1900-01-01", "1901-01-01"));
        versionPage.setDescription("description");
        EditVersionPage editVersionPage = versionPage.save(EditVersionPage.class);
        // And cleanup (deleting the newly created version)
        editVersionPage.getBreadcrumb().clickVersion().deleteVersion(classificationListPage.getSelectedVersion());
    }

    private void testEditVersionMetadata(ClassificationListPage classificationListPage) {
        EditVersionMetadataSubpage versionMetadataPage;

        // Cancel when no changes
        classificationListPage.openVersion(POLITIDISTRIKT_2002).clickAvbryt();

        // Cancel when updated metadata
        versionMetadataPage = classificationListPage.openVersion(POLITIDISTRIKT_2002).selectMetadataTab(
                EditVersionMetadataSubpage.class);
        versionMetadataPage.setLovhjemmel("testbench data");
        classificationListPage = versionMetadataPage.clickAvbrytAndAcceptConfirmationBox();

        // Edit metadata and save
        versionMetadataPage = classificationListPage.openVersion(POLITIDISTRIKT_2002).selectMetadataTab(
                EditVersionMetadataSubpage.class);
        versionMetadataPage.setLovhjemmel("testbench data");
        versionMetadataPage.saveAndHandleChangelog();
        assertEquals("testbench data", versionMetadataPage.getLovHjemmel());
        versionMetadataPage.setLovhjemmel("");
        versionMetadataPage.saveAndHandleChangelog();
        versionMetadataPage.getBreadcrumb().clickVersion();

        // Edit language nynorsk (metadata)
        versionMetadataPage = classificationListPage.openVersion(POLITIDISTRIKT_2002).selectMetadataTab(
                EditVersionMetadataSubpage.class);
        versionMetadataPage.selectEditLanguages();
        versionMetadataPage.selectLanguage(Language.NN);
        versionMetadataPage.updateTranslationForLovhjemmel("Lovhjemmel nynorsk");
        versionMetadataPage.saveAndHandleChangelog();
        assertEquals("Lovhjemmel nynorsk", versionMetadataPage.getTranslationForLovhjemmel());
        versionMetadataPage.updateTranslationForLovhjemmel("");
        versionMetadataPage.saveAndHandleChangelog();
        versionMetadataPage.getBreadcrumb().clickVersion();

        // Edit language english (metadata)
        versionMetadataPage = classificationListPage.openVersion(POLITIDISTRIKT_2002).selectMetadataTab(
                EditVersionMetadataSubpage.class);
        versionMetadataPage.selectEditLanguages();
        versionMetadataPage.selectLanguage(Language.EN);
        versionMetadataPage.updateTranslationForLovhjemmel("Lovhjemmel english");
        versionMetadataPage.saveAndHandleChangelog();
        assertEquals("Lovhjemmel english", versionMetadataPage.getTranslationForLovhjemmel());
        versionMetadataPage.updateTranslationForLovhjemmel("");
        versionMetadataPage.saveAndHandleChangelog();
        versionMetadataPage.getBreadcrumb().clickVersion();
    }

    private void testEditVersionCodes(ClassificationListPage classificationListPage) {
        EditVersionCodesSubpage versionCodesPage;
        // Cancel updated codes
        versionCodesPage = classificationListPage.openVersion(POLITIDISTRIKT_2002).selectCodesTab(
                EditVersionCodesSubpage.class);
        versionCodesPage.updateCode("01 - Oslo", "Oslooo");
        classificationListPage = versionCodesPage.clickAvbrytAndAcceptConfirmationBox();

        // Edit code and save
        versionCodesPage = classificationListPage.openVersion(POLITIDISTRIKT_2002).selectCodesTab(
                EditVersionCodesSubpage.class);
        versionCodesPage.updateCode("01 - Oslo", "Oslo_no");
        versionCodesPage.saveAndHandleChangelog();
        assertEquals(true, versionCodesPage.hasCode("01 - Oslo_no"));
        versionCodesPage.updateCode("01 - Oslo_no", "Oslo");
        versionCodesPage.saveAndHandleChangelog();
        versionCodesPage.getBreadcrumb().clickVersion();

        // Add level
        versionCodesPage = classificationListPage.openVersion(POLITIDISTRIKT_2002).selectCodesTab(
                EditVersionCodesSubpage.class);
        versionCodesPage.addLevel();
        versionCodesPage.saveAndHandleChangelog();
        assertEquals(2, versionCodesPage.getNumberOfLevels());
        versionCodesPage.getBreadcrumb().clickVersion();

        // Delete level
        versionCodesPage = classificationListPage.openVersion(POLITIDISTRIKT_2002).selectCodesTab(
                EditVersionCodesSubpage.class);
        versionCodesPage.deleteLevelAndAcceptConfirmationBox();
        versionCodesPage.saveAndHandleChangelog();
        assertEquals(1, versionCodesPage.getNumberOfLevels());
        versionCodesPage.getBreadcrumb().clickVersion();

        // Edit language nynorsk (codes)
        versionCodesPage = classificationListPage.openVersion(POLITIDISTRIKT_2002).selectCodesTab(
                EditVersionCodesSubpage.class);
        versionCodesPage.selectEditLanguages();
        versionCodesPage.selectLanguage(Language.NN);
        versionCodesPage.updateTranslationForCode("01 - Oslo", "Oslo_nn");
        versionCodesPage.saveAndHandleChangelog();
        assertEquals(true, versionCodesPage.hasTranslationCode("01 - Oslo_nn"));
        versionCodesPage.updateTranslationForCode("01 - Oslo_nn", "Oslo");
        versionCodesPage.saveAndHandleChangelog();
        versionCodesPage.getBreadcrumb().clickVersion();

        // Edit language english (codes)
        versionCodesPage = classificationListPage.openVersion(POLITIDISTRIKT_2002).selectCodesTab(
                EditVersionCodesSubpage.class);
        versionCodesPage.selectEditLanguages();
        versionCodesPage.selectLanguage(Language.EN);
        versionCodesPage.updateTranslationForCode("01 - Oslo", "Oslo_en");
        versionCodesPage.saveAndHandleChangelog();
        assertEquals(true, versionCodesPage.hasTranslationCode("01 - Oslo_en"));
        versionCodesPage.updateTranslationForCode("01 - Oslo_en", "Oslo");
        versionCodesPage.saveAndHandleChangelog();
        versionCodesPage.getBreadcrumb().clickVersion();
    }
}
