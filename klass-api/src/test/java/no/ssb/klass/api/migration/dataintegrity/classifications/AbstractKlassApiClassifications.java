package no.ssb.klass.api.migration.dataintegrity.classifications;

import io.restassured.response.Response;
import no.ssb.klass.api.migration.dataintegrity.AbstractKlassApiDataIntegrityTest;
import org.junit.jupiter.api.BeforeAll;

import java.util.HashMap;
import java.util.Map;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static no.ssb.klass.api.migration.MigrationTestUtils.validateItems;
import static no.ssb.klass.api.migration.MigrationTestUtils.validatePathListWithObjects;

public abstract class AbstractKlassApiClassifications extends AbstractKlassApiDataIntegrityTest {
    static Map<String, Object> paramsChangedSince = new HashMap<>();

    static String queryDate;

    static void iteratePages(int totalPages, Response sourceResponse, Response targetResponse, Map<String, Object> queryParams) {
        for(int i = 0; i < totalPages; i++) {
            validatePathListWithObjects(
                    sourceResponse, targetResponse, EMBEDDED_CLASSIFICATIONS, pathNamesClassificationsPage, ID);
            validateItems(sourceResponse, targetResponse, pathNamesClassificationsLinks);

            if(sourceResponse.path(LINKS_NEXT_HREF) == null) {
                return;
            }
            sourceResponse =
                    klassApiMigrationClient.getFromSourceApi(sourceResponse.path(LINKS_NEXT_HREF),queryParams, null);
            targetResponse = klassApiMigrationClient.getFromTargetApi(targetResponse.path(LINKS_NEXT_HREF), queryParams,null);

        }
    }

    @BeforeAll
    static void setUpClassifications() {
        queryDate = "2015-03-01T01:30:00.000-0200";
        paramsChangedSince.put(CHANGED_SINCE, queryDate);
    }
}
