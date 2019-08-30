package no.ssb.klass.designer.testbench;

import static org.junit.Assert.*;

import org.junit.Test;

import no.ssb.klass.designer.testbench.pages.ClassificationListPage;
import no.ssb.klass.designer.testbench.pages.GlobalFilterPage;

public class ListClassificationsTestBench extends AbstractTestBench {

    @Test
    public void listClassificationsTest() {
        ClassificationListPage listPage = getHomePage().selectOmrade("Region");

        // Filter
        assertEquals(2, listPage.countClassifications());
        listPage.filterClassifications("politi");
        assertEquals(1, listPage.countClassifications());
        listPage.clearClassificationsFilter();
        assertEquals(2, listPage.countClassifications());

        // Global filter
        assertEquals(2, listPage.countClassifications());
        GlobalFilterPage globalFilter = listPage.getGlobalFilter();
        globalFilter.selectKlassifikasjoner();
        assertEquals(2, listPage.countClassifications());
        globalFilter.selectKodelister();
        assertEquals(0, listPage.countClassifications());
        globalFilter.selectAlleTyperKodeverk();
        assertEquals(2, listPage.countClassifications());
        globalFilter.selectSeksjon("320");
        assertEquals(2, listPage.countClassifications());
        globalFilter.selectAlleSeksjoner();
        assertEquals(2, listPage.countClassifications());

        // Select
        assertEquals(2, listPage.countClassifications());
        listPage.selectClassification(STANDARD_FOR_POLITIDISTRIKT);
        assertEquals(2, listPage.countVersions());
        listPage.selectVersion(POLITIDISTRIKT_2002);
        assertEquals(1, listPage.countVariants());

        // Delete
        listPage.deleteVariant(POLITIDISTRIKT_2002_KOMMUNEINNDELING_2006);
        assertEquals(0, listPage.countVariants());
        listPage.deleteVersion(POLITIDISTRIKT_2002);
        assertEquals(1, listPage.countVersions());
        listPage.deleteClassification(STANDARD_FOR_POLITIDISTRIKT);
        assertEquals(1, listPage.countClassifications());
    }
}
