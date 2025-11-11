package no.ssb.klass.api.config;

import no.ssb.klass.core.config.ConfigurationProfiles;

import org.opensearch.client.RestHighLevelClient;
import org.opensearch.data.client.orhlc.AbstractOpenSearchConfiguration;
import org.opensearch.data.client.orhlc.ClientConfiguration;
import org.opensearch.data.client.orhlc.OpenSearchRestTemplate;
import org.opensearch.data.client.orhlc.RestClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.Duration;

@Configuration
@Profile("!" + ConfigurationProfiles.MOCK_SEARCH)
public class OpenSearchConfig extends AbstractOpenSearchConfiguration {
    @Value("${opensearch.url}")
    private String opensearchUri;

    @Value("${opensearch.username}")
    private String username;

    @Value("${opensearch.password}")
    private String password;

    @Value("${opensearch.ssl}")
    private boolean ssl;

    // Constant for the stemmer
    public static final String NORWEGIAN_STEMMER_ANALYZER = "norwegian_stemmer_analyzer";

    @Override
    @Bean(destroyMethod = "close")
    public RestHighLevelClient opensearchClient() {
        ClientConfiguration clientConfiguration =
                (ssl
                                ? ClientConfiguration.builder()
                                        .connectedTo(opensearchUri.replace("https://", ""))
                                        .usingSsl()
                                        .withBasicAuth(username, password)
                                        .withConnectTimeout(Duration.ofSeconds(10))
                                        .withSocketTimeout(Duration.ofSeconds(5))
                                : ClientConfiguration.builder()
                                        .connectedTo(
                                                opensearchUri
                                                        .replace("https://", "")
                                                        .replace("http://", ""))
                                        .withConnectTimeout(Duration.ofSeconds(15))
                                        .withSocketTimeout(Duration.ofSeconds(60)))
                        .build();

        return RestClients.create(clientConfiguration).rest();
    }

    @Bean
    public OpenSearchRestTemplate opensearchRestTemplate() {
        return new OpenSearchRestTemplate(opensearchClient());
    }
}
