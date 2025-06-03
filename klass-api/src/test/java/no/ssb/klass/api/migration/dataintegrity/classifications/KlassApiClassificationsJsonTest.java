package no.ssb.klass.api.migration.dataintegrity.classifications;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static no.ssb.klass.api.migration.MigrationTestUtils.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiClassificationsJsonTest extends AbstractKlassApiClassifications {

    @Test
    void getClassifications(){
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(CLASSIFICATIONS_PATH, null,null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(CLASSIFICATIONS_PATH, null,null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), CLASSIFICATIONS_PATH);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();
        }
        else {
            validateObject(sourceResponse, targetResponse, PAGE);
            int totalPages = sourceResponse.path(PAGE_TOTAL_ELEMENTS);
            iteratePages(totalPages, sourceResponse, targetResponse, null);

        }
    }

    @Test
    void getClassificationsIncludeCodeLists() {
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(CLASSIFICATIONS_PATH, paramsIncludeCodeLists,null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(CLASSIFICATIONS_PATH, paramsIncludeCodeLists,null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), CLASSIFICATIONS_PATH);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();
        } else {
            validateObject(sourceResponse, targetResponse, PAGE);
            int totalPages = sourceResponse.path(PAGE_TOTAL_ELEMENTS);
            iteratePages(totalPages, sourceResponse, targetResponse, paramsIncludeCodeLists);

        }
    }

    @Test
    void getClassificationsChangedSince(){
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(CLASSIFICATIONS_PATH, paramsChangedSince,null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(CLASSIFICATIONS_PATH, paramsChangedSince,null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), CLASSIFICATIONS_PATH);

        if (sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(null, sourceResponse, targetResponse)).isTrue();
        } else {

            validateObject(sourceResponse, targetResponse, PAGE);
            int totalPages = sourceResponse.path(PAGE_TOTAL_ELEMENTS);
            iteratePages(totalPages, sourceResponse, targetResponse, paramsChangedSince);

        }
    }
}
