package no.ssb.klass.designer.testbench;

import static org.junit.Assert.*;

import org.junit.Test;

import no.ssb.klass.designer.testbench.pages.ClassificationListPage;
import no.ssb.klass.designer.testbench.pages.CreateCorrespondenceTablePage;
import no.ssb.klass.designer.testbench.pages.EditCorrespondenceTablePage;
import no.ssb.klass.designer.testbench.pages.EditCorrespondenceTablePage.EditChangeTableCodesSubpage;

/**
 * Test creating and editing ChangeTables. A ChangeTable is a CorrespondenceTable whose source and target are related to
 * the same Classification, and hence describing changes between two versions of same classification.
 * <p>
 * Metadata handling for a ChangeTable is identical to a CorrespondenceTable, hence only the codes tab is tested here.
 */
public class ChangeTableTestBench extends AbstractTestBench {
    @Test
    public void editCorrespondenceTableTest() {
        ClassificationListPage classificationListPage = getHomePage().selectOmrade("Region");
        classificationListPage.selectClassification(STANDARD_FOR_POLITIDISTRIKT);
        classificationListPage.selectVersion(POLITIDISTRIKT_2002);

        String changeTableName = testCreateChangeTable(classificationListPage);
        testEditChangeTableCodes(classificationListPage, changeTableName);
        testDeleteChangeTable(classificationListPage, changeTableName);
    }

    private String testCreateChangeTable(ClassificationListPage classificationListPage) {
        CreateCorrespondenceTablePage correspondenceTablePage = classificationListPage.createCorrespondenceTable(
                STANDARD_FOR_POLITIDISTRIKT, POLITIDISTRIKT_2016);
        correspondenceTablePage.save(EditCorrespondenceTablePage.class).getBreadcrumb().clickCorrespondenceTable();
        return classificationListPage.getSelectedCorrespondenceTable();
    }

    private void testEditChangeTableCodes(ClassificationListPage classificationListPage, String changeTableName) {
        EditChangeTableCodesSubpage changeTableCodesPage;

        // Cancel when no changes
        classificationListPage.openCorrespondenceTable(changeTableName).clickAvbryt();

        // Cancel when updated codes
        changeTableCodesPage = classificationListPage.openCorrespondenceTable(changeTableName)
                .selectCodesTabForChangeTable();
        changeTableCodesPage.clickOpprettNyKorrespondanse();
        changeTableCodesPage.dragAndDropSource("01 - Oslo");
        changeTableCodesPage.clickAvbrytAndAcceptConfirmationBox();

        // Filter source codes (Kilde)
        changeTableCodesPage = classificationListPage.openCorrespondenceTable(changeTableName)
                .selectCodesTabForChangeTable();
        changeTableCodesPage.filterSourceCodes("oslo");
        assertEquals(1, changeTableCodesPage.countRowsInSource());
        changeTableCodesPage.clickAvbryt();

        // Filter target codes (Mål)
        changeTableCodesPage = classificationListPage.openCorrespondenceTable(changeTableName)
                .selectCodesTabForChangeTable();
        changeTableCodesPage.filterTargetCodes("oslo");
        assertEquals(1, changeTableCodesPage.countRowsInTarget());
        changeTableCodesPage.clickAvbryt();

        // Create new correspondenceMap
        changeTableCodesPage = classificationListPage.openCorrespondenceTable(changeTableName)
                .selectCodesTabForChangeTable();
        changeTableCodesPage.clickOpprettNyKorrespondanse();
        changeTableCodesPage.dragAndDropSource("01 - Oslo");
        changeTableCodesPage.dragAndDropTarget("p01 - Oslo");
        changeTableCodesPage.save();
        assertEquals(1, changeTableCodesPage.countRowsInMap());
        changeTableCodesPage.getBreadcrumb().clickCorrespondenceTable();

        // Delete created correspondenceMap
        changeTableCodesPage = classificationListPage.openCorrespondenceTable(changeTableName)
                .selectCodesTabForChangeTable();
        changeTableCodesPage.deleteCorrespondenceMapAndAcceptConfirmationBox("01");
        changeTableCodesPage.save();
        assertEquals(0, changeTableCodesPage.countRowsInMap());
        changeTableCodesPage.getBreadcrumb().clickCorrespondenceTable();
    }

    private void testDeleteChangeTable(ClassificationListPage classificationListPage, String changeTableName) {
        int correspondenceTablesCount = classificationListPage.countCorrespondenceTables();
        classificationListPage.deleteCorrespondenceTable(changeTableName);
        assertEquals(correspondenceTablesCount - 1, classificationListPage.countCorrespondenceTables());
    }
}
