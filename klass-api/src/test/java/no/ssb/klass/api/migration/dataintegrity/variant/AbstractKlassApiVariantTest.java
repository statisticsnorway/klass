package no.ssb.klass.api.migration.dataintegrity.variant;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static no.ssb.klass.api.migration.MigrationTestUtils.generateRandomId;

import no.ssb.klass.api.migration.dataintegrity.AbstractKlassApiDataIntegrityTest;

import org.junit.jupiter.api.BeforeAll;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractKlassApiVariantTest extends AbstractKlassApiDataIntegrityTest {

    static Integer randomId;

    static String variantNameId84 = "Klimagasser";
    static String variantDateId84 = "2015-01-01";

    static Map<String, Object> paramsVariantDate = new HashMap<>();

    static Map<String, Object> paramsVariantDateFrom = new HashMap<>();

    @BeforeAll
    static void beforeAllVariant() {

        randomId = generateRandomId(2000);

        paramsVariantDate.put(VARIANT_NAME, variantNameId84);
        paramsVariantDate.put(DATE, variantDateId84);
        paramsVariantDateFrom.put(VARIANT_NAME, variantNameId84);
        paramsVariantDateFrom.put(FROM, variantDateId84);
    }

    String getVariantAtPath(Integer id) {
        return CLASSIFICATIONS_PATH + "/" + id + "/" + VARIANT_AT;
    }

    String getVariantPath(Integer id) {
        return CLASSIFICATIONS_PATH + "/" + id + "/" + VARIANT;
    }

    String getVariantByIdPath(Integer id) {
        return "/" + VARIANTS + "/" + id;
    }
}
