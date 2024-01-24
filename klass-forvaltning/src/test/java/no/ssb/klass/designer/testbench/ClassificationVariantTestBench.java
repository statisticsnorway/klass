package no.ssb.klass.designer.testbench;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import no.ssb.klass.core.model.Language;
import no.ssb.klass.designer.testbench.pages.ClassificationListPage;
import no.ssb.klass.designer.testbench.pages.CreateVariantPage;
import no.ssb.klass.designer.testbench.pages.EditVariantPage;
import no.ssb.klass.designer.testbench.pages.EditVariantPage.EditVariantCodesSubpage;
import no.ssb.klass.designer.testbench.pages.EditVariantPage.EditVariantMetadataSubpage;

/**
 * Test creating and editing ClassificationVariants
 */
public class ClassificationVariantTestBench extends AbstractTestBench {
    private static final String VARIANT_NAME = "testbench_variant";
    private static final String VARIANT_DISPLAY_NAME = "testbench_variant 2002 - variant av " + POLITIDISTRIKT_2002;

    @Test
    public void editClassificationVariantTest() {
        ClassificationListPage classificationListPage = getHomePage().selectOmrade("Region");
        classificationListPage.selectClassification(STANDARD_FOR_POLITIDISTRIKT);
        classificationListPage.selectVersion(POLITIDISTRIKT_2002);

        testCreateVariant(classificationListPage);
        testEditVariantMetadata(classificationListPage);
        testEditVariantCodes(classificationListPage);
        testDeleteVariant(classificationListPage);
    }

    private void testDeleteVariant(ClassificationListPage classificationListPage) {
        int variantsCount = classificationListPage.countVariants();
        classificationListPage.deleteVariant(VARIANT_DISPLAY_NAME);
        assertEquals(variantsCount - 1, classificationListPage.countVariants());
    }

    private void testEditVariantCodes(ClassificationListPage classificationListPage) {
        EditVariantCodesSubpage variantCodesPage;

        // Cancel when updated codes
        variantCodesPage = classificationListPage.openVariant(VARIANT_DISPLAY_NAME).selectCodesTab(
                EditVariantCodesSubpage.class);
        variantCodesPage.createGroupingCode("tb_code", "testbench");
        variantCodesPage.clickAvbrytAndAcceptConfirmationBox();

        // Create grouping code and drag code
        variantCodesPage = classificationListPage.openVariant(VARIANT_DISPLAY_NAME).selectCodesTab(
                EditVariantCodesSubpage.class);
        variantCodesPage.createGroupingCode("tb_code", "testbench");
        assertEquals(2, variantCodesPage.countRowsInVariant()); // Including new code editor
        variantCodesPage.dragAndDrop("01 - Oslo", "tb_code - testbench");
        // droping will open hierarchy and show ekstra editor, thus 4 elements
        assertEquals(4, variantCodesPage.countRowsInVariant()); // Including new code editor
        variantCodesPage.save();
        variantCodesPage.getBreadcrumb().clickVariant();

        // Translate grouping code (nynorsk)
        variantCodesPage = classificationListPage.openVariant(VARIANT_DISPLAY_NAME).selectCodesTab(
                EditVariantCodesSubpage.class);
        variantCodesPage.selectEditLanguages();
        variantCodesPage.selectLanguage(Language.NN);
        variantCodesPage.doAutomaticTranslationOfNynorsk();
        variantCodesPage.save();
        assertEquals(1, variantCodesPage.countRowsInTranslation());
        assertEquals("tb_code - testbench", variantCodesPage.getTranslationForSingleCode());
        variantCodesPage.getBreadcrumb().clickVariant();

        // Translate grouping code (engelsk)
        variantCodesPage = classificationListPage.openVariant(VARIANT_DISPLAY_NAME).selectCodesTab(
                EditVariantCodesSubpage.class);
        variantCodesPage.selectEditLanguages();
        variantCodesPage.selectLanguage(Language.EN);
        variantCodesPage.translateSingleCode("testbench english");
        variantCodesPage.save();
        assertEquals(1, variantCodesPage.countRowsInTranslation());
        assertEquals("tb_code - testbench english", variantCodesPage.getTranslationForSingleCode());
        variantCodesPage.getBreadcrumb().clickVariant();

        // Delete created code
        variantCodesPage = classificationListPage.openVariant(VARIANT_DISPLAY_NAME).selectCodesTab(
                EditVariantCodesSubpage.class);
        variantCodesPage.selectEditLanguages();
        variantCodesPage.deleteGroupingCodeAndAcceptConfirmationBox("tb_code - testbench");
        variantCodesPage.save();
        assertEquals(1, variantCodesPage.countRowsInVariant()); // Only new code editor
        assertEquals(0, variantCodesPage.countRowsInTranslation()); // Also deleted translation
        variantCodesPage.getBreadcrumb().clickCorrespondenceTable();
    }

