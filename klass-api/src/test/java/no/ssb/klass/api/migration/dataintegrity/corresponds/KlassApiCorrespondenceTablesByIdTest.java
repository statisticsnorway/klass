package no.ssb.klass.api.migration.dataintegrity.corresponds;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;


import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static no.ssb.klass.api.migration.MigrationTestUtils.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiCorrespondenceTablesByIdTest extends AbstractKlassApiCorrespondsTest {

    @Test
    void getOneCorrespondenceTableById() {
        int correspondenceTableId = 1111;

        String path = getCorrespondenceTableByIdPath(correspondenceTableId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi( path, null,null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi( path, null,null);

        assertThat(sourceResponse).withFailMessage("source api returned no content").isNotNull();

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            System.out.println(LOG_MESSAGE_STATUS_CODE + sourceResponse.getStatusCode());
            assertThat(compareError(correspondenceTableId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateItems(sourceResponse, targetResponse, pathNamesCorrespondenceTable);
            validateList(sourceResponse, targetResponse, CORRESPONDENCE_MAPS);
            validateList(sourceResponse, targetResponse, CHANGELOGS);
            validateList(sourceResponse, targetResponse, PUBLISHED);
        }
    }

    @ParameterizedTest
    @MethodSource("rangeProvider")
    void getCorrespondenceTable(Integer correspondenceTableId) {

        String path = getCorrespondenceTableByIdPath(correspondenceTableId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, null,null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, null,null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(correspondenceTableId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateItems(sourceResponse, targetResponse, pathNamesCorrespondenceTable);
            validateList(sourceResponse, targetResponse, CORRESPONDENCE_MAPS);
            validateList(sourceResponse, targetResponse, CHANGELOGS);
            validateList(sourceResponse, targetResponse, PUBLISHED);
        }
    }
}
