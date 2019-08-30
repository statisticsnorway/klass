package no.ssb.klass.designer.testbench;

import static org.junit.Assert.*;

import org.junit.Test;

import no.ssb.klass.core.model.ClassificationType;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.designer.testbench.pages.ClassificationListPage;
import no.ssb.klass.designer.testbench.pages.CreateClassificationPage;
import no.ssb.klass.designer.testbench.pages.EditClassificationPage;

/**
 * Test creating and editing Classifications
 */
public class ClassificationTestBench extends AbstractTestBench {
    @Test
    public void editClassificationTest() {
        ClassificationListPage classificationListPage = getHomePage().selectOmrade("Region");

        testCreateClassification(classificationListPage);
        testEditClassification(classificationListPage);
    }

    private void testCreateClassification(ClassificationListPage classificationListPage) {
        CreateClassificationPage classificationPage;

        // Cancel when no changes
        classificationPage = classificationListPage.createClassification();
        classificationPage.clickAvbryt();

        // Cancel when updates
        classificationPage = classificationListPage.createClassification();
        classificationPage.setBeskrivelse("beskrivelse");
        classificationPage.clickAvbrytAndAcceptConfirmationBox();

        // Create new classification
        classificationPage = classificationListPage.createClassification();
        // Bug in vaadin testbench 4.1.0.beta2 must select first value in a ComboBox, see
        // https://dev.vaadin.com/ticket/15316 and https://vaadin.com/forum#!/thread/7966894.
        // Hence below does not work with ClassificationType.CLASSIFICATION, since it comes second in dropdown of
        // ComboBox
        classificationPage.setKodeverksType(ClassificationType.CODELIST);
        classificationPage.setBeskrivelse("beskrivelse");
        classificationPage.setTittel("tittel");
        classificationPage.setEnhet("Familie");
        EditClassificationPage editClassificationPage = classificationPage.save(EditClassificationPage.class);
        // And cleanup (deleting the newly created classification)
        editClassificationPage.getBreadcrumb().clickClassification().deleteClassification(classificationListPage
                .getSelectedClassification());
    }

    private void testEditClassification(ClassificationListPage classificationListPage) {
        // Cancel edit with no changes
        EditClassificationPage classificationPage = classificationListPage.openClassification(
                STANDARD_FOR_POLITIDISTRIKT);
        classificationListPage = classificationPage.clickAvbryt();

        // Cancel edit with changes
        classificationPage = classificationListPage.openClassification(STANDARD_FOR_POLITIDISTRIKT);
        classificationPage.changePrimaryLanguage(Language.EN);
        classificationListPage = classificationPage.clickAvbrytAndAcceptConfirmationBox();

        // Edit and save
        classificationPage = classificationListPage.openClassification(STANDARD_FOR_POLITIDISTRIKT);
        classificationPage.changePrimaryLanguage(Language.EN);
        classificationPage.save();
        assertEquals(Language.EN.getDisplayName(), classificationPage.getPrimaryLanguage());

        // And revert change and save
        classificationPage.changePrimaryLanguage(Language.NB);
        classificationPage.save();
    }
}