    private void testCreateVariant(ClassificationListPage classificationListPage) {
        CreateVariantPage variantPage;

        // Cancel when no changes
        variantPage = classificationListPage.createVariant(VARIANT_NAME);
        variantPage.clickAvbryt();

        // Cancel when updates
        variantPage = classificationListPage.createVariant(VARIANT_NAME);
        variantPage.setBeskrivelse("beskrivelse");
        variantPage.clickAvbrytAndAcceptConfirmationBox();

        // Create new correspondenceTable
        variantPage = classificationListPage.createVariant(VARIANT_NAME);
        variantPage.setBeskrivelse("beskrivelse");
        variantPage.save(EditVariantPage.class).getBreadcrumb().clickVariant();

    }

    private void testEditVariantMetadata(ClassificationListPage classificationListPage) {
        EditVariantMetadataSubpage correspondenceTableMetadataPage;

        // Cancel when no changes
        classificationListPage.openVariant(VARIANT_DISPLAY_NAME).clickAvbryt();

        // Cancel when updated metadata
        correspondenceTableMetadataPage = classificationListPage.openVariant(VARIANT_DISPLAY_NAME).selectMetadataTab(
                EditVariantMetadataSubpage.class);
        correspondenceTableMetadataPage.setBeskrivelse("updated beskrivelse");
        classificationListPage = correspondenceTableMetadataPage.clickAvbrytAndAcceptConfirmationBox();

        // Edit metadata and save
        correspondenceTableMetadataPage = classificationListPage.openVariant(VARIANT_DISPLAY_NAME).selectMetadataTab(
                EditVariantMetadataSubpage.class);
        correspondenceTableMetadataPage.setBeskrivelse("updated beskrivelse");
        correspondenceTableMetadataPage.save();
        assertEquals("updated beskrivelse", correspondenceTableMetadataPage.getBeskrivelse());
        correspondenceTableMetadataPage.setBeskrivelse("beskrivelse");
        correspondenceTableMetadataPage.save();
        correspondenceTableMetadataPage.getBreadcrumb().clickCorrespondenceTable();

        // Edit language nynorsk (metadata)
        correspondenceTableMetadataPage = classificationListPage.openVariant(VARIANT_DISPLAY_NAME).selectMetadataTab(
                EditVariantMetadataSubpage.class);
        correspondenceTableMetadataPage.selectEditLanguages();
        correspondenceTableMetadataPage.selectLanguage(Language.NN);
        correspondenceTableMetadataPage.updateTranslationForBeskrivelse("beskrivelse nynorsk");
        correspondenceTableMetadataPage.save();
        assertEquals("beskrivelse nynorsk", correspondenceTableMetadataPage.getTranslationForBeskrivelse());
        correspondenceTableMetadataPage.updateTranslationForBeskrivelse("");
        correspondenceTableMetadataPage.save();
        correspondenceTableMetadataPage.getBreadcrumb().clickCorrespondenceTable();

        // Edit language english (metadata)
        correspondenceTableMetadataPage = classificationListPage.openVariant(VARIANT_DISPLAY_NAME).selectMetadataTab(
                EditVariantMetadataSubpage.class);
        correspondenceTableMetadataPage.selectEditLanguages();
        correspondenceTableMetadataPage.selectLanguage(Language.EN);
        correspondenceTableMetadataPage.updateTranslationForBeskrivelse("beskrivelse english");
        correspondenceTableMetadataPage.save();
        assertEquals("beskrivelse english", correspondenceTableMetadataPage.getTranslationForBeskrivelse());
        correspondenceTableMetadataPage.updateTranslationForBeskrivelse("");
        correspondenceTableMetadataPage.save();
        correspondenceTableMetadataPage.getBreadcrumb().clickCorrespondenceTable();
    }
}
