package no.ssb.klass.designer.testbench;

import static org.junit.Assert.*;

import org.junit.Test;

import no.ssb.klass.designer.testbench.pages.ClassificationFamilyPage;
import no.ssb.klass.designer.testbench.pages.GlobalFilterPage;

/**
 * Testing home page (the page where classification families may be selected)
 */
public class ClassificationFamilyTestBench extends AbstractTestBench {
    @Test
    public void classificationFamilyTest() {
        ClassificationFamilyPage classificationFamilyPage = getHomePage();

        // Global filter
        assertEquals(2, classificationFamilyPage.countClassificationsForOmrade("Region"));
        GlobalFilterPage globalFilter = classificationFamilyPage.getGlobalFilter();
        globalFilter.selectKlassifikasjoner();
        assertEquals(2, classificationFamilyPage.countClassificationsForOmrade("Region"));
        globalFilter.selectKodelister();
        assertEquals(0, classificationFamilyPage.countClassificationsForOmrade("Region"));
        globalFilter.selectAlleTyperKodeverk();
        assertEquals(2, classificationFamilyPage.countClassificationsForOmrade("Region"));
        globalFilter.selectSeksjon("320");
        assertEquals(2, classificationFamilyPage.countClassificationsForOmrade("Region"));
        globalFilter.selectAlleSeksjoner();
        assertEquals(2, classificationFamilyPage.countClassificationsForOmrade("Region"));
    }
}
