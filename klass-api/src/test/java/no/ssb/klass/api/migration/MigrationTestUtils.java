package no.ssb.klass.api.migration;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MigrationTestUtils {

    public static Map<Object, Map<String, Object>> mapById(List<Map<String, Object>> versions) {
        return versions.stream()
                .collect(Collectors.toMap(v -> v.get("id"), Function.identity()));
    }
}
