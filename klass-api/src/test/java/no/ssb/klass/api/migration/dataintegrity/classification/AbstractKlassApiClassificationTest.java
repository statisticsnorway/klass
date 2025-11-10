package no.ssb.klass.api.migration.dataintegrity.classification;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;

import java.util.HashMap;
import java.util.Map;
import no.ssb.klass.api.migration.dataintegrity.AbstractKlassApiDataIntegrityTest;

public abstract class AbstractKlassApiClassificationTest extends AbstractKlassApiDataIntegrityTest {
  static Map<String, Object> paramsIncludeFuture = new HashMap<>();

  String getClassificationByIdPath(Integer id) {
    return CLASSIFICATIONS_PATH + "/" + id;
  }
}
