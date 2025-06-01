package no.ssb.klass.api.migration.dataintegrity.classification;

import io.restassured.response.Response;
import no.ssb.klass.api.migration.dataintegrity.AbstractKlassApiDataIntegrityTest;
import org.junit.jupiter.api.BeforeAll;

import java.util.HashMap;
import java.util.Map;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;

public abstract class AbstractKlassApiClassificationTest extends AbstractKlassApiDataIntegrityTest {
    Response sourceResponse;
    Response targetResponse;

    static Map<String, Object> paramsIncludeFuture = new HashMap<>();

    String getClassificationByIdPath(Integer id) {
        return CLASSIFICATIONS_PATH + "/" + id;
    }

    @BeforeAll
    static void beforeAllClassification() {
        paramsIncludeFuture.put(INCLUDE_FUTURE, TRUE);
    }
}
