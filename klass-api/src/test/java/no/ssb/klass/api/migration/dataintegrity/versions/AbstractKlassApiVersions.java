package no.ssb.klass.api.migration.dataintegrity.versions;

import static no.ssb.klass.api.migration.MigrationTestConstants.VERSIONS;
import static no.ssb.klass.api.migration.MigrationTestUtils.generateRandomId;

import no.ssb.klass.api.migration.dataintegrity.AbstractKlassApiDataIntegrityTest;

import org.junit.jupiter.api.BeforeAll;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public abstract class AbstractKlassApiVersions extends AbstractKlassApiDataIntegrityTest {
    static Integer randomId;

    String getVersionByIdPath(Integer id) {
        return "/" + VERSIONS + "/" + id;
    }

    static int versionIds = 2100;

    static Stream<Integer> rangeProviderVersionIds() {
        return IntStream.rangeClosed(0, versionIds).boxed();
    }

    @BeforeAll
    static void beforeAllVersions() {
        randomId = generateRandomId(versionIds);
    }
}
