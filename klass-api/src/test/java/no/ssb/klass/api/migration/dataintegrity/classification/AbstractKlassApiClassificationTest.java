package no.ssb.klass.api.migration.dataintegrity.classification;

import no.ssb.klass.api.migration.dataintegrity.AbstractKlassApiDataIntegrityTest;

import java.util.HashMap;
import java.util.Map;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;

public abstract class AbstractKlassApiClassificationTest extends AbstractKlassApiDataIntegrityTest {
    static Map<String, Object> paramsIncludeFuture = new HashMap<>();

    String getClassificationByIdPath(Integer id) {
        return CLASSIFICATIONS_PATH + "/" + id;
    }
}
