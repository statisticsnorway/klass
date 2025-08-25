package no.ssb.klass.api.migration;

public class MigrationTestConfig {

    public static String getSourceHost() {
        return System.getProperty("source.service.host",
                System.getenv().getOrDefault("SOURCE_SERVICE_HOST", "http://localhost:8082"));
    }

    public static String getTargetHost() {
        return System.getProperty("target.service.host",
                System.getenv().getOrDefault("TARGET_SERVICE_HOST", "http://localhost:8080"));
    }
}
