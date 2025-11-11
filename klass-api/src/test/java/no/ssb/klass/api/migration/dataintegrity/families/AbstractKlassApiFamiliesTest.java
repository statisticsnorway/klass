package no.ssb.klass.api.migration.dataintegrity.families;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static no.ssb.klass.api.migration.MigrationTestUtils.generateRandomId;

import no.ssb.klass.api.migration.dataintegrity.AbstractKlassApiDataIntegrityTest;

import org.junit.jupiter.api.BeforeAll;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public abstract class AbstractKlassApiFamiliesTest extends AbstractKlassApiDataIntegrityTest {

    static Stream<Integer> rangeProviderClassificationFamilyIds() {
        return IntStream.rangeClosed(0, 30).boxed();
    }

    String getClassificationFamilyByIdPath(Integer id) {
        return "/" + CLASSIFICATION_FAMILIES + "/" + id;
    }

    String getClassificationFamiliesPath() {
        return "/" + CLASSIFICATION_FAMILIES;
    }

    static Map<String, Object> paramsSsbSection = new HashMap<>();

    static int randomSsbSectionId;

    @BeforeAll
    static void beforeAllClassificationFamiliesById() {
        randomSsbSectionId = generateRandomId(ssbSectionNames.size());
    }
}
