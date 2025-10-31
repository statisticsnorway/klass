package no.ssb.klass.core.config;

/**
 * Profile name constants
 *
 * @author Mads Lundemo, SSB.
 */
public final class ConfigurationProfiles {

    public static final String POSTGRES_EMBEDDED = "postgres-embedded";

    private ConfigurationProfiles() {
        // Utility class
    }

    /**
     * Profile for beans that should only be activated for the API application
     */

    public static final String API_ONLY = "api";
    /**
     * Profile for beans that should only be activated for the Klient/frontend
     * application
     */
    public static final String FRONTEND_ONLY = "frontend";

    /**
     * Profile intended for production environment (all test features should be
     * excluded).
     */
    public static final String PRODUCTION = "production";

    /**
     * Profile to be used during development, skips indexing classifications for
     * search at startup
     */
    public static final String SKIP_INDEXING = "skip-indexing";

    /**
     * Profile to be used during development, imports only a small number of
     * classifications from Stabas
     */
    public static final String SMALL_IMPORT = "small-import";

    /**
     * Profile to be used during development, emails are logged and not sent via
     * mailserver
     */
    public static final String MOCK_MAILSERVER = "mock-mailserver";

    /**
     * Profile to be used during development, search is mocked
     */
    public static final String MOCK_SEARCH = "mock-search";

    /**
     * Profile to be used during the api documentation test
     */
    public static final String API_DOCUMENTATION_TEST = "api-documentation-test";

    public static final String OPEN_SEARCH_LOCAL = "open-search-local";
}
