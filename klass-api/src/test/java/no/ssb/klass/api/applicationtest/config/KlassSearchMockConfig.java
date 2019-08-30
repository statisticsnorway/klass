package no.ssb.klass.api.applicationtest.config;

import no.ssb.klass.solr.config.ConfigurationProfiles;
import org.apache.solr.client.solrj.SolrClient;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.solr.core.SolrTemplate;

/**
 * @author Mads Lundemo, SSB.
 */
@Configuration
@Profile(ConfigurationProfiles.MOCK_SEARCH)
public class KlassSearchMockConfig {

    @Bean
    public SolrClient solrClient() {
        return Mockito.mock(SolrClient.class);
    }

    @Bean
    public SolrTemplate mockedSolrTemplate() {
        return Mockito.mock(SolrTemplate.class);
    }

}
