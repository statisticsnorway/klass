package no.ssb.klass.api.migration.dataintegrity;

import io.restassured.response.Response;
import no.ssb.klass.api.migration.KlassApiMigrationClient;
import no.ssb.klass.api.migration.MigrationTestConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;

import java.util.ArrayList;
import java.util.List;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;

public abstract class AbstractKlassApiDataIntegrityTest {

    protected static KlassApiMigrationClient klassApiMigrationClient;

    static Response sourceResponseClassifications;
    static Response targetResponseClassifications;

    static Response ssbSectionResponse;

    static List<Integer> sourceResponseIdentifiers = new ArrayList<>();

    protected static int lastClassificationId;

    public static final String sourceHost = MigrationTestConfig.getSourceHost();

    public static final String targetHost = MigrationTestConfig.getTargetHost();

    static List<String> ssbSectionNames = new ArrayList<>();

    private static void setSourceResponseIdentifiers() {
        int totalPages = sourceResponseClassifications.path(PAGE_TOTAL_ELEMENTS);
        for(int i = 0; i < totalPages; i++) {
            List<Integer> pageIdentifiers = new ArrayList<>(sourceResponseClassifications.path(EMBEDDED_CLASSIFICATIONS_ID));
            sourceResponseIdentifiers.addAll(pageIdentifiers);

            if(sourceResponseClassifications.path(LINKS_NEXT_HREF) == null) {
                return;
            }
            sourceResponseClassifications =
                    klassApiMigrationClient.getFromSourceApi(sourceResponseClassifications.path(LINKS_NEXT_HREF), null,null);

        }
    }

    private static void setSsbSectionNames() {
        List<?> ssbSections = ssbSectionResponse.path(EMBEDDED_SSB_SECTIONS);
        for(int i = 0; i < ssbSections.size(); i++) {
            List <String> names = new ArrayList<>(ssbSectionResponse.path(EMBEDDED_SSB_SECTIONS_NAME));
            ssbSectionNames.addAll(names);
        }
    }

    @BeforeAll
    static void beforeAll() {
        klassApiMigrationClient = new KlassApiMigrationClient();

        boolean sourceUp = klassApiMigrationClient.isApiAvailable(sourceHost);
        boolean targetUp = klassApiMigrationClient.isApiAvailable(targetHost);

        Assumptions.assumeTrue(sourceUp && targetUp, "One or both APIs are not available, skipping tests.");

        sourceResponseClassifications = klassApiMigrationClient.getFromSourceApi(CLASSIFICATIONS_PATH, null, null);
        targetResponseClassifications = klassApiMigrationClient.getFromTargetApi(CLASSIFICATIONS_PATH, null, null);

        setSourceResponseIdentifiers();
        lastClassificationId = sourceResponseIdentifiers.get(sourceResponseIdentifiers.size() - 1);
        ssbSectionResponse = klassApiMigrationClient.getFromSourceApi("/" + SSB_SECTIONS,null, null);
        setSsbSectionNames();
    }

    @AfterAll
    public static void cleanUp(){
        System.out.println("Cleanup after tests");
    }
}
