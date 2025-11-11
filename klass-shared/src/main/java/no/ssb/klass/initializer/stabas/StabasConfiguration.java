package no.ssb.klass.initializer.stabas;

import no.ssb.klass.core.config.ConfigurationProfiles;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

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
