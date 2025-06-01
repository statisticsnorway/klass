package no.ssb.klass.api.migration.dataintegrity.classification;

import io.restassured.response.Response;
import no.ssb.klass.api.migration.dataintegrity.AbstractKlassApiDataIntegrityTest;
import org.junit.jupiter.api.BeforeAll;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;

public abstract class AbstractKlassApiClassificationTest extends AbstractKlassApiDataIntegrityTest {
    Response sourceResponse;
    Response targetResponse;

    static Map<String, Object> paramsLanguageNn = new HashMap<>();
    static Map<String, Object> paramsLanguageEn = new HashMap<>();
    static Map<String, Object> paramsIncludeFuture = new HashMap<>();

    static Stream<Integer> rangeProviderClassificationIds() {
        return IntStream.rangeClosed(0, lastClassificationId).boxed();
    }

    String getClassificationByIdPath(Integer id) {
        return CLASSIFICATIONS_PATH + "/" + id;
    }

    @BeforeAll
    static void beforeAllClassification() {
        paramsLanguageEn.put(LANGUAGE, EN);
        paramsLanguageNn.put(LANGUAGE, NN);
        paramsIncludeFuture.put(INCLUDE_FUTURE, TRUE);
    }
}
