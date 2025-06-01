package no.ssb.klass.api.migration.dataintegrity.corresponds;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static no.ssb.klass.api.migration.MigrationTestUtils.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiClassificationCorrespondsJsonTest extends AbstractKlassApiCorrespondsTest {

    @Test
    void getOneClassificationCorrespondence(){
        int classificationId = 131;
        System.out.println("Start test for ID " + classificationId + " at " + Instant.now());

        String path = getCorrespondsPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsTargetIdAndDateFrom,null);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsTargetIdAndDateFrom,null);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateList(sourceResponse, targetResponse, CORRESPONDENCE_ITEMS);
        }
    }
}
