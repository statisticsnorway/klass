package no.ssb.klass.api.migration.dataintegrity;

import io.restassured.response.Response;
import no.ssb.klass.api.migration.KlassApiMigrationClient;
import no.ssb.klass.api.migration.MigrationTestConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;

import java.time.format.DateTimeFormatter;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;

public abstract class AbstractKlassApiDataIntegrityTest {

    static KlassApiMigrationClient klassApiMigrationClient;

    static Response sourceResponseClassifications;
    static Response targetResponseClassifications;

    static int numClassifications;

    public static final String sourceHost = MigrationTestConfig.getSourceHost();

    public static final String targetHost = MigrationTestConfig.getTargetHost();

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    @BeforeAll
    static void beforeAll() {
        klassApiMigrationClient = new KlassApiMigrationClient();

        boolean sourceUp = klassApiMigrationClient.isApiAvailable(sourceHost);
        boolean targetUp = klassApiMigrationClient.isApiAvailable(targetHost);

        Assumptions.assumeTrue(sourceUp && targetUp, "One or both APIs are not available, skipping tests.");
        sourceResponseClassifications = klassApiMigrationClient.getFromSourceApi(CLASSIFICATIONS_PATH, null);
        targetResponseClassifications = klassApiMigrationClient.getFromTargetApi(CLASSIFICATIONS_PATH, null);

        numClassifications = sourceResponseClassifications.path(PAGE_TOTAL_ELEMENTS);
    }

    @AfterAll
    public static void cleanUp(){
        System.out.println("Cleanup after tests");
    }
}
