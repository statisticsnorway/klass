package no.ssb.klass.api.migration.dataintegrity.corresponds;

import io.restassured.response.Response;
import no.ssb.klass.api.migration.dataintegrity.AbstractKlassApiDataIntegrityTest;
import org.junit.jupiter.api.BeforeAll;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;

public class AbstractKlassApiCorrespondsTest extends AbstractKlassApiDataIntegrityTest {

    String getCorrespondsAtPath(Integer id) {
        return CLASSIFICATIONS_PATH + "/"+ id + "/" + CORRESPONDS_AT;
    }

    String getCorrespondsPath(Integer id) {
        return CLASSIFICATIONS_PATH + "/"+ id + "/" + CORRESPONDS;
    }

    static Map<String, Object> paramsTargetIdAndDate = new HashMap<>();

    static Map<String, Object> paramsTargetIdAndDateFrom = new HashMap<>();

    static Integer targetClassificationIdValue;
    static String fromDate;

    static String date;

    Response sourceResponse;
    Response targetResponse;

    static Stream<Integer> rangeProvider() {
        return IntStream.rangeClosed(0, 1500).boxed();
    }

    String getCorrespondenceTableByIdPath(Integer id) {
        return "/" + CORRESPONDENCE_TABLES + "/"  + id;
    }

    @BeforeAll
    static void beforeAllCorrespondence() {
        date = "2018-01-01";
        targetClassificationIdValue = 103;
        paramsTargetIdAndDate.put(TARGET_CLASSIFICATION_ID, targetClassificationIdValue);
        paramsTargetIdAndDate.put(DATE, date);

        fromDate = "2020-01-01";
        targetClassificationIdValue = 103;
        paramsTargetIdAndDateFrom.put(TARGET_CLASSIFICATION_ID, targetClassificationIdValue);
        paramsTargetIdAndDateFrom.put(FROM, fromDate);

    }
}
