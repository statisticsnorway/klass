package no.ssb.klass.api.migration.dataintegrity.corresponds;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static no.ssb.klass.api.migration.MigrationTestConstants.TEXT_CSV;
import static no.ssb.klass.api.migration.MigrationTestUtils.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiClassificationCorrespondsAtCsvTest extends AbstractKlassApiCorrespondsTest {

    @Test
    void getOneClassificationCorrespondenceAt(){
        int classificationId = 131;
        System.out.println("Start test for ID " + classificationId + " at " + Instant.now());

        String path = getCorrespondsAtPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsTargetIdAndDate,TEXT_CSV);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsTargetIdAndDate,TEXT_CSV);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateCSVDocument(path, sourceResponse, targetResponse);
        }
    }
}
