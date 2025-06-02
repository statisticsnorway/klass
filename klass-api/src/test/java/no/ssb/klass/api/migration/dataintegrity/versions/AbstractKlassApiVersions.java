package no.ssb.klass.api.migration.dataintegrity.versions;

import no.ssb.klass.api.migration.dataintegrity.AbstractKlassApiDataIntegrityTest;
import org.junit.jupiter.api.BeforeAll;

import static no.ssb.klass.api.migration.MigrationTestConstants.VERSIONS;
import static no.ssb.klass.api.migration.MigrationTestUtils.generateRandomId;

public class AbstractKlassApiVersions extends AbstractKlassApiDataIntegrityTest {
    static Integer randomId;
    String getVersionByIdPath(Integer id) {
        return "/" + VERSIONS + "/" + id;
    }

    @BeforeAll
    static void beforeAllVersions() {
        randomId = generateRandomId(2000);
    }

}
