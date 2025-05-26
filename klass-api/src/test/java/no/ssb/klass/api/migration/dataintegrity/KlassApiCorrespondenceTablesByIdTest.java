package no.ssb.klass.api.migration.dataintegrity;

import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiCorrespondenceTablesByIdTest extends AbstractKlassApiDataIntegrityTest {

    static Integer randomId;
    Object sourceField;
    Object targetField;

    @BeforeAll
    static void beforeAllVersions() {
        randomId = generateRandomId(2000);
    }

    @Test
    void getOneCorrespondenceTableById() {
        int correspondenceTableId = 1111;
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi( "/" + CORRESPONDENCE_TABLES + "/" + correspondenceTableId, null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi( "/" +  CORRESPONDENCE_TABLES + "/" + correspondenceTableId, null);

        if(sourceResponse.getStatusCode() != 200) {
            System.out.println(LOG_MESSAGE_STATUS_CODE + sourceResponse.getStatusCode());
            assertThat(compareError(correspondenceTableId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            for (String pathName : pathNamesVariantById) {
                sourceField = sourceResponse.path(pathName);
                targetField = targetResponse.path(pathName);
                System.out.println(sourceField + "->" + targetField);
                assertThat(sourceField).withFailMessage(FAIL_MESSAGE, pathName, sourceField, targetField).isEqualTo(targetField);
            }
        }
    }

    @ParameterizedTest
    @MethodSource("rangeProvider")
    void getCorrespondenceTable(Integer correspondenceTableId) {
        Response sourceResponse = klassApiMigrationClient.getFromSourceApi("/" + CORRESPONDENCE_TABLES + "/" + correspondenceTableId, null);
        Response targetResponse = klassApiMigrationClient.getFromTargetApi("/" + CORRESPONDENCE_TABLES + "/" + correspondenceTableId, null);

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
}
