package no.ssb.klass.api.migration;

import static no.ssb.klass.api.migration.MigrationTestConstants.DATA_SSB_HOST;
import static no.ssb.klass.api.migration.MigrationTestConstants.LOCAL_TARGET_HOST;

public class MigrationTestConfig {

    public static String getSourceHost() {
        System.setProperty("source.service.host", DATA_SSB_HOST);
        return System.getProperty("source.service.host",
                System.getenv().getOrDefault("SOURCE_SERVICE_HOST", "http://localhost:8082"));
    }

    public static String getTargetHost() {
        System.setProperty("target.service.host", LOCAL_TARGET_HOST);
        return System.getProperty("target.service.host",
                System.getenv().getOrDefault("TARGET_SERVICE_HOST", "http://localhost:8080"));
    }
}
