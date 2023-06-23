package no.ssb.klass.initializer.stabas;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import no.ssb.klass.core.config.ConfigurationProfiles;

@Configuration
@Profile(ConfigurationProfiles.SMALL_IMPORT)
public class StabasTestConfiguration extends StabasConfiguration {

    @Override
    public String getClassificationsZipFile() {
        return PATH + "dev/classifications.zip";
    }

    @Override
    public String getCorrespondenceTablesZipFile() {
        return PATH + "dev/correspondencetables.zip";
    }
}
