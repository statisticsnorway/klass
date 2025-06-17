package no.ssb.klass.api.migration.dataintegrity.search;

import no.ssb.klass.api.migration.dataintegrity.AbstractKlassApiDataIntegrityTest;
import org.junit.jupiter.api.BeforeAll;

import java.util.HashMap;
import java.util.Map;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;

public abstract class AbstractKlassApiSearchTest extends AbstractKlassApiDataIntegrityTest {

    static Map<String, Object> paramsQuery = new HashMap<>();
    static Map<String, Object> paramsQuerySsbSection = new HashMap<>();
    static Map<String, Object> paramsQueryIncludeCodeLists = new HashMap<>();

    String getSearchPath() {
        return CLASSIFICATIONS_PATH + "/" + SEARCH;
    }

    static String searchWord = "kommuner";

    @BeforeAll
    static void beforeAllSearch() {
        paramsQuery.put(QUERY, searchWord);
        paramsQueryIncludeCodeLists.putAll(
                Map.of(
                        QUERY, searchWord,
                        INCLUDE_CODE_LISTS, TRUE

                )
        );
        paramsQuerySsbSection.putAll(
                Map.of(
                QUERY, searchWord,
                SSB_SECTION, section320
                )
        );
    }
}
