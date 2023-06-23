package no.ssb.klass.initializer.stabas;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import no.ssb.klass.core.config.ConfigurationProfiles;

@Configuration
@Profile("!" + ConfigurationProfiles.SMALL_IMPORT)
public class StabasConfiguration {
    protected static final String PATH = "import/stabas/";

    public String getPatchfile() {
        return PATH + "patches.csv";
    }

    public String getClassificationsZipFile() {
        return PATH + "classifications.zip";
    }

    public String getCodelistsZipFile() {
        return PATH + "codelists.zip";
    }

    public String getCorrespondenceTablesZipFile() {
        return PATH + "correspondencetables.zip";
    }
}
