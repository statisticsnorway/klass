package no.ssb.klass.api.migration.dataintegrity.classifications;

import no.ssb.klass.api.migration.dataintegrity.AbstractKlassApiDataIntegrityTest;
import org.junit.jupiter.api.BeforeAll;

import java.util.HashMap;
import java.util.Map;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;

public class AbstractKlassApiClassifications extends AbstractKlassApiDataIntegrityTest {
    static Map<String, Object> paramsIncludeCodeLists = new HashMap<>();
    static Map<String, Object> paramsChangedSince = new HashMap<>();

    static String queryDate;

    @BeforeAll
    static void setUpClassifications() {
        queryDate = "2015-03-01T01:30:00.000-0200";
        paramsIncludeCodeLists.put(INCLUDE_CODE_LISTS, TRUE);
        paramsChangedSince.put(CHANGED_SINCE, queryDate);
    }
}
