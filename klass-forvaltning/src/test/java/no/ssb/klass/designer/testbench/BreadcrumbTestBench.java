package no.ssb.klass.designer.testbench;

import static org.junit.Assert.*;

import org.junit.Test;

import no.ssb.klass.designer.testbench.pages.ClassificationFamilyPage;
import no.ssb.klass.designer.testbench.pages.ClassificationListPage;
import no.ssb.klass.designer.testbench.pages.EditClassificationPage;
import no.ssb.klass.designer.testbench.pages.EditCorrespondenceTablePage;
import no.ssb.klass.designer.testbench.pages.EditVersionPage;

public class BreadcrumbTestBench extends AbstractTestBench {
    @Test
    public void breadcrumbTest() {
        ClassificationListPage classificationListPage = getHomePage().selectOmrade("Region");

        // Classification
        EditClassificationPage classificationPage = classificationListPage.openClassification(
                STANDARD_FOR_POLITIDISTRIKT);
        ClassificationFamilyPage classificationFamilyPage = classificationPage.getBreadcrumb().clickHome();
        classificationListPage = classificationFamilyPage.selectOmrade("Region");
        classificationPage = classificationListPage.openClassification(STANDARD_FOR_POLITIDISTRIKT);
        classificationListPage = classificationPage.getBreadcrumb().clickOmrade();
        classificationPage = classificationListPage.openClassification(STANDARD_FOR_POLITIDISTRIKT);
        classificationListPage = classificationPage.getBreadcrumb().clickClassification();
        assertEquals(STANDARD_FOR_POLITIDISTRIKT, classificationListPage.getSelectedClassification());

        // Version
        classificationListPage.selectClassification(STANDARD_FOR_POLITIDISTRIKT);
        EditVersionPage versionPage = classificationListPage.openVersion(POLITIDISTRIKT_2002);
        classificationListPage = versionPage.getBreadcrumb().clickVersion();
        assertEquals(POLITIDISTRIKT_2002, classificationListPage.getSelectedVersion());

        // Variant/CorrespondenceTable
        classificationListPage.selectClassification(STANDARD_FOR_POLITIDISTRIKT);
        classificationListPage.selectVersion(POLITIDISTRIKT_2002);
        EditCorrespondenceTablePage correspondenceTablePage =
                classificationListPage.openCorrespondenceTable(POLITIDISTRIKT_2002_KOMMUNEINNDELING_2006);
        correspondenceTablePage.getBreadcrumb().clickCorrespondenceTable();
        assertEquals(POLITIDISTRIKT_2002_KOMMUNEINNDELING_2006, classificationListPage
                .getSelectedCorrespondenceTable());
    }
}
