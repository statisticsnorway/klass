package no.ssb.klass.api.migration.dataintegrity;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiCorrespondenceTablesByIdTest extends AbstractKlassApiDataIntegrityTest {

    @Test
    void getOneCorrespondenceTableById() {
        int correspondenceTableId = 1111;

        String path = getCorrespondenceTableByIdPath(correspondenceTableId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi( path, null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi( path, null);

        assertThat(sourceResponse.getStatusCode()).withFailMessage(
                FAIL_MESSAGE, path, sourceResponse.getStatusCode(), targetResponse.getStatusCode()).isEqualTo(targetResponse.getStatusCode());

        if(sourceResponse.getStatusCode() != 200) {
            System.out.println(LOG_MESSAGE_STATUS_CODE + sourceResponse.getStatusCode());
            assertThat(compareError(correspondenceTableId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateItem(sourceResponse, targetResponse, pathNamesCorrespondenceTableById);
            validateLinks(sourceResponse, targetResponse, pathNamesCorrespondencesLinks);
            validateList(sourceResponse, targetResponse, CORRESPONDENCE_MAPS);
            validateList(sourceResponse, targetResponse, CHANGELOGS);
        }
    }

    @ParameterizedTest
    @MethodSource("rangeProvider")
    void getCorrespondenceTable(Integer correspondenceTableId) {

        String path = getCorrespondenceTableByIdPath(correspondenceTableId);
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi(path, null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi(path, null);

        assertThat(sourceResponse.getStatusCode()).withFailMessage(
                FAIL_MESSAGE, path, sourceResponse.getStatusCode(), targetResponse.getStatusCode()).isEqualTo(targetResponse.getStatusCode());

        if(sourceResponse.getStatusCode() != 200) {
            System.out.println(LOG_MESSAGE_STATUS_CODE + sourceResponse.getStatusCode());
            assertThat(compareError(correspondenceTableId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            System.out.println(LOG_MESSAGE_STATUS_CODE + sourceResponse.getStatusCode());

            validateItem(sourceResponse, targetResponse, pathNamesCorrespondenceTableById);
            validateLinks(sourceResponse, targetResponse, pathNamesCorrespondencesLinks);
            validateList(sourceResponse, targetResponse, CORRESPONDENCE_MAPS);
            validateList(sourceResponse, targetResponse, CHANGELOGS);
        }
    }

    static Stream<Integer> rangeProvider() {
        return IntStream.rangeClosed(0, 1500).boxed();
    }

    String getCorrespondenceTableByIdPath(Integer id) {
        return "/" + CORRESPONDENCE_TABLES + "/"  + id;
    }
}
